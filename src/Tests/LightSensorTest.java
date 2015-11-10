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

		int distance = 2;
		final TextLCD t = LocalEV3.get().getTextLCD();
		int buttonChoice = 0;

		col.printData("DATE:", "NOV 3");
		col.printData("Title: ", "Opperational distance of light sensor");
		while (true) {
			do {
				// clear the display
				t.clear();
				t.drawString(" Block Dist: " + distance, 0, 0);
				t.drawString("   Press Enter  ", 0, 2);
				t.drawString(" Escape to Exit ", 0, 4);

				buttonChoice = Button.waitForAnyPress();
			} while (buttonChoice != Button.ID_ENTER && buttonChoice != Button.ID_ESCAPE);

			if (buttonChoice == Button.ID_ENTER) {
				col.printData("Block Distance" + distance, String.format(",Red:,%.2f,Blue:,%.2f,Cond:,%b",
						Light.getRedValue(), Light.getBlueValue(), Light.seesBlueBlock()));

			} else {
				col.writer.close();
				System.exit(0);
			}
			distance += 2;
			LocalEV3.get().getAudio().systemSound(0);
		}
	}
}
