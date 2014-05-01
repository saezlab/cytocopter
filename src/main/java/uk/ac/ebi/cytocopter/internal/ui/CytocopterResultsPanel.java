package uk.ac.ebi.cytocopter.internal.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;

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

import uk.ac.ebi.cyrface.internal.utils.SVGPlots;

@SuppressWarnings("serial")
public class CytocopterResultsPanel extends JPanel implements CytoPanelComponent {

	private CyServiceRegistrar cyServiceRegistrar;
	private JTextArea logPanel;
	private JPanel plotPanel;
	
	public CytocopterResultsPanel (CyServiceRegistrar cyServiceRegistrar) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		
		// Define Panel properties
		setLayout(new BorderLayout());
		setSize(new Dimension(650, 400));
		setPreferredSize(new Dimension(650, 400));
		
		// Add Plot Panel Centre
		plotPanel = new JPanel(new BorderLayout());
		add(plotPanel, BorderLayout.CENTER);
		
		// Add log Panel South
		logPanel = new JTextArea();
		logPanel.setEditable(false);
		logPanel.setRows(15);
		logPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JScrollPane scrollLogPanel = new JScrollPane(logPanel);
		scrollLogPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		add(scrollLogPanel, BorderLayout.SOUTH);
	}
	
	public void appendSVGPlot (File plotFile) throws Exception {
		plotPanel.removeAll();
		
		SVGPlots plot = new SVGPlots(plotFile);
		plotPanel.add(plot.createPlotPanel(), BorderLayout.CENTER);
		
		showPanel();
	}
	
	public void appendLog (String log) {
		logPanel.setText(logPanel.getText() + "\n" + log);
	}
	
	public void showPanel () {
		CytoPanel cytoPanel = cyServiceRegistrar.getService(CySwingApplication.class).getCytoPanel(CytoPanelName.EAST);
		
		if (cytoPanel.getState() == CytoPanelState.HIDE) 
			cytoPanel.setState(CytoPanelState.DOCK);
		
		int index = cytoPanel.indexOfComponent(this);
		
		if (index != -1) 
			cytoPanel.setSelectedIndex(index);
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
