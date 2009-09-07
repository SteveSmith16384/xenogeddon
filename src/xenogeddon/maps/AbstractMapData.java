package xenogeddon.maps;

import java.awt.Point;
import java.util.ArrayList;
import ssmith.astar.IAStarMapInterface;
import ssmith.lang.Functions;
import xenogeddon.Main;
import xenogeddon.walls.FloorAndCeiling;

public abstract class AbstractMapData implements IAStarMapInterface {

	protected MapSquare map[][];
	protected Point player_start_pos;
	private ArrayList<Point> goody_start_points = new ArrayList<Point>();
	private ArrayList<Point> baddy_start_points = new ArrayList<Point>();
	public int qty_dripping_slime, qty_crates;
	protected ArrayList<Point> doors = new ArrayList<Point>();

	public AbstractMapData(int MAX, int slime, int crates) { 
		map = new MapSquare[MAX][MAX];
		//qty_fallen_tiles = fallen_tiles;
		qty_dripping_slime = slime;
		qty_crates = crates;

		this.clearMap();
	}

	protected void clearMap() {
		for (int y=0 ; y<this.getMapHeight() ; y++) {
			for (int x=0 ; x<this.getMapWidth() ; x++) {
				map[x][y] = new MapSquare(MapSquare.NOTHING, x, y);
			}
		}
		goody_start_points = new ArrayList<Point>();
		baddy_start_points = new ArrayList<Point>();
	}


	public void addGoodyStartPos(int x, int z) {
		if (map[x][z].major_type == MapSquare.FLOOR) {
			Point p = new Point(x, z);
			if (goody_start_points.contains(p) == false) {
				goody_start_points.add(p);
			}
		} else {
			Main.p("Warning - trying to start goody at empty location.");	
		}
	}


	public void addBaddyStartPos(int x, int z) {
		if (map[x][z].major_type == MapSquare.FLOOR) {
			Point p = new Point(x, z);
			if (baddy_start_points.contains(p) == false) {
				baddy_start_points.add(p);
			}
		} else {
			Main.p("Warning - trying to start baddy at empty location.");	
		}
	}


	public Point getGoodyStartPos() {
		if (this.goody_start_points.size() > 0) {
			Point p = this.goody_start_points.get(0);
			this.goody_start_points.remove(0);
			return p;
		} else {
			return null;
		}
	}

	public Point getBaddyStartPos() {
		if (this.baddy_start_points.size() > 0) {
			Point p = this.baddy_start_points.get(0);
			this.baddy_start_points.remove(0);
			return p;
		} else {
			return null;
		}
	}

	public Point getPlayerStartPos() {
		return this.player_start_pos;
	}


	public MapSquare getSq(int x, int z) {
		try {
			return map[x][z];
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
			Main.p("Error: sq " + x + "," + z + " not found.");
			ex.printStackTrace();
			return null;
		}
	}

	public MapSquare getSq(float x, float z) {
		return map[(int)x][(int)z];
	}

	protected void createRoomByCentre(int centre_x, int centre_y, int l, int d, int type, float height) {
		if (l > 1 || d > 1) {
			this.createRoomByTopLeft(centre_x - (l/2), centre_y - (d/2), l, d, type, height);
		} else {
			map[centre_x][centre_y].major_type = MapSquare.FLOOR;
			map[centre_x][centre_y].floor_tex = type;
		}

	}

	protected void createRoomByTopLeft(int x, int y, int l, int d, int type, float height) {
		for (int y2=y ; y2<=y+d ; y2++) {
			for (int x2=x ; x2<=x+l ; x2++) {
				MapSquare ms = map[x2][y2];
				ms.major_type = MapSquare.FLOOR;
				ms.floor_tex = type;
				ms.ceiling_height = height;
			}
		}

	}

	protected boolean isThereARoomAt(int x, int y, int l, int d) {
		for (int y2=y-1 ; y2<=y+d+1 ; y2++) {
			for (int x2=x-1 ; x2<=x+l+1 ; x2++) {
				if (map[x2][y2].major_type != MapSquare.NOTHING) {
					return true;
				}
			}
		}
		return false;
	}

