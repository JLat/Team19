package CaptureTheFlag;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;


public class Initializer {
	private static Port usPort = LocalEV3.get().getPort("S1");		
	private static Port identifierPort = LocalEV3.get().getPort("S2");
	private static Port colorPort = LocalEV3.get().getPort("S3");
	private static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static EV3LargeRegulatedMotor clawMotor;
	private static double wheelRadius, track;
	public static TextLCD t = LocalEV3.get().getTextLCD();
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
		UltrasonicPoller usPoller = new UltrasonicPoller(10, 10, 10, 200, 0);
		//LightPoller colorPoller = new LightPoller(colorPort, "Red");
		//Identifier identifier = new Identifier(identifierPort, "RGB", flag);
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor);
		Localization localizer = new Localization(navigator, usPoller, t);
		//LightLocalizer localizer2 = new LightLocalizer(navigator, colorPoller, t);
		//LCDdisplay display = new LCDdisplay(odometer, usPoller, colorPoller, identifier);
		//Brain controller = new Brain(odometer, navigator, localizer, localizer2, identifier, usPoller, display);
		usPoller.start();
		odometer.start();
		localizer.doLocalization();
		//colorPoller.start();
		//identifier.start();
		//controller.search();
	 
	}
}
