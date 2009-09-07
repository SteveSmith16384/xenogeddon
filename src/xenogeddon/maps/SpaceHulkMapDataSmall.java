package xenogeddon.maps;

import java.awt.Point;
import java.util.ArrayList;

import ssmith.astar.AStar;
import ssmith.lang.Functions;
import xenogeddon.Main;
import xenogeddon.walls.FloorAndCeiling;

public class SpaceHulkMapDataSmall extends AbstractMapData {

	private static final int MAX = 20;
	private static final int MAX_ROOM_SIZE = 5;
	private static final int ROOMS = 2;

	public SpaceHulkMapDataSmall() {
		super(MAX, ROOMS*2, ROOMS*8);

		while (true) {
			ArrayList<Point> centres = new ArrayList<Point>();
			Point must_end = null;

			super.clearMap();

			for (int r=0 ; r<ROOMS ; r++) {
				int x = Functions.rnd(1, MAX-MAX_ROOM_SIZE-2);
				int y = Functions.rnd(1, MAX-MAX_ROOM_SIZE-2);
				int w = Functions.rnd(2, 5);
				int h = Functions.rnd(2, 5);

				if (r == 0) { // Start room
					this.createRoomByTopLeft(x, y, 5, 5, FloorAndCeiling.NORMAL1, 1.5f);
					map[x][y].blocking_scenery_id = MapSquare.TELEPORTER;
					player_start_pos = new Point(x+1, y+1);
					//if (!Main.RELEASE_MODE) {
					//this.addGoodyStartPos(x, y+1);
					//this.addGoodyStartPos(x+1, y);
					//}
					map[x+3][y+3].blocking_scenery_id = MapSquare.RANDOM_SCENERY;
					
				} else {
					while (super.isThereARoomAt(x, y, w, h) == true) {
						x = Functions.rnd(1, MAX-MAX_ROOM_SIZE-2);
						y = Functions.rnd(1, MAX-MAX_ROOM_SIZE-2);
						w = Functions.rnd(2, 5);
						h = Functions.rnd(2, 5);
					}
					this.createRoomByTopLeft(x, y, w, h, Functions.rnd(1, 2), 1f);
					
					map[x+1][y].blocking_scenery_id = MapSquare.DEBRIS;
					map[x][y].blocking_scenery_id = MapSquare.CRATE;

					if (x > 0 && y > 0) {
						super.addBaddyStartPos(x, y);
						super.addBaddyStartPos(x+1, y);
						super.addBaddyStartPos(x, y+1);
						super.addBaddyStartPos(x+1, y+1);
					}
					if (r == ROOMS-1) {
						map[x+2][y+2].blocking_scenery_id = MapSquare.CONSOLE; // Make sure there's not a baddy in the same square!
						must_end = new Point(x+2, y+2);
					}
				}
				centres.add(new Point(x+(w/2), y+(h/2)));
			}


			// Connect rooms
			doors.clear();
			for (int i=0 ; i<centres.size() ; i++) {
				Point start = centres.get(i);
				Point end = centres.get(Functions.rnd(0, centres.size()-1));
				super.addPipes(start.x+1, end.y+1, Functions.rnd(0,1) == 0);
				this.addCorridorAndDoors(start.x, start.y, end.x, end.y);
			}

			// Check main rooms are connected
			AStar astar = new AStar(this);
			astar.findPath(this.player_start_pos.x, this.player_start_pos.y, must_end.x, must_end.y, false);
			if (!astar.wasSuccessful()) {
				//astar.showRoute();
				this.showMap();
				Main.p("Recreating map.");
			} else {
				addDoors();
				return;
			}
			

		}

	}

}
