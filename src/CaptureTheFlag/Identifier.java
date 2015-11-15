package CaptureTheFlag;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Identifier extends Thread {
	private boolean objectInspected;
	private boolean flagDetected;
	private SensorModes colorSensor;
	private SampleProvider sensor;
	private float[] colorData;
	private double rValue, gValue, bValue;
	private double error = 10;
	private Hashtable<String, List<Double>> blockID = new Hashtable<String, List<Double>>();
	//TODO: change this when we know how the flag color will be given to us
	private String flag = "yellow"; 
	
	/**
	 * Class constructor : inherited from LightPoller
	 * 
	 * @param sampler
	 * @param colorData
	 */
	public Identifier (Port identifierPort, String mode, String flag) {
		this.colorSensor = new EV3ColorSensor(identifierPort);
		this.sensor = colorSensor.getMode(mode);
		this.colorData = new float[sensor.sampleSize()];
		this.objectInspected = false;
		this.flagDetected = false;
		this.flag = flag;

		blockID.put("dark blue", Arrays.asList(new Double[] {4., 12., 22.}));
		blockID.put("light blue", Arrays.asList(new Double[] {31., 60., 46.}));
		blockID.put("red", Arrays.asList(new Double[] {34., 5., 6.}));
		blockID.put("yellow", Arrays.asList(new Double[] {49., 8., 34.}));
		blockID.put("white", Arrays.asList(new Double[] {56., 60., 57.}));
		blockID.put("wood", Arrays.asList(new Double[] {41., 21., 26.}));
		
		
		
		
		//TODO: add all the block colors and their average RGB values here 
		blockID.put("", Arrays.asList(new Double[] {1.0,2.0,3.0}));
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

			try {
				Thread.sleep(50);
			} catch (Exception e) {

			}
		}
		
	}
	
	public boolean colorMapping() {
		return Math.abs(blockID.get(this.flag).get(0) - this.rValue) <= this.error &&
			   Math.abs(blockID.get(this.flag).get(1) - this.gValue) <= this.error &&
			   Math.abs(blockID.get(this.flag).get(2) - this.bValue) <= this.error;
	}

	/**
	 * get methods for the RGB color values
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
	
	
	/**
	 * get method to check if flag is found
	 * @return this.flagDetected
	 */
	public boolean isFlagDetected() {
		return this.flagDetected;
	}
	
	/**
	 * get method to check whenever an object is found
	 * @return this.objectDetected
	 */
	public boolean isObjectInspected() {
		return this.objectInspected;
	}

}
