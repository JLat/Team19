package Tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Collect {
	public PrintWriter writer;

	public Collect() throws InterruptedException, FileNotFoundException, UnsupportedEncodingException {
		writer = new PrintWriter("data.csv", "UTF-8");
	}
	
	public Collect(String filePath) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException {
		writer = new PrintWriter(filePath, "UTF-8");
	}
	
	//Call printData to save data to csv file
	public void printData(String Title, Object data) {
			writer.write(Title + ": " + data + " \n");
	}
	public void printData(Object data){
		writer.write(data+"\n");
	}

}
