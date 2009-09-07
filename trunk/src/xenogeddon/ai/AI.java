package xenogeddon.ai;

import ssmith.astar.AStar;
import ssmith.lang.Functions;
import ssmith.util.PointF;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;
import com.jme.math.Vector3f;

public abstract class AI {

	protected Main main;
	protected CreatureObject body;
	protected AStar astar;
	protected long wait_until;
	protected int orig_mode; // The mode before we (say) recoiled

	public AI(Main m, CreatureObject _body) {
		main = m;
		body = _body;
	}

	public abstract void process(float interpolation);

	public abstract void hasWalkedIntoObject(GameObject o); // This is called if the creature was unable to move anywhere

	public abstract void hasBeenWalkedInto(CreatureObject cre); // Has been touched by another creature

	public abstract void hasWalkedIntoCreature(CreatureObject cre); // Has touched another creature

	public abstract void hasBeenWounded(CreatureObject cre);

	public abstract void setMode(int m);
	
	public PointF getPointCloseTo(int x, int z, int rad) {
		int x2, z2;
		while (true) {
			x2 = Functions.rnd(x-rad, x+rad);
			z2 = Functions.rnd(z-rad, z+rad);
			// Check it's not off the map
			if (x2 >= 0 && x2 < main.map.getMapWidth() && z2 >= 0 && z2 < main.map.getMapHeight()) {
				if (x2 != x || z2 != z) {
					break;
				}
			}
		}
		return new PointF(x2, z2);
	}

	public Vector3f getDirTo(PointF p, float y_off) {
		Vector3f dest = new Vector3f(p.x, y_off, p.z);
		Vector3f vect = new Vector3f();
		vect.set(dest.subtract(body.getLocalTranslation()).normalize());
		return vect;
	}

	protected CreatureObject getEnemy(boolean check_can_see, float max_dist) {
		Object o;
		CreatureObject closest = null;
		float dist = max_dist;
		for (int i=0 ; i<main.objects.size() ; i++) {
			o = main.objects.get(i);
			if (o instanceof CreatureObject) {
				CreatureObject c = (CreatureObject)o;
				if (c.is_alive && c.invisible == false) {
					if (c.side != this.body.side) {
						float d = this.body.getDistTo(c); 
						if (d <= dist) {
							if (check_can_see) {
								if (this.body.canSee(c, false) == false) {
									continue;
								}
							}
							dist = d;
							closest = c;
						}
					}
				}
			}
		}
		return closest;
	}

}
