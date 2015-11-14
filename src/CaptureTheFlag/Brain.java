package CaptureTheFlag;

import lejos.hardware.Sound;

public class Brain {
	
	private static Odometer odo;
	private static Navigation navi;
	private static Localization local;
	private static LightLocalizer local2;
	private static Identifier iden;
	private static UltrasonicPoller usPoller;
	private static LCDdisplay lcd;
	
	public Brain (Odometer odo, Navigation navi, Localization local, LightLocalizer local2,
				  Identifier iden, UltrasonicPoller usPoller, LCDdisplay l) {
		Brain.odo = odo;
		Brain.navi = navi;
		Brain.local = local;
		Brain.local2 = local2;
		Brain.iden = iden;
		Brain.usPoller = usPoller;
		Brain.lcd = l;
	}
	
	/**
	 * After initialization, the robot start searching
	 * which is the main function of the robot
	 */
	public void search(){
		Brain.odo.start();
		//Brain.local.doLocalization();
		Sound.beepSequenceUp();
		//Brain.local2.doLightLocalization();
	}
	
}
