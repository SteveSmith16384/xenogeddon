package xenogeddon;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;

public class CollisionBox extends Box implements ICollider {
	
	private GameObject owner;
	private boolean will_move;
	
	public CollisionBox(Main main, Vector3f centre, float x_extent, float y_extent, float z_extent, GameObject _owner, boolean _will_move) {
		super(_owner.getName() + "_collider", new Vector3f(0f, 0f, 0f), x_extent, y_extent, z_extent);
		
		will_move = _will_move;
		this.setLocalTranslation(centre);// Needs this so it tracks the owner!
		//this.setLocalRotation(CreatureObject.q90);
		
		// Note we don't make this invisible as some objects use it for their model (e.g. crate).
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
		this.setIsCollidable(true);

		owner = _owner;
	}
	
	public GameObject getOwner() {
		return this.owner;
	}

	public boolean willMove() {
		return will_move;
	}

}
