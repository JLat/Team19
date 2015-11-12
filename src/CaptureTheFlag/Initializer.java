package CaptureTheFlag;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;


public class Initializer {
	private static Port usPort = LocalEV3.get().getPort("S1");		
	private static Port colorPort = LocalEV3.get().getPort("S2");
	private static Port identifierPort = LocalEV3.get().getPort("S3");
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static EV3LargeRegulatedMotor clawMotor;
	private static double wheelRadius, track;
	public static TextLCD t;
	//TODO modify this according to how the info will be received via wifi
	private static String flag = "blue";
	
	/**
	 * Initializes all the objects in the following order :
	 * 		1- UltrasonicPoller
	 * 		2- LightPoller
	 * 		3- Identifier
	 * 		4- Odometer
	 *		5- Navigator
	 *		6- Localizer
	 *		7- Brain
	 * @param args
	 */
	public static void main(String [] args) {
		UltrasonicPoller usPoller = new UltrasonicPoller(10, 10, 10, 100, 0);
		LightPoller colorPoller = new LightPoller(colorPort, "Red");
		Identifier detector = new Identifier(identifierPort, "RGB", flag);
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor);
		Localization localizer = new Localization(navigator, usPoller);
		Search search = new Search (odometer, navigator, usPoller);
		Brain controller = new Brain(odometer, navigator, localizer, detector, usPoller, t, search);
		controller.search();
	 
	}
}
