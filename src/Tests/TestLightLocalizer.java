package Tests;

import CaptureTheFlag.LCDdisplay;
import CaptureTheFlag.LightLocalization;
import CaptureTheFlag.LightPoller;
import CaptureTheFlag.Navigation;
import CaptureTheFlag.Odometer;
import CaptureTheFlag.UltrasonicPoller;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

public class TestLightLocalizer {
	private static Port usPort = LocalEV3.get().getPort("S1");		
	private static Port colorPort = LocalEV3.get().getPort("S3");
	private static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final int bandCenter = 18; 
	private static final int bandWidth = 4;



	public static void main(String [] args) {
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		odometer.start();
		LightPoller colorPoller = new LightPoller(colorPort, "Red");
		colorPoller.start();
		LCDdisplay display = new LCDdisplay(odometer, null, colorPoller, null);
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor);
		LightLocalization lightLocalizer = new LightLocalization(navigator, colorPoller, display); 
		lightLocalizer.doLightLocalization();
	}
}
