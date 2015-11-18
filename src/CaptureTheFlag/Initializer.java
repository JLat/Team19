package CaptureTheFlag;
import wifi.*;

import java.io.IOException;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;


public class Initializer {
	private static Port usPort = LocalEV3.get().getPort("S1");		
	private static Port colorPort = LocalEV3.get().getPort("S3");
	private static Port identifierPort = LocalEV3.get().getPort("S2");
	private static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static EV3MediumRegulatedMotor sensorMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
	private static EV3MediumRegulatedMotor clawMotor = new EV3MediumRegulatedMotor (LocalEV3.get().getPort("C"));
	private static final int bandCenter = 18; 
	private static final int bandWidth = 4;
	private static Avoid p;
	private static double wheelRadius, track;
	public static TextLCD t = LocalEV3.get().getTextLCD();
	//TODO modify this according to how the info will be received via wifi
	private static String flag = "red";
	
	private static final String SERVER_IP = "192.168.1.7";
	private static final int TEAM_NUMBER = 19;
	
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
		odometer.start();
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor);
		p = new Avoid(leftMotor, rightMotor, bandCenter, bandWidth, navigator, sensorMotor);
		UltrasonicPoller usPoller = new UltrasonicPoller(10, 10, 10, 100, 0);
		usPoller.start();
		LightPoller colorPoller = new LightPoller(colorPort, "Red");
		Identifier detector = new Identifier(identifierPort, "RGB", flag);
		detector.start();
		LCDdisplay display = new LCDdisplay(odometer, usPoller, colorPoller, detector);
		Claw claw = new Claw(clawMotor);
		USLocalizer usLocalizer = new USLocalizer(navigator, odometer, usPoller, display);
		Search search = new Search (odometer, navigator, usPoller,display,detector,claw	);
		Brain controller = new Brain(odometer, navigator, usLocalizer, detector, usPoller, search, claw);
		
		
		
		WifiConnection conn = null;
		try {
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
		} catch (IOException e) {
			display.addInfo("Connection failed");
		}
		Transmission t = conn.getTransmission();
		if (t == null) {
			display.addInfo("Transmission Failed");
		} else {
			
			//NOT NEEDED FOR DEMO
			/*StartCorner corner = t.startingCorner;
			int homeZoneBL_X = t.homeZoneBL_X;
			int homeZoneBL_Y = t.homeZoneBL_Y;
			int opponentHomeZoneBL_X = t.opponentHomeZoneBL_X;
			int opponentHomeZoneBL_Y = t.opponentHomeZoneBL_Y;
			int dropZone_X = t.dropZone_X;
			int dropZone_Y = t.dropZone_Y;
			int	opponentFlagType = t.opponentFlagType;	
			*/
			int flagType = t.flagType;
				
		}
		String [] flag = {"light_blue","red","yellow","white","dark_blue"};
		display.clearAdditionalInfo();
		
		
		
		display.addInfo("distance");
		display.addInfo("red");
		display.addInfo("green");
		display.addInfo("blue");
		controller.search();
	 
	}
	
	public static EV3MediumRegulatedMotor getSensorMotor(){
		return sensorMotor;
	}
	
	public static Port getUsPort(){
		return usPort;
	}
	
	public static Avoid getPController(){
		return p;
	}
}
