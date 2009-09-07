package xenogeddon.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import ssmith.lang.Functions;
import xenogeddon.Main;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.animation.KeyframeController;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.Md3ToJme;


public abstract class AbstractMD3Model extends CreatureObject {
	
    protected KeyframeController kc;
    
    public AbstractMD3Model(Main m, String node_name, String display_name, int side, float _x, float _z, float y_off, float _diam, DisplaySystem display, String path, String mdl_lower, String skin_lower, String mdl_upper, String skin_upper, String mdl_head, String skin_head, float scale, float speed, int hlth, int att) throws IOException {
    	super(m, node_name, display_name, side, _x, _z, y_off, _diam, speed, hlth, att);
    	
    	//model = new Node();
    	this.attachChild(this.getNodeFromFile(display, path, mdl_lower, skin_lower, scale));
    	this.attachChild(this.getNodeFromFile(display, path, mdl_upper, skin_upper, scale));
    	this.attachChild(this.getNodeFromFile(display, path, mdl_head, skin_head, scale));
    }
    
    private Node getNodeFromFile(DisplaySystem display, String path, String mdl_lower, String skin_lower, float scale) throws IOException {
		File f = new File(Functions.AppendSlash(path) + mdl_lower);
		URL modelx = f.toURI().toURL();//new URL("file:/" + f.getAbsolutePath()); // Works! 
		//File f2 = new File(Functions.AppendSlash(path) + skin_lower);
		//URL texture = f2.toURI().toURL();//new URL("file:/" + f.getAbsolutePath()); // Works! 

		// Create something to convert .obj format to .jme
		FormatConverter converter = new Md3ToJme();
		// Point the converter to where it will find the .mtl file from
		converter.setProperty("mtllib",modelx);
		converter.setProperty("texdir",modelx);
		
		// This byte array will hold my .jme file
		ByteArrayOutputStream BO=new ByteArrayOutputStream();
		// Use the format converter to convert .obj to .jme
		converter.convert(modelx.openStream(), BO);
		Node n =(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
		n.setName(name);
		//n.setIsCollidable(false);

		// shrink this baby down some
		n.setLocalScale(scale);

		TextureState ts = display.getRenderer().createTextureState();  
		ts.setEnabled(true);  
		ts.setTexture(TextureManager.loadTexture(Functions.AppendSlash(path) + skin_lower, Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor), 0);  
		n.setRenderState(ts);  
		n.updateRenderState();  

		n.setModelBound(new BoundingSphere());
		//n.updateModelBound();

        kc=(KeyframeController) n.getChild(0).getController(0);
        kc.setSpeed(10);
        kc.setRepeatType(Controller.RT_CYCLE);
        kc.setNewAnimationTimes(161, 170);
    	
        return n;
    }

	public void anim_die() {
	}

	public void anim_recoil() {
	}

	public void anim_standStill() {
	}

	public void anim_walk() {
	}
	
	public void anim_attack() {
	}
	
}
