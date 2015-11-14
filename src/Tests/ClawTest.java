package Tests;

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
	public static Navigation nav = new Navigation(odo, leftMotor, leftMotor);
	public static LCDdisplay lcd = new LCDdisplay(odo, null, null, null);
	
	
	
	
	public static void main(String[] args) {
		close();
		lcd.addInfo("Closed");
		nav.goForward(10);
		nav.turnTo(0, true);
		open();
		lcd.clearAdditionalInfo();
		lcd.addInfo("Open");
		pause();
	}
	public static void close(){
		while(!clawMotor.isStalled()){
			clawMotor.setSpeed(600);
			clawMotor.backward();
		}
		LocalEV3.get().getAudio().systemSound(0);
		clawMotor.stop();
	}
	public static void open(){
		while(!clawMotor.isStalled()){
			clawMotor.setSpeed(600);
			clawMotor.forward();
		}
		LocalEV3.get().getAudio().systemSound(0);
		clawMotor.stop();
	}
	public static void pause(){
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE){
			
		}
		System.exit(0);
	}

}
