package CaptureTheFlag;

public class Avoid {
	Navigation navigator;
	UltrasonicPoller usPoller;
	
	/**
	 * Class constructor : takes in an instance of Navigation and UltrasonicPoller given by
	 * 						the Initializer class.
	 * @param navigator
	 * @param usPoller
	 */
	public Avoid(Navigation navigator, UltrasonicPoller usPoller) {
		this.navigator = navigator;
		this.usPoller = usPoller;
	}
	
	/**
	 * Run method for the Thread. Contains the obstacle avoidance algorithm 
	 */
	public void run() {
		
	}

}