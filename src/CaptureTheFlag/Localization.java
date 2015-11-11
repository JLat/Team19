package CaptureTheFlag;

import lejos.hardware.Sound;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class Localization implements TimerListener{
	
	private static Navigation navi;
	private static UltrasonicPoller usPoller;
	private static Odometer odo;
	private double previousDistance;
	private double angleA, angleB, angleDifference;
	private double maxA, maxB;
	private Timer clock;
	
	public Localization (Navigation navigator, UltrasonicPoller usPoller) {
		Localization.navi = navigator;
		Localization.usPoller = usPoller;
		Localization.odo = navi.getOdometer();
		this.clock = new Timer(25, this);
	}
	/**
	 * Let the robot do localization and goes to origin
	 */
	public void doLocalization(){
		navi.setSpeeds(navi.SLOW, navi.SLOW);
		navi.turn();
		while(usPoller.getProcessedDistance() < 50);
		navi.setSpeeds(0, 0);
		odo.setPosition(new double [] {0,0,0}, new boolean[] {true, true, true});
		this.clock.start();
		navi.turnBy(-360, true);
		this.clock.stop();
		navi.turnTo((this.maxA + this.maxB)/2, true);
		odo.setPosition(new double[] {0, 0, 225}, new boolean[] {true, true, true});
		navi.turnTo(0, true);
	}
	@Override
	public void timedOut() {
		double currentAngle = odo.getAng();
		double currentDistance = usPoller.getProcessedDistance();
		if(currentDistance < 50 && this.previousDistance > 50) {
			Sound.beep();
			this.angleA = currentAngle;
		}
		else if (currentDistance > 50 && this.previousDistance < 50) {
			Sound.beep();
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
