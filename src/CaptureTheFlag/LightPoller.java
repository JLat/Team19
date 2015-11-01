package CaptureTheFlag;

import java.util.Stack;
import lejos.robotics.SampleProvider;

public class LightPoller extends Thread implements SensorController {
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
	
	@Override
	/**process the data given by colorID
	 * 
	 * @param int colorID
	 */
	public void processData(int colorID) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * reads the data from the SampleProvider
	 * 
	 * @return int data
	 */
	@Override
	public int readData() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Returns the boolean value of whether or not a color change has been detected 
	 * by comparing the previous sample stored in a stack to the current one to a threshold
	 * 
	 * @param double threshold 
	 * @return (Math.abs(colorStack.pop - readData()) > threshold)  
	 */
	public boolean isColorChanged(int threshold) {
		return Math.abs(colorStack.peek() - readData()) > threshold;
	}

}
