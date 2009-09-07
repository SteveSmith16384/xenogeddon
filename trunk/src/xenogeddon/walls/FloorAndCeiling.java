package xenogeddon.walls;

import xenogeddon.CollisionQuad;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.maps.MapSquare;
import xenogeddon.models.CreatureObject;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

public final class FloorAndCeiling extends GameObject {
	
	public static final int NORMAL1 = 0;
	public static final int NORMAL2 = 1;
	public static final int NORMAL3 = 2;
	public static final int CORRIDOR = 3;
	public static final int WARNING = 4;

	public static Quaternion q = new Quaternion();
	public static CullState cs_ceiling, cs_floor;
	private static TextureState[] ts_floor;
	private static TextureState ts_ceiling;

	static {
		q.fromAngleAxis(FastMath.PI/2, new Vector3f(-1,0,0));
		ts_floor = new TextureState[5];
	}
	
	private static String GetTextureFromID(int id) {
		switch (id) {
		case NORMAL1:
			return "data/textures/metalfloor1.jpg";
		case NORMAL2:
			return "data/textures/floor3.jpg";
		case NORMAL3:
			return "data/textures/floor5.jpg";
		case CORRIDOR:
			return "data/textures/corridor.jpg";
		case WARNING:
			return "data/textures/floor4.jpg";
		default:
			return "data/textures/metalfloor1.jpg";
		}
	}

	public FloorAndCeiling(Main main, int x, int z, int ceiling_type, int textype, float height) {
		super(main, "FloorAndCeiling", true, (float)x + 0.5f, (float)z+0.5f, 0);

		// Floor
		CollisionQuad floor = new CollisionQuad(1, 1, this, false);
		floor.setLocalRotation(q);
		if (ts_floor[textype] == null) {
			ts_floor[textype] = main.getDisplay().getRenderer().createTextureState();  
			ts_floor[textype].setEnabled(true);  
			ts_floor[textype].setTexture(TextureManager.loadTexture(GetTextureFromID(textype), Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor), 0);
		}
		floor.setRenderState(ts_floor[textype]);
		
		if (cs_floor == null) {
			cs_floor = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
			cs_floor.setEnabled(true);
			cs_floor.setCullFace(CullState.Face.Back);
		}
		floor.setRenderState(cs_floor);

		floor.updateRenderState();
		this.attachChild(floor);

		// Ceiling
		if (ceiling_type == MapSquare.NORMAL) {
			CollisionQuad ceiling = new CollisionQuad(1, 1, this, false);
			ceiling.setLocalRotation(q);
			ceiling.setLocalTranslation(0f, height, 0f);

			if (cs_ceiling == null) {
				cs_ceiling = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
				cs_ceiling.setEnabled(true);
				cs_ceiling.setCullFace(CullState.Face.Front);
			}
			ceiling.setRenderState(cs_ceiling);
			
			if (ts_ceiling == null) {
				ts_ceiling = main.getDisplay().getRenderer().createTextureState();  
				ts_ceiling.setEnabled(true);  
				ts_ceiling.setTexture(TextureManager.loadTexture("data/textures/floor2.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor), 0);
			}
			ceiling.setRenderState(ts_ceiling);  
			ceiling.updateRenderState();

			this.attachChild(ceiling);
		}

		//this.setLocalTranslation((float)x + 0.5f, 0f, (float)z + 0.5f);
		this.setModelBound(new BoundingBox());
		this.updateModelBound();

	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}

	public void update(float interpolation) {
		// Do nothing
	}

}
