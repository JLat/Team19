package CaptureTheFlag;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.utility.Delay;

/**
 * @author Fabrice_Normandin Class responsible for the Ultrasonic Localization
 *         of the robot.
 */
public class USLocalizer {
	public static float ROTATION_SPEED = 100;

	private Odometer odo;
	private Navigation nav;
	private UltrasonicPoller uss;
	private LCDdisplay lcd;
	private int dist;
	private boolean facingWall;
	private int minAngleBetweenAandB = 150;
	private double angleA, angleB, delta, sensorOffset = 14.5;

	public USLocalizer(Navigation nav, Odometer odo, UltrasonicPoller uss, LCDdisplay LCD) {
		this.odo = odo;
		//starts the odometer if it hasn't started already.
		if (!this.odo.isAlive()) {
			this.odo.start();
		}
		//starts the USSpoller if it hasn't started already.
		this.uss = uss;
		if (!this.uss.isAlive()) {
			this.uss.start();
		}
		this.nav = nav;

		this.lcd = LCD;
		Logger.log("Created USLocalizer instance");
	}
	
	/**
	 * method that localizes the robot using the USSpoller class.
	 * 
	 * @param Cap: Value for the wall distance. (Strongly suggest leaving this at 30).
	 *            the distance at which a wall is considered detected.
	 */
	public void doLocalization(int Cap) {
		Logger.log("Starting USLocalization");
		// keep the previous parameters of the USSpoller into an int array, in
		// order to restore them after localization is done.
		int[] savedParameters = uss.saveParameters();

		//Old parameters: uss.setParameters(5, 15, 15, 50, 0);
		//to speed up the process, use a smaller filter size of 10
		uss.setParameters(5, 10, 15, 50, 0);
		
		lcd.addInfo("distance: ");
//		pause();
		
		
		// the distance at which a wall is considered detected.
		dist = Cap;

		// Allowing a small delay for the US sensor to initialize properly
		// before starting to rotate the robot.
		Delay.msDelay(150);
		nav.setSpeeds(100, -100);
		
		
		// if the robot initially faces a wall
		if (uss.getProcessedDistance() < dist) {
			lcd.addInfo("Facing wall");
			locateFromWall();
		} else {
			lcd.addInfo("Facing open");
			locateFromOpen();
		}
		
		//processes the values and calculates the displacement.
		processValues();
		
		// Travel to a point.
		nav.travelTo(0, 3);
		nav.turnTo(0, true);
		
		
		//pause();

		// restore the settings of the USSsensor.
		uss.restoreParameters(savedParameters);
		Logger.log("USLocalization complete");
	}

	private void locateFromWall() {
		Logger.log("Facing a wall at start of USLocalization");
		facingWall = true;
		// Even if this procedure assumes that the robot is facing a wall, we
		// make sure here:
		while (uss.getProcessedDistance() > 1.1 * dist)
			;
		// rotate until open space
		while (uss.getProcessedDistance() < 1.1 * dist)
			;
		angleA = odo.getThetaDegrees();
		// lcd.addInfo("Angle A: ", angleA);
		LocalEV3.get().getAudio().systemSound(0);

		// rotate until facing a wall
		angleB = angleA;

		// keep looking for the B angle until you make a full turn.
		while (angleTraveledFromTo(angleA, angleB) < minAngleBetweenAandB) {
			// rotate until a wall is encountered.
			while (uss.getProcessedDistance() > 0.9 * dist)
				;
			angleB = odo.getThetaDegrees();
		}
		LocalEV3.get().getAudio().systemSound(0);
		// lcd.addInfo("angle B: ", angleB);
		nav.setSpeeds(0, 0);
	}

