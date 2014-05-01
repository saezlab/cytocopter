package uk.ac.ebi.cytocopter.internal.utils;

import java.awt.Component;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;

public class CytoPanelUtils {

	public static CytoPanelComponent getCytoPanel (CyServiceRegistrar cyServiceRegistrar, Class<? extends CytoPanelComponent> panelClass) {
		CytoPanel cytoPanel = cyServiceRegistrar.getService(CySwingApplication.class).getCytoPanel(CytoPanelName.EAST);
		
		for (int i = 0; i < cytoPanel.getCytoPanelComponentCount(); i++) {
			Component panel = cytoPanel.getComponentAt(i);
			
			if (panelClass.isInstance(panel))
				return (CytoPanelComponent) panel;
		}
		
		return null;
	}
	
}
