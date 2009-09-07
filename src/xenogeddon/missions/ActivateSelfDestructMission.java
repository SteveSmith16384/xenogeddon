package xenogeddon.missions;

import xenogeddon.Main;
import xenogeddon.maps.SingleRoomMapData;
import xenogeddon.maps.SpaceHulkMapData2;
import xenogeddon.maps.SpaceHulkMapDataSmall;

public final class ActivateSelfDestructMission extends AbstractMission {

	private boolean console_destroyed = false;

	public ActivateSelfDestructMission(Main m) {
		super(m);
	}

	public void initMission() {
		if (!Main.RELEASE_MODE) {
			main.map = new SpaceHulkMapDataSmall();//SpaceHulkMapData2();//SpaceHulkMapDataSmall();
		} else {
			main.map = new SpaceHulkMapData2();
		}
	}
	
	public void modelsLoaded() {
		main.text_hud.clearText();
		main.text_hud.add("Location:");
		main.text_hud.add("Space Hulk SH-VP178");
		main.text_hud.add("Mission:");
		main.text_hud.add("Destroy Computer Console");
		main.text_hud.add("Warning:");
		main.text_hud.add("Time is limited");
	}

	
	public void update(float interpolation) {

	}

	
	public void targetDestroyed() {
		if (!console_destroyed) {
			console_destroyed = true;
			main.lights.red_flashing = true;
			main.text_hud.add("Self-destruct activated!", 0);
			main.text_hud.add("Return to extraction zone");
			main.time_remaining = 60*2;
		}
	}

	public void playerEnteredTeleporter() {
		if (console_destroyed) {
			if (mission_completed == false) {
				mission_completed = true;
				main.missionCompleted();
			}
		}
	}

	public boolean showTime() {
		return this.console_destroyed && mission_completed == false;
	}

}
