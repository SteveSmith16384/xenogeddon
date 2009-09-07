package xenogeddon.missions;

import xenogeddon.Main;

public abstract class AbstractMission {
	
	protected Main main;
	protected boolean mission_completed = false;
	
	public AbstractMission(Main m) {
		main = m;
		
	}
	
	public abstract void initMission();
	
	public abstract void modelsLoaded();
	
	public abstract void update(float interpolation);
	
	public abstract void playerEnteredTeleporter();

	public abstract void targetDestroyed();

	public abstract boolean showTime();
	
}
