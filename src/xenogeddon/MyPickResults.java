package xenogeddon;

import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;

public final class MyPickResults extends PickResults {
	
	private TriMesh ignore;
	
	public MyPickResults(TriMesh _ignore) {
		ignore = _ignore; // the shooter or looker or whatever
	}

	public void addPick(Ray ray, Geometry g) {
		if (g != ignore) {
			super.addPickData(new PickData(ray, g, true));
		}
	}
	
	public GameObject getGameObject(int i) {
		ICollider ic = (ICollider)super.getPickData(i).getTargetMesh();
		return (GameObject)ic.getOwner();
	}

	public void processPick() {
		// Do nothing
	}

}
