package CaptureTheFlag;

import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class Scanner extends Thread{

	private int SPEED = 100;
	public double angleRatio = -90./300;
	public EV3MediumRegulatedMotor scannerMotor;
	private static boolean radarMode = false;
	
	public Scanner(EV3MediumRegulatedMotor scannerMotor){
		this.scannerMotor = scannerMotor;
		this.scannerMotor.resetTachoCount();
		Logger.log("Created Scanner instance");
	}

	public void run(){
		while(true){
			if(radarMode){
				scannerMotor.setSpeed(500);
				// TODO: -Fabrice: are you accounting for the gear reduction here ?
				scannerMotor.rotateTo(40, true);
				while(scannerMotor.getPosition()<40){
				}
				scannerMotor.rotateTo(-40, true);
				while(scannerMotor.getPosition()>-40){
				}
			}
		}
	}
	
	/**
	 * Starts the scanning
	 * 
	 * @param start
	 */
	public static void setRadarMode(boolean start){
		Logger.log("Setting scanner to Scanner Mode");
		Scanner.radarMode = start;
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
	
	/**
	 * getAngle()
	 * 
	 * @return
	 */
	public double getAngle(){
		return scannerMotor.getTachoCount()*angleRatio;
	}

}
