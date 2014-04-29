package uk.ac.ebi.cytocopter.internal.ui.utils;

import java.awt.GridBagConstraints;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;

public class LayoutUtils {

	public static GridBagConstraints createGridBagConstraints (int gridx, int gridy) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		return c;
	}
	
	public static DefaultComboBoxModel getAllCyNetworkComboBoxModel (CyServiceRegistrar cyServiceRegistrar) {
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
 		
		Set<CyNetwork> cyNetworks = cyServiceRegistrar.getService(CyNetworkManager.class).getNetworkSet();		
		for (CyNetwork cyNetwork : cyNetworks) {
			String cyNetworkName = cyNetwork.getRow(cyNetwork).get(CyNetwork.NAME, String.class);
			comboBoxModel.addElement(cyNetworkName);
		}
		
		return comboBoxModel;
	}
	
}
