package xenogeddon.effects;

import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;

import com.jme.math.Vector3f;

public class AbstractEffect extends GameObject {

	protected long remove_at;
	protected boolean permanent;

	public AbstractEffect(Main m, String name, float x, float y, float z, boolean perm, long duration) {
		super(m, name, false, x, z, y);
		main.attachToRootNode(this);

		permanent = perm;
		if (!permanent) {
			remove_at = System.currentTimeMillis() + duration;
			main.addObjectForUpdate(this);
		}
	}

	public void update(float interpolation) {
		//if (this.permanent == false) {
			if (System.currentTimeMillis() > remove_at) {
				this.remove();
			}
		//}
	}
	
	protected void remove() {
		main.removeObjectForUpdate(this);
		this.removeFromParent();
		
		main.getRootNode().updateGeometricState(0, true);
	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}
}
