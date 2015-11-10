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

		final TextLCD t = LocalEV3.get().getTextLCD();
		int buttonChoice = 0;
		
		String Colors[] = {"RED","YELLOW","WHITE","DARK BLUE", "LIGHT BLUE", "HALF BLUE", "WOOD"};
		int i = 0;

		col.printData("DATE:", "NOV 9");
		col.printData("Title: ", "Light Sensor Values For Different Blocks");
		col.printData("Block Color", ",RED,BLUE,GREEN");
		while (true) {
			do {
				// clear the display
				t.clear();
				t.drawString("BLOCK COLOR:", 0, 0);
				t.drawString( Colors[i], 0, 1);
				t.drawString("ESC - EXIT", 0, 5);
				buttonChoice = Button.waitForAnyPress();
				if (buttonChoice == Button.ID_DOWN){
					i ++;
				}
				else if (buttonChoice == Button.ID_UP){
					i --;
				}
				
			} while (buttonChoice != Button.ID_ENTER && buttonChoice != Button.ID_ESCAPE);


			
			if (buttonChoice == Button.ID_ENTER) {
				col.printData(Colors[i], String.format(",%.2f,%.2f,%.2f",
						Light.getRedValue(), Light.getBlueValue(), Light.getGreenValue()));
			}
			else {
				col.writer.close();
				System.exit(0);
			}
			LocalEV3.get().getAudio().systemSound(0);
		}
	}
}
