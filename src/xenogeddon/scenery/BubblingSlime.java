package xenogeddon.scenery;

import java.util.ArrayList;
import ssmith.lang.Functions;
import ssmith.util.Interval;
import xenogeddon.Main;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class BubblingSlime extends AbstractScenery {

	private static final float BUBBLES = 10;

	public static Quaternion q90 = new Quaternion();

	private ArrayList<Bubble> bubbles = new ArrayList<Bubble>();
	private Interval check_interval = new Interval(5000);
	private boolean is_shown = true;
	private static TextureState ts;


	/*static {
		q90.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
	}*/

	public BubblingSlime(Main m, float map_x, float map_z) {
		super(m, "BubblingSlime", map_x+Functions.rndFloat(0, 1f), Functions.rndFloat(0.001f, 0.01f), map_z+Functions.rndFloat(0f, 1f));

		if (ts == null) {
			ts = m.getDisplay().getRenderer().createTextureState();  
			ts.setEnabled(true);  
			Texture t = TextureManager.loadTexture("data/textures/alienskin.png", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);
			ts.setTexture(t);
		}

		// Create bubbles
		for (int i=0 ; i<BUBBLES ; i++) {
			Bubble b = new Bubble(m, ts);
			bubbles.add(b);
			this.attachChild(b);
		}
		this.updateRenderState();

		setModelBound(new BoundingBox());
		updateModelBound();

		this.updateGeometricState(0, true);

	}

	public void update(float interpolation) {
		if (check_interval.hitInterval()) {
			boolean player_close = this.getDistTo(main.player) <= Main.VIEW_DIST + 1;
			if (is_shown && !player_close) {
				this.is_shown = false;
				this.removeFromParent();
				//this.updateGeometricState(0, true);
			} else if (!is_shown && player_close) {
				this.is_shown = true;
				//this.updateGeometricState(0, true);
			}
		}

		if (is_shown) {
			for (int i=0 ;i<bubbles.size() ; i++) {
				Bubble drop = bubbles.get(i);
				drop.process(interpolation);
			}
		}
		this.updateGeometricState(0, true);
	}

	private class Bubble extends Sphere {

		private static final float MAX_RAD = 0.05f;
		private static final float INC_SIZE = 0.0002f;

		public Bubble(Main m, TextureState ts) {
			super("Bubble", 8, 8, Functions.rndFloat(0, MAX_RAD));

			float x = Functions.rndFloat(0, 0.2f);
			float z = Functions.rndFloat(0, 0.2f);
			this.setLocalTranslation(x, 0, z);

			this.setModelBound(new BoundingBox());
			this.updateModelBound();

			this.setRenderState(ts);


		}

		public void process(float interpolation) {
			this.radius = this.radius + INC_SIZE;
			if (this.getRadius() > MAX_RAD) {
				this.radius = 0f;
			}
			this.updateGeometry(this.getLocalTranslation(), 8, 8, radius);
		}

	}

}
