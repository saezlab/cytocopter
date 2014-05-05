package uk.ac.ebi.cytocopter.internal.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;

@SuppressWarnings("serial")
public class LogPanel extends JPanel implements CytoPanelComponent {

	private CyServiceRegistrar cyServiceRegistrar;
	
	private JTextArea logPanel;
	private StringBuilder logText;
	
	public LogPanel (CyServiceRegistrar cyServiceRegistar) {
		this.cyServiceRegistrar = cyServiceRegistar;
		this.logText = new StringBuilder();
		
		// Define Panel properties
		setLayout(new BorderLayout());
		setSize(new Dimension(650, 400));
		setPreferredSize(new Dimension(650, 400));
		
		// Create panel
		createLogPanel();
	}
	
	private void createLogPanel () {
		logPanel = new JTextArea(logText.toString());
		logPanel.setEditable(false);
		logPanel.setRows(10);
		logPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		JScrollPane scrollLogPanel = new JScrollPane(logPanel);
		scrollLogPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		add(scrollLogPanel, BorderLayout.SOUTH);
	}
	
	/** 
	 * Checks if Result Panel, CytoPanelName.EAST, is visible if not display it and focus on Cytocopter result pane.
	 * 
	 */
	public void showPanel () {
		CytoPanel cytoPanel = cyServiceRegistrar.getService(CySwingApplication.class).getCytoPanel(CytoPanelName.SOUTH);

		if (cytoPanel.getState() == CytoPanelState.HIDE) 
			cytoPanel.setState(CytoPanelState.DOCK);

		int index = cytoPanel.indexOfComponent(this);

		if (index != -1) 
			cytoPanel.setSelectedIndex(index);
	}
	
	/**
	 * Appends the given log text to the existing log.
	 * 
	 * @param log
	 */
	public void appendLog (String log) {
		logText.append(log);
		logPanel.setText(logText.toString());
		
		showPanel();
	}

	
	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	@Override
	public String getTitle() {
		return "Cytocopter";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

}
