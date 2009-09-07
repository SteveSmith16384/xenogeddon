package xenogeddon.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import ssmith.lang.Functions;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.collada.ColladaImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.Md2ToJme;

public final class ModelLoaders {
	
	private ModelLoaders() {
		
	}

	public static Node LoadMD2Model(DisplaySystem display, String path, String mdl, String skin, float scale) throws IOException {
		String jme_file = Functions.AppendSlash(path) + mdl + ".jme";
		while (new File(jme_file).canRead() == false) {
			File f = new File(Functions.AppendSlash(path) + mdl);
			URL modelx = f.toURI().toURL(); 

			// Create something to convert .obj format to .jme
			FormatConverter converter = new Md2ToJme();
			// Point the converter to where it will find the .mtl file from
			converter.setProperty("mtllib",modelx);
			converter.setProperty("texdir",modelx);

			// This byte array will hold my .jme file
			ByteArrayOutputStream BO=new ByteArrayOutputStream();
			// Use the format converter to convert .obj to .jme
			converter.convert(modelx.openStream(), BO);
			Node body = (Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));

			BinaryExporter bo = BinaryExporter.getInstance();
			bo.save(body, new File(jme_file));
		}

		File f = new File(jme_file);
		BinaryImporter importer = BinaryImporter.getInstance();
		Node body = (Node)importer.load(f); 

		body.setLocalScale(scale);

		TextureState ts = display.getRenderer().createTextureState();  
		ts.setEnabled(true);  
		ts.setTexture(TextureManager.loadTexture(Functions.AppendSlash(path) + skin, Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor));
		body.setRenderState(ts);  
		body.updateRenderState();

		body.setModelBound(new BoundingBox());
		body.updateModelBound();

		return body;
	}

	public static Node LoadColladaModel(DisplaySystem display, String model_url, String tex_url, float scale) throws IOException {
		File f = new File(model_url);
		URL modelx = f.toURI().toURL(); 
		InputStream mobboss = modelx.openStream();
		
	    ColladaImporter.load(mobboss, "model");
	    Node model = ColladaImporter.getModel();
	    ColladaImporter.cleanUp();

	    if (tex_url.length() > 0) {
		TextureState ts = display.getRenderer().createTextureState();  
		ts.setEnabled(true);  
		ts.setTexture(TextureManager.loadTexture(tex_url, Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor));
		model.setRenderState(ts);  
		model.updateRenderState();
	    }

		model.setLocalScale(scale);

	    model.setModelBound(new BoundingBox());
		model.updateModelBound();

		return model;
		
	}

}
