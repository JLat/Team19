package CaptureTheFlag;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class that takes care of Logging data during a program run.
 * 
 * @author Fabrice
 *
 */
public class Logger {
	public static PrintWriter writer;

	public static ArrayList<String> visibleLogs = new ArrayList<String>();

	
	/** creates a Logger using the given fileName.
	 * @param filePath
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public Logger(String filePath){
		
		try {
			writer = new PrintWriter(filePath, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// print the error.
			e.printStackTrace();
		}
		
		

		// getting current date and time using Date class
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date today = new Date();
		writer.println("Log of: " + format.format(today));
	}

	/**
	 * Adds a piece of information to the LOG file. In order for the message to
	 * appear, it is needed that its class name be added to the visibleLogs list
	 * using the addClass(String className) method.
	 * 
	 * @param data
	 *            The message to pass in.
	 */
	public static void log(Object message) {
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

		// verifying that the entry's class has logging enabled.
		if (visibleLogs.contains(className)) {
			writer.println("[" + className + "." + methodName + "]: \t" + message);
		} else {

			// the entry's caller class was not part of the visibleLogs list,
			// and so it is not shown.
		}
	}

	/**
	 * method that adds the desired class to the visibleLogs list.
	 * 
	 * @param className
	 *            the name of the class to add (needs to have
	 *            CaptureTheFlag.CLASSNAMEHERE format, and exact syntax).
	 */
	public static void addClass(String className) {
		visibleLogs.add(className);
	}

	/**
	 * removes the
	 * 
	 * @param className
	 *            name of the class to be removed.
	 * @CAREFUL: Never tested.
	 */
	public static void removeClass(String className) {
		visibleLogs.remove(className);
	}

}
