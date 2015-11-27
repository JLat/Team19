package CaptureTheFlag;

import java.util.LinkedList;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class UltrasonicPoller extends Thread {

	/**
	 * this class is to be a wrapper for the US sensor, effectively smoothing
	 * out the values and allowing control of the smoothing from an exterior
	 * class, as well as a mean of getting the processed value.
	 * 
	 */
	private static Port usPort;
	private SampleProvider us;
	private float[] usData;

	private LinkedList<Integer> recent;
	private int
	// original distance
	rawDistance,
			// altered distance
			processedDistance,
			// list size, greater list size improves smoothness but reduces
			// robot responsiveness.
			recentListSize,
			// previous average of the list before insertion of new data.
			previousAverage,
			// absolute bounds on the distance value, (set to 0-255) by default.
			upperBound = 255, lowerBound = 0,
			// offset bound values from previousAverage to the next accept
			plusOffset, minusOffset;

	/**
	 * Main Constructor
	 * 
	 * @param recentListSize
	 *            Size of date points to be stored
	 * @param PlusOffset
	 *            Positive offset bound values from previous average
	 * @param MinusOffset
	 *            Minus offset bound values from previous average
	 * @param UpperBound
	 *            Upper absolute bound of sensor
	 * @param LowerBound
	 *            Lower absolute bound of sensor
	 */

	public UltrasonicPoller(int recentListSize, int PlusOffset, int MinusOffset, int UpperBound, int LowerBound) {

		this.recent = new LinkedList<Integer>();

		this.recentListSize = recentListSize;
		this.plusOffset = PlusOffset;
		this.minusOffset = MinusOffset;
		this.upperBound = UpperBound;
		this.lowerBound = LowerBound;
		usPort = Initializer.getUsPort();

		@SuppressWarnings("resource")

		// usSensor is the instance
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		this.us = usSensor.getMode("Distance");

		// SampleProvider usDistance = usSensor.getMode("Distance");
		// usDistance provides samples from this instance
		this.usData = new float[us.sampleSize()];
		// usData is the buffer in which data are returned
		Logger.log("created UltrasonicPoller instance");
	}

	public void run() {
		this.recent.clear();
		while (true) {
			// acquire data
			us.fetchSample(usData, 0);

			// extract from buffer, cast to int
			this.rawDistance = (int) (usData[0] * 100.0);

			processDistance();

			try {
				Thread.sleep(70);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}

	/**
	 * Change parameters of the USSPoller after it had already been instantiated
	 * 
	 * @param recentListSize
	 *            Size of date points to be stored
	 * @param PlusOffset
	 *            Positive offset bound values from previous average
	 * @param MinusOffset
	 *            Minus offset bound values from previous average
	 * @param UpperBound
	 *            Upper absolute bound of sensor
	 * @param LowerBound
	 *            Lower absolute bound of sensor
	 */
	public void setParameters(int recentListSize, int PlusOffset, int MinusOffset, int UpperBound, int LowerBound) {
		this.recentListSize = recentListSize;
		this.plusOffset = PlusOffset;
		this.minusOffset = MinusOffset;
		this.upperBound = UpperBound;
		this.lowerBound = LowerBound;
		Logger.log("Set USPoller parameters to ("+recentListSize+","+PlusOffset+","+MinusOffset+","+UpperBound+","+LowerBound+")");
	}

	/**
	 * Filters past recorded distances and updates ProcessedDistance
	 * 
	 */
	public void processDistance() {
		// the distance is constrained as to remove unpleasant values.
		// note: we use the rawDistance in the first call to min() in order
		// to use the rawDistance value, but not change it.

		processedDistance = Math.min(upperBound, Math.max(lowerBound, rawDistance));

		// use a linked list of size recentListSize to store recent US
		// readings. Every time a new reading is received, it is added to
		// the list, and the oldest reading is removed.

		previousAverage = getAverage(recent);

		// conserving the "original" distance since we will tinker with the
		// distance value.

		/*
		 * The US sensor seems unable to detect short distances when placed at
		 * an angle, causing many unwanted 255cm readings when right next to the
		 * wall. The distance value is limited to the current average +- an
		 * offset. This feature allows for smoothing out the irregular and
		 * "extreme" values while still allowing a change in the average over
		 * consistent readings.
		 * 
		 * note: the constant value added or removed to the currentAverage is
		 * based on experimentation, and might be greater for lower US values to
		 * allow a faster response to walls than to open space.
		 * 
		 */
		if (recent.size() >= 0.5 * recentListSize) {
			processedDistance = Math.min(previousAverage + plusOffset, processedDistance);
			processedDistance = Math.max(processedDistance, previousAverage - minusOffset);
		}

		// we add the processed distance to the recent values list.
		this.recent.addLast(processedDistance);

		// the size of the list is controlled.
		if (recent.size() > recentListSize) {
			recent.removeFirst();
		}
		if (recent.size() == recentListSize) {
			// if the list is full, we set the distance to be equal to the
			// average of the values in the list.
			// (the getAverage function is defined at the end of this page.)
			this.processedDistance = getAverage(recent);
		} else {
			// the list is not yet full, the distance value remains the
			// processed value.

		}

	}

	/**
	 * Find the average value of a list of values
	 * 
	 * 
	 * @param list
	 *            array to find average of
	 * @return average of the list
	 */
	public int getAverage(LinkedList<Integer> list) {
		int result = 0;
		if (list.isEmpty()) {
			return 0;
		}
		for (Integer i : list) {
			result += i;
		}
		return (result) / list.size();
	}

	/**
	 * @return processed distance of sensor
	 */
	public int getProcessedDistance() {
		return this.processedDistance;
	}

	/**
	 * @return raw distance of sensor
	 */
	public int getRawDistance() {
		return this.rawDistance;
	}

	/**
	 * Clears values stored inside sensor poller linked-List (cache)
	 */
	public void clear() {
		this.recent.clear();
	}

	/**
	 * @return current number of values stored in poller
	 */
	public int getCurrentListSize() {
		return this.recent.size();
	}

	/**
	 * @return List size of sensor poller
	 */
	public int getListSize() {
		return this.recentListSize;
	}

	/**
	 * @return If sensor poller recent array is full
	 */
	public boolean isFull() {
		return this.recent.size() == this.recentListSize;
	}

	/** saves the parameters of the USPoller to an array for saving.
	 * @return and array with the settings.
	 */
	public int[] saveParameters() {
		return new int[] { this.recentListSize, this.plusOffset, this.minusOffset, this.upperBound, this.lowerBound };
	}

	/** restores the settings of the USPoller with some previously saved settings.
	 * @param params
	 */
	public void restoreParameters(int[] params) {
		this.setParameters(params[0], params[1], params[2], params[3], params[4]);
	}

}
