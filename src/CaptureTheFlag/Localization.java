package CaptureTheFlag;

import lejos.hardware.Sound;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class Localization implements TimerListener{
	
	private static Navigation navi;
	private static UltrasonicPoller usPoller;
	private static Odometer odo;
	private double previousDistance;
	private double angleA = 0, angleB = 0, angleDifference = 0;
	private double maxA, maxB;
	private Timer clock;
	private TextLCD t;
	
	public Localization (Navigation navigator, UltrasonicPoller usPoller, TextLCD t) {
		Localization.navi = navigator;
		Localization.usPoller = usPoller;
		Localization.odo = navi.getOdometer();
		this.t = t;
		this.clock = new Timer(20, this);
	}
	
	/**
	 * Let the robot do localization and goes to origin
	 */
	public void doLocalization(){
		//Sound.beepSequenceUp();
		usPoller.setParameters(8, 30, 30, 250, 0);
		navi.turn();	
		while(usPoller.getProcessedDistance() < 30);
		navi.setSpeeds(0, 0);
		odo.setPosition(new double [] {0,0,0}, new boolean[] {true, true, true});
		this.clock.start();
		//navi.turnBy(2*Math.PI);
		navi.turnBy(2*Math.PI);
		this.clock.stop();
		t.drawString("MaxA:" + maxA, 0, 4);
		t.drawString("MaxB:" + maxB, 0, 5);
		navi.turnTo(Math.toRadians((this.maxA + this.maxB)/2), true);
//		odo.setPosition(new double[] {0, 0, 225}, new boolean[] {true, true, true});
//		navi.turnTo(0, true);
	}
	@Override
	public void timedOut() {
		double currentAngle = Math.toDegrees(odo.getTheta());
		int currentDistance = usPoller.getProcessedDistance();
		t.clear();
		t.drawString("D: "+ currentDistance, 0, 0);
		t.drawString("T: "+ currentAngle, 0, 1);
		t.drawString("A" + angleA, 0, 2);
		t.drawString("B" + angleB, 0, 3);
		if(currentDistance < 60 && this.previousDistance >= 60) {
			this.angleA = currentAngle;
			//Sound.beep();
		}
		else if (currentDistance >= 60 && this.previousDistance < 60) {
			//Sound.buzz();
			this.angleB = currentAngle;
			if(this.angleA - this.angleB > this.angleDifference) {
				this.angleDifference = this.angleA - this.angleB;
				this.maxA = this.angleA;
				this.maxB = this.angleB;
			}
		}
		this.previousDistance = currentDistance;
	}
	

}
