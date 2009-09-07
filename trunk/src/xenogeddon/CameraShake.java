package xenogeddon;

import ssmith.lang.Functions;
import ssmith.util.Interval;
import com.jme.math.Vector3f;

public final class CameraShake {

	private static final boolean ENABLED = true;
	private static final float SHAKE_DIST = 0.05f;

	private Vector3f cam_pos_before_shake = null;
	private Main main;
	private Interval next_change = new Interval(10000);
	private boolean shaking = false;

	public CameraShake(Main m) {
		main = m;
	}

	public void resetCamPos() {
		if (ENABLED) {
			if (cam_pos_before_shake == null) {
				cam_pos_before_shake = new Vector3f();
				cam_pos_before_shake.set(main.cam.getLocation());
				// Don't reset the cam pos as we've obviously not stored it yet!
			} else {
				if (shaking) {
					main.cam.getLocation().set(cam_pos_before_shake);
				}
			}
		}
	}

	public void shake() {
		if (ENABLED) {
			if (next_change.hitInterval()) {
				shaking = !shaking;
				if (shaking) {
					next_change.setInterval(Functions.rnd(1000, 3000), true);
				} else {
					next_change.setInterval(Functions.rnd(20000, 30000), true);
				}
			}

			if (shaking) {
				cam_pos_before_shake.set(main.cam.getLocation());

				// Camera shake
				main.cam.getLocation().x += Functions.rndFloat(-SHAKE_DIST, SHAKE_DIST);
				main.cam.getLocation().y += Functions.rndFloat(-SHAKE_DIST, SHAKE_DIST);
			}
		}
	}

}
