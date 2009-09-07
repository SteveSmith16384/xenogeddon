package ssmith.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class CustomJPEG {
	
	private BufferedImage buf;
	private Graphics g;
	
	public CustomJPEG(int w, int h) {
		buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		g = buf.getGraphics();
	}
	
	public Graphics getGraphics() {
		return g;
	}
	
	public byte[] generateDataAsJPEG() throws ImageFormatException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(bos);
		enc.encode(buf);
		return bos.toByteArray();
	}

}
