package xenogeddon.hud;

import java.awt.Color;
import java.awt.Graphics2D;
import ssmith.util.Interval;
import xenogeddon.IPainter;
import xenogeddon.IUpdating;
import xenogeddon.Main;
import xenogeddon.TextureImage;
import xenogeddon.models.CreatureObject;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;

public final class ScannerHUD extends Quad implements IPainter, IUpdating {

	private static final Color GREEN = new Color(0f, 1f, 0f, 0.5f);
	private static final Color WHITE = new Color(1f, 1f, 1f, 0.5f);
	private static final Color RED = new Color(1f, 0f, 0f, 0.5f);

	private static final int SIZE = 200;
	private static final int TEX_SIZE = 256;
	private static final int DIAM = 10;

	private Main main;
	private TextureImage img;
	private Interval update_interval = new Interval(1000);
	private TextureState ts;

	public ScannerHUD(Main m) {
		super("ScannerHUD", SIZE, SIZE);

		main = m;

		this.setCullHint(Spatial.CullHint.Never);
		this.setIsCollidable(false);
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
		this.setLocalTranslation(main.getDisplay().getWidth() - (SIZE/2), main.getDisplay().getHeight() - (SIZE/2), 0);

		ts = m.getDisplay().getRenderer().createTextureState();  
		ts.setEnabled(true);
		Texture t = new Texture2D();

		img = new TextureImage(this, TEX_SIZE);
		t.setImage(img);

		ts.setTexture(t);
		this.setRenderState(ts);

		//this.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		BlendState tempBlendState = main.getDisplay().getRenderer().createBlendState();
		tempBlendState.setBlendEnabled(true);
		tempBlendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		tempBlendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		tempBlendState.setTestEnabled(true);
		tempBlendState.setTestFunction(BlendState.TestFunction.Always);
		tempBlendState.setEnabled(true);
		this.setRenderState(tempBlendState);

		this.updateRenderState();
	}

	public void paint(Graphics2D g, TextureImage ti) {
		g.setBackground(new Color(0f, 0f, 0f, 0.3f));
		g.clearRect(0, 0, ti.getWidth(), ti.getHeight());

		g.setColor(WHITE);
		g.fillOval(TEX_SIZE/2, TEX_SIZE/2, DIAM, DIAM);

		Object o;
		for (int i=0 ; i<main.objects.size() ; i++) {
			o = main.objects.get(i);
			if (o != main.player) {
				if (o instanceof CreatureObject) {
					CreatureObject c = (CreatureObject)o;
					if (c.is_alive) {
						if (c.invisible == false) {
							float d = main.player.getDistTo(c); 
							if (d <= 8) {
								if (main.player.canSee(c, false) == false) {
									continue;
								}
								int x = (int)((c.getLocalTranslation().x - main.player.getLocalTranslation().x) * 10);
								int z = (int)((c.getLocalTranslation().z - main.player.getLocalTranslation().z) * 10);

								if (c.side != main.player.side) {
									g.setColor(RED);
								} else {
									g.setColor(GREEN);
								}
								g.fillOval(TEX_SIZE/2+x, TEX_SIZE/2+z, DIAM, DIAM);
							}
						}
					}
				}
			}
		}

	}

	public void update(float interpolation) {
		if (update_interval.hitInterval()) {
			this.img.refreshImage(ts);
		}

	}

}

