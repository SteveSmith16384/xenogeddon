package xenogeddon.models;

import ssmith.lang.Functions;
import ssmith.util.Interval;
import xenogeddon.CollisionBox;
import xenogeddon.GameObject;
import xenogeddon.ICollider;
import xenogeddon.Main;
import xenogeddon.MyPickResults;
import xenogeddon.ai.AI;
import xenogeddon.effects.Bullet;
import xenogeddon.effects.GreenBloodSpurt;
import xenogeddon.effects.RedBloodSpurt;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

public abstract class CreatureObject extends GameObject implements IAnimated {

	protected static final int SIDE_TERMINATORS = 0;
	protected static final int SIDE_TYRANTS = 1;
	private static final long CORPSE_DURATION = 60*1000;

	public int side, melee_attack, health;
	public boolean is_alive = true;
	public AI ai = null;
	protected float prev_x, prev_z;
	protected float speed;
	private MyPickResults can_see_results;
	public int ammo = 999;
	public boolean invisible = false; // hiding?
	public boolean model_nodes_loaded = false;
	private Interval check_load_model_interval = new Interval(3000);
	private String display_name;
	public Node body_node, weapon_node;
	private long remove_at;
	private Vector3f last_move_dir = new Vector3f();

	public CreatureObject(Main m, String node_name, String _display_name, int _side, float _x, float _z, float _y_off, float _diam, float spd, int hlth, int att) {
		super(m, node_name, false, _x, _z, _y_off);
		side = _side;
		this.speed = spd;
		this.health = hlth;
		this.melee_attack = att;
		can_see_results = new MyPickResults(this.collider);
		display_name = _display_name;

		float h = (this.getLocalTranslation().y - 0.01f);
		collider = new CollisionBox(main, this.getLocalTranslation(), _diam/2, h, _diam/2, this, false); 
		if (Main.SHOW_COLLISION_BOXES) {
			collider.setRenderState(main.wireState);
			collider.updateRenderState();
		} else {
			collider.setCullHint(CullHint.Always);
		}

		main.attachToRootNode(collider);
	}

	public abstract void loadModels();

	public abstract void unloadModels();

	public abstract void playAttackSound();

	public abstract void playShootingSound();

	public abstract void playDeathSound();

	public abstract void playHarmSound();

	public abstract void playFallingSound();

	public void updateGeometricState(float time, boolean initiator) {
		super.updateGeometricState(time, initiator);
		if (collider != null) {
			collider.updateGeometricState(time, initiator);
		}
	}
	
	public Vector3f getLastMoveDir() {
		return last_move_dir;
	}

	public void update(float interpolation) {
		if (check_load_model_interval.hitInterval()) {
			if (this.model_nodes_loaded == false) {
				if (this.getDistTo(main.player) <= Main.VIEW_DIST + 1) {
					this.loadModels();
				}
			} else {
				if (this.getDistTo(main.player) > Main.VIEW_DIST + 1) {
					this.unloadModels();
				}

			}
		}
		if (this.is_alive) {
			if (ai != null) {
				ai.process(interpolation);
			}
		} else if (Main.REMOVE_CORPSES) {
			if (System.currentTimeMillis() > remove_at) {
				main.removeObjectForUpdate(this);
				this.removeFromParent();
				main.getRootNode().updateGeometricState(interpolation, true);
			}
		}

	}

	public float getSpeed() {
		return this.speed;
	}


	public boolean canSee(GameObject other, boolean check_dist) {
		if (check_dist) {
			if (this.getDistTo(other) > Main.VIEW_DIST) {
				return false;
			}
		}
		Vector3f dir = other.getLocalTranslation().subtract(this.getLocalTranslation()).normalize();
		Ray r = new Ray(this.getLocalTranslation(), dir);
		can_see_results.clear();
		can_see_results.setCheckDistance(true);
		main.getRootNode().findPick(r, can_see_results);
		if (can_see_results.getNumber() > 0) {
			for (int i=0 ; i<can_see_results.getNumber() ; i++) {
				GameObject obj = can_see_results.getGameObject(i);
				if (obj == other) {
					return true;
				} else if (obj.getClass().equals(this.getClass()) == false) {
					return false; // Cannot see through any other object except those of the same type.
				}
			}
		}
		return false;

	}


