package ssmith.lang;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.nio.channels.*;
import ssmith.io.TextFile;

public final class Functions {

	private static Random random = new Random();

	public static int biggest(int a, int b) {
		return Math.max(a, b);
	}

	public static int rnd(int a, int b) {
		return random.nextInt(b + 1 - a) + a;
	}

	public static float rndFloat(float a, float b) {
		return (random.nextFloat() * (b - a)) + a;
	}

	public static double rndDouble(double a, double b) {
		return (random.nextDouble() * (b - a)) + a;
	}

	public static int sign(int a) {
		if (a == 0) {
			return 0;
		}
		else if (a > 0) {
			return 1;
		}
		else {
			return -1;
		}
	}

	public static int sign(float a) {
		if (a == 0) {
			return 0;
		}
		else if (a > 0) {
			return 1;
		}
		else {
			return -1;
		}
	}

	public static int sign(double a) {
		if (a == 0) {
			return 0;
		}
		else if (a > 0) {
			return 1;
		}
		else {
			return -1;
		}
	}

	public static double distance(int x1, int y1, int x2, int y2) {
		//System.out.println(x1+","+y1+"  "+x2+","+y2);
		double side1 = 0;
		if (x1 != x2) {
			side1 = Math.pow( (double) (x2 - x1), 2);
		}
		//System.out.println("Side1: "+side1);
		double side2 = 0;
		if (y1 != y2) {
			side2 = Math.pow( (double) (y2 - y1), 2);
		}
		//System.out.println("Side2: "+side2);
		if (side1 == 0 && side2 == 0) {
			return 0;
		}
		else {
			double result = Math.sqrt(side1 + side2);
			//System.out.println("Distance: "+result);
			return result;
		}
	}

	public static double distance(float x1, float y1, float x2, float y2) {
		//System.out.println(x1+","+y1+"  "+x2+","+y2);
		double side1 = 0;
		if (x1 != x2) {
			float x3 = (x2 - x1);
			side1 = x3 * x3;
		}
		//System.out.println("Side1: "+side1);
		double side2 = 0;
		if (y1 != y2) {
			float y3 = (y2 - y1);
			side2 = y3 * y3;
		}
		double result = Math.sqrt(side1 + side2);
		return result;
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		//System.out.println(x1+","+y1+"  "+x2+","+y2);
		double side1 = 0;
		if (x1 != x2) {
			side1 = Math.pow( (x2 - x1), 2);
		}
		//System.out.println("Side1: "+side1);
		double side2 = 0;
		if (y1 != y2) {
			side2 = Math.pow( (y2 - y1), 2);
		}
		double result = Math.sqrt(side1 + side2);
		return result;
	}

	public static double distance(float x1, float y1, float z1, float x2,
			float y2, float z2) {
		double side1 = Math.pow( (double) (x2 - x1), 2);
		double side2 = Math.pow( (double) (y2 - y1), 2);
		double side3 = Math.pow( (double) (z2 - z1), 2);

		double result = Math.sqrt(side1 + side2 + side3);
		return result;
	}
	
	public static float MakeSameSignAs(float num, float s) {
		if (sign(num) != sign(s)) {
			return num * -1;
		} else {
			return num;
		}
	}

	public static int mod(int x) {
		if (x >= 0) {
			return x;
		}
		else {
			return x * -1;
		}
	}

	public static float mod(float x) {
		if (x >= 0) {
			return x;
		}
		else {
			return x * -1;
		}
	}

	public static double mod(double x) {
		if (x >= 0) {
			return x;
		}
		else {
			return x * -1;
		}
	}

	/*
	 * Function to return the co-ords of the point in the same direction
	 * as start to finish, but only so far in the distance.
	 */
	public static Point getPoint(int x1, int y1, int x2, int y2, int pcent) {
		int width = mod(x2 - x1);
		int height = mod(y2 - y1);

		width = (width * pcent) / 100;
		height = (height * pcent) / 100;

		return new Point(Math.min(x1, x2) + width, Math.min(y1, y2) + height);
	}

	public int remainder(int a, int d) {
		int r = (int) Math.IEEEremainder( (double) a, (double) d);
		if (r < 0) {
			r = r + d;
		}
		return r;
	}

	/**
	 * This will return true if targ x, y is inside the defined area.
	 */
	public static boolean isInsideArea(int areaX, int areaY, int width,
			int height, int targX, int targY) {
		boolean result = false;
		if (targX >= areaX && targY >= areaY) {
			if (targX <= areaX + width && targY <= areaY + height) {
				result = true;
			}
		}
		return result;
	}

