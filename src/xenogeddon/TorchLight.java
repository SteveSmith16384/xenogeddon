package xenogeddon;

import com.jme.light.SpotLight;
import com.jme.renderer.ColorRGBA;

/**
 * This looks a bit crap at the moment.
 *
 */
public final class TorchLight extends SpotLight {
	
	private static float DEF_AMBIENCE = 0.8f;

	public TorchLight(Main main) {
		super();

		try {
			DEF_AMBIENCE = Float.parseFloat(main.cfg.get("torch_brightness").toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Direction and location is set in Main, linked to the player
		this.setAngle(20);
		this.setEnabled(true);
		this.setAmbient( new ColorRGBA( DEF_AMBIENCE, DEF_AMBIENCE, DEF_AMBIENCE, DEF_AMBIENCE ) );
		this.setDiffuse( new ColorRGBA( DEF_AMBIENCE, DEF_AMBIENCE, DEF_AMBIENCE, DEF_AMBIENCE ) );
		this.setSpecular( new ColorRGBA( DEF_AMBIENCE, DEF_AMBIENCE, DEF_AMBIENCE, DEF_AMBIENCE ) );
		
	}

}
