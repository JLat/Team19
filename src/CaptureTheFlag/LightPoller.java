package CaptureTheFlag;

import java.util.Stack;
import lejos.robotics.SampleProvider;

public class LightPoller extends Thread {
	private SampleProvider sampler;
	private float[] colorData;
	private Stack<Float> colorStack;
	
	/**
	 * Class constructor : makes a new LightPoller  
	 * 
	 * @param sampler
	 * @param colorData
	 */
	public LightPoller(SampleProvider sampler, float[] colorData) {
		this.sampler = sampler;
		this.colorData = colorData;
	}
	
	/**
	 * run() for the Runnable interface of Thread.
	 * while the thread is running, reads and process the data from sampler
	 */
	public void run() {
		
	}
	


}
