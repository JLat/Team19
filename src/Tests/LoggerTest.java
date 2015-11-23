package Tests;

import CaptureTheFlag.Logger;

public class LoggerTest {

	public static void main(String[] args){

		new Logger("LOG.txt");
		Logger.visibleLogs.add("Tests.LoggerTest");
		Logger.log("inside LoggerTest");
		Logger.writer.close();


	}

}
