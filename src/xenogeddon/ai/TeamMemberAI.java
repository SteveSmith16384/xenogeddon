package xenogeddon.ai;

import java.awt.Point;
import ssmith.astar.AStar;
import ssmith.astar.WayPoints;
import ssmith.lang.Functions;
import ssmith.util.Interval;
import ssmith.util.PointF;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.maps.MapSquare;
import xenogeddon.models.CreatureObject;
import xenogeddon.models.IAnimated;

import com.jme.math.Vector3f;

public final class TeamMemberAI extends AI {

	private static final long WAIT_TIME = 3000;
	private static final long RECOIL_TIME = 750;

	// AI Modes:-
	private static final int FINDING_DEST = 1;
	private static final int MOVING_DIRECT = 2;
	private static final int MOVING_ASTAR = 3;
	private static final int SHOOTING = 4;
	private static final int WAITING = 5;
	private static final int RECOILING = 6;

	private int mode = -1;
	private WayPoints waypoints;
	private PointF final_dest;
	private Interval check_for_enemies_interval = new Interval(1000);
	private Interval shoot_interval = new Interval(200);
	private Interval bullet_interval = new Interval(50);
	private CreatureObject enemy;

	public TeamMemberAI(Main m, CreatureObject _body) {
		super(m, _body);
		this.setMode(WAITING);
	}

	public void process(float interpolation) {
		/*if (this.body.is_alive == false) {
			int redgfdg = 546654;
		}*/
		if (mode == RECOILING) {
			long diff = this.wait_until - System.currentTimeMillis();
			if (diff < 0) {
				this.setMode(orig_mode);
			}
		} else {
			if (enemy == null) {
				if (check_for_enemies_interval.hitInterval()) {
					enemy = this.getEnemy(true, Main.VIEW_DIST);
				}
			} else {
				if (enemy.is_alive == false) {
					enemy = null;
				} else if (this.body.canSee(enemy, true) == false) {
					enemy = null;
				}
			}
			if (enemy != null) {
				this.body.lookAt(enemy.getLocalTranslation(), Vector3f.UNIT_Y);
				this.body.updateGeometricState(interpolation, true);
				this.setMode(SHOOTING);
				if (shoot_interval.hitInterval()) {
					this.body.shootAt(enemy);
				} else if (bullet_interval.hitInterval()) {
					// This is so we can have several bullets
					this.body.launchBullet(enemy);
				}
			} else {
				if (this.mode == SHOOTING) {
					this.setMode(this.orig_mode);
				}
				if (astar != null) {
					if (astar.isFindingPath()) {
						return;
					} else {
						if (astar.wasSuccessful()) {
							this.setMode(MOVING_ASTAR);
							waypoints = astar.getRoute();
						} else { // Failed to find path.
							this.setMode(WAITING);
						}
						astar = null;
						this.body.blocked_square = null;
					}
				} else if (mode == WAITING) {
					long diff = this.wait_until - System.currentTimeMillis();
					if (diff < 0) {
						double dist = this.body.getDistTo(main.player);
						//Main.p("AI at " + this.body.getLocalTranslation());
						if (dist > 3) {
							this.setMode(FINDING_DEST);
						}
					}
				} else if (mode == FINDING_DEST) {
					final_dest = super.getPointCloseTo((int)main.player.getLocalTranslation().x, (int)main.player.getLocalTranslation().z, 4);
					if (main.map.getSq(final_dest.x, final_dest.z).major_type == MapSquare.FLOOR) {
						//Main.p("AI going to " + this.final_dest);
						//Main.p("Player at " + main.player.getLocalTranslation());
						final_dest.x += 0.5f;
						final_dest.z += 0.5f;
						this.setMode(MOVING_DIRECT);
					}
				} else if (mode == MOVING_DIRECT) {
					//Vector3f move_dest = new Vector3f(final_dest.x, this.body.y_off, final_dest.z);

					double dist_to_dest = Functions.distance(this.body.getLocalTranslation().x, this.body.getLocalTranslation().z, final_dest.x, final_dest.z); // Must be floats!

					Vector3f move_dir = super.getDirTo(final_dest, this.body.y_off);//.mult();
					Vector3f last_move_dir = this.body.move(move_dir, this.body.getSpeed() * interpolation);

					double new_dist_to_dest = Functions.distance(this.body.getLocalTranslation().x, this.body.getLocalTranslation().z, final_dest.x, final_dest.z); // Must be floats!
					//Main.p("Dist to dest = " + dist_to_dest);
					if (new_dist_to_dest >= dist_to_dest) { //new_dist_to_dest - dist_to_dest
						// Either now further away or no closer
						this.setMode(WAITING);
					} else {
						this.body.lookAt(this.body.getLocalTranslation().add(last_move_dir), Vector3f.UNIT_Y); // Must be before we move, in case we move PAST our dest, and end up doing a 180.
					}

				} else if (mode == MOVING_ASTAR) {
					Point p2 = this.waypoints.getNextPoint();
					if (p2 == null) { 
						waypoints = null;
						this.setMode(WAITING);
					} else {
						PointF pf = new PointF(p2.x + 0.5f, p2.y + 0.5f);
						double dist_to_dest = Functions.distance(this.body.getLocalTranslation().x, this.body.getLocalTranslation().z, pf.x, pf.z); // Must be floats!
						Vector3f move_dir = super.getDirTo(pf, this.body.y_off);
						this.body.move(move_dir, this.body.getSpeed() * interpolation);

						Vector3f dest = new Vector3f(pf.x, this.body.y_off, pf.z);
						this.body.lookAt(dest, Vector3f.UNIT_Y);
						this.body.updateGeometricState(0, true);

						double new_dist_to_dest = Functions.distance(this.body.getLocalTranslation().x, this.body.getLocalTranslation().z, pf.x, pf.z); // Must be floats!
						if (new_dist_to_dest >= dist_to_dest) {
							this.waypoints.remove(0);
						}
					}
				}
			}
		}
	}


