package CaptureTheFlag;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class Claw {
	private EV3MediumRegulatedMotor clawMotor;
	static int clawSpeed;
	private boolean isClosed;
	
	/**
	 * Class constructor : takes in a static EV3 Motor given by the Initializer class
	 * @param clawMotor
	 */
	// assumes the claw starts in an open position.
	public Claw(EV3MediumRegulatedMotor clawMotor) {
		this.clawMotor = clawMotor;
		this.isClosed = false;
		//(Optional) open the claw when the constructor is called.
		//open();
		this.clawMotor.resetTachoCount();
	}
	
	/**
	 * Open claw
	 */
	public void open() {
		while(!clawMotor.isStalled()){
			clawMotor.setSpeed(800);
			clawMotor.forward();
		}
		LocalEV3.get().getAudio().systemSound(0);
		clawMotor.stop();
		this.isClosed = false;
		this.clawMotor.resetTachoCount();
	}
	
	/**
	 * Close claw around object
	 */
	public void close(){
		while(!clawMotor.isStalled()){
			clawMotor.setSpeed(800);
			clawMotor.backward();
		}
		LocalEV3.get().getAudio().systemSound(0);
		clawMotor.stop();
		this.isClosed = true;
	}
	
	public boolean isClosed(){
		return this.isClosed();
	}
	
	public int getTachoCount(){
		return this.clawMotor.getTachoCount();
	}
	

}
