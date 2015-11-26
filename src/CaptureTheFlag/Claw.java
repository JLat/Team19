package CaptureTheFlag;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class Claw {
	private EV3MediumRegulatedMotor clawMotor;
	private int clawSpeed = 800;

	/**
	 * Class constructor : takes in a static EV3 Motor given by the Initializer
	 * class
	 * 
	 * @param clawMotor
	 */
	// assumes the claw starts in an open position.
	public Claw(EV3MediumRegulatedMotor clawMotor) {
		this.clawMotor = clawMotor;
		// (Optional) open the claw when the constructor is called.
		// open();
		this.clawMotor.resetTachoCount();
	}

	/**
	 * Open claw
	 */
	public void open() {
		Logger.log("Opening claw");
		while (!clawMotor.isStalled()) {
			clawMotor.setSpeed(clawSpeed);
			clawMotor.forward();
		}
		LocalEV3.get().getAudio().systemSound(0);
		clawMotor.stop();
		clawMotor.flt();
		this.clawMotor.resetTachoCount();
		Logger.log("Claw opened");
		
	}
	public void partialOpen(){
		Logger.log("Opening claw partially");
		clawMotor.rotate(-250);
		Logger.log("Claw opened");
	}
	

	/**
	 * Close claw around object
	 */
	public void close() {
		Logger.log("Closing claw");
		while (!clawMotor.isStalled()) {
			clawMotor.setSpeed(clawSpeed);
			clawMotor.backward();
		}
		LocalEV3.get().getAudio().systemSound(0);
		clawMotor.stop();
		Logger.log("Closed claw "+ (hasBlock()? "with" : "without") + " a block");
		
	}

	public int getTachoCount() {
		return this.clawMotor.getTachoCount();
	}

	public boolean isClosed() {
		return this.clawMotor.getTachoCount() < -350;
	}

	public boolean hasBlock() {
		return this.clawMotor.getTachoCount() < -350 && this.clawMotor.getTachoCount() > -450
				&& !this.clawMotor.isMoving();
	}

}
