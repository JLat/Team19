/*
 * Odometer.java
 */

package CaptureTheFlag;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Odometer class: Extends Thread to update the (x,y) position and heading theta of the robot every 25 ms
 *
 */
public class Odometer extends Thread {
	// robot position
	private double x, y, theta, thetaDeg;

	// constant
	public static double WB = Navigation.TRACK;
	public static double WR = Navigation.WHEEL_RADIUS;

	// variables
	public static int lastTachoL;// Tacho L at last sample
	public static int lastTachoR;// Tacho R at last sample
	public static int nowTachoL;// Current tacho L
	public static int nowTachoR;// Current tacho R

	// Resources
	static TextLCD t = LocalEV3.get().getTextLCD();
	private EV3LargeRegulatedMotor leftMotor; // L
	private EV3LargeRegulatedMotor rightMotor; // L

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	/**
	 * Class constructor
	 * 
	 * @param leftmotor
	 * @param rightmotor
	 */
	public Odometer(EV3LargeRegulatedMotor leftmotor,
			EV3LargeRegulatedMotor rightmotor) {
		x = 0.0; //initializing values to zero
		y = 0.0;
		theta = 0.0;
		lock = new Object();
		this.leftMotor = leftmotor; //initializing motors
		this.rightMotor = rightmotor;

	}

	/**
	 * run method (required for Thread)
	 */
	public void run() {
		long updateStart, updateEnd;
		double distL, distR, deltaD, deltaT, dX, dY;

		while (true) {
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here

			nowTachoL = leftMotor.getTachoCount(); // get tacho counts
			nowTachoR = rightMotor.getTachoCount();
			
			distL = Math.PI * WR * (nowTachoL - lastTachoL) / 180; // compute left wheel displacement												
			distR = Math.PI * WR * (nowTachoR - lastTachoR) / 180; // compute right wheel displacement
			
			lastTachoL = nowTachoL; // save tacho counts for next iteration
			lastTachoR = nowTachoR;
			
			deltaD = 0.5 * (distL + distR); // compute displacement of vehicle (center of track)
			deltaT = (distL - distR) / WB; // compute change in heading

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				
				theta += deltaT;
				if (theta >= Math.PI*2) { //creating upper bound on theta at 360
					theta = theta - Math.PI*2;
				}
				if (theta < 0) { //creating lower bound on theta at 0
					theta = Math.PI*2 + theta;
				}
				
				dX = deltaD * Math.sin(theta); // compute X component of displacement
				dY = deltaD * Math.cos(theta); // compute Y component of displacement
				
				x = x + dX; //update x position
				y = y + dY; //update y position
				thetaDeg = theta * 180/Math.PI; //convert theta into degrees to display onscreen
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	/**
	 * Get methods to access position (x,y) and heading theta
	 * 
	 * @param position
	 * @param update
	 */
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = thetaDeg; //display theta in degrees on screen
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}
	public double getThetaDegrees() {
		double result;

		synchronized (lock) {
			result = Math.toDegrees(theta);
		}

		return result;
	}

	/**
	 * Set methods to modify positions (x,y) and heading theta
	 * 
	 * @param position
	 * @param update
	 */
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2]; //positions are actually only set in radians
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}
	

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
	public void setThetaDegrees(double thetaDegrees) {
		synchronized (lock) {
			this.theta = Math.toRadians(thetaDegrees);
		}
	}
}