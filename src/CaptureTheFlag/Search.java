package CaptureTheFlag;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;

public class Search {
	Navigation nav;
	Odometer odo;
	Identifier detector;
	UltrasonicPoller USS;
	LCDdisplay display;

	/**
	 * Class constructor : takes in an instance of Navigation given by the
	 * Initializer class
	 * 
	 * @param navigator
	 */
	public Search(Odometer odo, Navigation nav, UltrasonicPoller USS, LCDdisplay display, Identifier detector) {
		this.nav = nav;
		this.odo = odo;
		this.USS = USS;
		this.display = display;
		this.detector = detector;
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

	public boolean Snake(int startX, int startY) {

		/* TODO: this...
		// Search = 0 - If no block is found mark tile as empty and don't search
		// it anymore
		// Search = 1 - If wrong block take it outside the zone and research
		// that tile]
		// Search = 2 - If flag take it to final point

		int pos[][] = { { startX, startY, 0 }, { startX, startY + 30, 0 }, { startX + 60, startY + 60, 180 },
				{ startX + 60, startY + 30, 180 } };

		for (int i = 0; i < 4; i++) {
			nav.travelTo(pos[i][0], pos[i][1]);
			nav.turnToAngle(pos[i][3], true);
			if (search(pos[i][0], pos[i][1]) == 2)
				return true;
		}
		*/
		
		int result = 0;
		
		for (int i = 0; i < 2; i++) {
			nav.travelTo(startX, startY + 30 * i);
			nav.turnTo(0,true);
			result = search(startX, startY + 30 * i);
			if (result == 2)
				return true;
			else if (result == 1)
				i--;
		}

		// TODO: NOT NEEDED FOR DEMO;
		/*
		 * nav.travelTo(startX, startY + 90 - 12); nav.turnTo(Math.PI / 2,
		 * true);
		 * 
		 * for (int i = 0; i < 2; i++) { if (search(startX+ 30 * i, startY + 90
		 * - 12) == 2) return true; }
		 */

		for (int i = 0; i < 2; i++) {
			nav.travelTo(startX + 60, startY + 60 - (30 * i));
			nav.turnTo(Math.PI, true);
			result = search(startX + 60, startY + 60 - (30 * i));
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

		for (int i = 0; i <= 90; i += 3) {
			scan.turnTo(i);
			if (USS.getProcessedDistance() < minVal) {
				minVal = USS.getProcessedDistance();
				minAngle = scan.getAngle();
				startAngle = scan.getAngle();
			} else if (USS.getProcessedDistance() == minVal) {
				endAngle = scan.getAngle();
			}
		}
		if (endAngle > startAngle) {
			minAngle = (startAngle + endAngle) / 2;
		}

		if (minVal < 28) {
			double offY = 12 + minVal * Math.cos(Math.toRadians(minAngle));
			double offX = minVal * Math.sin(Math.toRadians(minAngle));
			double error = Math.atan(offX / offY);
			nav.turnTo(odo.getTheta() + error, true);
			return approachBlock();
			// Turn to block (Relate angle of sensor to angle of block
		}

		// Stage 2 - Search for non-angled blocks

		// TODO: Allow travel in other direction
		while (Math.abs(xCorner - odo.getX()) < 30 && Math.abs(yCorner - odo.getY()) < 30) {
			nav.setSpeeds(150, 150);

			if (USS.getProcessedDistance() < 25) {
				nav.setSpeeds(0, 0);
				nav.goForward(12);
				nav.setSpeeds(0, 0);
				nav.turnTo(odo.getTheta() + Math.PI / 2, true);
				nav.goForward(-5);
				return approachBlock();
			}
		}
		nav.setSpeeds(0, 0);
		return 0;

	}

	public int approachBlock() {
		LocalEV3.get().getAudio().systemSound(0);
		scan.turnTo(0);
		nav.setSpeeds(100, 100);
		
		while (USS.getProcessedDistance() > 3) {

		}
		nav.setSpeeds(0, 0);
		nav.goForward(2);
		if (detector.colorMapping()) {
			
			//Grab block and then get out of there
			LocalEV3.get().getAudio().systemSound(1);
			
			
			//System.exit(0);
			return 2;
		}
		else{
			LocalEV3.get().getAudio().systemSound(0);
			
			//System.exit(0);
			//Remove block from zone then research zone
			
			return 0;
		}

	}

}