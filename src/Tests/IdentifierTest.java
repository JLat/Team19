package Tests;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import CaptureTheFlag.Identifier;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class IdentifierTest {
	public static void main(String[] args)
			throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {
		Identifier identifier = new Identifier(LocalEV3.get().getPort("S2"), "RGB", "light blue");
		identifier.start();

		Collect col = new Collect("blockColors.csv");

		final TextLCD t = LocalEV3.get().getTextLCD();
		int buttonChoice = 0;

		String Colors[] = { "red", "yellow", "white", "dark blue", "light blue", "wood" };
		int i = 0;

		col.printData("DATE:", "NOV 17");
		col.printData("Author", "Fabrice Normandin");
		col.printData("Title: ", "Light sensor ratios for different colors of blocks");
		col.printData("Block Color", ",Detected Color,R/G,G/B,B/R,error");
		while (true) {
			do {
				// clear the display
				t.clear();
				t.drawString("BLOCK COLOR:", 0, 0);
				t.drawString(Colors[Math.abs(i)%(Colors.length)], 0, 1);
				t.drawString("ESC - EXIT", 0, 5);
				buttonChoice = Button.waitForAnyPress();
				if (buttonChoice == Button.ID_DOWN) {
					i++;
				} else if (buttonChoice == Button.ID_UP) {
					i--;
				}

			} while (buttonChoice != Button.ID_ENTER && buttonChoice != Button.ID_ESCAPE);

			if (buttonChoice == Button.ID_ENTER) {
				col.printData(Colors[Math.abs(i)%(Colors.length)]+","+identifier.getBlockColor()+
								String.format(",%.2f,%.2f,%.2f,%.2f,", identifier.getRoverG(),
										identifier.getGoverB(), identifier.getBoverR(), identifier.getError()));
			} else {
				col.writer.close();
				System.exit(0);
			}
			LocalEV3.get().getAudio().systemSound(0);
		}
	}
}
