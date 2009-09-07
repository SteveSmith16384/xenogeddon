package xenogeddon.ai;

import java.awt.Point;
import ssmith.astar.AStar;
import ssmith.astar.WayPoints;
import ssmith.lang.Functions;
import ssmith.util.Interval;
import ssmith.util.PointF;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;
import xenogeddon.models.IAnimated;
import xenogeddon.models.Tyrant;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial.CullHint;

public final class EnemyAI extends AI {

	private static final long WAIT_TIME = 1000; // Don't want to be too long since if enemy in front gets killed, we don't want to hang around!
	private static final long RECOIL_TIME = 400;
	private static final long ATTACK_TIME = 500;
	private static final float START_FALL_SPEED = 0.01f;
	private static final int FALL_VIEW_DIST = 5;

	// AI Modes:-
	private static final int NOTHING = 0;
	private static final int MOVING_DIRECT = 1;
	private static final int MOVING_ASTAR = 2;
	private static final int WAITING = 3; // Waiting for something to move out the way?
	private static final int RECOILING = 4;
	private static final int ATTACKING = 5;
	public static final int CEILING_WAITING = 6;
	private static final int CEILING_FALLING = 7;

	private int mode = -1; // To ensure we change mode

	private WayPoints waypoints;
	private PointF final_dest;
	private Interval check_for_enemies_interval = new Interval(500);
	private CreatureObject enemy;
	private float fall_speed = START_FALL_SPEED;

	public EnemyAI(Main m, CreatureObject _body) {
		super(m, _body);
		this.setMode(WAITING);
	}

	public void process(float interpolation) {
		if (mode == CEILING_WAITING) {
			if (check_for_enemies_interval.hitInterval()) {
				enemy = super.getEnemy(false, FALL_VIEW_DIST);
				if (enemy != null) { // this
					this.setMode(CEILING_FALLING);
					body.playFallingSound();
					body.setCullHint(CullHint.Never);
				}
			}
		} else if (mode == CEILING_FALLING) {
			if (this.body.getLocalTranslation().y > Tyrant.Y_OFF) {
				this.body.getLocalTranslation().y -= (fall_speed * interpolation);
				this.body.updateGeometricState(interpolation, true);
				fall_speed = fall_speed * 2;
			} else {
				this.body.invisible = false;
				this.body.getLocalTranslation().y = Tyrant.Y_OFF;
				this.setMode(NOTHING);
				this.check_for_enemies_interval.fireInterval(); // SO we run for player straight away
			}
		} else if (mode == RECOILING) {
			long diff = this.wait_until - System.currentTimeMillis();
			if (diff < 0) {
				this.setMode(orig_mode);
			} else {
				if (this.orig_mode == MOVING_DIRECT) {
					// Keep moving fwd even though we're recoiling
					this.moveDirect(interpolation, this.body.getSpeed()/4);
				}
			}
		} else if (mode == ATTACKING) {
			long diff = this.wait_until - System.currentTimeMillis();
			if (diff < 0) {
				this.setMode(orig_mode);
			}
		} else {
			if (astar != null) {
				if (astar.isFindingPath()) {
					return;
				} else {
					if (astar.wasSuccessful()) {
						this.setMode(MOVING_ASTAR);
						waypoints = astar.getRoute();
						//astar_count = 4; // No. of square to use a star
					} else { // Failed to find path.
						this.setMode(WAITING);
					}
					astar = null;
					this.body.blocked_square = null;
				}
			}

			if (check_for_enemies_interval.hitInterval()) {
				if (enemy == null) {
					enemy = super.getEnemy(true, Main.VIEW_DIST);
					if (enemy == null) {
						// Find closest non-visible enemy  WHY?  CANT REMEMBER
						enemy = super.getEnemy(false, Main.ENEMY_NON_VISIBLE_DIST);
					}
				} else if (enemy.is_alive == false){
					enemy = null;
				}
				if (enemy != null) {
					// Update our dest
					final_dest = new PointF(enemy.getLocalTranslation().x, enemy.getLocalTranslation().z);
				} else {
					this.setMode(NOTHING);
				}
			}

			if (mode == NOTHING) {
				if (enemy == null) {
					// Wander?  No, takes up too much CPU
					/*final_dest = super.getPointCloseTo((int)this.body.getLocalTranslation().x, (int)this.body.getLocalTranslation().z, 4);
					if (main.map.getSq(final_dest.x, final_dest.z).type == MapSquare.FLOOR) {
						final_dest.x += 0.5f;
						final_dest.z += 0.5f;
						this.setMode(MOVING_DIRECT);
					}*/
				} else {
					this.setMode(MOVING_DIRECT);
				}
			} else if (mode == WAITING) {
				long diff = this.wait_until - System.currentTimeMillis();
				if (diff < 0) {
					this.setMode(NOTHING);
					this.check_for_enemies_interval.hitInterval();
				}
			} else if (mode == MOVING_DIRECT) {
				this.moveDirect(interpolation, this.body.getSpeed());
			} else if (mode == MOVING_ASTAR) {
				Point p2 = this.waypoints.getNextPoint();
				if (p2 == null) { 
					waypoints = null;
					this.setMode(WAITING);
				} else {
					PointF p = new PointF(p2.x + 0.5f, p2.y + 0.5f);
					double dist_to_dest = Functions.distance(this.body.getLocalTranslation().x, this.body.getLocalTranslation().z, p.x, p.z); // Must be floats!
					Vector3f move_dir = super.getDirTo(p, this.body.y_off);
					this.body.move(move_dir, this.body.getSpeed() * interpolation);

					Vector3f dest = new Vector3f(p.x, this.body.y_off, p.z);
					this.body.lookAt(dest, Vector3f.UNIT_Y);
					this.body.updateGeometricState(0, true);

					double new_dist_to_dest = Functions.distance(this.body.getLocalTranslation().x, this.body.getLocalTranslation().z, p.x, p.z); // Must be floats!
					if (new_dist_to_dest >= dist_to_dest) {
						this.waypoints.remove(0);
					}
				}
			}
		}
	}

