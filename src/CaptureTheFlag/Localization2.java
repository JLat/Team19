package CaptureTheFlag;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.utility.Delay;

public class Localization2 {
	public static float ROTATION_SPEED = 60;

	private Odometer odo;
	private Navigation nav;
	private UltrasonicPoller uss;
	private LCDdisplay lcd;
	private int distanceCap;
	private double detectionRatio = 0.7;

	public Localization2(Navigation nav, Odometer odo, UltrasonicPoller uss, LCDdisplay LCD) {
		this.odo = odo;
		if (!this.odo.isAlive()) {
			this.odo.start();
		}
		this.uss = uss;
		if (!this.uss.isAlive()) {
			this.uss.start();
		}
		this.nav = nav;

		this.lcd = LCD;

	}

	/**
	 * method that localizes the robot using the USSpoller class.
	 * 
	 * @param Cap:
	 *            the distance at which a wall is considered detected.
	 */
	public void doLocalization(int Cap) {

		// keep the previous parameters of the USSpoller into an int array, in
		// order to restore them after localization is done.
		int[] savedParameters = uss.saveParameters();

		uss.setParameters(10, 5, 5, 50, 0);

		// the distance at which a wall is considered detected.
		distanceCap = Cap;
		double[] pos = new double[3];
		double angleA, angleB, delta;
		lcd.addInfo("distance: ");

		// start rotating the robot.
		nav.setSpeeds(30, -30);
		Delay.msDelay(500);

		if (uss.getProcessedDistance() < distanceCap) {

			lcd.addInfo("Facing wall");
			locateFromWall();

		} else {

			lcd.addInfo("Facing open");
			locateFromOpen();
		}
		pause();

		// restore the settings of the USSsensor.
		uss.restoreParameters(savedParameters);
	}

	private void locateFromWall() {

		// rotate until open space
		while (uss.getProcessedDistance() < 1.2 * distanceCap);
		double angleA = odo.getThetaDegrees();
		lcd.addInfo("Angle A: ", angleA);

		// rotate until facing a wall
		double angleB;
		while(Math.abs(angleDifference(angleA,odo.getThetaDegrees()))<100){
			while (uss.getProcessedDistance() > 0.8 * distanceCap);
			angleB = odo.getThetaDegrees();
		}
		
		angleB = odo.getThetaDegrees();
		lcd.addInfo("Angle B: ", angleB);
		pause();

	}

	private void locateFromOpen() {
		pause();
	}

	public void pause() {
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}

	/**
	 * returns the signed difference between two angles( in degrees)
	 * 
	 * @param angle1
	 *            angle 1 in degrees
	 * @param angle2
	 *            angle 2 in degrees
	 * @return
	 */
	public double angleDifference(double angle1, double angle2) {
		double difference = angle1 - angle2;
		double result = Math.abs(angle1 - angle2) % 360;
		if (result > 180) {
			result = 360 - result;
		}
		int sign = (difference >= 0 && difference <= 180) || (difference <= -180 && difference >= -360) ? 1 : -1;
		result *= sign;
		return result;
	}
}
