package CaptureTheFlag;

public interface SensorController {
	
	/**
	 * @param distance
	 */
	public void processData(int distance);
	
	/**
	 * 
	 * @return int distance
	 */
	public int readData();
	
}