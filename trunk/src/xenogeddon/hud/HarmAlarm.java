package xenogeddon.hud;

import java.awt.Color;
import java.awt.Graphics2D;
import ssmith.util.Interval;
import xenogeddon.IPainter;
import xenogeddon.IUpdating;
import xenogeddon.Main;
import xenogeddon.TextureImage;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;

public final class HarmAlarm extends Quad implements IPainter, IUpdating {

	private static final float REDUCTION = 0.1f;

	private Main main;
	private TextureImage img;
	private Interval reduce_alpha_int = new Interval(50);
	private float alpha = 0f;
	private TextureState ts;

	public HarmAlarm(Main m) {
		super("HarmAlarm", m.getDisplay().getWidth()*2, m.getDisplay().getHeight()*2);

		main = m;

		this.setCullHint(Spatial.CullHint.Never);
		this.setIsCollidable(false);
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
		this.setLocalTranslation(0, 0, 0);
		ts = m.getDisplay().getRenderer().createTextureState();  
		ts.setEnabled(true);
		Texture t = new Texture2D();
		img = new TextureImage(this, 64);
		t.setImage(img);
		ts.setTexture(t);
		this.setRenderState(ts);

		//this.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		BlendState tempBlendState = main.getDisplay().getRenderer().createBlendState();
		tempBlendState.setBlendEnabled(true);
		tempBlendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha );
		tempBlendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		tempBlendState.setTestEnabled(true);
		tempBlendState.setTestFunction(BlendState.TestFunction.Always);
		tempBlendState.setEnabled(true);
		this.setRenderState(tempBlendState);

		/*
		ZBufferState tempZBuffer = main.getDisplay().getRenderer().createZBufferState();
		tempZBuffer.setEnabled(true);
		tempZBuffer.setWritable(false);
		tempZBuffer.setFunction(ZBufferState.TestFunction.EqualTo);
		this.setRenderState(tempZBuffer);

		MaterialState tempMaterialState = main.getDisplay().getRenderer().createMaterialState();
		tempMaterialState.setEnabled(true);
		tempMaterialState.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
		this.setRenderState(tempMaterialState);*/

		this.updateRenderState();
	}

	public void startAlarm() {
		alpha = 0.8f;
	}

	public void paint(Graphics2D g, TextureImage ti) {
		//Main.p("Painting HarmAlarm");

		// Fill background rect
		g.setBackground(new Color(1f, 0f, 0f, alpha));
		g.clearRect(0, 0, ti.getWidth(), ti.getHeight());

		/*g.setColor(new Color(1f, 0f, 0f, alpha));
		g.fillRect(0, 0, ti.getWidth(), ti.getHeight());*/
		
		/*g.setColor(new Color(1f, 1f, 1f, 1f));
		g.drawLine(0, 0, 500, 500);*/
	}

	public void update(float interpolation) {
		if (alpha > 0) {
			if (reduce_alpha_int.hitInterval()) {
				//Main.p("Alpha: " + alpha);
				alpha = alpha - REDUCTION;
				if (alpha < 0) {
					alpha = 0;
				}
				img.refreshImage(ts);
				//ts.deleteAll();
			}
		}
	}
	
}