	public static void delay(int milliseconds) {
		if (milliseconds > 0) {
			try {
				Thread.sleep(milliseconds);
			}
			catch (InterruptedException e) {
			}
		}
	}

	public static void delay(long milliseconds) {
		if (milliseconds > 0) {
			try {
				Thread.sleep(milliseconds);
			}
			catch (InterruptedException e) {
			}
		}
	}

	public static String AppendSlash(String file) {
		if (file.endsWith("\\") || file.endsWith("/")) {
			return file;
		} else {
			return file + "/";
		}
	}

	public static String CheckApostraphes(String SQL) {
		return SQL.replaceAll("'", "''");
	}

	public static void CopyFile(String in, String out) throws Exception {
		FileChannel sourceChannel = new FileInputStream(new File(in)).getChannel();
		FileChannel destinationChannel = new FileOutputStream(new File(out)).getChannel();
		sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		// or
		//  destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		sourceChannel.close();
		destinationChannel.close();
	}


	/*public static void LogStackTrace(Exception ex, String URL) {
		try {
			TextFile tf = new TextFile();
			tf.openFile(URL, TextFile.APPEND);
			tf.writeLine(ex.getMessage());
			StringBuffer str = new StringBuffer();
			for (int c = 0; c < ex.getStackTrace().length; c++) {
//				StackTraceElement el = ex.getStackTrace()[ex.getStackTrace().length-1];
				str.append(ex.getStackTrace()[c].getClassName());
				str.append(":" + ex.getStackTrace()[c].getLineNumber() + " - ");
				str.append(ex.getStackTrace()[c].getMethodName());
				tf.writeLine(str.toString());
				str.delete(0, str.length()-1);
			}
			tf.close();
		} catch (Exception e) {
			System.out.print("Error logging error: " + e.getMessage());
		}
	}*/

	public static int GetDiffBetweenAngles(int angle1, int angle2) {
		// Rotate angle1 with angle2 so that the sought after
		// angle is between the resulting angle and the x-axis
		angle1 -= angle2;

		// "Normalize" angle1 to range [-180,180)
		while(angle1 < -180)
			angle1 += 360;
		while(angle1 >= 180)
			angle1 -= 360;

		// angle1 has the signed answer, just "unsign it"
		return Functions.mod(angle1);
	}

	public static int RestateAngle(int a) {
		while (a >=360) {
			a-=360;
		}
		while (a < 0) {
			a+=360;
		}
		return a;
	}

	public static int GetAbsoluteAngleTo(int sx, int sy, int ex, int ey) {
		double x = ex - sx;
		double z = ey - sy;
		if (z == 0) {
			z = 1; // Avoid div by zero
		}
		if (x>=0 && z>0) {
			return (int) Math.toDegrees(Math.atan(x/z));
		} else if (x<0 && z>0) {
			return (int) Math.toDegrees(Math.atan(x/z)) + 360;
		} else {
			return (int) Math.toDegrees(Math.atan(x/z)) + 180;
		}
	}

	public static int GetAbsoluteAngleTo(float sx, float sy, float ex, float ey) {
		double x = ex - sx;
		double z = ey - sy;
		if (z == 0) {
			z = 1; // Avoid div by zero
		}
		if (x>=0 && z>0) {
			return (int) Math.toDegrees(Math.atan(x/z));
		} else if (x<0 && z>0) {
			return (int) Math.toDegrees(Math.atan(x/z)) + 360;
		} else {
			return (int) Math.toDegrees(Math.atan(x/z)) + 180;
		}
	}

	public static Hashtable<String, String> ConfigFile(String url, String sep) throws FileNotFoundException, IOException {
		Hashtable<String, String> ht = new Hashtable<String, String>();

		TextFile tf = new TextFile();
		tf.openFile(url, TextFile.READ);
		while (tf.isEOF() == false) {
			String line = tf.readLine();
			//System.out.println(line);
			if (line.length() > 0) {
				if (line.startsWith("#") == false) {
					String csv[] = line.split(sep);
					String key = csv[0];
					String value = csv[1];
					//System.out.println("Ext:" + ext + "  Ctype:" + ctype);
					ht.put(key, value);
				}
			}
		}
		tf.close();
		return ht;
	}

}


