package CaptureTheFlag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import wifi.StartCorner;
import wifi.Transmission;
import wifi.WifiConnection;

public class Initializer {
	private static Port usPort = LocalEV3.get().getPort("S1");
	private static Port colorPort1 = LocalEV3.get().getPort("S3");
	private static Port colorPort2 = LocalEV3.get().getPort("S4");
	private static Port identifierPort = LocalEV3.get().getPort("S2");
	private static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static EV3MediumRegulatedMotor sensorMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
	private static EV3MediumRegulatedMotor clawMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final int bandCenter = 18;
	private static final int bandWidth = 4;
	private static double wheelRadius, track;
	public static TextLCD t;
	public static UltrasonicPoller usPoller;

	private static final String SERVER_IP = "192.168.10.200";
	private static final int TEAM_NUMBER = 19;
	// TODO modify this according to how the info will be received via wifi
	private static String flag = "";
	
	public static int homeZoneBL_X;
	public static int homeZoneBL_Y;
	public static int opponentHomeZoneBL_X;
	public static int opponentHomeZoneBL_Y;
	public static int dropZone_X;
	public static int dropZone_Y;
	public static int opponentFlagType;

	/**
	 * Initializes all the objects in the following order : 1- UltrasonicPoller
	 * 2- LightPoller 3- Identifier 4- Odometer 5- LCDdisplay 6- Navigator 7-
	 * Localizer 8- Brain
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args){
		
//		//creating the Logger class;
//		new Logger("LOG.txt");
//		
//		// adding the classes that will produce visible logs.
//		// TODO: comment out classes that you don't want appearing in the log file.
//		Logger.addClass("CaptureTheFlag.Brain");
//		Logger.addClass("CaptureTheFlag.Claw");
//		Logger.addClass("CaptureTheFlag.Identifier");
//		Logger.addClass("CaptureTheFlag.Initializer");
//		//Logger.addClass("CaptureTheFlag.LCDdisplay");
//		Logger.addClass("CaptureTheFlag.LightLocalization");
//		Logger.addClass("CaptureTheFlag.LightPoller");
//		Logger.addClass("CaptureTheFlag.Navigation");
//		//Logger.addClass("CaptureTheFlag.Odometer");
//		//Logger.addClass("CaptureTheFlag.Point3D");
//		Logger.addClass("CaptureTheFlag.Scanner");
//		Logger.addClass("CaptureTheFlag.Search");
//		//Logger.addClass("CaptureTheFlag.UltrasonicPoller");
//		Logger.addClass("CaptureTheFlag.USLocalizer");
//		
//		// example of a call.
//		Logger.log("Starting Program");
//		Logger.log("Please: add some log calls inside your methods!");
		
		
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		odometer.start();
		usPoller = new UltrasonicPoller(10, 10, 20, 100, 0);
		usPoller.start();
		LightPoller colorPoller = new LightPoller(colorPort1, colorPort2, "Red");
		colorPoller.start();
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor, colorPoller);
		Identifier detector = new Identifier(identifierPort, "RGB", flag);
		detector.start();
		LCDdisplay display = new LCDdisplay(odometer, usPoller, colorPoller, detector);
		Claw claw = new Claw(clawMotor);
		USLocalizer USLoc = new USLocalizer(navigator, odometer, usPoller, display);
		LightLocalization Lloc = new LightLocalization(navigator, colorPoller, display);
		Search search = new Search(odometer, navigator, usPoller, display, detector, claw);
		Brain controller = new Brain(odometer, navigator, USLoc, Lloc, detector, usPoller, search, claw);
		int flagType = 3;
		String[] flags = { "light blue", "red", "yellow", "white", "dark blue" };

//		
//
//		WifiConnection conn = null;
//		try {
//			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
//		} catch (IOException e) {
//			display.addInfo("Connection failed", 0);
//			LocalEV3.get().getAudio().systemSound(0);
//		}
//
//		// example usage of Transmission class
//		Transmission t = conn.getTransmission();
//		if (t == null) {
//			display.addInfo("Failed to read transmission", 0);
//		} else {
//			StartCorner corner = t.startingCorner;
//			homeZoneBL_X = t.homeZoneBL_X;
//			homeZoneBL_Y = t.homeZoneBL_Y;
//			opponentHomeZoneBL_X = t.opponentHomeZoneBL_X;
//			opponentHomeZoneBL_Y = t.opponentHomeZoneBL_Y;
//			dropZone_X = t.dropZone_X;
//			dropZone_Y = t.dropZone_Y;
//			flagType = t.flagType;
//			opponentFlagType = t.opponentFlagType;
//		}

		detector.setFlag(flags[flagType - 1]);
		display.clearAdditionalInfo();
		display.addInfo("Connected", 0);
		// display.addInfo(flags[flagType -1], flagType - 1);
		//
		//
		//
		//
		//
		// display.addInfo("distance");
		// display.addInfo("red");
		// display.addInfo("green");
		// display.addInfo("blue");
		controller.search();
		display.addInfo("distance", usPoller.getProcessedDistance());
		
//		
//		Logger.log("Closing Program");
//		Logger.close();

	}

	public static EV3MediumRegulatedMotor getSensorMotor() {
		return sensorMotor;
	}

	public static Port getUsPort() {
		return usPort;
	}

	public static UltrasonicPoller getUsPoller() {
		return usPoller;
	}
}
