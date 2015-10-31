package Tests;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import CaptureTheFlag.*;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class LightSensorTest {
	public static void main(String[] args)
			throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {
		LightController Light = new LightController("S4", "RGB");
		Light.start();
		
		Collect col = new Collect();

		int test = 1;
		final TextLCD t = LocalEV3.get().getTextLCD();
		int buttonChoice = 0;
		
		col.printData("DATE:", "OCT 31");
		while (true) {
			do {
				// clear the display
				t.clear();
				t.drawString("    Test #" + test, 0, 0);
				t.drawString("< Left | Right >", 0, 1);
				t.drawString("  Blue |  Wood  ", 0, 2);
				t.drawString("                ", 0, 3);
				t.drawString(" Escape to Exit ", 0, 4);

				buttonChoice = Button.waitForAnyPress();
			} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT
					&& buttonChoice != Button.ID_ESCAPE);
			col.printData("TEST #", test);
			if (buttonChoice == Button.ID_RIGHT) {
				
				for (int i = 0; i < 50; i++) {
					col.printData("Wood Block", String.format(",R: %.2f, B: %.2f, G: %.2f", Light.getRedValue(),
							Light.getBlueValue(), Light.getGreenValue()));
				}
			} else if (buttonChoice == Button.ID_LEFT) {
				for (int i = 0; i < 50; i++) {
					col.printData("Blue Block", String.format(",R: %.2f, B: %.2f, G: %.2f", Light.getRedValue(),
							Light.getBlueValue(), Light.getGreenValue()));
				}

			} else {
				col.writer.close();
				System.exit(0);
			}
			test++;
			LocalEV3.get().getAudio().systemSound(0);
		}
	}
}
