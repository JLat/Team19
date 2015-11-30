package Tests;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import CaptureTheFlag.LCDdisplay;
import CaptureTheFlag.Navigation;
import CaptureTheFlag.Odometer;
import CaptureTheFlag.USLocalizer;
import CaptureTheFlag.UltrasonicPoller;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Delay;

public class USPollerParametersTest {
	public static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static EV3MediumRegulatedMotor clawMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	public static Odometer odo = new Odometer(leftMotor, rightMotor);
	private static UltrasonicPoller uss = new UltrasonicPoller(5, 15, 15, 50, 0);
	
	public static LCDdisplay lcd = new LCDdisplay(odo, uss, null, null);
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {
		
		
		Collect col = new Collect("USTEST_"+Arrays.toString(uss.saveParameters())+".csv");
		uss.start();
		
		LocalEV3.get().getAudio().systemSound(0);
		while(!uss.isFull());
		LocalEV3.get().getAudio().systemSound(0);
		
		
		
		odo.start();
		lcd.addInfo("distance");
		rightMotor.setSpeed(100);
		leftMotor.setSpeed(100);
		rightMotor.backward();
		leftMotor.forward();
		
		odo.setThetaDegrees(45);
		int angle = (int)odo.getThetaDegrees();
		while((int)odo.getThetaDegrees()!=44){
			int currentAngle = (int)odo.getThetaDegrees();
			if(currentAngle!=angle){
				col.printData((int)odo.getThetaDegrees()+","+uss.getProcessedDistance());
				angle = (int)odo.getThetaDegrees();
			}
		}
		rightMotor.stop(true);
		leftMotor.stop(false);
		
		col.writer.close();
		System.exit(0);
		
	}


}
