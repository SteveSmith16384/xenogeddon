package xenogeddon.scenery;

import java.util.ArrayList;
import ssmith.lang.Functions;
import ssmith.util.Interval;
import xenogeddon.Main;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Disk;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public final class DrippingSlime extends AbstractScenery {

	private static final float MAX_RAD = 0.5f;
	private static final float INC_SIZE = 0.002f;

	public static Quaternion q90 = new Quaternion();

	private ArrayList<DropOfSlime> drops = new ArrayList<DropOfSlime>();
	private float radius = 0.1f;
	private Interval check_interval = new Interval(5000);
	private boolean is_shown = true;
	private Disk c;
	public static TextureState ts;

	static {
		q90.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
	}

	public DrippingSlime(Main m, float map_x, float map_z) {
		super(m, "DrippingSlime", map_x+Functions.rndFloat(0, 1f), Functions.rndFloat(0.001f, 0.01f), map_z+Functions.rndFloat(0f, 1f));

		if (ts == null) {
			ts = m.getDisplay().getRenderer().createTextureState();  
			ts.setEnabled(true);  
			Texture t = TextureManager.loadTexture("data/textures/alienskin.png", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);
			ts.setTexture(t);
		}

		// Ceiling pool
		Disk ceiling = new Disk("DrippingSlime", 15, 15, 0.1f);
		ceiling.setLocalTranslation(0f, main.map.getSq(this.getLocalTranslation().x, this.getLocalTranslation().z).ceiling_height - 0.015f, 0f);
		ceiling.setLocalRotation(q90);

		ceiling.setRenderState(ts);
		
		ceiling.setModelBound(new BoundingBox());
		ceiling.updateModelBound();
		this.attachChild(ceiling);

		// Floor
		c = new Disk("DrippingSlime", 15, 15, radius);
		c.setLocalRotation(q90);

		c.setModelBound(new BoundingBox());
		c.updateModelBound();

		c.setRenderState(ts);

		this.attachChild(c);
		
		//this.updateGeometricState(0, true);

		this.setModelBound(new BoundingBox());
		this.updateModelBound();

	}

	public void update(float interpolation) {
		if (check_interval.hitInterval()) {
			boolean player_close = this.getDistTo(main.player) <= Main.VIEW_DIST + 1;
			if (is_shown && !player_close) {
				this.is_shown = false;
				this.detachAllChildren();
				this.drops.clear();
				this.updateGeometricState(0, true);
			} else if (!is_shown && player_close) {
				this.is_shown = true;
				this.radius = 0;
				this.updateGeometricState(0, true);
			}
		}

		if (is_shown) {
			for (int i=0 ;i<drops.size() ; i++) {
				DropOfSlime drop = drops.get(i);
				drop.process(interpolation);
				if (drop.getLocalTranslation().y < 0) {
					this.detachChild(drop);
					drops.remove(i);
					i--;
					if (radius < MAX_RAD) {
						this.radius += INC_SIZE;
						c.updateGeometry(15, 15, radius);
					}

				}

			}
			if (Functions.rnd(1, 50) == 1) {
				DropOfSlime s = new DropOfSlime(main, main.map.getSq(this.getLocalTranslation().x, this.getLocalTranslation().z).ceiling_height);
				drops.add(s);
				this.attachChild(s);
			}
			this.updateGeometricState(0, true);
			//this.updateRenderState();
		}
	}
}
