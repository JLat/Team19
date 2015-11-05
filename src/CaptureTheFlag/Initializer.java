package CaptureTheFlag;
import lejos.hardware.sensor.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;


public class Initializer {
	private static Port usPort;		
	private static Port colorPort;
	private static Port gyroPort;
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static EV3LargeRegulatedMotor clawMotor;
	private static double wheelRadius, track;
	public static TextLCD t;
	
	/**
	 * Initializes all the objects in the following order :
	 * 		1- UltrasonicPoller
	 * 		2- LightPoller
	 * 		3- Identifier
	 * 		4- Odometer
	 *		5- Navigator
	 *		6- Localizer
	 *		7- Brain
	 * @param args
	 */
	public static void main(String [] args) {
		
	}
}
