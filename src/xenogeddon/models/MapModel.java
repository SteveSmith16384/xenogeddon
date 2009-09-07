package xenogeddon.models;

import java.io.IOException;
import java.util.Hashtable;
import ssmith.lang.Functions;
import xenogeddon.Main;
import xenogeddon.effects.CeilingSpark;
import xenogeddon.effects.Smoke;
import xenogeddon.maps.AbstractMapData;
import xenogeddon.maps.MapSquare;
import xenogeddon.scenery.BubblingSlime;
import xenogeddon.scenery.Bulkhead;
import xenogeddon.scenery.ComputerConsole;
import xenogeddon.scenery.Crate;
import xenogeddon.scenery.DrippingSlime;
import xenogeddon.scenery.FallenCeilingTile;
import xenogeddon.scenery.FallingTile;
import xenogeddon.scenery.Pipe;
import xenogeddon.scenery.Teleporter;
import xenogeddon.walls.FloorAndCeiling;
import xenogeddon.walls.SlidingDoor;
import xenogeddon.walls.Wall;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

public final class MapModel extends Node {

	private static final int SUBNODE_SIZE = 5;

	private AbstractMapData map;
	private Hashtable<String,Node> subnodes;
	private Main main;

	public MapModel(Main m, AbstractMapData _map) throws IOException {
		super("Map_Node");
		main = m;
		map = _map;
		subnodes = new Hashtable<String,Node>();

		this.setIsCollidable(true);
		this.loadMapObjects();
	}

