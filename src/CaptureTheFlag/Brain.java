package CaptureTheFlag;

import lejos.hardware.Button;
import lejos.hardware.Sound;

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
	//object was never used. 
	//	private Scanner scanner = new Scanner(Initializer.getSensorMotor());
	
	/**
	 * After initialization, the robot start searching
	 * which is the main function of the robot
	 */
		
	public void search(){
		Logger.log("Starting Search routine (main program)");
		Logger.setStartTime();
				
		USLoc.doLocalization(30);
		LLoc.doLightLocalization(0,0);
		adjustStartPosition();
		nav.travelToAxis(Initializer.homeZoneBL_X*30, Initializer.homeZoneBL_Y*30);
		nav.turnTo(0,true);
		LLoc.doLightLocalization((int)odo.getX(),(int) odo.getY());
		search.Snake(Initializer.homeZoneBL_X*30, Initializer.homeZoneBL_Y*30,"BottomLeft");
		nav.turnTo(odo.getTheta() + Math.PI, true);
		claw.partialOpen();
		nav.goForward(-10);
		claw.close();
		nav.travelToAxis(Initializer.dropZone_X*30, Initializer.dropZone_Y*30);
		claw.open();
		nav.travelToAxis(120,120);
		nav.turnTo(0, true);
		

		
	}
	
	public void adjustStartPosition() {
		odo.setX(Initializer.corner.getX());
		odo.setY(Initializer.corner.getY());
		switch (Initializer.corner.getId()) {
		case 1:
			odo.setTheta(0);
		case 2:
			odo.setTheta(Math.PI / 2);
		case 3:
			odo.setTheta(Math.PI);
		case 4:
			odo.setTheta(3 * Math.PI / 2);
		}

	}
	
	
	
	public static void pause(){
		int choice = Button.waitForAnyPress();
		if(choice == Button.ID_ESCAPE){
			Logger.close();
			System.exit(0);
		}
		
	}
}

