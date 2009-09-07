package xenogeddon.scenery;

import ssmith.lang.Functions;
import xenogeddon.Main;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class FallenCeilingTile extends AbstractScenery {
	
	public static Quaternion q90 = new Quaternion();
	public Quaternion q_skew = new Quaternion();
	
	static {
		q90.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
	}
	
	public FallenCeilingTile(Main m, float map_x, float map_z) {
		super(m, "FallenCeilingTile", map_x, Functions.rndFloat(0.001f, 0.01f), map_z);
		

		Quad c = new Quad("FallenCeilingTile", 1f, 1f);
		
		c.setLocalTranslation(Functions.rndFloat(0, 1), Functions.rndFloat(0, 1), 0.0f);
		this.setLocalRotation(q90); 
		
		q_skew.fromAngleAxis(Functions.rndFloat(-2, 2), new Vector3f(0,0,1));
		c.setLocalRotation(q_skew);
		
		c.setModelBound(new BoundingBox());
		c.updateModelBound();
		TextureState ts = m.getDisplay().getRenderer().createTextureState();  
		ts.setEnabled(true);  
		Texture t = TextureManager.loadTexture("data/textures/floor2.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);
		ts.setTexture(t);
		c.setRenderState(ts);
		this.attachChild(c);
		
		setModelBound(new BoundingBox());
		updateModelBound();
		
	
	}

}
