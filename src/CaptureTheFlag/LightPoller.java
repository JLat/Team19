package CaptureTheFlag;

import java.util.Stack;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightPoller extends Thread {
	private SensorModes colorSensor;
	private SampleProvider sensor;
	private float[] colorData;
	private Stack<Float> colorStack;
	
	
	/**
	 * Class constructor : makes a new LightPoller  
	 * 
	 * @param sampler
	 * @param colorData
	 */
	public LightPoller(Port port, String mode) {
		this.colorSensor = new EV3ColorSensor(port);
		this.sensor = colorSensor.getMode(mode);
		this.colorData = new float[sensor.sampleSize()];
	}
	
	/**
	 * run() for the Runnable interface of Thread.
	 * while the thread is running, reads and process the data from sampler
	 */
	public void run() {
		
	}
	
	//Returns the float value picked up by the red mode of the color sensor when called. 
	public float getSensorSample() {
		colorSensor.fetchSample(colorData, 0);
		return colorData[0];
	}
	
	
	/*
	 * Returns the boolean value of whether or not a color change has been detected 
	 * by comparing the previous sample stored in a stack to the current one.
	 */
	public boolean colorChange() {
		boolean change = false;
		float currentSample = getSensorSample();
		if(!colorStack.isEmpty()){
			float previousSample = colorStack.pop();
			if(previousSample - currentSample > 0.05) {
				change = true; 
			}
		}
		colorStack.push(currentSample);
		return change;
	}
	


}
