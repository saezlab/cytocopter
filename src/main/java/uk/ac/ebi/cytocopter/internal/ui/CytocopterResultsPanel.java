package uk.ac.ebi.cytocopter.internal.ui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;

@SuppressWarnings("serial")
public class CytocopterResultsPanel extends JPanel implements CytoPanelComponent {

	public CyServiceRegistrar cyServiceRegistrar;
	
	public CytocopterResultsPanel (CyServiceRegistrar cyServiceRegistrar) {
		this.cyServiceRegistrar = cyServiceRegistrar;
	}
	
	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public String getTitle() {
		return "Cytocopter";
	}

}
