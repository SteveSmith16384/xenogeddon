package xenogeddon.scenery;

import xenogeddon.CollisionBox;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class Teleporter extends GameObject {
	
	private static final float DIAM = 1f;
	private static final float HEIGHT = .2f;
	
	public Teleporter(Main m, float x, float z) {
		super(m, "Teleporter", true, x, z, 0);//HEIGHT/2);
		
		CollisionBox b = new CollisionBox(m, new Vector3f(DIAM/2, HEIGHT/2, DIAM/2), DIAM/2, HEIGHT/2, DIAM/2, this, false);
		
		TextureState ts = main.getDisplay().getRenderer().createTextureState();  
		ts.setEnabled(true);  
		ts.setTexture(TextureManager.loadTexture("./data/textures/teleporter.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor), 0);  
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
		this.getLocalRotation().y += 0.01f;
		this.updateGeometricState(0, true);
	}

}
