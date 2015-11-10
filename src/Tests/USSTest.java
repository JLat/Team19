package Tests;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import CaptureTheFlag.*;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class USSTest {
	public static void main(String[] args)
			throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {
		UltrasonicPoller USS = new UltrasonicPoller(10, 10, 10, 100, 0);
		USS.start();
		Collect col = new Collect();
		col.printData("DATE", "NOV 3");
		col.printData("Title: ", "Distance of block before error");

		final TextLCD t = LocalEV3.get().getTextLCD();
		int buttonChoice = 0;
		int distance = 0;
		while (true) {
			do {
				// clear the display
				t.clear();
				t.drawString(" Block Dist: " + distance, 0, 0);
				t.drawString("   Press Enter  ", 0, 2);
				t.drawString(" Escape to Exit ", 0, 4);

				buttonChoice = Button.waitForAnyPress();
			} while (buttonChoice != Button.ID_ENTER && buttonChoice != Button.ID_ESCAPE);

			col.printData("Block Distance:", distance);
			if (buttonChoice == Button.ID_ENTER) {
				col.printData("Raw Distance ", "," + USS.getRawDistance());
			} else {
				col.writer.close();
				System.exit(0);
			}
			distance += 2;
			LocalEV3.get().getAudio().systemSound(0);
		}
	}
}
