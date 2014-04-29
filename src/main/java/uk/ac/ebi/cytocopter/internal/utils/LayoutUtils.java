package uk.ac.ebi.cytocopter.internal.utils;

import java.awt.GridBagConstraints;

public class LayoutUtils {

	public static GridBagConstraints createGridBagConstraints (int gridx, int gridy) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		return c;
	}
	
}