	private void locateFromOpen() {
		Logger.log("Facing open space at start of USLocalization");
		// travel to a wall, call it A, find the next open space, call it B. If
		// they appear too close to each other, reset.
		// assumes that the robot is facing open space.
		while (uss.getProcessedDistance() < 1.1 * dist)
			;

		boolean repeat = true;
		while (repeat) {
			// rotate until a wall is detected.
			while (uss.getProcessedDistance() > 0.9 * dist)
				;
			angleA = odo.getThetaDegrees();

			LocalEV3.get().getAudio().systemSound(0);

			// start looking for angle B (the rising edge).
			angleB = angleA;
			while (uss.getProcessedDistance() < 1.1 * dist)
				;
			angleB = odo.getThetaDegrees();
			LocalEV3.get().getAudio().systemSound(0);

			repeat = angleTraveledFromTo(angleA, angleB) < minAngleBetweenAandB;

		}
		// lcd.addInfo("Angle A: ", angleA);
		// lcd.addInfo("Angle B: ", angleB);
		nav.setSpeeds(0, 0);

	}

	/**
	 * calculates the delta value (displacement in degrees from reference y
	 * axis) using the angleA and angleB values provided by the two possible
	 * procedures.
	 */
	private void processValues() {

		// calculates the delta value based on the procedure used. This allows
		// for a more custom calibration for each possible case. (it just so
		// happens that the values are interchanged. need to investigate that)
		if (facingWall) {
			if (angleA < angleB) {
				delta = 60 - (angleA + angleB) / 2;
				//lcd.addInfo("Delta: ", delta);
			} else {
				delta = 235 - (angleA + angleB) / 2;
				//lcd.addInfo("delta: ", delta);
			}
		} else {
			if (angleA < angleB) {
				delta = 235 - (angleA + angleB) / 2;
				//lcd.addInfo("Delta: ", delta);
			} else {
				delta = 60 - (angleA + angleB) / 2;
				//lcd.addInfo("delta: ", delta);
			}
		}

		// correct the odometer's heading.
		double currentHeading = odo.getThetaDegrees();
		
		Logger.log("Angle error is estimated to be "+(int)delta+" degrees");
		
		odo.setThetaDegrees(currentHeading + delta);

		// in order to minimize the time required for localization, the
		// coordinate checked is the
		// closest one to the last position of the robot.
		if (facingWall) {
			measureY();
			measureX();
		} else {
			measureX();
			measureY();
		}
		Logger.log("Approximate position is ("+odo.getX()+","+odo.getY()+")");

	}

	/**
	 * measures the X coordinate by rotating the robot to face the Y-axis wall.
	 * Sets the Odometer's x value accordingly.
	 */
	public void measureX() {
		// measure the X.
		nav.turnToAngle(270, true);
		Delay.msDelay(250);
		odo.setX(-30 + sensorOffset + uss.getProcessedDistance());
	}

	/**
	 * measures the Y coordinate by rotating the robot to face the X-axis wall.
	 * Sets the Odometer's Y value accordingly.
	 */
	public void measureY() {
		// Measure the Y.
		nav.turnToAngle(180, true);
		Delay.msDelay(250);
		odo.setY(-30 + sensorOffset + uss.getProcessedDistance());
	}

	/**
	 * small helper method to pause the routine. Pressing any key except the
	 * ESCAPE key will cause the program to continue. pressing ESCAPE exits.
	 */
	public void pause() {
		int choice = Button.waitForAnyPress();
		if (choice == Button.ID_ESCAPE)
			System.exit(0);
	}

	/**
	 * returns the clockwise difference between two angles (in degrees)
	 * 
	 * @param angle1
	 *            angle 1 in degrees
	 * @param angle2
	 *            angle 2 in degrees
	 * @return the clockwise angle from angle1 to angle2 in degrees.
	 */
	public double angleTraveledFromTo(double angle1, double angle2) {
		// assumes clockwise motion (growing angle)
		if (angle1 == angle2) {
			return 0;
		} else if (angle2 > angle1) {
			return angle2 - angle1;
		} else {
			return 360 - (angle1 - angle2);
		}
	}
}
