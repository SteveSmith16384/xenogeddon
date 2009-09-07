package xenogeddon;

import java.awt.Graphics2D;
import com.jme.image.Image;

public final class TextureImage extends PaintableImage {
	
	private IPainter painter;
	
	public TextureImage(IPainter p, int w) {
		super(w, w, true);
		painter = p;
		this.setFormat(Image.Format.RGBA2);
		refreshImage(null);
	}

	public void paint(Graphics2D g) {
		/*g.setBackground(new Color(0f, 0f, 0f, 1f));
		g.clearRect(0, 0, getWidth(), getHeight());*/
		
		/*g.setColor(new Color(1f, 1f, 1f, 1f));
		g.drawString("Choose your Chapter", 10, 20);*/
		painter.paint(g, this);

	}
}

