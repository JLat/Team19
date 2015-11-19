package CaptureTheFlag;

import java.util.ArrayList;

import lejos.hardware.Sound;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LightLocalization implements TimerListener {
	private static Odometer odo;
	private static LightPoller lightPoller;
	private static Navigation navi;
	private LCDdisplay display;
	private Timer timer;
	private double sensorDistance = 9.6;
	private double sensorOffsetAngle = 28;
	private ArrayList<Double> angles = new ArrayList<Double>();

	public LightLocalization(Navigation navi, LightPoller lightPoller, LCDdisplay display) {
		LightLocalization.navi = navi;
		LightLocalization.lightPoller = lightPoller;
		LightLocalization.odo = navi.getOdometer();
		this.display = display;
		this.timer = new Timer(20, this);
	}

	public void doLightLocalization() {
		// odo.setPosition(new double [] {0,0,0}, new boolean [] {true, true,
		// true});
		// navi.travelTo(10, 10);
		// navi.turnToAngle(0, true);
		odo.setPosition(new double[] { 0, 0, 0 }, new boolean[] { true, true, true });
		timer.start();
		navi.turnBy(2 * Math.PI, true);
		odo.setPosition(new double[] { 0, 0, 0 }, new boolean[] { true, true, true });
		// while(angles.size() != 4);
		timer.stop();
		// if(angles.size() == 4) {
		if (angles.get(3) < angles.get(2)) {
			angles.set(3, 2 * Math.PI - angles.get(3));
		}
		processAngles();
		display.addInfo("Angles" + angles.toString());
		navi.travelTo(0, 0);
		navi.turnTo(0, true);
	}

	public void processAngles() {
		double[] position = new double[3];
		double thetaX = angles.get(2) - angles.get(0);
		double thetaY = angles.get(3) - angles.get(1);
		position[0] = sensorDistance * Math.cos(thetaY / 2);
		position[1] = sensorDistance * Math.cos(thetaX / 2);
		double deltaY = (3*Math.PI/2) + angles.get(1) + (thetaY / 2);
		double deltaX = -Math.PI + angles.get(2) - (thetaX / 2);
//		position[2] = odo.getTheta() - deltaY + ((Math.PI/2)  + Math.toRadians(sensorOffsetAngle));
		position[2] = (2*Math.PI-45*(Math.PI/180)+odo.getTheta()+(Math.PI/2 -((((2*Math.PI+angles.get(1)-angles.get(3))%(2*Math.PI))/2)+angles.get(3))%(2*Math.PI)))%(2*Math.PI);
		
		odo.setPosition(position, new boolean[] { true, true, true }); 
	}

	@Override
	public void timedOut() {
		double currentAngle = odo.getTheta();
		if (lightPoller.colorChange()) {
			Sound.buzz();
			angles.add(currentAngle);
			display.addInfo("CHANGE: " + currentAngle);
		}
	}
}