	/**
	 * This returns the direction we actually moved.
	 * @param move_dir
	 * @param spd
	 * @return
	 */
	public Vector3f move(Vector3f move_dir, float spd) {
		last_move_dir.x = move_dir.x;
		last_move_dir.y = 0f;
		last_move_dir.z = move_dir.z;

		boolean hit_x = false;
		boolean hit_z = false;

		Vector3f dir = move_dir.mult(spd);

		collision_results.clear();
		int current_collisions = 0;

		// Move X
		this.getLocalTranslation().x += dir.x;
		this.updateGeometricState(0, true);
		collider.findCollisions(main.getRootNode(), collision_results);
		if (collision_results.getNumber() > 0) {
			current_collisions = collision_results.getNumber();
			this.getLocalTranslation().x -= dir.x;
			hit_x = true;
			last_move_dir.x = 0;
		}

		// Move Z
		this.getLocalTranslation().z += dir.z;
		this.updateGeometricState(0, true);
		collider.findCollisions(main.getRootNode(), collision_results);
		if (collision_results.getNumber() > current_collisions) {
			current_collisions = collision_results.getNumber();
			this.getLocalTranslation().z -= dir.z;
			hit_z = true;
			last_move_dir.z = 0;
		}

		// Move X again 
		if (!hit_x && hit_z) {
			float new_x = Functions.MakeSameSignAs(dir.z, dir.x); 
			this.getLocalTranslation().x += new_x;
			this.updateGeometricState(0, true);
			collider.findCollisions(main.getRootNode(), collision_results);
			if (collision_results.getNumber() > current_collisions) {
				// Collided again!
				current_collisions = collision_results.getNumber();
				this.getLocalTranslation().x -= new_x;
				this.updateGeometricState(0, true);
				hit_x = true;
			}
		} else if (hit_x && !hit_z) {
			// Move Z again
			float new_z = Functions.MakeSameSignAs(dir.x, dir.z); 
			this.getLocalTranslation().z += new_z;
			this.updateGeometricState(0, true);
			collider.findCollisions(main.getRootNode(), collision_results);
			if (collision_results.getNumber() > current_collisions) {
				current_collisions = collision_results.getNumber();
				this.getLocalTranslation().z -= new_z;
				this.updateGeometricState(0, true);
				hit_z = true;
			}
		} else {
			this.updateGeometricState(0, true);
		}

		if ((hit_x || dir.x == 0) && (hit_z || dir.z == 0)) {
			if (dir.x != 0 && dir.z != 0) { // Check they actually tried to move somewhere
				if (ai != null) {
					for (int i=0 ; i<collision_results.getNumber() ; i++) {
						ICollider ic = (ICollider)collision_results.getCollisionData(i).getTargetMesh();
						if (ic.willMove() == false) {
							ai.hasWalkedIntoObject(ic.getOwner());
							break;
						}
					}
				}
			}
		}

		// Now process all the collisions
		this.collision_results.processCollisions();

		return last_move_dir;

	}


	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		if (is_alive) {
			if (this.side == SIDE_TYRANTS) {
				GreenBloodSpurt.AddBloodSpurt(main, pos, dir);
			} else {
				RedBloodSpurt.AddBloodSpurt(main, pos, dir);
			}
			this.health -= amt;
			if (this == main.player) {
				main.ha.startAlarm();
				main.updateBreathInterval();
				main.updateStatHUD(); // To show new health
			}
			if (this.health <= 0) {
				playDeathSound();
				//Main.p(this.name + " has been killed.");

				this.is_alive = false;
				this.collider.removeFromParent();
				this.collider = null;
				if (Main.REMOVE_CORPSES) {
					remove_at = System.currentTimeMillis() + CORPSE_DURATION;
				} else {
					main.removeObjectForUpdate(this);
				}
				this.anim_die();
				if (this == main.player) {
					main.playerDied();
				} else if (this.side == main.player.side) {
					//main.text_hud.add(this.display_name + " has been killed.");
					main.text_hud.add("A Terminator has been killed.");
				}
			} else {
				playHarmSound();
				if (ai != null) { // Not the player
					this.ai.hasBeenWounded(cre);
				}
			}
		}
	}

	public void shootAt(CreatureObject o) {
		//Main.p(this.name + " is shooting at " + o.name);
		//new BulletStreak(main, this.getLocalTranslation(), o.getLocalTranslation());
		this.launchBullet(o);
		this.playShootingSound();
		o.damage(1, this, o.getLocalTranslation(), this.getDirTo(o));
	}

	public void launchBullet(CreatureObject o) {
		new Bullet(main, this.weapon_node.getWorldBound().getCenter(), o.getLocalTranslation());
		main.lights.shot_flash = true;
	}

	public void setSpeed(float s) {
		this.speed = s;
	}

}
