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

	public Brain(Odometer odo, Navigation navi, USLocalizer USLoc, LightLocalization LLoc, Identifier iden,
			UltrasonicPoller usPoller, Search search, Claw claw) {
		Brain.odo = odo;
		Brain.nav = navi;
		Brain.USLoc = USLoc;
		Brain.LLoc = LLoc;
		Brain.iden = iden;
		Brain.usPoller = usPoller;
		Brain.search = search;
		Brain.claw = claw;
	}
	// object was never used.
	// private Scanner scanner = new Scanner(Initializer.getSensorMotor());

	/**
	 * After initialization, the robot start searching which is the main
	 * function of the robot
	 */

	public void search() {

		// wait until the usPoller cache is full of readings
		while (!usPoller.isFull())
			;

		Logger.log("Starting Search routine (main program)");
		Logger.setStartTime();
		LocalEV3.get().getAudio().systemSound(2);

		USLoc.doLocalization(28);
		nav.travelTo(3, 3);

		Logger.log("Turning the robot to 335 degrees for ideal light sensor placement.");
		nav.turnToAngle(335, true);

		LLoc.doLightLocalization(0, 0);
		Logger.log("Travelling to 0,0");
		nav.travelTo(0, 0);
		adjustStartPosition();
		nav.turnToAngle(0, true);

		// optional pause, used for calibrating the localization.
		// pause();

		// Choose which corner to travel to
		int[] pos = chooseSearchCorner((int) odo.getX(), (int) odo.getY());
		int destinationX = pos[0], destinationY = pos[1], corner = pos[2];
		Logger.log("Travelling to corner " + pos[2] + " located at (" + pos[0] + "," + pos[1] + ")");

		nav.travelToAxis(destinationX * 30, destinationY * 30);
		nav.turnTo(0, true);
		Logger.log(
				"Prior to search localization: (" + odo.getX() + ";" + odo.getY() + ";" + odo.getThetaDegrees() + ")");
		nav.turnToAngle(335, true);
		LLoc.doLightLocalization(destinationX * 30, destinationY * 30);
		nav.travelTo(destinationX * 30, destinationY * 30);
		nav.turnTo(0, true);
		// optional pause, used to separate the travel from searching.
		// pause();

		Logger.log("Starting Searching Algorithm");
		search.Snake(destinationX * 30, destinationY * 30, corner);

		nav.turnTo(odo.getTheta() + Math.PI, true);
		claw.partialOpen();
		nav.goForward(-10);
		claw.close();

		nav.travelToAxis(Initializer.dropZone_X * 30, Initializer.dropZone_Y * 30);
		nav.turnTo(Math.toRadians(225), true);

	}

	/**Change robots current position depending on starting corner of robot
	 * 
	 */
	public void adjustStartPosition() {
		Logger.log("Current coordinates are:  (" + odo.getX() + ";" + odo.getY() + ";" + odo.getThetaDegrees() + ")");
		odo.setX(odo.getX() + Initializer.corner.getX());
		odo.setY(odo.getY() + Initializer.corner.getY());
		if (Initializer.corner.getId() == 2)
			odo.setTheta((odo.getTheta() - Math.PI / 2 + 2 * Math.PI) % (2 * Math.PI));
		else if (Initializer.corner.getId() == 3)
			odo.setTheta((odo.getTheta() - Math.PI + 2 * Math.PI) % (2 * Math.PI));
		else if (Initializer.corner.getId() == 4)
			odo.setTheta((odo.getTheta() - 3 * Math.PI / 2 + 2 * Math.PI) % (2 * Math.PI));

		Logger.log("Setting new coordinates:  (" + odo.getX() + ";" + odo.getY() + ";" + odo.getThetaDegrees() + ")");
	}

	/**
	 * Determine which corner of the search area to travel to
	 * 
	 * 
	 * @param x X position of robot
	 * @param y Y position of robot
	 * @return x,y and corner of the search area
	 */
	public int[] chooseSearchCorner(int x, int y) {
		int[] out = new int[3];
		int minDist = 1000;
		// BL,BR, TR,TL
		int[][] corner = { { Initializer.homeZoneBL_X, Initializer.homeZoneBL_Y },
				{ Initializer.homeZoneTR_X, Initializer.homeZoneBL_Y },
				{ Initializer.homeZoneTR_X, Initializer.homeZoneTR_Y },
				{ Initializer.homeZoneBL_X, Initializer.homeZoneTR_Y } };
		for (int i = 0; i < 4; i++) {
			if (distanceTo(x, y, corner[i][0] * 30, corner[i][1] * 30) < minDist && corner[i][0] != -1
					&& corner[i][0] != 11 && corner[i][1] != -1 && corner[i][1] != 11) {
				minDist = (int) distanceTo(x, y, corner[i][0] * 30, corner[i][1] * 30);
				out[0] = corner[i][0];
				out[1] = corner[i][1];
				out[2] = i;
			}
		}
		return out;
	}

	/**
	 * Pauses the program flow
	 */
	public static void pause() {
		int choice = Button.waitForAnyPress();
		if (choice == Button.ID_ESCAPE) {
			Logger.close();
			System.exit(0);
		}
	}

	/**
	 * Calculate the distance between 2 Cartesian coordinates
	 * 
	 * @param x1 X coord of 1st point
	 * @param y1 Y coord of 1st point
	 * @param x2 X coord of 2nd point
	 * @param y2 Y coord of 2nd point
	 * @return Distance between the two points
	 */
	public double distanceTo(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
}
