package xenogeddon.scenery;

import xenogeddon.CollisionBox;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.effects.Explosion;
import xenogeddon.models.CreatureObject;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class ComputerConsole extends GameObject {
	
	private static final float DIAM = .5f;
	
	public ComputerConsole(Main m, float x, float z) {
		super(m, "ComputerConsole", true, x, z, DIAM/2);
		
		CollisionBox b = new CollisionBox(m, new Vector3f(0, DIAM/2, 0), DIAM/2, DIAM, DIAM/2, this, false);
		
		TextureState ts = main.getDisplay().getRenderer().createTextureState();  
		ts.setEnabled(true);  
		ts.setTexture(TextureManager.loadTexture("./data/textures/ComputerConsole.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor), 0);  
		b.setRenderState(ts);  
		b.updateRenderState();

		this.attachChild(b);
		
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		Node n = this.getParent(); 
		n.detachChild(this);
		n.updateGeometricState(0, true);
		main.mission.targetDestroyed();
		
		new Explosion(main, this.getLocalTranslation().x, this.getLocalTranslation().y, this.getLocalTranslation().z);
	}

	public void update(float interpolation) {
		
	}

}
