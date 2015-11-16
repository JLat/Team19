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
	private double sensorDistance = 4.4;
	private double sensorOffset;
	private ArrayList<Double> angles = new ArrayList<Double>();
	
	public LightLocalization (Navigation navi, LightPoller lightPoller, LCDdisplay display) {
		LightLocalization.navi = navi;
		LightLocalization.lightPoller = lightPoller;
		LightLocalization.odo = navi.getOdometer();
		this.display = display;
		this.timer = new Timer(20, this);
	}
	
	public void doLightLocalization() {
		timer.start();
		odo.setPosition(new double [] {0,0,0}, new boolean [] {true, true, true});
		navi.travelTo(5, 5);
		navi.turnTo(0, true);
		odo.setPosition(new double [] {0,0,0}, new boolean [] {true, true, true});		
		navi.turn("counterclockwise");
		while(angles.size() != 4);
		timer.stop();
		processAngles();
		navi.travelTo(0, 0);
		navi.turnTo(0, true);
	}
	
	public void processAngles(){
		double[] position = new double[3];
		double thetaX = Math.abs((angles.get(2)-angles.get(0)));
		double thetaY = Math.abs((angles.get(3)-angles.get(1)));
		position[0] = -sensorDistance * Math.cos(Math.toRadians(thetaY/2));
		position[1] = -sensorDistance * Math.cos(Math.toRadians(thetaX/2));
		position[2] = 270 - angles.get(3) + (thetaY/2);
		odo.setPosition(position, new boolean [] {true, true, true});
	}

	@Override
	public void timedOut() {
		double currentAngle = odo.getTheta();
		if(lightPoller.colorChange()) {
			angles.add(currentAngle);
		}
	}
}
