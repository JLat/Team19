package CaptureTheFlag;

import java.util.Stack;
import lejos.robotics.SampleProvider;

public class UltrasonicPoller extends Thread implements SensorController {
	private SampleProvider sampler;
	private float[] usData;
	private Stack<Float> usStack;
	
	/**
	 * Class constructor : makes a new UltrasonicPoller
	 * @param sampler
	 * @param usData
	 */
	public UltrasonicPoller(SampleProvider sampler, float[] usData) {
		this.sampler = sampler;
		this.usData = usData;
	}

	/**
	 * run() for the Runnable interface of Thread.
	 * while the thread is running, reads and process the data from sampler
	 */
	public void run() {
		
	}
	
	@Override
	/**process the data given by colorID
	 * @param int distance
	 */
	public void processData(int distance) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * reads the data from the SampleProvider
	 * @return int data
	 */
	public int readData() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 *filter the data from the usStack at every int interval
	 * @param int interval
	 */
	private void filter(int interval) {
		
	}

}
