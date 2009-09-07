package ssmith.util;

public class PointF {
	
	public float x, z;
	
	public PointF() {
	}
	
	public PointF(float _x, float _z) {
		x = _x;
		z = _z;
	}
	
	public String toString() {
		return x + "," + z;
	}

}
