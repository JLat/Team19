package CaptureTheFlag;

import java.util.ArrayList;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Identifier extends Thread {

	private SensorModes colorSensor;
	private SampleProvider sensor;
	private float[] colorData;
	private double rValue, gValue, bValue;
	private double error;
	private Point3D dark_blue, light_blue, red, yellow, white, wood;
	private ArrayList<Point3D> colorList;

	private String flagType;

	// using 3d Points for the colors.

	// variable representing the type of object that corresponds to the current
	// reading.
	private String typeOfBlock;

	/**
	 * Class constructor, creates an Identifier object.
	 *
	 * @param identifierPort
	 *            the port used, default is "S2"
	 * @param mode
	 *            mode of the sensor, Strongly recommended to be set to "RGB".
	 * @param flag
	 *            the name of the target flag. The flag should be one of the
	 *            possible colors (i.e. either "dark blue", "light blue", "red",
	 *            "yellow", "white" or "wood".
	 */
	public Identifier(Port identifierPort, String mode, String flag) {
		this.colorSensor = new EV3ColorSensor(identifierPort);
		this.sensor = colorSensor.getMode(mode);
		this.colorData = new float[sensor.sampleSize()];
		this.flagType = flag;

		// sets the ratio values for the different reference colors;
		// TODO: SET THESE VALUES BASED ON EXPERIMENTATION.
		// first value: Red over Green
		// second value: Green over Blue
		// third value: Blue over Red
		dark_blue = new Point3D(0.35, 0.53, 5.41);
		light_blue = new Point3D(0.605, 1.81, 0.94);
		red = new Point3D(5.625, 1.3125, 0.15625);
		yellow = new Point3D(1.811, 3.763, 0.154);
		white = new Point3D(1.26, 0.673, 1.194);
		wood = new Point3D(1.75, 1.20, 0.48);

		this.colorList = new ArrayList<Point3D>();
		colorList.add(dark_blue);
		colorList.add(light_blue);
		colorList.add(red);
		colorList.add(yellow);
		colorList.add(white);
		colorList.add(wood);

	}

	/**
	 * run method inherited from superclass which extends Thread
	 */
	public void run() {
		while (true) {

			sensor.fetchSample(colorData, 0);

			this.rValue = colorData[0] * 1024;
			this.gValue = colorData[1] * 1024;
			this.bValue = colorData[2] * 1024;

			// method that sets the typeOfBlock String to be the closest color
			// in terms of color ratios.
			this.typeOfBlock = categorizeReadings();

			try {
				Thread.sleep(50);
			} catch (Exception e) {

			}
		}

	}

	/**
	 * returns the color that best matches the current reading.
	 * 
	 * @return returns a String representation of the given color: possible
	 *         values are: "dark blue", "light blue", "red", "yellow", "white",
	 *         "wood"
	 * @CAREFUL:
	 */
	public String getBlockColor() {
		return this.typeOfBlock;
	}

	/**
	 * Categorizes the current reading as one of the possible types of blocks.
	 * 
	 * @return returns a String representation of the closest color
	 * 
	 */
	public String categorizeReadings() {

		// calculate the ratio of the color readings.
		double RoverG = rValue / gValue;
		double GoverB = gValue / bValue;
		double BoverR = bValue / rValue;

		// classifies the point using the ratios and returns a String
		// representation of the type of color detected.
		Point3D closestType = getClosestColor(RoverG, GoverB, BoverR);
		
		if (closestType.equals(this.dark_blue))
			return "dark blue";
		else if (closestType.equals(this.light_blue))
			return "light blue";
		else if (closestType.equals(this.red))
			return "red";
		else if (closestType.equals(this.yellow))
			return "yellow";
		else if (closestType.equals(this.white))
			return "white";
		else if (closestType.equals(this.wood))
			return "wood";
		else {
			return "UNKNOWN";
		}

	};

	/**
	 * method that returns the closest point (color) given a set of coordinates
	 * (ratios of colors).
	 * 
	 * @param RoverG
	 *            : the ratio between the Red color value and the Green color
	 *            value of the current reading.
	 * @param GoverB
	 *            : the ratio between the Green color value and the Blue color
	 *            value of the current reading.
	 * @param BoverR
	 *            : the ratio between the Blue color value and the Red color
	 *            value of the current reading.
	 * @return the color type that is closest to the given color ratio readings.
	 */
	private Point3D getClosestColor(double RoverG, double GoverB, double BoverR) {

		// encoding the given color ratios into a Point3D object for
		// manipulation.
		Point3D readColor = new Point3D(RoverG, GoverB, BoverR);

		// point representing the closest color (with minimal error).
		Point3D result = new Point3D(0, 0, 0);

		if (this.colorList.isEmpty()) {
			
		}

		// returns the closest point among the colorList
		double error = Double.MAX_VALUE;
		double distance;
		for (Point3D colorType : this.colorList) {
			distance = readColor.distance(colorType);
			if (distance < error) {
				error = distance;
				result = colorType;
			}
		}
		this.error = error;
		return result;
	}

	/**
	 * get methods for the RGB color values
	 * 
	 * @return
	 */
	public double getRedValue() {
		return this.rValue;
	}

	public double getGreenValue() {
		return this.gValue;
	}

	public double getBlueValue() {
		return this.bValue;
	}
	
	public double getRoverG(){
		return rValue/gValue;
	}
	public double getGoverB(){
		return gValue/bValue;
	}
	public double getBoverR(){
		return bValue/rValue;
	}
	/**
	 * get method to check if flag is found
	 * 
	 * @return this.flagDetected
	 */
	public boolean isFlagDetected() {
		return this.typeOfBlock == this.flagType;
	}

	public double getError() {
		return this.error;
	}

}
