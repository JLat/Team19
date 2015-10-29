package CaptureTheFlag;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Collect {
	PrintWriter writer;

	public Collect() throws InterruptedException, FileNotFoundException, UnsupportedEncodingException {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy HH:mm");
		Calendar cal = Calendar.getInstance();
		
		writer = new PrintWriter("data.csv", "UTF - 8");
		writer.write("DATE:" + dateFormat.format(cal.getTime()));
		writer.write("");
	}
	
	//Call printData to save data to csv file
	public void printData(String Title, Object data) {
		try {
			writer.write(Title + ": " + data);
		} finally {
			writer.close();
		}
	}

}
