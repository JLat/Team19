package Tests;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


import CaptureTheFlag.Logger;

public class LoggerTest {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

		new Logger("LOG.csv");
		Logger.visibleLogs.add("Tests.LoggerTest");
		Logger.log("inside LoggerTest");
		Logger.writer.close();


	}

}
