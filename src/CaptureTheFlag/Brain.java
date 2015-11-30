package CaptureTheFlag;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;

/**
 * Brain class: responsible for the entire logical process of the routine
 *
 */
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
		
		
		// wait until the usPoller cache is full of readings
		while(!usPoller.isFull());
		
		Logger.log("Starting Search routine (main program)");
		Logger.setStartTime();
		LocalEV3.get().getAudio().systemSound(2);
		

		
		
		USLoc.doLocalization(28);
		nav.travelTo(3,3);
		
		Logger.log("Turning the robot to 335 degrees for ideal light sensor placement.");
		nav.turnToAngle(335, true);
		
		LLoc.doLightLocalization(0,0);
		Logger.log("Travelling to 0,0");
		nav.travelTo(0, 0);
		adjustStartPosition();
		nav.turnToAngle(0, true);
		
		// optional pause, used for calibrating the localization.
		//pause();
		
		
		nav.travelToAxis(Initializer.homeZoneBL_X*30, Initializer.homeZoneBL_Y*30);
		//nav.travelTo(Initializer.homeZoneBL_X*30, Initializer.homeZoneBL_Y*30);
		nav.turnTo(0,true);
		Logger.log("Prior to search localization:  ("+odo.getX()+";"+odo.getY()+";"+odo.getThetaDegrees()+")");
		nav.turnToAngle(335, true);
		LLoc.doLightLocalization(Initializer.homeZoneBL_X*30,Initializer.homeZoneBL_Y*30);
		nav.travelTo(Initializer.homeZoneBL_X*30, Initializer.homeZoneBL_Y*30);
		nav.turnTo(0, true);
		//optional pause, used to separate the travel from searching.
		pause();
		
		
		
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
	
	/**
	 *Pauses the program flow
	 */
	public static void pause(){
		int choice = Button.waitForAnyPress();
		if(choice == Button.ID_ESCAPE){
			Logger.close();
			System.exit(0);
		}
		
	}
}

