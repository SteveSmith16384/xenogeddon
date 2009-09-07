package xenogeddon.scenery;

import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;

import com.jme.math.Vector3f;

public abstract class AbstractScenery extends GameObject {
	
	public AbstractScenery(Main m, String name, float _x, float _y, float _z) {
		super(m, name, false, _x, _z, _y);
	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}

	public void update(float interpolation) {
	}

}
