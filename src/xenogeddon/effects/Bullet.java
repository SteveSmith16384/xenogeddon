package xenogeddon.effects;

import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class Bullet extends GameObject {
	
	private static final float SPEED = 13f;
	private static final float RAD = 0.002f;
	
	private static TextureState ts;

	private Vector3f move_dir;
	private float max_dist;
	private float dist_so_far = 0;

	public Bullet(Main m, Vector3f start, Vector3f end) {
		super(m, "Bullet", false, start.x, start.z, start.y);
		
		move_dir = end.subtract(start).normalize().mult(SPEED);
		max_dist = end.subtract(start).length();
		
		if (max_dist == Float.POSITIVE_INFINITY) {
			return;
		}
		
		Cylinder s = new Cylinder("Bullet_Cylinder", 2, 5, RAD, 0.2f);
		
		if (ts == null) {
			ts = main.getDisplay().getRenderer().createTextureState();  
			ts.setEnabled(true);  
			ts.setTexture(TextureManager.loadTexture("data/textures/cells3.png", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor), 0);
		}
		s.setRenderState(ts);  

		CullState cs = main.getDisplay().getRenderer().createCullState();
		//cs.setCullFace(CullState.Face.Back);

		BlendState as = main.getDisplay().getRenderer().createBlendState();
		as.setBlendEnabled(true);
		as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		as.setDestinationFunction(BlendState.DestinationFunction.One);

		ColorRGBA color = new ColorRGBA(1f, 0.15f, 0.15f, 0.2f);
		MaterialState mat = main.getDisplay().getRenderer().createMaterialState();
		mat.setAmbient(color);
		mat.setEmissive(color);
		mat.setDiffuse(color);

		this.setLightCombineMode(Spatial.LightCombineMode.Replace);
		this.setTextureCombineMode(Spatial.TextureCombineMode.Off);
		this.setRenderState(mat);
		this.setRenderState(as);
		this.setRenderState(cs);
		this.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		this.setLocalRotation(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
		this.updateRenderState();

		s.updateRenderState();

		s.setModelBound(new BoundingSphere());
		this.updateModelBound();
		this.attachChild(s);
		
		main.addObjectForUpdate(this);
		main.attachToRootNode(this);
		
		this.updateGeometricState(0, true);
		this.lookAt(end, Vector3f.UNIT_Y);
		
	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}

	public void update(float interpolation) {
		Vector3f act_dir = move_dir.mult(interpolation);
		this.getLocalTranslation().addLocal(act_dir);
		dist_so_far += act_dir.length();
		if (dist_so_far > max_dist) {
			this.removeFromParent();
			main.removeObjectForUpdate(this);
		} else {
			this.updateGeometricState(interpolation, true);
		}
	}

}

