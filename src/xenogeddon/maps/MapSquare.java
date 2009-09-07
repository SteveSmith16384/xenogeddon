package xenogeddon.maps;

import ssmith.lang.Functions;

public final class MapSquare {
	
	// Major types
	public static final int NOTHING = 0;
	public static final int FLOOR = 1;
	
	// Ceiling types
	public static final int NONE = 0;
	public static final int NORMAL = 1;
	public static final int FALLING = 2;
	
	// Unique blocking scenery
	public static final int TELEPORTER = 1;
	public static final int CONSOLE = 2;
	public static final int RANDOM_SCENERY = 3;
	public static final int CRATE = 4;
	public static final int DEBRIS = 5;
	
	public int major_type;
	public int floor_tex; // See FloorAndCeiling class
	public int x, z;
	public float ceiling_height = 1f;
	public PipeData pipe_data = new PipeData();
	public boolean door_ew = false;
	public boolean door_ns = false;
	public int ceiling_type;
	public int blocking_scenery_id = NOTHING;
	
	public MapSquare(int t, int x, int z) {
		this.major_type = t;
		this.x = x;
		this.z = z;
		
		int c = Functions.rnd(1, 5);
		switch (c) {
		case 1:
			this.ceiling_type = NONE;
			break;
		case 2:
			this.ceiling_type = FALLING;
			break;
		default:
			this.ceiling_type = NORMAL;
			break;
		}
	}

}
