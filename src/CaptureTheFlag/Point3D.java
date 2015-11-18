package CaptureTheFlag;

/**
 * simple class that recreates the javaFx.geometry.Point3D class, because the
 * runtime version of lejos EV3 seems unable to support it.
 * 
 * @author Fabrice
 *
 */
public class Point3D implements Comparable<Point3D> {
	private double x;
	private double y;
	private double z;

	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean equals(Point3D otherPoint) {
		return (otherPoint.getX() == this.x) && (otherPoint.getY() == this.y) && (otherPoint.getZ() == this.z);
	}

	public double distance(Point3D otherPoint){
		double x2=otherPoint.getX(),y2=otherPoint.getY(),z2=otherPoint.getZ();
		return Math.sqrt( 	Math.pow((x2-x),2)	+	Math.pow((y2-y),2)	+	Math.pow((z2-z),2)	);
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}
	
	public String toString(){
		return "("+this.x+";"+this.y+";"+this.z+")";
	}

	@Override
	public int compareTo(Point3D other) {
		return 0;
	}
}
