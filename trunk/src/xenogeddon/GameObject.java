package xenogeddon;

import java.awt.Point;

import ssmith.astar.IAStarMapInterface;
import xenogeddon.maps.MapSquare;
import xenogeddon.models.CreatureObject;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;

public abstract class GameObject extends Node implements IUpdating, IAStarMapInterface {
	
	public float y_off; // The height of the object to make it stand on the floor.
	protected Main main;
	public TriMesh collider; // All instances must implement ICollider!
	public MyCollisionResults collision_results;
	public Point blocked_square; // For A*

	public GameObject(Main m, String name, boolean _collides, float _x, float _z, float _y_off) {
		super(name);
		main = m;
		collision_results = new MyCollisionResults(m);
		this.getLocalTranslation().x = _x;
		this.getLocalTranslation().y = _y_off;
		this.getLocalTranslation().z = _z;
		y_off = _y_off;

		super.setIsCollidable(_collides);
	}
	
	public abstract void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir);

    public float getDistTo(GameObject go) {
		return this.getLocalTranslation().distance(go.getLocalTranslation());
	}

	public float getDistTo(Vector3f v) {
		return this.getLocalTranslation().distance(v);
	}

	
	public float getDistTo(float x, float y, float z) {
		return this.getLocalTranslation().distance(new Vector3f(x, y, z));
	}

	
	public Vector3f getDirTo(GameObject go) {
		Vector3f vect = new Vector3f();
		vect.set(go.getLocalTranslation().subtract(this.getLocalTranslation()).normalize());
		return vect;
	}
	
	public Vector3f getDirTo(Vector3f v) {
		Vector3f vect = new Vector3f();
		vect.set(v.subtract(this.getLocalTranslation()).normalize());
		return vect;
	}
	
	protected void AddAxis(Node n) {
		// Add axis lines
		Vector3f[] vertex = new Vector3f[2];
		vertex[0] = new Vector3f(0, 0, 0);
		vertex[1] = new Vector3f(10, 0, 0);
		Line l = new Line("LinesX", vertex, null, null, null);
		l.setIsCollidable(false);
		l.setModelBound(new BoundingBox());
		l.updateModelBound();
		this.attachChild(l);

		vertex = new Vector3f[2];
		vertex[0] = new Vector3f(0, 0, 0);
		vertex[1] = new Vector3f(0, 10, 0);
		l = new Line("LinesY", vertex, null, null, null);
		l.setIsCollidable(false);
		l.setModelBound(new BoundingBox());
		l.updateModelBound();
		this.attachChild(l);

		vertex = new Vector3f[2];
		vertex[0] = new Vector3f(0, 0, 0);
		vertex[1] = new Vector3f(0, 0, 10);
		l = new Line("LinesZ", vertex, null, null, null);
		l.setIsCollidable(false);
		l.setModelBound(new BoundingBox());
		l.updateModelBound();
		this.attachChild(l);

	}


	
	
//	----------------------------------------------------------
//	ASTAR

	public int getMapHeight() {
		return main.map.getMapHeight();
	}

	public float getMapSquareDifficulty(int x, int z) {
		return 1;
	}

	public int getMapWidth() {
		return main.map.getMapWidth();
	}

	public boolean isMapSquareTraversable(int x, int z) {
		if (blocked_square != null) {
			if (blocked_square.x == x && blocked_square.y == z) {
				return false;
			}
		}
		return main.map.getSq(x, z).major_type == MapSquare.FLOOR;
	}

//	End of ASTAR
//	----------------------------------------------------------



}
