package xenogeddon.scenery;

import xenogeddon.Main;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class Pipe extends AbstractScenery {

	public static Quaternion q90 = new Quaternion();
	private static TextureState ts;

	static {
		q90.fromAngleAxis(FastMath.PI/2, new Vector3f(0,-1,0));
	}

	public Pipe(Main m, boolean ew, float map_x, float height, float map_z) {
		super(m, "Pipe", map_x + 0.5f, height, map_z + 0.5f);

		TriMesh c;
		c = new Cylinder("Pipe", 2, 8, 0.05f, 1f);
		if (ew) {
			c.setLocalRotation(q90);
		}

		c.setModelBound(new BoundingBox());
		c.updateModelBound();

		if (ts == null) {
			ts = m.getDisplay().getRenderer().createTextureState();  
			ts.setEnabled(true);  
			Texture t = TextureManager.loadTexture("data/textures/pipe.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);
			ts.setTexture(t);
		}
		c.setRenderState(ts);
		this.attachChild(c);


		setModelBound(new BoundingBox());
		updateModelBound();


	}

}
