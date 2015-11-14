package CaptureTheFlag;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.utility.Delay;

public class Search {
	Navigation nav;
	Odometer odo;
	Identifier detector;
	UltrasonicPoller USS;

	/**
	 * Class constructor : takes in an instance of Navigation given by the
	 * Initializer class
	 * 
	 * @param navigator
	 */
	public Search(Odometer odo, Navigation nav, UltrasonicPoller USS) {
		this.nav = nav;
		this.odo = odo;
		this.USS = USS;
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
		for (int i = 0; i < 2; i++) {

			if (search(startX, startY + 30 * i))
				return true;
		}
		nav.travelTo(startX + 65, startY + 82);
		nav.turnTo(Math.PI, true);
		
		for (int i = 0; i < 2; i++) {
			if (search(startX + 65, startY + 82 - (30*i)))
				return true;
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
	 * @return Whether the flag or NO/Wrong block is found
	 * 
	 */
	public boolean search(int xCorner, int yCorner) {
		// Search function for each tile
		// Assume starting at bottom left corner of the tile

		// Stage 1 - Search for angled blocks
		// Find smallest distance while scanning and if that value is below threshold then use that as the position of the block
		double minVal = 100;
		double minAngle = 0;
		for (int i = 0; i <= 90; i+=3) {
			LocalEV3.get().getTextLCD().clear();
			LocalEV3.get().getTextLCD().drawInt(USS.getProcessedDistance(), 4, 4);
			scan.turnTo(i);
			if (USS.getProcessedDistance() < minVal){
				minVal = USS.getProcessedDistance();
				minAngle = scan.getAngle();
			}
		}

		if (minVal < 28) {
			double offY = 12 + minVal * Math.cos(Math.toRadians(minAngle));
			double offX = minVal * Math.sin(Math.toRadians(minAngle));
			double error = Math.atan(offX / offY);
			nav.turnTo(odo.getTheta() + error, true);
			return approachBlock();
			// Turn to block (Relate angle of sensor to angle of block
		}

		LocalEV3.get().getAudio().systemSound(1);
		
		// Stage 2 - Search for non-angled blocks

		// TODO: Allow travel in other direction
		while (Math.abs(odo.getX() -xCorner) < 30 && Math.abs(  yCorner - odo.getY()) < 30) {
			LocalEV3.get().getTextLCD().clear();
			LocalEV3.get().getTextLCD().drawInt(USS.getProcessedDistance(), 4, 4);
			nav.setSpeeds(100, 100);

			if (USS.getProcessedDistance() < 25) {
			nav.goForward(12);
			nav.setSpeeds(0, 0);
				nav.turnTo(odo.getTheta() + Math.PI/2, true);
				return approachBlock();
			}
		}
		nav.setSpeeds(0, 0);
		return false;

	}

	public boolean approachBlock() {
		LocalEV3.get().getAudio().systemSound(0);
		scan.turnTo(0);
		nav.setSpeeds(50, 50);
		while (USS.getProcessedDistance() > 2) {

		}
		nav.setSpeeds(0, 0);
		// if (detector.isFlagDetected())
		// return true;
		return false;

	}

}