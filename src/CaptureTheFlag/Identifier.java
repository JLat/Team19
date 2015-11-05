package CaptureTheFlag;

import lejos.robotics.SampleProvider;

public class Identifier extends LightPoller {
	private boolean objectInspected;
	private boolean flagDetected;
	
	/**
	 * Class constructor : inherited from LightPoller
	 * 
	 * @param sampler
	 * @param colorData
	 */
	public Identifier(SampleProvider sampler, float[] colorData) {
		super(sampler, colorData);
		this.objectInspected = false;
		this.flagDetected = false;
	}
	
	/**
	 * run method inherited from superclass which extends Thread
	 * 			if the colorID is > 10 	-> a wooden block is detected
	 * 			if the colorID is [0,10]-> a styrofoam block is detected
	 * 			if the colorID is -1 	-> no objects are within detection range
	 */
	public void run() {
		
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
