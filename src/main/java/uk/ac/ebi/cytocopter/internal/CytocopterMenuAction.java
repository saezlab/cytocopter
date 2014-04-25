package uk.ac.ebi.cytocopter.internal;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.service.util.CyServiceRegistrar;

@SuppressWarnings("serial")
public class CytocopterMenuAction extends AbstractCyAction {

	public CytocopterMenuAction(CyServiceRegistrar cyServiceRegistrar, final String menuTitle) {
		super(menuTitle, cyServiceRegistrar.getService(CyApplicationManager.class), null, null);
		setPreferredMenu("Apps");
		
	}

	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null, "Hello Cytoscape World!");
	}
}
