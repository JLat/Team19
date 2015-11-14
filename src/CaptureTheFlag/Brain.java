package CaptureTheFlag;

import lejos.hardware.lcd.TextLCD;

public class Brain {
	
	private static Odometer odo;
	private static Navigation navi;
	private static Localization local;
	private static Identifier iden;
	private static UltrasonicPoller usPoller;
	private static TextLCD lcd;
	private static Search search;
	public Brain (Odometer odo, Navigation navi, Localization local, 
				  Identifier iden, UltrasonicPoller usPoller, TextLCD l,Search search ) {
		Brain.odo = odo;
		Brain.navi = navi;
		Brain.local = local;
		Brain.iden = iden;
		Brain.usPoller = usPoller;
		Brain.lcd = l;
		Brain.search = search;
	}
	
	private Scanner scanner = new Scanner(Initializer.getSensorMotor());
	
	/**
	 * After initialization, the robot start searching
	 * which is the main function of the robot
	 */
		
	public void search(){
		//search.search(0, 0, true);
		search.Snake(0,0);
	}
	
}
