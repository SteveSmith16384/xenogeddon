package xenogeddon.models;

import java.io.IOException;

import xenogeddon.CollisionBox;
import xenogeddon.Main;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial.CullHint;

public class StasisPod extends AbstractColladaModel {
	
	private static final float DIAM = 0.5f;
	private static final float HEIGHT = .9f;
	
	public StasisPod(Main main, float x, float z) throws IOException {
		super(main, "StasisPod", true, x, z, DIAM);
		
		Node n = ModelLoaders.LoadColladaModel(main.getDisplay(), "./data/models/StasisPod/models/StasisPod.dae", "", 0.0006f);
		n.setIsCollidable(false);
		n.setLocalTranslation(0.5f, 0, 0.5f);
		this.attachChild(n);
		
		CollisionBox b = new CollisionBox(main, new Vector3f(HEIGHT/2, HEIGHT/2, HEIGHT/2), HEIGHT/2, HEIGHT/2, HEIGHT/2, this, false);
		b.setCullHint(CullHint.Always);
		this.attachChild(b);
		
		Quaternion q = new Quaternion();
		q.fromAngleAxis(FastMath.PI/2, new Vector3f(-1,0,0));
		n.setLocalRotation(q);
	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}

	public void update(float interpolation) {
		// Do nothing
	}

}
