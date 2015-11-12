package CaptureTheFlag;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class Scanner {

	private int SPEED = 100;
	public double angleRatio = -90./300;
	public EV3MediumRegulatedMotor scannerMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
	
	public Scanner(){
		this.scannerMotor.resetTachoCount();
	}

	/**
	 * Turn sensor to desired angle 
	 * Forward -- 0 deg
	 * Left -- Increasing
	 * Right -- Decreasing 
	 *
	 * 
	 * @param angle Angle to turn to
	 */
	public void turnTo(double angle) {
		double currentAngle = scannerMotor.getTachoCount() * angleRatio;
		double error = currentAngle - angle;
		while (Math.abs(error) >= 1) {
			scannerMotor.setSpeed(SPEED);
			if (error > 0) {
				scannerMotor.forward();
			} else {
				scannerMotor.backward();
			}			
			currentAngle= scannerMotor.getTachoCount() * angleRatio;
			error = scannerMotor.getTachoCount() * angleRatio - angle;
		}
		scannerMotor.stop();
	}
	
	public double getAngle(){
		return scannerMotor.getTachoCount()*angleRatio;
	}

}
