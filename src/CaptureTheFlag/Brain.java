package CaptureTheFlag;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Brain {
	
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	
	
	Odometer Odo = new Odometer(leftMotor, rightMotor, 30, true);
	Navigator Nav = new Navigator(Odo);
	LightController Light = new LightController("S4", "RGB");
	USSController Uss = new USSController(10, 10, 15, 50, 0);
	LCD Lcd = new LCD(Odo, Uss, Light);
	Localizer Loc = new Localizer();	
	
	public static void main (String [] args){
		//Main function of robot
		
		
		
	}
	
}
