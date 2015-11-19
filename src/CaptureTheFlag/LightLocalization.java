package CaptureTheFlag;

import java.util.ArrayList;

import lejos.hardware.ev3.LocalEV3;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LightLocalization implements TimerListener {
	private static Odometer odo;
	private static LightPoller lightPoller;
	private static Navigation navi;
	private LCDdisplay display;
	private Timer timer;
	private double sensorDistance = 9.6;
	private ArrayList<Double> angles = new ArrayList<Double>();

	public LightLocalization(Navigation navi, LightPoller lightPoller, LCDdisplay display) {
		LightLocalization.navi = navi;
		LightLocalization.lightPoller = lightPoller;
		LightLocalization.odo = navi.getOdometer();
		this.display = display;
		this.timer = new Timer(20, this);
	}

	public void doLightLocalization() {

		timer.start();
		navi.turnBy(2 * Math.PI, true);
		timer.stop();
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
		position[2] = (2 * Math.PI - Math.toRadians(70) + odo.getTheta()+ (Math.PI / 2 - ((((2 * Math.PI + angles.get(1) - angles.get(3)) % (2 * Math.PI)) / 2) + angles.get(3))% (2 * Math.PI)))% (2 * Math.PI);

		odo.setPosition(position, new boolean[] { true, true, true });
	}

	@Override
	public void timedOut() {
		double currentAngle = odo.getTheta();
		if (lightPoller.seesLine()) {
			LocalEV3.get().getAudio().systemSound(0);
			angles.add(currentAngle);
			display.addInfo("CHANGE: " + currentAngle);
		}
	}
}