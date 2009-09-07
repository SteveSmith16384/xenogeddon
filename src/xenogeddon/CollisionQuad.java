package xenogeddon;

import com.jme.scene.shape.Quad;

public class CollisionQuad extends Quad implements ICollider {

	private GameObject owner;
	private boolean will_move;
	
	public CollisionQuad(float w, float h, GameObject _owner, boolean _will_move) {
		super(_owner.getName() + "_Collider", w, h);
		owner = _owner;
		will_move = _will_move;
	}
	
	public GameObject getOwner() {
		return this.owner;
	}

	public boolean willMove() {
		return will_move;
	}

}
