package CaptureTheFlag;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;


public class Initializer {
	private static Port usPort = LocalEV3.get().getPort("S1");		
	private static Port colorPort = LocalEV3.get().getPort("S2");
	private static Port identifierPort = LocalEV3.get().getPort("S3");
	private static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static EV3MediumRegulatedMotor sensorMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
	private static EV3LargeRegulatedMotor clawMotor;
	private static final int bandCenter = 18; 
	private static final int bandWidth = 4;
	private static Avoid p;
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
	 * 		5- LCDdisplay
	 *		6- Navigator
	 *		7- Localizer
	 *		8- Brain
	 * @param args
	 */
	public static void main(String [] args) {
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor);
		p = new Avoid(leftMotor, rightMotor, bandCenter, bandWidth, navigator, sensorMotor);
		UltrasonicPoller usPoller = new UltrasonicPoller(10, 10, 10, 100, 0, p, usPort);
		usPoller.start();
		LightPoller colorPoller = new LightPoller(colorPort, "Red");
		Identifier detector = new Identifier(identifierPort, "RGB", flag);
		LCDdisplay display = new LCDdisplay(odometer, usPoller, colorPoller, detector);
		Localization localizer = new Localization(navigator, usPoller);
		Search search = new Search (odometer, navigator, usPoller);
		Brain controller = new Brain(odometer, navigator, localizer, detector, usPoller, t, search);
		controller.search();
	 
	}
	
	public static EV3MediumRegulatedMotor getSensorMotor(){
		return sensorMotor;
	}
}
