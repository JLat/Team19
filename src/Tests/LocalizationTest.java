package Tests;

import CaptureTheFlag.LCDdisplay;
import CaptureTheFlag.Localization2;
import CaptureTheFlag.Navigation;
import CaptureTheFlag.Odometer;
import CaptureTheFlag.UltrasonicPoller;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class LocalizationTest {

	
	public static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static EV3MediumRegulatedMotor clawMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	public static Odometer odo = new Odometer(leftMotor, rightMotor);
	public static Navigation nav = new Navigation(odo, leftMotor, rightMotor);
	private static UltrasonicPoller uss = new UltrasonicPoller(10, 5, 5, 50, 0);
	
	public static LCDdisplay lcd = new LCDdisplay(odo, uss, null, null);
	
	public static void main(String[] args) {
		Localization2 localization = new Localization2(nav, odo, uss,lcd);
		localization.doLocalization(30);
	}

}
