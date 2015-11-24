package Tests;

import CaptureTheFlag.Logger;

public class LoggerTest {

	public static void main(String[] args){

		//put this block of code anywhere to add some logs.
		
		new Logger("LOG.txt");
		
		// add the EXACT name of your class here.
		Logger.visibleLogs.add("Tests.LoggerTest");
		
		Logger.log("this is your message");
		
		// closes the writer and prints the messages.
		Logger.close();


	}

}
