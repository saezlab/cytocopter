package uk.ac.ebi.cytocopter.internal.utils;

public class RActionUtils {

	public static String getWindowsCorrectPath (String filePath) {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
			return filePath.replace('\\', '/');
		return filePath;
	}
	
}
