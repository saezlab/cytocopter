package uk.ac.ebi.cytocopter.internal.utils;

public class RActionUtils {

	public static final String CAPTURE_FUNCTION_PREFIX = "messages <- capture.output(";
	public static final String CAPTURE_FUNCTION_SUFFIX = ")";
	
	
	public static String getWindowsCorrectPath (String filePath) {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
			return filePath.replace('\\', '/');
		return filePath;
	}
	
}
