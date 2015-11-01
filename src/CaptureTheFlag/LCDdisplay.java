package CaptureTheFlag;


import java.util.Timer;
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

}
