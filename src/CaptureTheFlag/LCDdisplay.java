package CaptureTheFlag;

import java.util.ArrayList;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LCDdisplay implements TimerListener {
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	private TextLCD LCD = LocalEV3.get().getTextLCD();
	private UltrasonicPoller USS;
	private LightPoller lSensor;
	private Identifier identifier;

	// arrays for displaying data
	private double[] pos;
	private ArrayList<String> additionalInfo = new ArrayList<String>();

	public LCDdisplay(Odometer odo, UltrasonicPoller USS, LightPoller lSensor, Identifier identifier) {
		this.USS = USS;
		this.lSensor = lSensor;
		this.identifier = identifier;
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		// initialize the arrays for displaying data
		pos = new double[3];

		// start the timer
		lcdTimer.start();
	}

	public void timedOut() {
		odo.getPosition(pos, new boolean[]{true,true,true});
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString(formattedDoubleToString((pos[0]), 2), 3, 0);
		LCD.drawString(formattedDoubleToString((pos[1]), 2), 3, 1);
		LCD.drawString(formattedDoubleToString((pos[2]), 2), 3, 2);

		int i = 3;

		// display all additional information.

		// in order to avoid ConcurrentModificationException, we iterate over a
		// copy of the object such that the iterator doesn't throw an exception.
		ArrayList<String> temp = new ArrayList<String>(additionalInfo);
		
		
		
		for (String element : temp) {
			// if the element contains "Distance: ", then it represent the ultrasonic Distance value. we update the value.
			if(element.toLowerCase().contains(("distance"))){
				element = "Distance: "+(this.USS.getProcessedDistance());
				LCD.drawString(element,0, i);
			}else if(element.toLowerCase().contains("red")){
				element = "Red: "+formattedDoubleToString((this.identifier.getRedValue()),2);
				LCD.drawString(element,0, i);
			}else if(element.contains("Green: ")){
				element = "Green: "+formattedDoubleToString((this.identifier.getGreenValue()),2);
				LCD.drawString(element,0, i);
			}else if(element.contains("Blue: ")){
				element = "Blue: "+formattedDoubleToString((this.identifier.getBlueValue()),2);
				LCD.drawString(element,0, i);
			}else{
				LCD.drawString(element, 0, i);
			}
			
			i++;
		}
	}
	
	//Add info to be displayed onto screen
	public void addInfo(String label, double data) {
		this.additionalInfo.add(label +": "+ formattedDoubleToString(data,2));
		
	}
	public void addInfo(String info) {
		this.additionalInfo.add(info);
	}
	public void removeInfo(String info){
		for(String element:this.additionalInfo){
			if(element.contains(info)){
				this.additionalInfo.remove(element);
			}
		}
	}

	//Clear additional information on lcd display
	public void clearAdditionalInfo() {
		this.additionalInfo.clear();
	}
	

	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;

		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";

		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long) x;
			if (t < 0)
				t = -t;

			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}

			result += stack;
		}

		// put the decimal, if needed
		if (places > 0) {
			result += ".";

			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long) x);
			}
		}

		return result;
	}

}