	private void moveDirect(float interpolation, float speed) {
		double dist_to_dest = Functions.distance(this.body.getLocalTranslation().x, this.body.getLocalTranslation().z, final_dest.x, final_dest.z); // Must be floats!

		Vector3f move_dir = super.getDirTo(final_dest, this.body.y_off);
		this.body.move(move_dir, speed * interpolation);

		double new_dist_to_dest = Functions.distance(this.body.getLocalTranslation().x, this.body.getLocalTranslation().z, final_dest.x, final_dest.z); // Must be floats!
		if (new_dist_to_dest >= dist_to_dest) {// new_dist_to_dest - dist_to_dest  Must be ">=" in case they don't move!
			// Need to update dest since the target has obviously moved
			this.check_for_enemies_interval.fireInterval();
			this.setMode(NOTHING); // Since we've reached our dest
		}
		// Now look where we are going
		Vector3f move_dest = new Vector3f(final_dest.x, this.body.y_off, final_dest.z);
		this.body.lookAt(move_dest, Vector3f.UNIT_Y);
	}

	public void setMode(int m) {
		if (this.mode != m) {
			//Main.p("EnemyAI mode now " + m);
			if (this.mode == WAITING || this.mode == NOTHING || this.mode == MOVING_DIRECT || this.mode == MOVING_ASTAR) {
				orig_mode = this.mode;
			}

			IAnimated anim = (IAnimated)this.body;
			switch (m) {
			case WAITING:
				this.wait_until = System.currentTimeMillis() + WAIT_TIME;
				anim.anim_standStill();
				break;
			case NOTHING:
			case CEILING_WAITING:
				anim.anim_standStill();
				break;
			case MOVING_DIRECT:
			case MOVING_ASTAR:
				anim.anim_walk();
				break;
			case RECOILING:
				this.wait_until = System.currentTimeMillis() + RECOIL_TIME;
				anim.anim_recoil();
				break;
			case CEILING_FALLING:
				anim.anim_recoil();
				break;
			case ATTACKING:
				this.wait_until = System.currentTimeMillis() + ATTACK_TIME;
				anim.anim_attack();
				break;
			default:
				throw new RuntimeException("Unknown mode:" + m);
			}

			this.mode = m;
		}
	}

	public void hasWalkedIntoCreature(CreatureObject cre) {
		if (cre.side == this.body.side) {
//			this.setMode(WAITING); // Wait for them to get out the way.
			//body.move_around = true;
		} else {
			this.setMode(ATTACKING);
		}
	}

	public void hasBeenWalkedInto(CreatureObject cre) {
		// Nothing to do
	}

	public void hasBeenWounded(CreatureObject cre) {
		this.setMode(RECOILING);
		if (this.enemy == null) {
			this.enemy = cre;
			this.check_for_enemies_interval.fireInterval(); // Need this so we calc the enemies' co-ords for our dest!
		}
	}

	public void hasWalkedIntoObject(GameObject o) {
		// Turn around since we're facing the corner!
		this.body.getLocalRotation().mult(-1);
		if (this.waypoints != null) {
			// Shouldn't happen, unless they've hit a crate or teammate something!
			//Main.p("EnemyAI has hit wall with A*!");
			//this.body.blocked_square = new Point((int)o.getLocalTranslation().x, (int)o.getLocalTranslation().z);
			this.setMode(WAITING);
		} else {
			astar = new AStar(body);
			astar.findPath((int)this.body.getLocalTranslation().x, (int)this.body.getLocalTranslation().z, (int)final_dest.x, (int)final_dest.z, true);
		}
	}
}
