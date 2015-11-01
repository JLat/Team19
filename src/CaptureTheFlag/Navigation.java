package CaptureTheFlag;


import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	static int FAST, SLOW;
	private double degError, cmError;
	private Odometer odo;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	/**
	 * Navigator constructor
	 * @param odo
	 */
	public Navigation (Odometer odo) {
		
	}


	/**
	 * Functions to set the motor speeds jointly
	 * @param lSpd
	 * @param rSpd
	 */
	public void setSpeeds(int lSpd, int rSpd) {

	}

	/**
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 * @param x
	 * @param y
	 */
	public void travelTo(double x, double y) {

	}

	/**
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 * @param angle
	 * @param stop
	 */
	public void turnTo(double angle, boolean stop) {

	}
	
	/**
	 * TurnBy function which takes an angle and boolean as arguments 
	 * the angle determines what degree it should turn and the boolean controls 
	 *  whether or not to stop the motors when the turn is completed
	 * @param angle
	 * @param stop
	 */
	public void turnBy(double angle, boolean stop) {

	}

	/**
	 * Go foward a set distance in cm
	 * @param distance
	 */
	public void goForward(double distance) {

	}

	/**
	 * Go backwards a set distance in cm (used to grab block)
	 * @param distance
	 */
	public void goBackward(double distance) {
		
	}
	
	/**
	 * convertAngle function takes a angle as input and calculate/return
	 * the degrees that wheel should turn based on that input
	 * @param turningAngle
	 * @return
	 */
	private static int convertAngle(double turningAngle) {
		int angle = 0;
		return angle;
	}

}
