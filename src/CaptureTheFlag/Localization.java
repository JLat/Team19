package CaptureTheFlag;

public class Localization {
	
	private static Navigation navi;
	private static UltrasonicPoller usPoller;
	private static Odometer odo;
	
	public Localization (Navigation navigator, UltrasonicPoller usPoller) {
		Localization.navi = navigator;
		Localization.usPoller = usPoller;
		Localization.odo = navi.getOdometer();
	}
	/**
	 * Let the robot do localization and goes to origin
	 */
	private static void doLocalization(){
		
	}

}
