
package Tests;
import CaptureTheFlag.*;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;



public class SearchTest {
	
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static TextLCD t;
	
	public static void main(String [] args) {
		UltrasonicPoller usPoller = new UltrasonicPoller(10, 10, 10, 100, 0);
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor);
		LCDdisplay display = null;
		Search search = new Search (odometer, navigator, usPoller,display, null,null);
		final TextLCD t = LocalEV3.get().getTextLCD();
		
		odometer.start();
		usPoller.start();
		navigator.start();
		int buttonChoice = 0;
		while (true) {
			do {
				t.clear();
				t.drawString("Press Escape",0,0);
				buttonChoice = Button.waitForAnyPress();
			} while (buttonChoice != Button.ID_ESCAPE);
			search.search(0, 0);
			//LocalEV3.get().getAudio().systemSound(0);
			System.exit(0);
		}
	}
	
	

}

