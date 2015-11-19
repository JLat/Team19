package CaptureTheFlag;


public class Brain {
	
	private static Odometer odo;
	private static Navigation nav;
	private static USLocalizer USLoc;
	private static Identifier iden;
	private static UltrasonicPoller usPoller;
	private static LightLocalization LLoc;
	private static Search search;
	private static Claw claw;
	public Brain (Odometer odo, Navigation navi, USLocalizer USLoc,LightLocalization LLoc, 
				  Identifier iden, UltrasonicPoller usPoller,Search search, Claw claw ) {
		Brain.odo = odo;
		Brain.nav = navi;
		Brain.USLoc = USLoc;
		Brain.LLoc = LLoc;
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
		USLoc.doLocalization(30);
		LLoc.doLightLocalization(0,0);
		nav.travelToAxis(120, 120);
		nav.turnTo(0,true);
		LLoc.doLightLocalization((int)odo.getX(),(int) odo.getY());
		search.Snake((int)odo.getX(),(int)odo.getY());
		nav.turnTo(odo.getTheta() + Math.PI, true);
		nav.goForward(-10);
		claw.close();
		nav.travelToAxis(0, 0);
		claw.open();
		
		
	}
	
}
