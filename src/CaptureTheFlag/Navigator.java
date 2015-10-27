package CaptureTheFlag;


import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator {
	final static int FAST = 100, SLOW = 60, ACCELERATION = 2000;
	// TODO: the USS_SENSOR_OFFSET value is closer to 12 in real life.
	final static double DEG_ERR = 3, CM_ERR = 1.0, USS_SENSOR_OFFSET = 8.8;
	private Odometer odometer;
	private LCD LCD;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	public Navigator(Odometer odo) {
		this.odometer = odo;
		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	public Navigator(Odometer odo,  LCD lcd) {
		this.odometer = odo;
		this.LCD = lcd;
		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * Float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		// while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y -
		// odometer.getY()) > CM_ERR) {
		while (distanceBetween(odometer.getX(), odometer.getY(), x, y) >= CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;

			this.turnTo(minAng, false);

			// set the speeds slower if the robot is close to objective.
			if (distanceBetween(odometer.getX(), odometer.getY(), x, y) < 5) {
				this.setSpeeds(FAST / 2, FAST / 2);
			} else {
				this.setSpeeds(FAST, FAST);
			}

		}
		this.setSpeeds(0, 0);
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		// double error = angle - this.odometer.getAng();
		double error = Odometer.fixDegAngle(Odometer.minimumAngleFromTo(this.odometer.getAng(), angle));

		while (Math.abs(error) > DEG_ERR) {

			// error = angle - this.odometer.getAng();
			error = Odometer.fixDegAngle(Odometer.minimumAngleFromTo(this.odometer.getAng(), angle));
			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}

	// Go foward a set distance in cm
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * distance + odometer.getX(),
				Math.sin(Math.toRadians(this.odometer.getAng())) * distance + odometer.getY());
	}

	// FROM SQUARE DRIVER (LAB 2)
	// Go backwards a set distance in cm (used to grab block)
	public void goBackward(double distance) {
		leftMotor.setSpeed(250);
		rightMotor.setSpeed(250);
		leftMotor.rotate(-convertDistance(2.093, distance), true);
		rightMotor.rotate(-convertDistance(2.093, distance), false);
	}

	// FROM SQUARE DRIVER (LAB 2)
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public static double distanceBetween(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2));
	}

}