	protected void addPipes(int sx, int sz, boolean ew) {
		MapSquare ms = map[sx][sz];
		boolean pipes;
		if (Functions.rnd(1, 2) == 1) {
			ms.pipe_data.pipe_smoke = true;
			pipes = true;
		} else {
			ms.pipe_data.sparks = true;
			pipes = false;
		}
		float height = 0.9f;//Functions.rndFloat(0.9f, ms.ceiling_height);
		if (ew) {
			int z = sz;
			for (int x=sx ; x<getMapWidth() ; x++) {
				if (map[x][z].major_type == MapSquare.FLOOR) {
					if (pipes) {
						map[x][z].pipe_data.pipe_ew = true;
					} else {
						map[x][z].pipe_data.bulkhead_ew = true;
					}
					map[x][z].pipe_data.pipe_ew_height = height;
				} else {
					// Reached end of room
					break;
				}
			}
			for (int x=sx ; x>= 0 ; x--) {
				if (map[x][z].major_type == MapSquare.FLOOR) {
					if (pipes) {
						map[x][z].pipe_data.pipe_ew = true;
					} else {
						map[x][z].pipe_data.bulkhead_ew = true;
					}
					map[x][z].pipe_data.pipe_ew_height = height;
				} else {
					// Reached end of room
					break;
				}
			}
		} else {
			int x = sx;
			for (int z=sz ; z<getMapHeight() ; z++) {
				if (map[x][z].major_type == MapSquare.FLOOR) {
					if (pipes) {
					map[x][z].pipe_data.pipe_ns = true;
					} else {
						map[x][z].pipe_data.bulkhead_ns = true;
					}
					map[x][z].pipe_data.pipe_ns_height = height;
				} else {
					// Reached end of room
					break;
				}
			}
			for (int z=sz ; z>= 0 ; z--) {
				if (map[x][z].major_type == MapSquare.FLOOR) {
					if (pipes) {
					map[x][z].pipe_data.pipe_ns = true;
					} else {
						map[x][z].pipe_data.bulkhead_ns = true;
					}
					map[x][z].pipe_data.pipe_ns_height = height;
				} else {
					// Reached end of room
					break;
				}
			}
		}		
	}

	
	protected void addCorridorAndDoors(int sx, int sy, int ex, int ey) {
		// Make sure the values are right way round
		if (ex < sx) {
			int t = sx;
			sx = ex;
			ex = t;
		}
		if (ey < sy) {
			int t = sy;
			sy = ey;
			ey = t;
		}

		int difx = Functions.sign(ex-sx);
		int dify = Functions.sign(ey-sy);

		// Across
		if (difx != 0) {
			boolean door_added = false;
			boolean enemy_added = false;
			int y = sy;
			for (int x=sx ; x<ex+1 ; x += difx) {
				MapSquare ms = map[x][y]; 
				if (ms.major_type != MapSquare.FLOOR) {
					ms.major_type = MapSquare.FLOOR;
					ms.floor_tex = FloorAndCeiling.CORRIDOR;
					ms.ceiling_height = 1;
					if (!door_added) {
						if (map[x][y+1].major_type == MapSquare.NOTHING && map[x][y-1].major_type == MapSquare.NOTHING) {
							//map[x][y].door_ew = true;
							doors.add(new Point(x, y));
							door_added = true; 
						}
					} else if (!enemy_added) {
						if (Functions.rnd(1, 10) == 1) {
							this.addBaddyStartPos(x, y);
							enemy_added = true; 
						}
					}
				}
			}
		}
		// Down
		if (dify != 0) {
			boolean door_added = false;
			boolean enemy_added = false;
			int x = sx;
			for (int y=sy ; y<ey+1 ; y += dify) {
				MapSquare ms = map[x][y];
				if (ms.major_type != MapSquare.FLOOR) {
					ms.major_type = MapSquare.FLOOR;
					ms.floor_tex = FloorAndCeiling.CORRIDOR;
					ms.ceiling_height = 1;
					if (!door_added && y>sy) {
						if (map[x+1][y].major_type == MapSquare.NOTHING && map[x-1][y].major_type == MapSquare.NOTHING) {
							//map[x][y].door_ns = true;
							doors.add(new Point(x, y));
							door_added = true; 
						}
					} else if (!enemy_added) {
						if (Functions.rnd(1, 10) == 1) {
							this.addBaddyStartPos(x, y);
							enemy_added = true; 
						}
					}
				}
			}
		}

	}

	public void showMap() {
		try {
			int w = map[0].length;
			int h = map.length;

			for(int z=0 ; z< h ; z++) {
				for(int x=0 ; x<w ; x++) {
					String s = " ";
					if (x == this.player_start_pos.x && z == this.player_start_pos.y) {
						s = "S";
					} else if (map[x][z].door_ns) {
						s = "N";
					} else if (map[x][z].door_ew) {
						s = "E";
					} else if (map[x][z].major_type == MapSquare.FLOOR) {
						s = "X";
					}
					System.out.print(s);
				}
				System.out.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	protected void addDoors() {
		for (int i=0 ; i<doors.size() ; i++) {
			Point p = doors.get(i);
			if (map[p.x+1][p.y].major_type == MapSquare.NOTHING && map[p.x-1][p.y].major_type == MapSquare.NOTHING) {
				map[p.x][p.y].door_ns = true;
			} else if (map[p.x][p.y+1].major_type == MapSquare.NOTHING && map[p.x][p.y-1].major_type == MapSquare.NOTHING) {
				map[p.x][p.y].door_ew = true;
			}
		}
	}

	//	A*

	public int getMapHeight() {
		return map[0].length;
	}

	public float getMapSquareDifficulty(int x, int z) {
		return 0;
	}

	public int getMapWidth() {
		return map.length;
	}

	public boolean isMapSquareTraversable(int x, int z) {
		MapSquare sq = this.getSq(x, z); 
		return sq.major_type == MapSquare.FLOOR && sq.blocking_scenery_id == MapSquare.NOTHING;
	}

}
