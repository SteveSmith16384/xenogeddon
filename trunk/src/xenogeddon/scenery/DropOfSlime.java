package xenogeddon.scenery;

import xenogeddon.Main;
import com.jme.bounding.BoundingSphere;
import com.jme.scene.shape.Sphere;

public final class DropOfSlime extends Sphere {

	private static final float DROP_START_SPEED = 0.05f;
	private static final float RAD = 0.01f;
	
	private float fall_speed = DROP_START_SPEED;

	public DropOfSlime(Main m, float height) {
		super("DropOfSlime", 4, 4, RAD);
		this.setLocalTranslation(0f, height, 0f);
		//fall_speed = DROP_START_SPEED;

		this.setRenderState(DrippingSlime.ts);
		this.updateRenderState();
		
		this.setModelBound(new BoundingSphere());
		this.updateModelBound();
	}
	
	public void process(float interpolation) {
		this.getLocalTranslation().y -= (fall_speed * interpolation);
		this.updateModelBound();
		this.updateGeometricState(interpolation, true);
		fall_speed = fall_speed + DROP_START_SPEED;
	}

}
