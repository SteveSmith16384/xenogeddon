package xenogeddon.scenery;

import ssmith.lang.Functions;
import ssmith.util.Interval;
import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;
import xenogeddon.walls.FloorAndCeiling;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class FallingTile extends GameObject {

	private static final float DROP_START_SPEED = 0.05f;

	private float fall_speed = DROP_START_SPEED;
	private Interval check_for_player_interval = new Interval(1000);
	private boolean falling = false;
	private boolean fallen = false;
	private Quad ceiling;
	private Quaternion q_skew_x = new Quaternion();
	private Quaternion q_skew_y = new Quaternion();
	private static TextureState ts_ceiling;

	public FallingTile(Main main, int x, int z, float height) {
		super(main, "FallingTile", true, (float)x + 0.5f, (float)z+0.5f, 0);
		
		this.setIsCollidable(false);

		ceiling = new Quad("FallingTile", 1, 1);
		ceiling.setLocalRotation(FloorAndCeiling.q);
		ceiling.setLocalTranslation(0f, height, 0f);
		
		q_skew_x.fromAngleAxis(Functions.rndFloat(-.5f, .5f), new Vector3f(0,1,0));
		q_skew_y.fromAngleAxis(Functions.rndFloat(1f, -1f), new Vector3f(0,0,1));
		
		if (ts_ceiling == null) {
			ts_ceiling = main.getDisplay().getRenderer().createTextureState();  
			ts_ceiling.setEnabled(true);  
			ts_ceiling.setTexture(TextureManager.loadTexture("data/textures/floor2.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor), 0);
		}
		ceiling.setRenderState(ts_ceiling);  
		ceiling.updateRenderState();

		this.attachChild(ceiling);

		this.setModelBound(new BoundingBox());
		this.updateModelBound();

	}

	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}

	public void update(float interpolation) {
		if (!fallen) {
			if (!falling) {
				if (check_for_player_interval.hitInterval()) {
					if (this.getDistTo(main.player) < 3f) {
						this.falling = true;
						ceiling.setLocalRotation(FloorAndCeiling.q.mult(q_skew_x));
						ceiling.updateGeometricState(0, true);
					}
				}
			} else {
				ceiling.getLocalTranslation().y -= this.fall_speed;
				//this.fall_speed = this.fall_speed * 2;
				if (ceiling.getLocalTranslation().y <= 0) {
					ceiling.getLocalTranslation().y = 0.005f;
					ceiling.setLocalRotation(FloorAndCeiling.q.mult(q_skew_y));
					ceiling.updateGeometricState(0, true);
					fallen = true;
					main.removeObjectForUpdate(this);
				}
				this.updateGeometricState(0, true);
			}
		}

	}

}
