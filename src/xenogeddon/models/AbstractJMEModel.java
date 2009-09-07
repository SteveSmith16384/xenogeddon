package xenogeddon.models;

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


public abstract class AbstractJMEModel extends CreatureObject {

    protected KeyframeController kc;
    
    public AbstractJMEModel(Main m, String node_name, String display_name, int side, float _x, float _z, float y_off, float _diam, DisplaySystem display, String path, String mdl1, String skin1, float scale, float speed, int hlth, int att) throws IOException {
    	super(m, node_name, display_name, side, _x, _z, y_off, _diam, speed, hlth, att);
    	
		File f = new File(Functions.AppendSlash(path) + mdl1);
		URL modelx = f.toURI().toURL(); 

		// Use the format converter to convert .obj to .jme
		Node node = (Node)BinaryImporter.getInstance().load(modelx);
		node.setName(name);

		// shrink this baby down some
		node.setLocalScale(scale);

		TextureState ts = display.getRenderer().createTextureState();  
		ts.setEnabled(true);  
		ts.setTexture(TextureManager.loadTexture(Functions.AppendSlash(path) + skin1, Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor), 0);  
		node.setRenderState(ts);  
		node.updateRenderState();
		
		node.setModelBound(new BoundingSphere());
		//node.updateModelBound();

        kc=(KeyframeController) node.getChild(0).getController(0);
        
        //kc.getMorphMesh().getVertexBuffer();
        //kc.setActive(false);
        kc.setSpeed(10);
        kc.setRepeatType(Controller.RT_CYCLE);
        kc.setNewAnimationTimes(0, 1);
    
    }
	
}
