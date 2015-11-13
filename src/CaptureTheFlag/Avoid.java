package CaptureTheFlag;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;

public class Avoid {

	private final int bandCenter, bandwidth;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private EV3MediumRegulatedMotor sensorMotor;
	private int distance;
	private int filterControl;
	private int pathFilter;
	private int turnFilter; // handles gaps in the wall
	public boolean startStop = false;
	private double lastDegree;
	private boolean firstTimeEnter, XorY;
	private static Navigation navi;

	public Avoid(EV3LargeRegulatedMotor leftmotor, EV3LargeRegulatedMotor rightmotor, int bandCenter,
			int bandwidth, Navigation navi, EV3MediumRegulatedMotor sensorMotor) {
		// Default Constructor
		this.leftMotor = leftmotor;
		this.rightMotor = rightmotor;
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		leftMotor.setSpeed(motorStraight); // Initialize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		filterControl = 0;
		pathFilter = 0;
		turnFilter = 20;
		lastDegree = 0;
		firstTimeEnter = true;
		this.sensorMotor = sensorMotor;
		XorY = false;
		Avoid.navi = navi;
	}

	public void processUSData(int distance) {
		/*processUSData is called every time by the thread therefore we needed to create
		 * two large IF cases: (a) is there a wall -- use PController and (b) is there no wall -- travel regular path*/
		if (distance < 2 && distance > 0 && firstTimeEnter) {
			startStop = true;
			firstTimeEnter = false; //when the robot is already in PController, prevents the robot from entering
									// this code block every iteration

			//originalRatio = Math.abs(ratioDeltaY) / Math.abs(ratioDeltaX); //slope of the original path
			lastDegree = Navigation.getTheta(); //saves theta for comparison
			if((lastDegree >= 235 || lastDegree < 45)||(lastDegree >= 135 && lastDegree < 215)){
				XorY = true;
			}else{
				XorY = false;
			}
			sensorMotor.setSpeed(120);
			sensorMotor.rotate(250, true); //turns sensor towards the wall (sensor is mounted on a motor)
		}
		if (startStop) { //filters out 255
			if (distance == 255 && filterControl < FILTER_OUT) {
				filterControl++;
			} else if (distance == 255) {
				this.distance = distance;
			} else {
				filterControl = 0;
				this.distance = distance;
			}

			
			/*Get a ratio of the accuracy of the car with proportion to the bandCenter. 
			 * This ratio is used to define the relationship between speed and distance*/
			double speedRatio = ((Math.abs(((double) distance - bandCenter)) / (6 * bandCenter)));

			/* In the case that the distance is far greater than bandCenter, set
			   a max ratio to avoid wheels sliding*/
			if (speedRatio > 1)
				speedRatio = 1;

			/* CASE 1
			   if sensor returns far distance, increment the turnFilter while
			   continuing straight
			   this is the case where we ignore a gap in the wall*/
//			if (this.distance >= 50 && turnFilter < 20) {
//				turnFilter++; // TURN FILTER EXISTS FOR GAPS
//				setSpeed(motorStraight * speedRatio, motorStraight * speedRatio, true, true);
//			}

			/* CASE 2
			   if sensor has returned the far distance repeatedly, then turn
			   left*/
			if (this.distance >= 50 && turnFilter >= 20 && speedRatio >= 1) {
				setSpeed((double) (motorStraight * speedRatio), (double) (motorStraight * speedRatio * 2), true, true);
			}

			/* CASE 3
			   if sensor has returned the far distance repeatedly however the
			   vehicle is relatively close to the wall, turn gradually*/
			else if (this.distance >= 50 && turnFilter >= 20 && speedRatio < 1) {
				setSpeed((double) motorStraight * (1 - speedRatio), (double) motorStraight * (1 + speedRatio), true,
						true);
			}

			/* CASE 4
			   if the robot is further from the wall than the desirable distance
			   (bandcenter+bandwidth) but not too far away (<50)
			   we know that the robot is within reasonable range, so turn and
			   reset the turnFilter*/
			else if (this.distance > 1.5*bandCenter + bandwidth) {
				setSpeed((double) motorStraight * (1.0 - speedRatio), (double) motorStraight * (1.0 + speedRatio), true,
						true);
				//turnFilter = 0;
			}

			/* CASE 6
			   Robot is too close to the wall. Turn right*/
			else if (this.distance < 1.5*bandCenter - bandwidth) {
				setSpeed((double) (motorStraight), (double) (motorStraight), true, false);
				//turnFilter = 0;
			}

			else { //The robot is within reasonable range from the wall. simply go straight
				setSpeed((double) (motorStraight), (double) (motorStraight), true, true);
				//turnFilter = 0;
			}

			double x = Navigation.getX();
			double y = Navigation.getY();
																					    //current position and final position
			

			/*This is detecting the error for the P Controller path with respect to the original path. If
			 * it comes within 10% of the original path, escape P Controller. It escapes by simply calling the
			 * "travelTo" method to the Final position FROM the current position.*/
			//if ((ratio >= originalRatio * 0.90 && ratio <= originalRatio * 1.1) 
			//		|| (ratio <= originalRatio * 0.90 && ratio >= originalRatio * 1.1)) {
			if ((XorY && (x>=Navigation.getXFinal()-1 && x<Navigation.getXFinal()+1)) 
					|| (!XorY && (y>=Navigation.getYFinal()-1 && y<Navigation.getYFinal()+1))) {
				if (pathFilter >= 100){ //comparison of current degree and the Theta when the robot first entered
												 //the p controller
					this.startStop = false; 
					firstTimeEnter = true;
					pathFilter = 0;
					sensorMotor.setSpeed(500);
					sensorMotor.rotate(-250, true); //Our sensor was mounted onto a motor so that it could turn smoothly
												  //This resets the sensor to face forward
					navi.travelTo(Navigation.getXFinal(), Navigation.getYFinal()); //once it escapes pcontroller, resets
												//final position and calls TravelTo again to ensure it gets there accurately
				}
			}
			pathFilter++;
		}
	}

	/* method used to set the speed and direction of the rotation of the wheels
	   boolean values that are passed into the method correspond with the
	   escape PController if  direction of rotation
	   boolean value TRUE means to rotate forwards, and FALSE means to rotate
	   backwards*/
	public synchronized void setSpeed(double leftSpeed, double rightSpeed, boolean direction1, boolean direction2) {
		leftMotor.setSpeed((int) leftSpeed);
		rightMotor.setSpeed((int) rightSpeed);

		if (direction1) // setting direction of rotation
			leftMotor.forward();
		else
			leftMotor.backward();

		if (direction2)
			rightMotor.forward();
		else
			rightMotor.backward();
	}

	public int readUSDistance() {
		return this.distance;
	}

}
