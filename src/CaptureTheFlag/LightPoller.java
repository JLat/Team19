package CaptureTheFlag;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightPoller extends Thread {
	private SensorModes colorSensor1;
	private SensorModes colorSensor2;
	private SampleProvider sensor1, sensor2;
	private float[] colorData1, colorData2;
	public boolean colorChanged, calibrated, newLine;
	private double woodValue, lineDifference = 20, currentValue1, currentValue2;

	/**
	 * Class constructor : makes a new LightPoller
	 * 
	 * @param sampler
	 * @param colorData
	 */
	public LightPoller(Port port1, Port port2, String mode) {
		this.colorSensor1 = new EV3ColorSensor(port1);
		this.colorSensor2 = new EV3ColorSensor(port2);
		this.sensor1 = colorSensor1.getMode(mode);
		this.colorData1 = new float[sensor1.sampleSize()];
		this.sensor2 = colorSensor2.getMode(mode);
		this.colorData2 = new float[sensor2.sampleSize()];
	}

	/**
	 * run() for the Runnable interface of Thread. while the thread is running,
	 * reads and process the data from sampler
	 */
	public void run() {
		while (true) {
			if (!calibrated)
				calibrate();
			sensor1.fetchSample(colorData1, 0);
			currentValue1 = 100*colorData1[0];
			sensor2.fetchSample(colorData2, 0);
			currentValue2 = 100*colorData2[0];
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
			sensor1.fetchSample(colorData1, 0);
			temp += 100 * colorData1[0];
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
	public boolean seesLine1() {
		if (calibrated) {
			return woodValue - currentValue1 > lineDifference;
		}
		return false;
	}
	public boolean seesLine2() {
		if (calibrated) {
			return woodValue - currentValue2 > lineDifference;
		}
		return false;
	}

}