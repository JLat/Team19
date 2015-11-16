package Tests;

import CaptureTheFlag.Claw;
import CaptureTheFlag.LCDdisplay;
import CaptureTheFlag.Navigation;
import CaptureTheFlag.Odometer;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class ClawTest {
	// claw used for testing the claw assembly.
	
	public static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static EV3MediumRegulatedMotor clawMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	public static Odometer odo = new Odometer(leftMotor, rightMotor);
	public static Navigation nav = new Navigation(odo, leftMotor, rightMotor);
	public static LCDdisplay lcd = new LCDdisplay(odo, null, null, null);
	
	
	
	
	public static void main(String[] args) {
		Claw claw = new Claw(clawMotor);
		claw.close();
		lcd.addInfo("isClosed: "+claw.isClosed());
		lcd.addInfo("Has block: "+claw.hasBlock());
		nav.turnTo(Math.PI, true);
		claw.open();
		lcd.clearAdditionalInfo();
		lcd.addInfo("Open");
		pause();
	}
	
	public static void pause(){
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE){
			
		}
		System.exit(0);
	}

}
