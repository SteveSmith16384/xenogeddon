package xenogeddon.maps;

import java.awt.Point;

import xenogeddon.walls.FloorAndCeiling;

public final class SingleRoomMapData extends AbstractMapData {

	public SingleRoomMapData() {
		super(30, 15, 15);
		
		super.createRoomByTopLeft(1, 1, 14, 14, FloorAndCeiling.NORMAL1, 1.2f);
		
		super.addPipes(6, 6, true);
		super.addPipes(6, 6, false);
		
		this.player_start_pos = new Point(4, 4);
		//super.addGoodyStartPos(5, 5);
		super.addBaddyStartPos(12, 12);
		//super.addBaddyStartPos(13, 13);
		//super.addBaddyStartPos(13, 12);
		//super.addBaddyStartPos(12, 13);
		
		super.map[4][6].blocking_scenery_id = MapSquare.CONSOLE;
		super.map[6][3].blocking_scenery_id = MapSquare.TELEPORTER;
		
		
	}

}
