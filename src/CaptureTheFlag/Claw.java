package CaptureTheFlag;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Claw {
	private static EV3LargeRegulatedMotor clawMotor;
	static int clawSpeed;
	
	/**
	 * Class constructor : takes in a static EV3 Motor given by the Initializer class
	 * @param clawMotor
	 */
	public Claw(EV3LargeRegulatedMotor clawMotor) {
		this.clawMotor = clawMotor;
	}
	
	/**
	 * Open claw
	 */
	public void open() {
		
	}
	
	/**
	 * Close claw around object
	 */
	public void close(){
		
	}

}
