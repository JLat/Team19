package CaptureTheFlag;

/**
 * Search class: responsible for snaking through the tiles to find the flag
 */
import lejos.hardware.ev3.LocalEV3;

public class Search {
	Navigation nav;
	Odometer odo;
	Identifier detector;
	UltrasonicPoller USS;
	LCDdisplay display;
	Claw claw;

	/**
	 * Class constructor : takes in an instance of Navigation given by the
	 * Initializer class
	 * 
	 * @param navigator
	 */
	public Search(Odometer odo, Navigation nav, UltrasonicPoller USS, LCDdisplay display, Identifier detector,
			Claw claw) {
		this.nav = nav;
		this.odo = odo;
		this.USS = USS;
		this.display = display;
		this.detector = detector;
		this.claw = claw;
		Logger.log("Created Search instance");
	}
	
	Scanner scan = new Scanner(Initializer.getSensorMotor());

	/**
	 * Run method for the Thread. Contains the searching algorithm
	 */

	/**
	 * Iterate the search algorithm through each tile
	 * 
	 * 
	 * @return Whether the flag was successfully found or not
	 */

	/**
	 * @param startX
	 *            Starting X coordinate of search area
	 * @param startY
	 *            Starting Y coordinate of search area
	 * @return true if block is found
	 */
	public boolean Snake(int startX, int startY, int corner) {
		Logger.log("Starting snake routine");

		// Search = 0 - If no block is found mark tile as empty and don't search
		// it anymore
		// Search = 1 - If wrong block take it outside the zone and research
		// that tile]
		// Search = 2 - If flag take it to final point

		int posBL[][] = { { startX, startY, 0 }, { startX, startY + 30, 0 }, 
				{ startX, startY + 90, 90 },{ startX + 30, startY + 90, 90 }, 
				{ startX + 60, startY + 60, 180 },{ startX + 60, startY + 30, 180 } };
		
		int posTR[][] = { { startX, startY, 180 }, { startX, startY - 30, 180 }, 
				{ startX, startY - 90, 270 },{ startX - 30, startY - 90, 270 }, 
				{ startX - 60, startY - 60, 0 },{ startX - 60, startY - 30, 0 } };

		int result = 0;
		
		int pos[][];
		if (corner == 1)
			pos = posBL;
		else
			pos = posTR;
		
		
		for (int i = 0; i < 6; i++) {

			// Offset for walls;
			if (pos[i][0] <= -30)
				pos[i][0] += 12;
			else if (pos[i][0] >= 300)
				pos[i][0] -= 12;
			if (pos[i][1] <= -30)
				pos[i][1] += 12;
			else if (pos[i][1] >= 330)
				pos[i][1] -= 12;

			nav.travelTo(pos[i][0], pos[i][1]);
			nav.turnTo(Math.toRadians(pos[i][2]), true);
			
			if (claw.isClosed())
				claw.open();
			result = search(pos[i][0], pos[i][1]);
			if (result == 2)
				return true;
			else if (result == 1)
				i--;
		}
		return false;
	
	}

	/**
	 * Search tile for a block
	 * 
	 * @param xCorner
	 *            Starting X coordinate of square
	 * @param yCorner
	 *            Starting Y coordinate of square
	 * @param wall
	 *            Whether to account for a wall or not
	 * @return Whether the flag or NO/Wrong block is found
	 * 
	 */
	public int search(int xCorner, int yCorner) {
		Logger.log("Searching at corner ("+xCorner+","+yCorner+")");
		nav.goForward(-10);

		// Search function for each tile
		// Assume starting at bottom left corner of the tile

		// Stage 1 - Search for angled blocks
		// Find smallest distance while scanning and if that value is below
		// threshold then use that as the position of the block
		double minVal = 100;
		double minAngle = 0;
		double startAngle = 0;
		double endAngle = 0;
		
		//Scan sensor searching for block in front of threshold
		for (int i = 0; i <= 90; i += 5) {
			scan.turnTo(i);
			if (USS.getProcessedDistance() < minVal) {
				minVal = USS.getProcessedDistance();
				minAngle = scan.getAngle();
				startAngle = scan.getAngle();
			} else if (USS.getProcessedDistance() == minVal) {
				endAngle = scan.getAngle();
			}
		}
		//Find average of angles where smallest distance was found
		if (endAngle > startAngle) {
			minAngle = (startAngle + endAngle) / 2;
		}
		
		//Block found, turn towards block and approach
		if (minVal < 28) {
			double offY = 12 + minVal * Math.cos(Math.toRadians(minAngle));
			double offX = minVal * Math.sin(Math.toRadians(minAngle));
			double error = Math.atan(offX / offY);
			nav.turnTo(odo.getTheta() + error, true);
			return approachBlock(xCorner, yCorner);
			// Turn to block (Relate angle of sensor to angle of block
		}

		// Stage 2 - Search for non-angled blocks

		//Travel forwards with sensor at 90 deg (right)
		while (Math.abs(xCorner - odo.getX()) < 30 && Math.abs(yCorner - odo.getY()) < 30) {
			nav.setSpeeds(200, 200);
		//Block found, move forwards to adjust for sensor offset, then turn 90deg. Approach and check
			if (USS.getProcessedDistance() < 25) {
				nav.setSpeeds(0, 0);
				nav.goForward(12);
				nav.setSpeeds(0, 0);
				nav.turnTo(odo.getTheta() + Math.PI / 2, true);
				return approachBlock(xCorner, yCorner);
			}
		}
		nav.setSpeeds(0, 0);
		return 0;

	}

	public int approachBlock(int xCorner, int yCorner) {
		Logger.log("approaching block at ("+xCorner+","+yCorner+")");
		LocalEV3.get().getAudio().systemSound(0);
		scan.turnTo(0);
		nav.setSpeeds(100, 100);

		//Move forward until within 3 cm from block
		while (USS.getProcessedDistance() > 3) {

		}
		nav.setSpeeds(0, 0);
		nav.goForward(3);
		
		//check which type of flag is in front of robot
		if (detector.isFlagDetected()) {

			//In front of block, exit search
			LocalEV3.get().getAudio().systemSound(1);
			Logger.log("Flag found in ("+xCorner + "," + yCorner+")");
			return 2;
		} else {
			LocalEV3.get().getAudio().systemSound(0);
			Logger.log("NON Flag found in ("+xCorner + "," + yCorner+")");
			// Remove block from zone then research zone
			nav.turnTo(odo.getTheta() + Math.PI, true);
			claw.partialOpen();
			nav.goForward(-8);
			claw.close();

			return 1;
		}

	}

}