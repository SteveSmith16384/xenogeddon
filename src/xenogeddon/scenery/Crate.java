package xenogeddon.scenery;

import ssmith.lang.Functions;
import xenogeddon.CollisionBox;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class Crate extends GameObject {

	private static final float HEIGHT = .3f;

	public Quaternion q_skew = new Quaternion();
	private static TextureState ts;

	public Crate(Main m, float x, float z) {
		super(m, "Crate", true, x, z, 0);//HEIGHT/2);

		this.setIsCollidable(false); // Cause problems cos the AI cant get round it.
		
		CollisionBox b = new CollisionBox(m, new Vector3f(HEIGHT/2, HEIGHT/2, HEIGHT/2), HEIGHT/2, HEIGHT/2, HEIGHT/2, this, false);

		q_skew.fromAngleAxis(Functions.rndFloat(-2, 2), new Vector3f(0,1,0));
		b.setLocalRotation(q_skew);

		if (ts == null) {
			ts = main.getDisplay().getRenderer().createTextureState();  
			ts.setEnabled(true);  
			ts.setTexture(TextureManager.loadTexture("./data/textures/Crate.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor), 0);
		}
		b.setRenderState(ts);  
		b.updateRenderState();

		this.attachChild(b);

		this.setModelBound(new BoundingBox());
		this.updateModelBound();
	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}

	public void update(float interpolation) {
	}

}
