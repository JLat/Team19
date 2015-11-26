package CaptureTheFlag;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Delay;

public class Navigation extends Thread {
	private static Odometer odo;
	private static EV3LargeRegulatedMotor leftMotor, rightMotor;
	private static EV3MediumRegulatedMotor sensorMotor;
	private static double deltaX, deltaY, xfinal, yfinal;
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 9.85, lightSensorOffset = 4.6;
	// Changed the motorLow to 150 it was previously 100
	public static final int motorHigh = 250, motorLow = 150, leftAngle = 250, rightAngle = -250, sensorHigh = 500,
			sensorLow = 120, smallLeftAngle = 84, smallRightAngle = -84;
	private static UltrasonicPoller usPoller;
	private static LightPoller lightPoller;
	public static boolean isNavigating = false;

	/**
	 * Navigator constructor
	 * 
	 * @param odo
	 */
	public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		Navigation.odo = odo;
		Navigation.leftMotor = leftMotor;
		Navigation.rightMotor = rightMotor;
		leftMotor.setAcceleration(1200);
		rightMotor.setAcceleration(1200);
		usPoller = Initializer.getUsPoller();
		sensorMotor = Initializer.getSensorMotor();
	}

	public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			LightPoller lightPoller) {
		Navigation.odo = odo;
		Navigation.leftMotor = leftMotor;
		Navigation.rightMotor = rightMotor;
		leftMotor.setAcceleration(1200);
		rightMotor.setAcceleration(1200);
		usPoller = Initializer.getUsPoller();
		sensorMotor = Initializer.getSensorMotor();
		Navigation.lightPoller = lightPoller;
	}

	public Odometer getOdometer() {
		return Navigation.odo;
	}

	/**
	 * Functions to set the motor speeds jointly
	 * 
	 * @param lSpd
	 * @param rSpd
	 */
	public synchronized void setSpeeds(float lSpd, float rSpd) {
		Navigation.leftMotor.setSpeed(lSpd);
		Navigation.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			Navigation.leftMotor.backward();
		else
			Navigation.leftMotor.forward();
		if (rSpd < 0)
			Navigation.rightMotor.backward();
		else
			Navigation.rightMotor.forward();
	}

	public synchronized void setSpeeds(int lSpd, int rSpd) {
		Navigation.leftMotor.setSpeed(lSpd);
		Navigation.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			Navigation.leftMotor.backward();
		else
			Navigation.leftMotor.forward();
		if (rSpd < 0)
			Navigation.rightMotor.backward();
		else
			Navigation.rightMotor.forward();
	}

	/**
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 * 
	 * @param xfinal
	 * @param yfinal
	 */
	public void travelTo(double xfinal, double yfinal) {
		isNavigating = true;
		double x = odo.getX(); // current positions
		double y = odo.getY();
		deltaX = xfinal - x; // difference between current and final positions
		deltaY = yfinal - y;

		Navigation.xfinal = xfinal;
		Navigation.yfinal = yfinal;

		double newDeg, distance;

		newDeg = calculateNewAngle(xfinal, yfinal, deltaX, deltaY); // absolute
																	// Theta
																	// that it
																	// needs to
																	// turn to
		turnTo(newDeg, false);
		isNavigating = true;
		distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY); // calculates
																	// distance
																	// from
																	// final
																	// position

		leftMotor.setSpeed(motorHigh);
		rightMotor.setSpeed(motorHigh);
		leftMotor.rotate(convertDistance(WHEEL_RADIUS, distance), true); // tells
																			// motor
																			// how
																			// many
																			// times
																			// each
																			// wheel
																			// should
																			// turn
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, distance), false); // convertDistance
																			// is
																			// converting
																			// distance
																			// to
																			// angle
																			// (number
																			// of
																			// rotations)
		isNavigating = false;
	}

	public void travelToAxis(double xfinal, double yfinal) {
		Navigation.xfinal = xfinal;
		Navigation.yfinal = yfinal;
		while (true) {
			double x = odo.getX(); // current positions
			double y = odo.getY();
			double xDirection = -1;
			double yDirection = -1;
			double xEmptySpace = 0;
			double yEmptySpace = 0;
			double xFrontEmptySpace = 0;
			double yFrontEmptySpace = 0;
			double xLeftEmptySpace = 0;
			double yLeftEmptySpace = 0;
			double xRightEmptySpace = 0;
			double yRightEmptySpace = 0;
			// compare current position and destination
			if (yfinal > y + 0.5) {
				yDirection = 0;
			} else if (yfinal < y - 0.5) {
				yDirection = Math.PI;
			}
			if (xfinal > x + 0.5) {
				xDirection = Math.PI / 2;
			} else if (xfinal < x - 0.5) {
				xDirection = Math.PI * 3 / 2;
			}
			// choose which way to go
			// normal condition
			if (xDirection >= 0) {
				this.turnTo(xDirection, true);
				sensorMotor.rotateTo(0, false);
				Delay.msDelay(50);
				xFrontEmptySpace = usPoller.getProcessedDistance();
				sensorMotor.rotateTo(smallLeftAngle, false);
				Delay.msDelay(50);
				xLeftEmptySpace = usPoller.getProcessedDistance();
				sensorMotor.rotateTo(smallRightAngle, false);
				Delay.msDelay(50);
				xRightEmptySpace = usPoller.getProcessedDistance();
				xEmptySpace = Math.min(xFrontEmptySpace,
						Math.min(xLeftEmptySpace * 2 / 1.732, xRightEmptySpace * 2 / 1.732));
			}
			if (yDirection >= 0) {
				this.turnTo(yDirection, true);
				sensorMotor.rotateTo(0, false);
				Delay.msDelay(50);
				yFrontEmptySpace = usPoller.getProcessedDistance();
				sensorMotor.rotateTo(smallLeftAngle, false);
				Delay.msDelay(50);
				yLeftEmptySpace = usPoller.getProcessedDistance();
				sensorMotor.rotateTo(smallRightAngle, false);
				Delay.msDelay(50);
				yRightEmptySpace = usPoller.getProcessedDistance();
				yEmptySpace = Math.min(yFrontEmptySpace,
						Math.min(yLeftEmptySpace * 2 / 1.732, yRightEmptySpace * 2 / 1.732));
			}
			Initializer.getDisplay().addInfo("xEmpty", xEmptySpace);
			Initializer.getDisplay().addInfo("yEmpty", yEmptySpace);
			sensorMotor.rotateTo(0, false);
			// when destination is on the same axis and there is a object on the
			// way
			boolean yBlocked = (xfinal > x - 1) && (xfinal < x + 1) && (yEmptySpace < 28) && (yEmptySpace > 0);
			boolean xBlocked = (yfinal > y - 1) && (yfinal < y + 1) && (xEmptySpace < 28) && (xEmptySpace > 0);
			if (xBlocked || yBlocked) {
				Sound.beep();
				if (xBlocked) {
					turnTo(xDirection, true);
				} else {
					turnTo(yDirection, true);
				}
				sensorMotor.setSpeed(sensorHigh);
				sensorMotor.rotateTo(leftAngle, false);
				double leftSpace = usPoller.getProcessedDistance();
				sensorMotor.rotateTo(rightAngle, false);
				double rightSpace = usPoller.getProcessedDistance();
				if (leftSpace > rightSpace) {
					turnTo((odo.getTheta() - Math.PI / 2 + 2 * Math.PI) % (2 * Math.PI), true);
					sensorMotor.rotateTo(rightAngle, false);
					this.setSpeeds(motorHigh, motorHigh);
				} else {
					turnTo((odo.getTheta() + Math.PI / 2 + 2 * Math.PI) % (2 * Math.PI), true);
					sensorMotor.rotateTo(leftAngle, false);
					this.setSpeeds(motorHigh, motorHigh);
				}

				while (usPoller.getProcessedDistance() < 25) {
				}
				this.setSpeeds(0, 0);
				sensorMotor.rotateTo(0);
				goForward(30);
				if (xBlocked) {
					turnTo(xDirection, true);
				} else {
					turnTo(yDirection, true);
				}
				goForward(30);
			} else {
				Sound.twoBeeps();
				if (xEmptySpace > yEmptySpace) {
					this.turnTo(xDirection, true);
					// this.goStraight(Math.min(Math.abs(xfinal - x),
					// xEmptySpace));
					travelTailsWithCorrection(Math.min((int) (Math.abs(xfinal - x) / 30), (int) (xEmptySpace / 30)));
				} else {
					this.turnTo(yDirection, true);
					// this.goStraight(Math.min(Math.abs(yfinal - y),
					// yEmptySpace));
					travelTailsWithCorrection(Math.min((int) (Math.abs(yfinal - y) / 30), (int) (yEmptySpace / 30)));
				}
				// while (usPoller.getProcessedDistance() > 15 &&
				// this.isMoving()) {
				//
				// }
				this.setSpeeds(0, 0);
			}
			if ((Math.abs(odo.getX() - xfinal) < 0.5) && (Math.abs(odo.getY() - yfinal) < 0.5)) {
				Sound.buzz();
				break;
			}
		}
	}

	public void travelToReverse(double xfinal, double yfinal) {
		isNavigating = true;
		double x = odo.getX(); /* current positions */
		double y = odo.getY();
		double deltaX = xfinal
				- x; /* difference between current and final positions */
		double deltaY = yfinal - y;

		double newDeg, newDegReverse, distance;

		newDeg = calculateNewAngle(xfinal, yfinal, deltaX,
				deltaY); /* absolute Theta that it needs to turn to */
		newDegReverse = (newDeg + Math.PI) % (2 * Math.PI);
		turnTo(newDegReverse,
				true); /* since we pass a degree into this function, */
		/* we convert it into radius */
		isNavigating = true;
		distance = Math.sqrt(deltaX * deltaX + deltaY
				* deltaY); /* calculates distance from final position */

		leftMotor.setSpeed(motorLow);
		rightMotor.setSpeed(motorLow);
		leftMotor.rotate(-convertDistance(WHEEL_RADIUS, distance),
				true); /* tells motor how many times each wheel should turn */
		rightMotor.rotate(-convertDistance(WHEEL_RADIUS, distance),
				true); /* convertDistance is converting distance to */
		/* angle (number of rotations) */
	}

	/**
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 * 
	 * @param angle
	 * @param stop
	 */
	public synchronized void turnTo(double thetaInRadians, boolean stop) {
		double theta = thetaInRadians;
		double turnDeg, currentDeg = odo.getTheta(); // initiate variables
		boolean isTurningClockwise;
		int leftAngle, rightAngle;

		isNavigating = true; // the method has been called therefore
								// isNavigating is set to true

		if ((2 * Math.PI + theta - currentDeg)
				% (2 * Math.PI) < Math.PI) { /* turn clockwise */
			turnDeg = (2 * Math.PI + theta - currentDeg) % (2 * Math.PI);
			isTurningClockwise = true;
		} else { /* turn counter clockwise */
			turnDeg = (2 * Math.PI - theta + currentDeg) % (2 * Math.PI);
			isTurningClockwise = false;
		}
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		Delay.msDelay(5);
		leftMotor.setSpeed(motorLow);
		rightMotor.setSpeed(motorLow);

		leftAngle = convertAngle(WHEEL_RADIUS, TRACK, turnDeg); // converts
																// robots
																// turning
																// degrees to
																// the wheels
																// turn degree
		rightAngle = leftAngle;

		if (isTurningClockwise)
			rightAngle *= -1; // orient directions of wheel rotation
		else
			leftAngle *= -1;

		leftMotor.rotate(leftAngle, true); // turn
		rightMotor.rotate(rightAngle, false);
		isNavigating = false;
		if (stop) {
			this.setSpeeds(0, 0);
		}
	}

	/**
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 * 
	 * @param angle
	 * @param stop
	 */
	public void turnToAngle(double thetaInDegrees, boolean stop) {
		double theta = Math.toRadians(thetaInDegrees);
		turnTo(theta, stop);
	}

	/**
	 * TurnBy function which takes an angle and boolean as arguments the angle
	 * determines what degree it should turn and the boolean controls whether or
	 * not to stop the motors when the turn is completed
	 * 
	 * @param angle
	 * @param stop
	 */
	public void turnDegrees(double angle, boolean stop) {

	}

	public void turnRadians(double angle, boolean stop) {

	}

	/*
	 * Based on current and final positions, what is the absolute angle of the
	 * final position
	 */
	public double calculateNewAngle(double x, double y, double dx, double dy) {
		if (dx >= 0) {
			if (dy >= 0) {
				return Math.atan(Math.abs(dx) / Math.abs(dy));
			} else {
				return Math.PI / 2 + Math.atan(Math.abs(dy) / Math.abs(dx));
			}
		} else {
			if (dy >= 0) {
				return Math.PI * 3 / 2 + Math.atan(Math.abs(dy) / Math.abs(dx));
			} else {
				return Math.PI + Math.atan(Math.abs(dx) / Math.abs(dy));
			}
		}
	}

	private int convertDistance(double radius, double distance) { // converts
																	// distance
																	// to angle
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public boolean isMoving() {
		if (leftMotor.isMoving() || rightMotor.isMoving()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Go forward/backward a set distance in cm
	 * 
	 * @param distance
	 */
	public synchronized void goForward(double distance) {
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		Delay.msDelay(5);
		leftMotor.setSpeed(motorHigh);
		rightMotor.setSpeed(motorHigh);
		int wheelAngle = convertDistance(WHEEL_RADIUS, distance);
		leftMotor.rotate(wheelAngle, true);
		rightMotor.rotate(wheelAngle, false);
	}

	public synchronized void goStraight(double distance) {
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		Delay.msDelay(5);
		leftMotor.setSpeed(motorHigh);
		rightMotor.setSpeed(motorHigh);
		int wheelAngle = convertDistance(WHEEL_RADIUS, distance);
		leftMotor.rotate(wheelAngle, true);
		rightMotor.rotate(wheelAngle, true);
	}

	public void travelTailsWithCorrection(int numberOfBlocks) {
		for (int i = 0; i < numberOfBlocks; i++) {

			double tempTheta = odo.getTheta();
			if (tempTheta >= Math.toRadians(45) && tempTheta < Math.toRadians(135)) {
				tempTheta = Math.toRadians(90);
			} else if (tempTheta >= Math.toRadians(135) && tempTheta < Math.toRadians(225)) {
				tempTheta = Math.toRadians(180);
			} else if (tempTheta >= Math.toRadians(225) && tempTheta < Math.toRadians(315)) {
				tempTheta = Math.toRadians(270);
			} else {
				tempTheta = 0;
			}
			// correction

			setSpeeds(motorHigh, motorHigh);
			boolean leftSeeLineAlready = false, rightSeeLineAlready = false;
			while (!(lightPoller.seesLine2() && leftSeeLineAlready)
					|| !(lightPoller.seesLine1() && rightSeeLineAlready)) {
				if (lightPoller.seesLine2() && !leftSeeLineAlready) {
					leftSeeLineAlready = true;
					while (!lightPoller.seesLine1() && !rightSeeLineAlready) {
						setSpeeds(0, motorHigh);
					}
					setSpeeds(0, 0);
					rightSeeLineAlready = true;
				}

				if (lightPoller.seesLine1() && !rightSeeLineAlready) {
					rightSeeLineAlready = true;
					while (!lightPoller.seesLine2() && !leftSeeLineAlready) {
						setSpeeds(motorHigh, 0);
					}
					setSpeeds(0, 0);
					rightSeeLineAlready = true;
				}
				if (leftSeeLineAlready && rightSeeLineAlready) {
					Sound.twoBeeps();
					break;
				}

			}
			goForward(lightSensorOffset);
			int lineY = (int) (odo.getY() + 15) / 30;
			int lineX = (int) (odo.getX() + 15) / 30;
			odo.setY(lineY * 30);
			odo.setX(lineX * 30);
			odo.setTheta(tempTheta);

		}
	}

	/**
	 * TurnBy function which takes an angle and boolean as arguments the angle
	 * determines what degree it should turn and the boolean controls whether or
	 * not to stop the motors when the turn is completed
	 * 
	 * @param angle
	 * @param stop
	 */
	public void turnBy(double angle, boolean stop) {
		leftMotor.setSpeed(motorLow);
		rightMotor.setSpeed(motorLow);
		// the 1.055 is there to improve the accuracy of the turns
		leftMotor.rotate(convertAngle(WHEEL_RADIUS, 1.055 * TRACK, angle), true);
		rightMotor.rotate(-convertAngle(WHEEL_RADIUS, 1.055 * TRACK, angle), false);
		if (stop) {
			this.setSpeeds(0, 0);
		}

	}

	public void turn(String direction) {
		leftMotor.setSpeed(motorLow);
		rightMotor.setSpeed(motorLow);
		if (direction.toUpperCase() == "CLOCKWISE") {
			leftMotor.backward();
			rightMotor.forward();
		} else {
			leftMotor.forward();
			rightMotor.backward();
		}
	}

	// /**
	// * Go backwards a set distance in cm (used to grab block)
	// * @param distance
	// */
	// public void goBackward(double distance) {
	//
	// }

	/**
	 * convertAngle function takes a angle as input and calculate/return the
	 * degrees that wheel should turn based on that input
	 * 
	 * @param turningAngle
	 * @return
	 */
	private int convertAngle(double radius, double width, double angle) { // converts
																			// rotations
																			// of
																			// robot
																			// to
		return convertDistance(radius, Math.PI * width * angle / (2 * Math.PI)); // the
																					// required
																					// angle
																					// rotation
																					// of
																					// the
																					// wheel
	}

	public static EV3LargeRegulatedMotor getLeftMotor() { // get left and right
															// motor
		return leftMotor;
	}

	public static EV3LargeRegulatedMotor getRightMotor() {
		return rightMotor;
	}

	public static double getRadius() { // get TRACK and RADIUS value
		return WHEEL_RADIUS;
	}

	public static double getTrack() {
		return TRACK;
	}

	public static double getX() {
		return odo.getX();
	}

	public static double getY() {
		return odo.getY();
	}

	public static double getThetaDegrees() {
		return odo.getThetaDegrees();
	}

	public static double getThetaRadians() {
		return odo.getTheta();
	}

	public static double getDeltaX() {
		return deltaX;
	}

	public static double getDeltaY() {
		return deltaY;
	}

	public static double getXFinal() {
		return Navigation.xfinal;
	}

	public static double getYFinal() {
		return Navigation.yfinal;
	}

}
