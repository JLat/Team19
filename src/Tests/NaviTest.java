package Tests;

import CaptureTheFlag.Navigation;
import CaptureTheFlag.Odometer;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class NaviTest {

	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final Odometer odo = new Odometer(leftMotor, rightMotor);
	
	public static void main(String[] args){
		Navigation navi = new Navigation(odo, leftMotor, rightMotor);
		navi.travelTo(0, 60);
		navi.travelTo(60, 60);
		System.exit(0);
	}
}
