package Tests;

import CaptureTheFlag.LCDdisplay;
import CaptureTheFlag.LightLocalization;
import CaptureTheFlag.LightPoller;
import CaptureTheFlag.Navigation;
import CaptureTheFlag.Odometer;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

public class TestLightLocalizer {	
	private static Port colorPort = LocalEV3.get().getPort("S3");
	private static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	


	public static void main(String [] args) {
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		odometer.start();
		LightPoller colorPoller = new LightPoller(colorPort, null, "Red");
		colorPoller.start();
		LCDdisplay display = new LCDdisplay(odometer, null, colorPoller, null);
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor);
		navigator.start();
		LightLocalization lightLocalizer = new LightLocalization(navigator, colorPoller, display); 
		lightLocalizer.doLightLocalization(0,0);
	}
}