	private void loadMapObjects() throws IOException {
		for (int z=0 ; z<main.map.getMapHeight() ; z++) {
			for (int x=0 ; x<main.map.getMapWidth() ; x++) {
				MapSquare ms = map.getSq(x, z); //map.getSq(5, 5)
				if (ms.major_type == MapSquare.FLOOR) {
					// Floor
					Node n = this.getSubNode(x, z);
					n.attachChild(new FloorAndCeiling(main, x, z, ms.ceiling_type, ms.floor_tex, ms.ceiling_height));

					if (Main.HIGH_DETAIL) {
						if (ms.ceiling_type == MapSquare.NONE) {
							FallenCeilingTile fct = new FallenCeilingTile(main, x, z);
							n.attachChild(fct);
						} else if (ms.ceiling_type == MapSquare.FALLING) {
							FallingTile f = new FallingTile(main, x, z, ms.ceiling_height);
							main.addObjectForUpdate(f);
							n.attachChild(f);
						}
						if (ms.blocking_scenery_id == MapSquare.CRATE) {
							Crate fct = new Crate(main, x, z);
							n.attachChild(fct);
						}

						// Pipes
						if (ms.pipe_data.pipe_ew) {
							n.attachChild(new Pipe(main, true, x, ms.pipe_data.pipe_ew_height, z));
						}
						if (ms.pipe_data.pipe_ns) {
							n.attachChild(new Pipe(main, false, x, ms.pipe_data.pipe_ns_height, z));
						}
						if (ms.pipe_data.bulkhead_ew) {
							n.attachChild(new Bulkhead(main, true, x, ms.pipe_data.pipe_ew_height, z));
						}
						if (ms.pipe_data.bulkhead_ns) {
							n.attachChild(new Bulkhead(main, false, x, ms.pipe_data.pipe_ns_height, z));
						}

						// Smoke
						if (ms.pipe_data.pipe_smoke) {
							n.attachChild(new Smoke(main, x, ms.pipe_data.getSmokeHeight(), z, new Vector3f(0f, -1f, 0f)));
						}
						// Sparks
						if (ms.pipe_data.sparks) {
							n.attachChild(new CeilingSpark(main, x, ms.pipe_data.getSmokeHeight(), z, new Vector3f(0f, -1f, 0f)));
						}
					}

					// Doors
					if (ms.door_ns) {
						SlidingDoor s = new SlidingDoor(main, x, z, 0, null);
						n.attachChild(s);

						SlidingDoor s2 = new SlidingDoor(main, x, z, 2, s);
						n.attachChild(s2);

						s.opposite = s2;
					} else if (ms.door_ew) {
						SlidingDoor s = new SlidingDoor(main, x, z, 1, null);
						n.attachChild(s);

						SlidingDoor s2 = new SlidingDoor(main, x, z, 3, s);
						n.attachChild(s2);

						s.opposite = s2;
					}

					if (ms.blocking_scenery_id == MapSquare.CONSOLE) {
						ComputerConsole c = new ComputerConsole(main, x, z);
						n.attachChild(c);
					} else if (ms.blocking_scenery_id == MapSquare.TELEPORTER) {
						Teleporter c = new Teleporter(main, x, z);
						n.attachChild(c);
					}

					if (Main.HIGH_DETAIL) {
						if (ms.blocking_scenery_id == MapSquare.RANDOM_SCENERY) {
							int type = 1;//Functions.rnd(1, 2); // todo -re-add StasisPod
							if (type == 1) {
								BlissBeamCannon b = new BlissBeamCannon(main, x, z);
								//main.addObjectForUpdate(b);
								n.attachChild(b);
							} else if (type == 2) {
								StasisPod b = new StasisPod(main, x, z);
								n.attachChild(b);
							}

						} else if (ms.blocking_scenery_id == MapSquare.DEBRIS) {
							// todo
							//Debris b = new Debris(main, x, z);
							//n.attachChild(b);
						}
					}


				} else if (ms.major_type == MapSquare.NOTHING) {
					// Do nothing
				} else {
					throw new RuntimeException("Unknown mapsquare type: " + ms.major_type);
				}
			}
		}

		// Walls - note we leave out the outermost squares
		for (int z=1 ; z<main.map.getMapHeight()-1 ; z++) {
			for (int x=1 ; x<main.map.getMapWidth()-1 ; x++) {
				MapSquare ms = map.getSq(x, z);
				if (ms.major_type == MapSquare.FLOOR) {
					// Check surrounding squares
					int i=x-1;
					int j=z;
					this.addWall(ms, i, j, 0);

					i=x+1;
					j=z;
					this.addWall(ms, i, j, 2);

					i=x;
					j=z-1;
					this.addWall(ms, i, j, 1);

					i=x;
					j=z+1;
					this.addWall(ms, i, j, 3);
				}
			}
		}

		// Slime
		if (Main.HIGH_DETAIL) {
			/*if (!Main.RELEASE_MODE) {
				map.qty_dripping_slime = map.qty_dripping_slime * 10;
			}*/
			for (int i=0 ; i<map.qty_dripping_slime ; i++) {
				int x = Functions.rnd(0, main.map.getMapWidth()-1);
				int z = Functions.rnd(0, main.map.getMapHeight()-1);
				if (main.map.getSq(x, z).major_type == MapSquare.FLOOR && main.map.getSq(x, z).ceiling_type == MapSquare.NORMAL) {
					Node n = this.getSubNode(x, z);
					if (Functions.rnd(1, 2) == 1) {
						DrippingSlime fct = new DrippingSlime(main, x, z);
						main.addObjectForUpdate(fct);
						n.attachChild(fct);
					} else {
						BubblingSlime fct = new BubblingSlime(main, x, z);
						main.addObjectForUpdate(fct);
						n.attachChild(fct);
					}
				} else {
					i--;
				}
			}


			// Crates
			//if (Main.HIGH_DETAIL) {
			/*for (int i=0 ; i<map.qty_crates ; i++) {
			int x = Functions.rnd(0, main.map.getMapWidth()-1);
			int z = Functions.rnd(0, main.map.getMapHeight()-1);
			if (main.map.getSq(x, z).major_type == MapSquare.FLOOR) {
				Node n = this.getSubNode(x, z); 
				Crate fct = new Crate(main, x, z);
				//main.addObjectForUpdate(fct);
				n.attachChild(fct);
			} else {
				i--;
			}
		}*/

			//BlissBeamCannon b = new BlissBeamCannon(main, 8, 8);
		}

		this.setModelBound(new BoundingBox());
		this.updateModelBound();

		this.updateGeometricState(0, true);

	}

	private void addWall(MapSquare ms, int i, int j, int rot) {
		float bottom = -999f;
		if (map.getSq(i, j).major_type == MapSquare.NOTHING) {
			bottom = 0f;
		} if (map.getSq(i, j).major_type == MapSquare.FLOOR) {
			if (map.getSq(i, j).ceiling_height != ms.ceiling_height) {
				bottom = Math.min(map.getSq(i, j).ceiling_height, ms.ceiling_height);
			}
		}
		if (bottom != -999f) {
			Node n = this.getSubNode(ms.x, ms.z);
			n.attachChild(new Wall(main, ms.x, ms.z, rot, bottom, ms.ceiling_height));
		}

	}

	private Node getSubNode(int x, int z) {
		x = x/SUBNODE_SIZE;
		z = z/SUBNODE_SIZE;
		String s = x + "," + z;
		while (subnodes.containsKey(s) == false) {
			Node n = new Node("MapNode_" + x + "_" + z);
			n.setModelBound(new BoundingBox());
			n.updateModelBound();
			this.attachChild(n);
			this.subnodes.put(s, n);

		}
		Node n2 = (Node)this.subnodes.get(s);
		return n2;
	}

	public Node getNode() {
		return this;
	}


}
