
package Tests;
import CaptureTheFlag.*;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;



public class ScannerTest {


	public static void main(String[] args){
		Scanner scan = new Scanner(Initializer.getSensorMotor());
		final TextLCD t = LocalEV3.get().getTextLCD();
		int buttonChoice = 0;
		while (true) {
			do {
				t.clear();
				t.drawString("Press Escape",0,0);
				buttonChoice = Button.waitForAnyPress();
			} while (buttonChoice != Button.ID_ESCAPE);
			
			scan.turnTo(90);
			//LocalEV3.get().getAudio().systemSound(0);
			System.exit(0);
		}
	}
	
	

}

