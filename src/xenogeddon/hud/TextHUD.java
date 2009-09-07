package xenogeddon.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import ssmith.util.Interval;
import xenogeddon.IPainter;
import xenogeddon.IUpdating;
import xenogeddon.Main;
import xenogeddon.TextQueue;
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

public final class TextHUD extends Quad implements IPainter, IUpdating {

	private static final int SIZE = 266;

	private Main main;
	private TextureImage img;
	private TextureState ts;
	private Interval update_interval = new Interval(150);
	private TextQueue t_q = new TextQueue();
	private Font font = new Font("Dialog", Font.PLAIN, 20);

	public TextHUD(Main m) {
		super("TextHUD", SIZE, SIZE);

		main = m;

		this.setCullHint(Spatial.CullHint.Never);
		this.setIsCollidable(false);
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
		this.setLocalTranslation(SIZE/2, -SIZE/4, 0);

		Quaternion q = new Quaternion();
		q.fromAngleAxis(FastMath.PI, new Vector3f(1,0,0));
		this.setLocalRotation(q);

		ts = m.getDisplay().getRenderer().createTextureState();  
		ts.setEnabled(true);
		Texture t = new Texture2D();

		img = new TextureImage(this, 256);
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
		g.setFont(font);

		g.drawString(t_q.getString(true), 10, 30);
	}

	public void add(String s) {
		this.t_q.add(s);
	}

	public void add(String s, int pos) {
		this.t_q.add(pos, s);
		t_q.resetPos();
	}
	
	public void clearText() {
		t_q.clear();
		t_q.resetPos();
	}

	public void update(float interpolation) {
		if (update_interval.hitInterval()) {
			t_q.update(interpolation);
			this.img.refreshImage(ts);
		}


	}

}

