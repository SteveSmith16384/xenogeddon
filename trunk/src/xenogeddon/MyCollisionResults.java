package xenogeddon;

import java.util.ArrayList;

import ssmith.lang.Functions;
import xenogeddon.models.BlissBeamCannon;
import xenogeddon.models.CreatureObject;
import xenogeddon.models.StasisPod;
import xenogeddon.scenery.ComputerConsole;
import xenogeddon.scenery.Crate;
import xenogeddon.scenery.Teleporter;
import xenogeddon.walls.FloorAndCeiling;
import xenogeddon.walls.SlidingDoor;
import xenogeddon.walls.Wall;
import com.jme.intersection.CollisionData;
import com.jme.intersection.CollisionResults;
import com.jme.scene.Geometry;

public final class MyCollisionResults extends CollisionResults {

	private Main main;
	private ArrayList<GameObject> processed_targets = new ArrayList<GameObject>();
	//private boolean will_blockage_move;

	public MyCollisionResults(Main m) {
		super();
		main = m;
	}

	public void clear() {
		super.clear();
	}

	public void addCollision(Geometry s, Geometry t) {
		super.addCollisionData(new CollisionData(s, t));
	}

	public void processCollisions() {
		processed_targets.clear();
		if (this.getNumber() > 0) {
			for (int i=0 ; i<this.getNumber() ; i++) { // We need to check all collisions, in case the first one is a wall and the second is an alien
				CollisionData data = super.getCollisionData(i);

				GameObject source = null;
				ICollider isource= null;
				//try {
					isource = (ICollider)data.getSourceMesh();
					source = isource.getOwner();
				/*} catch (java.lang.ClassCastException ex) {
					Main.p(data.getSourceMesh().getName());
					ex.printStackTrace();
					//return null;
				}*/

				GameObject target = null;
				ICollider itarget;
				//try {
					itarget = (ICollider)data.getTargetMesh();
					target = itarget.getOwner();
				/*} catch (java.lang.ClassCastException ex) {
					Main.p(data.getTargetMesh().getName()); 
					ex.printStackTrace();
					//return null;
				}*/

				// Ensure we only process each target once
				if (processed_targets.contains(target) == false) {
					checkCollision(source, target);
					processed_targets.add(target);
				}
			}
		}
	}

	private void checkCollision(GameObject source, GameObject target) {
		//Main.p(System.currentTimeMillis() + ": " + source  + " collided with " + target);
		if (source instanceof CreatureObject) {
			if (target instanceof CreatureObject) {
				this.creatureObjectVCreatureObject((CreatureObject)source, (CreatureObject)target);
				return;
			} else if (target instanceof Wall) {
				// Do nothing
				return;
			} else if (target instanceof FloorAndCeiling) {
				// Do nothing
				return;
			} else if (target instanceof Teleporter) {
				main.mission.playerEnteredTeleporter();
				return;
			} else if (target instanceof ComputerConsole) {
				return;
			} else if (target instanceof Crate) {
				return;
			} else if (target instanceof BlissBeamCannon) {
				return;
			} else if (target instanceof StasisPod) {
				return;
			} else if (target instanceof SlidingDoor) {
				SlidingDoor s = (SlidingDoor)target;
				s.touched(source);
				return;
			}
		} else if (source instanceof SlidingDoor) {
			if (target instanceof Wall == false && target instanceof FloorAndCeiling == false && target instanceof SlidingDoor == false) {
				SlidingDoor s = (SlidingDoor)source;
				s.touched(target); // Start opening again!
				return;
			} else if (target instanceof Wall) {
				// Do nothing
				return;
			} else if (target instanceof FloorAndCeiling) {
				// Do nothing
				return;
			} else if (target instanceof SlidingDoor) {
				// Do nothing
				return;
			} else if (target instanceof Crate) {
				// Do nothing
				return;
			}
		}
		Main.p("No collision set up for " + source + " v " + target); // These are sometimes null!?
	}


	private void creatureObjectVCreatureObject(CreatureObject o1, CreatureObject o2) {
		/*if (o1.is_alive == false || o2.is_alive == false) {
			int dfgfdg = 45646;
		}*/
		if (o1.side != o2.side) {
			o1.playAttackSound();
			int att = Functions.rnd(0, o1.melee_attack);
			int def = Functions.rnd(0, o2.melee_attack);

			if (att > def) {
				o2.damage(1, o1, o2.getLocalTranslation(), o2.getDirTo(o1));
			} else if (att < def) {
				o1.damage(1, o2, o1.getLocalTranslation(), o1.getDirTo(o1));
			}
		}
		if (o1.ai != null) {
			o1.ai.hasWalkedIntoCreature(o2);
		}
		if (o2.ai != null) {
			o2.ai.hasBeenWalkedInto(o1);
		}
		/*if (o1.side != o2.side) {
			Main.p(o1 + " has hit " + o2);
		}*/
	}

}
