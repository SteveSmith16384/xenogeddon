package xenogeddon.models;

import xenogeddon.GameObject;
import xenogeddon.Main;

public abstract class AbstractColladaModel extends GameObject {
	
	public AbstractColladaModel(Main m, String name, boolean collides, float x, float z, float diam) {
		super(m, name, collides, x, z, 0f);
	}

}
