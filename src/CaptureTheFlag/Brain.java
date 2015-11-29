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
		nav.travelTo(0, 0);
		adjustStartPosition();
		
		
		nav.travelToAxis(Initializer.homeZoneBL_X*30, Initializer.homeZoneBL_Y*30);
		nav.turnTo(0,true);
		Logger.log("Prior to search localiztion:  ("+odo.getX()+";"+odo.getY()+";"+odo.getThetaDegrees()+")");
		LLoc.doLightLocalization2(-100);
		
		Logger.log("Starting Searching Algorithm");
		search.Snake(Initializer.homeZoneBL_X*30, Initializer.homeZoneBL_Y*30,"BottomLeft");

		nav.turnTo(odo.getTheta() + Math.PI, true);
		claw.partialOpen();
		nav.goForward(-10);
		claw.close();
		
		nav.travelToAxis(Initializer.dropZone_X*30, Initializer.dropZone_Y*30);
		nav.turnTo(Math.toRadians(225), true);

		

		
	}
	
	public void adjustStartPosition() {
		Logger.log("Current coordinates are:  ("+odo.getX()+";"+odo.getY()+";"+odo.getThetaDegrees()+")");
		odo.setX(odo.getX() + Initializer.corner.getX());
		odo.setY(odo.getY() + Initializer.corner.getY());
		if(Initializer.corner.getId() ==2 )
			odo.setTheta((odo.getTheta() - Math.PI/2  + 2*Math.PI)% (2*Math.PI));
		else if (Initializer.corner.getId() ==3 )
			odo.setTheta((odo.getTheta() - Math.PI  + 2*Math.PI)% (2*Math.PI));
		else if (Initializer.corner.getId() ==4 )
			odo.setTheta((odo.getTheta()-  3 * Math.PI / 2  + 2*Math.PI)% (2*Math.PI));
		
		Logger.log("Setting new coordinates:  ("+odo.getX()+";"+odo.getY()+";"+odo.getThetaDegrees()+")");
	}
	
	
	
	public static void pause(){
		int choice = Button.waitForAnyPress();
		if(choice == Button.ID_ESCAPE){
			Logger.close();
			System.exit(0);
		}
		
	}
}

