package xenogeddon.walls;

import xenogeddon.CollisionQuad;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class Wall extends GameObject {

	public static Quaternion q90 = new Quaternion();
	private static TextureState ts;
	private static MaterialState ms;

	static {
		q90.fromAngleAxis(FastMath.PI/2, new Vector3f(0,-1,0));
	}

	public Wall(Main m, float x, float z, int side, float bottom, float top) {
		super(m, "Wall_" + (int)x + "_" + (int)z, true, x, z, 0f);

		float height = top - bottom;
		switch (side) {
		case 0: // Side
			setLocalRotation(q90);
			setLocalTranslation((float)x, bottom + (height/2), (float)z + 0.5f);
			break;
		case 1: // Back
			setLocalTranslation((float)x + 0.5f, bottom + (height/2), (float)z);
			break;
		case 2: // Other side
			setLocalRotation(q90);
			setLocalTranslation((float)x + 1f, bottom + (height/2), (float)z + 0.5f);
			break;
		case 3: // Front
			setLocalTranslation((float)x + 0.5f, bottom + (height/2), (float)z + 1f);
			break;
		default:
			throw new RuntimeException("Unknown wall rotation");
		}

		this.collider = new CollisionQuad(1, height, this, false);
		collider.setModelBound(new BoundingBox());
		collider.updateModelBound();
		this.attachChild(collider);
		this.setIsCollidable(true);

		if (ts == null) {
			ts = main.getDisplay().getRenderer().createTextureState();  
			ts.setEnabled(true);  
			Texture t = TextureManager.loadTexture("./data/textures/wall2.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);
			ts.setTexture(t);
		}
		collider.setRenderState(ts);

		if (ms == null) {
			ms = main.getDisplay().getRenderer().createMaterialState();  
			ms.setEnabled(true);
			ms.setShininess(128);

		}
		collider.setRenderState(ts);

		setModelBound(new BoundingBox());
		updateModelBound();
	}

	public void update(float interpolation) {
		// Do nothing
	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}

}
