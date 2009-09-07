package xenogeddon;

import com.jme.light.PointLight;
import com.jme.renderer.ColorRGBA;

public class AmbientLights extends PointLight implements IUpdating {

	private static final long DURATION = 1800;
	private static float DEF_AMBIENCE = 0.7f;
	private static ColorRGBA DEF_COL;

	private long change_time;
	private boolean on = true;
	private Main main;
	public boolean red_flashing = false;
	public boolean shot_flash = false;
	private boolean restore_lights = true;

	public AmbientLights(Main m) {
		main = m;
		this.setEnabled(true);
		restore_lights = true;
		try {
			DEF_AMBIENCE = Float.parseFloat(main.cfg.get("main_light_brightness").toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		DEF_COL = new ColorRGBA( DEF_AMBIENCE, DEF_AMBIENCE, DEF_AMBIENCE, DEF_AMBIENCE );
	}

	public void update(float interpolation) {
		if (red_flashing) {
			if (System.currentTimeMillis() > this.change_time) {
				this.change_time = System.currentTimeMillis() + DURATION;
				on = !on;
				if (!on) {
					// Normal
					setNormalLights();
				} else {
					// RED!
					this.setAmbient( new ColorRGBA( 1.0f, 0.0f, 0.0f, 1.0f ) );
					this.setDiffuse( new ColorRGBA( 1f, 0f, 0f, 1.0f ) );
					this.setSpecular( new ColorRGBA( 1f, 0f, 0f, 1.0f ) );
				}
				main.getRootNode().updateRenderState();
			}
		} else if (shot_flash) {
			shot_flash = false;
			this.setAmbient(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			this.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			this.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			main.getRootNode().updateRenderState();
			restore_lights = true;
		} else if (restore_lights) {
			restore_lights = false;
			setNormalLights();
			main.getRootNode().updateRenderState();
		}
	}
	
	private void setNormalLights() {
		this.setDiffuse(DEF_COL);
		this.setSpecular(DEF_COL);
		this.setAmbient(DEF_COL);
	}

}