	public void hasWalkedIntoCreature(CreatureObject cre) {
		/*if (cre.side == this.body.side) {
			body.move_around = true;
		}*/
	}

	public void hasBeenWounded(CreatureObject cre) {
		this.body.lookAt(cre.getLocalTranslation(), Vector3f.UNIT_Y);
		this.enemy = cre;
		if (cre == main.player) {
			main.text_hud.add("YOU HAVE SHOT A TEAM MEMBER!");
		}
		//this.setMode(RECOILING);
	}


	public void hasBeenWalkedInto(CreatureObject cre) {
		if (this.mode == WAITING) {
			if (cre.side == this.body.side) { // Then move out of the way!
				Vector3f pos = cre.getLocalTranslation().subtract(this.body.getLocalTranslation()).normalize();
				pos.multLocal(2f);
				float x = this.body.getLocalTranslation().x - pos.x;
				float z = this.body.getLocalTranslation().z - pos.z;
				if (main.map.getSq(x, z).major_type == MapSquare.FLOOR) {
					final_dest = new PointF(x, z);
					this.setMode(MOVING_DIRECT);
				}
			} else { // Face the fight!
				this.enemy = cre; // this will also face us in the right direction
			}
		}
	}

	public void setMode(int m) {
		if (this.mode != m) {
			/*if (Main.DEBUG) {
				Main.p("TeamMember AI mode now " + m);
			}*/
			if (this.mode == WAITING || this.mode == FINDING_DEST || this.mode == MOVING_DIRECT || this.mode == MOVING_ASTAR) {
				orig_mode = this.mode;
			}
			IAnimated anim = (IAnimated)this.body;
			switch (m) {
			case WAITING:
				this.wait_until = System.currentTimeMillis() + WAIT_TIME;
				anim.anim_standStill();
				break;
			case FINDING_DEST:
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
			case SHOOTING:
				anim.anim_shoot();
				break;
			default:
				throw new RuntimeException("Unknown AI mode: " + m);
			}
			this.mode = m;
		}
	}

	public void hasWalkedIntoObject(GameObject o) {
		// Turn around since we're facing the corner!
		this.body.getLocalRotation().mult(-1);
		if (this.waypoints != null) {
			// Shouldn't happen, unless they've hit a crate or teammate something!
			this.setMode(WAITING);
		} else {
			astar = new AStar(body);
			astar.findPath((int)this.body.getLocalTranslation().x, (int)this.body.getLocalTranslation().z, (int)final_dest.x, (int)final_dest.z, true);
		}
	}

}
