package CaptureTheFlag;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * LightPoller class : Extends Thread and fetches the RGB values from a colorSensor
 * 					   every 20 ms. 
 *
 */
public class LightPoller extends Thread {
	private SensorModes colorSensor1;
	private SensorModes colorSensor2;
	private SampleProvider sensor1, sensor2;
	private float[] colorData1, colorData2;
	public boolean colorChanged, calibrated, newLine;
	//TODO: are you sure that this value for lineDifference is good ? it seems a little high to me.
	private double woodValue, lineDifference = 25, currentValue1, currentValue2;

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
		Logger.log("Created LightPoller instance");
	}

	/**
	 * run() for the Runnable interface of Thread. while the thread is running,
	 * reads and process the data from sampler
	 */
	public void run() {
		// calibrate only once! not at every reading.
		if(!this.calibrated){
			calibrate();
		}
		
		while (true) {
			sensor1.fetchSample(colorData1, 0);
			currentValue1 = 100*colorData1[0];
			sensor2.fetchSample(colorData2, 0);
			currentValue2 = 100*colorData2[0];
			try {
				Thread.sleep(20);
			} catch (Exception e) {

			}
		}

	}

	/** takes in 10 readings and sets the floor (wood) value accordingly.
	 * @NOTE: this assumes that the sensor is facing wood, and not a line.
	 * 
	 */
	public void calibrate() {
		Logger.log("Starting LightPoller calibration");
		int calibrationCounter = 0;
		double temp = 0;
		while (calibrationCounter < 10) {
			sensor1.fetchSample(colorData1, 0);
			temp += 100 * colorData1[0];
			calibrationCounter++;
		}
		this.woodValue = temp / 10;
		this.calibrated = true;
		Logger.log("LightPoller calibrated. Wood light value is "+(int)this.woodValue);
	}

	/**
	 * Returns the boolean value of whether or not a color change has been
	 * detected by comparing the previous sample stored in a stack to the
	 * current one.
	 * @return
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