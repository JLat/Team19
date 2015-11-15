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
		Sound.beepSequenceUp();
		navi.turn();	
		while(usPoller.getProcessedDistance() < 30);
		navi.setSpeeds(0, 0);
		odo.setPosition(new double [] {0,0,0}, new boolean[] {true, true, true});
		this.clock.start();
		Sound.beepSequence();
		//Assuming angle in degrees
		navi.turnBy(-360, true);
		this.clock.stop();
//		this.t.drawString("Diff: "+ angleDifference, 0, 3);
		this.t.drawString("MaxA: "+ maxA, 0, 4);
		this.t.drawString("MaxB: "+ maxB, 0, 5);
//		this.t.drawString("turn: "+ (this.maxA + this.maxB)/2, 0, 6);
		navi.turnTo((this.maxA + this.maxB)/2, true);
		odo.setPosition(new double[] {0, 0, 225}, new boolean[] {true, true, true});
		navi.turnTo(0, true);
	}
	@Override
	public void timedOut() {
		double currentAngle = odo.getTheta();
		//TODO try getting the currentDistance through a median filter instead to improve accuracy
		int currentDistance = usPoller.getProcessedDistance();
		t.clear();
		t.drawString("D: "+currentDistance, 0, 0);
//		t.drawString("P: "+this.previousDistance, 0, 1);
		if(currentDistance < 60 && this.previousDistance >= 60) {
			this.angleA = currentAngle;
		}
		else if (currentDistance >= 60 && this.previousDistance < 60) {
			this.angleB = currentAngle;
			if(this.angleB - this.angleA > this.angleDifference) {
				this.angleDifference = this.angleB - this.angleA;
				this.maxA = this.angleA;
				this.maxB = this.angleB;
			}
		}
		this.previousDistance = currentDistance;
	}

}
