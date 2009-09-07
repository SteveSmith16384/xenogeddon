package xenogeddon.walls;

import ssmith.util.Interval;
import xenogeddon.CollisionQuad;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class SlidingDoor extends GameObject {

	private static final float SPEED = 0.01f;
	private static final float MAX_LEFT = -0.85f;
	private static final float MAX_RIGHT = -0.25f;//-0.15f;

	public static Quaternion q90 = new Quaternion();
	public static Quaternion q180 = new Quaternion();
	public static Quaternion q270 = new Quaternion();

	static {
		q90.fromAngleAxis(FastMath.PI/2, new Vector3f(0,-1,0));
		q180.fromAngleAxis(FastMath.PI, new Vector3f(0,1,0));
		q270.fromAngleAxis(FastMath.PI/2, new Vector3f(0,1,0));
	}

	private boolean is_moving = false;
	private Interval close_interval = new Interval(7000);
	private int dir = 0;
	public SlidingDoor opposite;

	public SlidingDoor(Main m, float x, float z, int side, SlidingDoor opp) {
		super(m, "SlidingDoor" + (int)x + "_" + (int)z, true, x + 0.5f, z + 0.5f, 0f);
		
		opposite = opp;

		switch (side) {
		case 0: // Side
			break;
		case 1: // Back
			setLocalRotation(q90);
			break;
		case 2: // Other side
			setLocalRotation(q180);
			break;
		case 3: // Front
			setLocalRotation(q270);
			break;
		default:
			throw new RuntimeException("Unknown door rotation");
		}

		// Our collider quad is also the actual rendered object
		this.collider = new CollisionQuad(.5f, 1, this, true);
		collider.setLocalTranslation(-0.25f, 0.5f, 0f);
		collider.setModelBound(new BoundingBox());
		collider.updateModelBound();
		this.attachChild(collider);
		this.setIsCollidable(true);

		TextureState ts = main.getDisplay().getRenderer().createTextureState();  
		ts.setEnabled(true);  
		Texture t = TextureManager.loadTexture("data/textures/door1.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);
		ts.setTexture(t, 0);
		collider.setRenderState(ts);

		setModelBound(new BoundingBox());
		updateModelBound();

	}

	public void touched(GameObject o) {
		this.startOpening();
		this.opposite.startOpening();
	}
	
	public void startOpening() {
		if (is_moving == false) {
			main.sfx.playSound("./data/sounds/door.wav");
			is_moving = true;
			main.addObjectForUpdate(this);
			close_interval.restartTimer();
		}
		dir = -1;
		
	}

	public void update(float interpolation) {
		if (is_moving) {
			if (dir == -1) { // Opening
				this.collider.getLocalTranslation().x -= SPEED; // Open
				if (this.collider.getLocalTranslation().x <= MAX_LEFT) { // Have gone far enough?
					this.collider.getLocalTranslation().x = MAX_LEFT;
					main.sfx.playSound("./data/sounds/door.wav");
					dir = 0;
				}
			} else if (dir == 0){ // Closing
				if (close_interval.hitInterval()) {
					dir = 1;
				}
			} else { // Closing
				this.collider.getLocalTranslation().x += SPEED;
				if (this.collider.getLocalTranslation().x >= MAX_RIGHT) {
					this.collider.getLocalTranslation().x = MAX_RIGHT;
					this.is_moving = false;
					main.sfx.playSound("./data/sounds/door.wav");
				}
				collision_results.clear();
				this.calculateCollisions(main.getRootNode(), collision_results);
			}
		} else {
			main.removeObjectForUpdate(this);
		}
	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}

}
