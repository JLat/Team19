package CaptureTheFlag;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightPoller extends Thread {
	private SensorModes colorSensor;
	private SampleProvider sensor;
	private float[] colorData;
	public boolean colorChanged, calibrated, newLine;
	private double woodValue, lineDifference = 30, currentValue;

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
	 * run() for the Runnable interface of Thread. while the thread is running,
	 * reads and process the data from sampler
	 */
	public void run() {
		while (true) {
			if (!calibrated)
				calibrate();
			sensor.fetchSample(colorData, 0);
			currentValue = 100*colorData[0];
			try {
				Thread.sleep(50);
			} catch (Exception e) {

			}
		}

	}

	public void calibrate() {
		int calibrationCounter = 0;
		double temp = 0;
		while (calibrationCounter < 10) {
			sensor.fetchSample(colorData, 0);
			temp += 100 * colorData[0];
			calibrationCounter++;
		}
		this.woodValue = temp / 10;
		this.calibrated = true;
	}

	/*
	 * Returns the boolean value of whether or not a color change has been
	 * detected by comparing the previous sample stored in a stack to the
	 * current one.
	 */
	public boolean seesLine() {
		if (calibrated) {
			return woodValue - currentValue > lineDifference;
		}
		return false;
	}

}
