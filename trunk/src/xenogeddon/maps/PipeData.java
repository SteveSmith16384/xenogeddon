package xenogeddon.maps;

public class PipeData {

	public boolean pipe_ew = false;
	public boolean pipe_ns = false;
	public boolean bulkhead_ew = false;
	public boolean bulkhead_ns = false;
	public float pipe_ew_height;
	public float pipe_ns_height;
	public boolean pipe_smoke = false;
	public boolean sparks = false;
	
	public float getSmokeHeight() {
		if (pipe_ew_height == 0) {
			return pipe_ns_height;
		} else if (pipe_ns_height == 0) {
			return pipe_ew_height;
		} else {
			return Math.max(pipe_ew_height, pipe_ns_height);
		}
	}

}
