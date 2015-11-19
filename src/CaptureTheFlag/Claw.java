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
		while (!clawMotor.isStalled()) {
			clawMotor.setSpeed(clawSpeed);
			clawMotor.forward();
		}
		LocalEV3.get().getAudio().systemSound(0);
		clawMotor.stop();
		clawMotor.flt();
		this.clawMotor.resetTachoCount();
		
	}
	public void partialOpen(){
		clawMotor.rotate(-250);
	}
	

	/**
	 * Close claw around object
	 */
	public void close() {
		while (!clawMotor.isStalled()) {
			clawMotor.setSpeed(clawSpeed);
			clawMotor.backward();
		}
		LocalEV3.get().getAudio().systemSound(0);
		clawMotor.stop();
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
