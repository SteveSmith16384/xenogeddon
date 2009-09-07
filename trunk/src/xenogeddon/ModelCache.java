package xenogeddon;

import java.util.Hashtable;

import com.jme.scene.Node;
import com.jme.util.CloneImportExport;
import com.jme.util.export.Savable;

public final class ModelCache extends Hashtable {
	
	public ModelCache() {
		super();
	}
	
	public void putNode(String name, Node n) {
        CloneImportExport cloneEx = new com.jme.util.CloneImportExport();
        cloneEx.saveClone(n);
        this.put(name, cloneEx);
        //Main.p(this.size() + " nodes cached.");
	}

    public Savable getNode(String def) {
        CloneImportExport cloneIn = (CloneImportExport) this.get(def);
        if (cloneIn != null) {
        	return cloneIn.loadClone();
        } else {
        	return null; // Not saved yet!
        }
    }
}
