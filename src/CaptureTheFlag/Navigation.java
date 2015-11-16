package CaptureTheFlag;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	static int FAST = 300, SLOW = 40, ACCELERATION = 3000;
	private double degError = 3.0, cmError = 1.5;
	private static Odometer odo;
	private static EV3LargeRegulatedMotor leftMotor, rightMotor;
	private static double x, y, deltaX, deltaY, xfinal,yfinal;
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 9.85;
	public static final int motorHigh = 250;
	public static final int motorLow = 150;

	/**
	 * Navigator constructor
	 * @param odo
	 */
	public Navigation (Odometer odo, EV3LargeRegulatedMotor leftmotor,
								     EV3LargeRegulatedMotor rightmotor) {
		Navigation.odo = odo;
		Navigation.leftMotor = leftmotor;
		Navigation.rightMotor = leftmotor;
		
		Navigation.leftMotor.setAcceleration(ACCELERATION);
		Navigation.rightMotor.setAcceleration(ACCELERATION);
	}

	public Odometer getOdometer() {
		return Navigation.odo;
	}

	/**
	 * Functions to set the motor speeds jointly
	 * @param lSpd
	 * @param rSpd
	 */
	public void setSpeeds(int lSpd, int rSpd) {
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
	 * @param xfinal
	 * @param yfinal
	 */
	public void travelTo(double xfinal, double yfinal) {
		x=odo.getX(); //current positions
		y=odo.getY();
		deltaX = xfinal - x; //difference between current and final positions
		deltaY = yfinal - y;
		
		Navigation.xfinal = xfinal;
		Navigation.yfinal = yfinal;
		
		double newDeg, distance;

		newDeg = calculateNewDegree(xfinal,yfinal,deltaX,deltaY); //absolute Theta that it needs to turn to
		turnTo(newDeg, false);
		distance = Math.sqrt(deltaX*deltaX + deltaY*deltaY); //calculates distance from final position

		leftMotor.setSpeed(motorHigh);
		rightMotor.setSpeed(motorHigh);
		leftMotor.rotate(convertDistance(WHEEL_RADIUS, distance), true); //tells motor how many times each wheel should turn
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, distance), false); //convertDistance is converting distance to
																		   // angle (number of rotations)
	}

	/**
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 * @param angle
	 * @param stop
	 */
	public void turnTo(double angle, boolean stop) {
		
		double error = angle - Navigation.odo.getTheta();

		while (Math.abs(error) > degError) {

			error = angle - Navigation.odo.getTheta();

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
	/**
	 * TurnBy function which takes an angle and boolean as arguments 
	 * the angle determines what degree it should turn and the boolean controls 
	 *  whether or not to stop the motors when the turn is completed
	 * @param angle
	 * @param stop
	 */
	public void turnBy(double angle, boolean stop) {
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		leftMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, angle), true);
		rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, angle), false);
		if (stop) {
			this.setSpeeds(0, 0);
		}

	}

	/* Based on current and final positions, what is the absolute angle of the final position*/
	public static double calculateNewDegree(double x, double y, double dx, double dy){ 
		if (dx >= 0) {
			if (dy >= 0) {
				return Math.atan(Math.abs(dx) / Math.abs(dy));
			} else {
				return Math.PI/2 + Math.atan(Math.abs(dy) / Math.abs(dx));
			}
		} else {
			if (dy >= 0) {
				return Math.PI*3/2 + Math.atan(Math.abs(dy) / Math.abs(dx));
			} else {
				return Math.PI + Math.atan(Math.abs(dx) / Math.abs(dy));
			}
		}
	}
	
	private static int convertDistance(double radius, double distance) { //converts distance to angle
		return (int) ((180.0 * distance) / (Math.PI * radius));
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
	
	public void turn(){
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		leftMotor.backward();
		rightMotor.forward();
	}
	
	/**
	 * convertAngle function takes a angle as input and calculate/return
	 * the degrees that wheel should turn based on that input
	 * @param turningAngle
	 * @return
	 */
	private int convertAngle(double radius, double width, double angle) {
		double distance = Math.PI * width * angle / 360;
		return (int) ((180 * distance) / (Math.PI * radius));
	}

}
