package CaptureTheFlag;

import java.util.ArrayList;
import java.util.Arrays;

import lejos.hardware.ev3.LocalEV3;
import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

/**
 * LightLocalization class : Implements timer listener which calls timedOut() every 20ms
 * 							 to check for color changes	in lines
 *
 */
public class LightLocalization implements TimerListener {
	private static Odometer odo;
	private static LightPoller lightPoller;
	private static Navigation navi;
	private LCDdisplay display;
	private Timer timer;
	private double sensorDistance = 9.6;
	private ArrayList<Double> angles = new ArrayList<Double>();
	private boolean sawLine = false;

	/**
	 * Class constructor: takes in a navigator, a lightPoller (color sensor) and a display
	 * @param navi
	 * @param lightPoller
	 * @param display
	 */
	public LightLocalization(Navigation navi, LightPoller lightPoller, LCDdisplay display) {
		LightLocalization.navi = navi;
		LightLocalization.lightPoller = lightPoller;
		LightLocalization.odo = navi.getOdometer();
		this.display = display;
		this.timer = new Timer(20, this);
		Logger.log("Created LightLocalization instance");
	}

	/**
	 * Main class method 1st light localization
	 * 1- detects the lines and stores their respective angle after a 360 turn
	 * 2- calls processAngles
	 * 3- turn to an approximate (0, 0) point before calling 2nd localization
	 * @param x
	 * @param y
	 */
	public void doLightLocalization(int x, int y) {
		Logger.log("----------------Starting first LightLocalization at corner ("+x+","+y+")--------------------");
		
		
		
		//Start detecting lines by calling TimedOut
		timer.start();
		//Turn by 360 degrees and stop
		navi.turnBy(2 * Math.PI, true);
		timer.stop();
		
		//Set the odometer to the robot's actual position
		processAngles(x,y);
		//display.addInfo("Angles" + Arrays.toString(angles));
		
		//Navigate to that position and turn to (0,0) heading
		Logger.log("----------------LightLocalization complete------------------------");
		Logger.log("robot is now at coordinates: ("+odo.getX()+";"+odo.getY()+";"+odo.getThetaDegrees()+")");
		//doLightLocalization2(-100);
	}
	
	/**
	 * Second light localization to improve accuracy
	 * @param speed
	 */
	public void doLightLocalization2(int speed) {
		Logger.log("--Starting second LightLocalization with speed set at "+speed+"--");
		navi.setSpeeds(speed, speed);
		while (!lightPoller.seesLine1() || !lightPoller.seesLine2()) {
			if (lightPoller.seesLine1()) {
				while (!lightPoller.seesLine2()) {
					navi.setSpeeds(speed, -5);
				}
				navi.setSpeeds(0, 0);
			}
			if (lightPoller.seesLine2()) {
				while (!lightPoller.seesLine1()) {
					navi.setSpeeds(-5, speed);
				}
				navi.setSpeeds(0, 0);
			}
		}
		odo.setTheta(0);
		navi.goForward(4.6);
		int lineY = (int) (odo.getY() + 15) / 30;
		odo.setY(lineY*30);
		Logger.log("--Second Light Localization complete--");
		Logger.log("robot is now at coordinates: ("+odo.getX()+";"+odo.getY()+";"+odo.getThetaDegrees()+")");
	}
	
	/**
	 * processAngles: computes the distance from the robot's position using the angles stored by timedOut()
	 * @param x
	 * @param y
	 */
	public void processAngles(int x , int y) {
		
		//Robot must have crossed 4 lines while turning
		if (angles.size() < 4){
			Logger.log("!!!!ERROR! Did not detect 4 lines!!!!");
			return;
		}
		//If the last angle wraps-around fix it so all the angles 0 to 4 are in increasing order 
		if (angles.get(3) < angles.get(2)) {
			angles.set(3, 2 * Math.PI + angles.get(3));
		}
		
		double[] position = new double[3];
		
		//Calculate the x and y position according to the recorded angles and the distance of our sensor
		double thetaX = angles.get(2) - angles.get(0);
		double thetaY = angles.get(3) - angles.get(1);
		position[0] = sensorDistance * Math.cos(thetaY / 2) + x;
		position[1] = sensorDistance * Math.cos(thetaX / 2) + y;
		position[2] = (2 * Math.PI - Math.toRadians(62) + odo.getTheta()+ (Math.PI / 2 - ((((2 * Math.PI + angles.get(1) - angles.get(3)) % (2 * Math.PI)) / 2) + angles.get(3))% (2 * Math.PI)))% (2 * Math.PI);

		Delay.msDelay(400);
		//Set the position to the newly calculated positions of (x,y)
		odo.setPosition(position, new boolean[] { true, true, true });
		Logger.log("Setting new position to ("+odo.getX()+","+odo.getY()+","+odo.getThetaDegrees()+")");
		
	}
	
	/**
	 *timedOut() fetches the data from light poller to look for color change
	 *if it detects one, stores the current angle as displayed by the odometer 
	 */
	@Override
	public void timedOut() {
		double currentAngle = odo.getTheta();
		
		// checks if the lightPoller sees a line. The boolean variable is used in order not to detect the same line twice.
		if (lightPoller.seesLine1() && !sawLine) {
			sawLine = true;
			//LocalEV3.get().getAudio().systemSound(0);
			//Record that angle if a line is detected
			angles.add(currentAngle);
			//display.addInfo("CHANGE: " + currentAngle);
			Logger.log("lightPoller detected a line at "+(int)odo.getThetaDegrees());
		}else if (!lightPoller.seesLine1()){
			// if the sensor doesnt see a line anymore, then allow for the detection of other lines.
			sawLine = false;
		}else{
			
		}
			
		
	}
}













