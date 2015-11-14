package CaptureTheFlag;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Identifier extends Thread{
//public class Identifier extends LightPoller {
	private boolean objectInspected;
	private boolean flagDetected;
	private SampleProvider sensor;
	private SensorModes mode;
	private float[] colorData;
	private double rValue, gValue, bValue;
	private double error = 8;
	private Hashtable<String, List<Double>> blockID = new Hashtable<String, List<Double>>();
	//TODO: change this when we know how the flag color will be given to us
	private String flag = "blue"; 
	
	/**
	 * Class constructor : inherited from LightPoller
	 * 
	 * @param sampler
	 * @param colorData
	 */
	public Identifier (Port identifierPort, String mode, String flag) {
		this.mode = new EV3ColorSensor(identifierPort);
		this.sensor = this.mode.getMode(mode);
		this.colorData = new float[sensor.sampleSize()];
		//super(identifierPort, mode);
		this.objectInspected = false;
		this.flagDetected = false;
		this.flag = flag;

		blockID.put("blue", Arrays.asList(new Double[] {19.05, 71.96, 52.25}));
		//TODO: add all the block colors and their average RBG values here 
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
			
			if (colorMapping (blockID.get(this.flag).get(0),
							  blockID.get(this.flag).get(1), 
							  blockID.get(this.flag).get(2))) {
				this.flagDetected = true;
				
			}

			try {
				Thread.sleep(50);
			} catch (Exception e) {

			}
		}
		
	}
	
	public boolean colorMapping(double r, double g, double b) {
		return Math.abs(r - this.rValue) <= this.error &&
			   Math.abs(g - this.gValue) <= this.error &&
			   Math.abs(b - this.bValue) <= this.error;
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
