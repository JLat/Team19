package CaptureTheFlag;


import java.util.Timer;

import lejos.hardware.Sound;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.TimerListener;

public class LCDdisplay implements TimerListener {

	private Timer timer;
	private TextLCD lcd;
	
	/**
	 * Constantly drawing data/information on screen 
	 */
	@Override
	public void timedOut() {
		
	}
	
	public void printMainMenu(String[] a) {
		lcd.clear();
		if (a != null){
			for (int i = 0; i < a.length; i ++) {
				lcd.drawString(a[i],  0, i);
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Sound.buzz();
		}
		
	}

}
