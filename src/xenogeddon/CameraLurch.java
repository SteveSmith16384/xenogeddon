package xenogeddon;

public class CameraLurch {
	
	private static final float MAX_HEIGHT = 0.07f;
	private static final float HEIGHT_INC = 0.002f;
	
	//private Main main;
	private float curr_height = 0;
	private int diff = 1;
	
	public CameraLurch() {
		//main = m;
	}
	
	public void update() {
		this.curr_height += (HEIGHT_INC * diff);
		if (this.curr_height > MAX_HEIGHT) {
			diff = -1;
		} else if (this.curr_height < 0) {
			diff = 1;
		}
	}
	
	public float getCurrHeight() {
		return this.curr_height;
	}

}
