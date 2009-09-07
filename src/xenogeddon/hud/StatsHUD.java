package xenogeddon.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import xenogeddon.IPainter;
import xenogeddon.Main;
import xenogeddon.TextureImage;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;

public final class StatsHUD extends Quad implements IPainter {

	private static final int SIZE = 256;
	private Font font_normal = new Font("Dialog", Font.PLAIN, 14);
	private Font font_large = new Font("Dialog", Font.PLAIN, 28);

	private Main main;
	private TextureImage img;
	private TextureState ts;

	public StatsHUD(Main m) {
		super("StatsHUD", SIZE, SIZE);

		main = m;

		this.setCullHint(Spatial.CullHint.Never);
		this.setIsCollidable(false);
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
		this.setLocalTranslation( (SIZE/2)+10, main.getDisplay().getHeight() - (SIZE/2), 0);

		Quaternion q = new Quaternion();
		q.fromAngleAxis(FastMath.PI, new Vector3f(1,0,0));
		this.setLocalRotation(q);

		ts = m.getDisplay().getRenderer().createTextureState();  
		ts.setEnabled(true);
		Texture t = new Texture2D();

		img = new TextureImage(this, SIZE);
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
		g.setBackground(new Color(0f, 0f, 0f, 0.0f));
		g.clearRect(0, 0, ti.getWidth(), ti.getHeight());

		g.setColor(new Color(.8f, .8f, 0f));
		g.setFont(font_normal);
		g.drawString("Health: ", 20, 23);
		g.drawString("Ammo: ", 20, 58);
		if (main.mission.showTime()) {
			g.drawString("Time: ", 20, 93);
		}
		if (Main.SHOW_FPS) {
			g.drawString("FPS: ", 20, 128);
		}

		g.setFont(font_large);
		g.drawString("" + main.player.health, 70, 30);
		g.drawString("" + main.player.ammo, 70, 65);
		if (main.mission.showTime()) {
			g.drawString("" + main.getTimeRemaining(), 70, 100);
		}
		if (Main.SHOW_FPS) {
			g.drawString("" + (int)main.getFPS(), 70, 135);
		}
	}

	public void refresh() {
		this.img.refreshImage(ts);
	}

}

