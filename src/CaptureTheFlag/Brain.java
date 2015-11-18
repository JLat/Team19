package CaptureTheFlag;

import lejos.hardware.lcd.TextLCD;

public class Brain {
	
	private static Odometer odo;
	private static Navigation nav;
	private static USLocalizer local;
	private static Identifier iden;
	private static UltrasonicPoller usPoller;
	private static TextLCD lcd;
	private static Search search;
	private static Claw claw;
	public Brain (Odometer odo, Navigation navi, USLocalizer local, 
				  Identifier iden, UltrasonicPoller usPoller,Search search, Claw claw ) {
		Brain.odo = odo;
		Brain.nav = navi;
		Brain.local = local;
		Brain.iden = iden;
		Brain.usPoller = usPoller;
		Brain.search = search;
		Brain.claw = claw;
	}
	
	private Scanner scanner = new Scanner(Initializer.getSensorMotor());
	
	/**
	 * After initialization, the robot start searching
	 * which is the main function of the robot
	 */
		
	public void search(){
		//search.search(0, 0, true);
		
		
		
		search.Snake((int)odo.getX(),(int)odo.getY());
		nav.turnTo(odo.getTheta() + Math.PI, true);
		nav.goForward(-10);
		claw.close();
		nav.travelToWithAvoidance(0, 0);
		claw.open();
		
		
	}
	
}
