package xenogeddon.scenery;

import xenogeddon.Main;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class Bulkhead extends AbstractScenery {
	
	private static final float MAX_SIZE = 0.35f;

	public static Quaternion q90 = new Quaternion();
	private static TextureState ts;

	static {
		q90.fromAngleAxis(FastMath.PI/2, new Vector3f(0,-1,0));
	}

	public Bulkhead(Main m, boolean ew, float map_x, float height, float map_z) {
		super(m, "Bulkhead", map_x + 0.5f, height, map_z + 0.5f);

		// The height is the minimum hright it can be to avoid cutting people's heads off
		TriMesh c = new Box("Bulkhead", new Vector3f(-0.5f, 0, -MAX_SIZE/2), new Vector3f(0.5f, MAX_SIZE, MAX_SIZE/2));
		if (!ew) {
			c.setLocalRotation(q90);
		}

		c.setModelBound(new BoundingBox());
		c.updateModelBound();

		if (ts == null) {
			ts = m.getDisplay().getRenderer().createTextureState();  
			ts.setEnabled(true);  
			Texture t = TextureManager.loadTexture("data/textures/bulkhead2.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);
			ts.setTexture(t);
		}
		c.setRenderState(ts);
		this.attachChild(c);


		setModelBound(new BoundingBox());
		updateModelBound();


	}

}
