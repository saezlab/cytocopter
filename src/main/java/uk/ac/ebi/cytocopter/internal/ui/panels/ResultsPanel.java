package uk.ac.ebi.cytocopter.internal.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;


@SuppressWarnings("serial")
public class ResultsPanel extends JPanel implements CytoPanelComponent {

	private CyServiceRegistrar cyServiceRegistrar;

	private JPanel toolBarPanel;
	private JButton previousPlot;
	private JButton nextPlot;

	private JPanel plotPanel;

	private List<JPanel> plotList;
	
	private int currentPlotIndex;


	public ResultsPanel (CyServiceRegistrar cyServiceRegistrar) {
		this.cyServiceRegistrar = cyServiceRegistrar;

		plotList = new ArrayList<JPanel>();

		// Define Panel properties
		setLayout(new BorderLayout());
		setSize(new Dimension(600, 400));
		setPreferredSize(new Dimension(600, 400));

		// Create Panels
		createNorthPanel();
		createCentrePanel();

		// Initialise Panels
		initialiseNorthPanel();
	}

	private void createNorthPanel () {
		toolBarPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		previousPlot = new JButton("<");
		nextPlot = new JButton(">");

		toolBarPanel.add(previousPlot);
		toolBarPanel.add(nextPlot);

		add(toolBarPanel, BorderLayout.NORTH);
	}

	private void createCentrePanel () {
		plotPanel = new JPanel(new BorderLayout());
		add(plotPanel, BorderLayout.CENTER);
	}

	private void initialiseNorthPanel () {
		nextPlot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					showNextPlot();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		previousPlot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					showPreviousPlot();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/** 
	 * From the current plot index shows the plot in the index after.
	 * 
	 * @throws Exception
	 */
	public void showNextPlot () throws Exception {
		if (currentPlotIndex+1 < plotList.size()) {
			currentPlotIndex++;
			showPlot(plotList.get(currentPlotIndex));
		}
	}

	/** 
	 * From the current plot index shows the plot in the index before.
	 * 
	 * @throws Exception
	 */
	public void showPreviousPlot () throws Exception {
		if (currentPlotIndex-1 >= 0) {
			currentPlotIndex--;
			showPlot(plotList.get(currentPlotIndex));
		}
	}

	/**
	 * This method removes all the components in the plot panel and displays the given plot. 
	 * 
	 * @throws Exception
	 */
	public void showPlot (JPanel jpanelPlot) throws Exception {
		clearPlotPanel();
		
		
		plotPanel.add(jpanelPlot);		

		showPanel();
	}
	
	public void clearPlotPanel () {
		plotPanel.removeAll();
	}

	/** 
	 * Checks if Result Panel, CytoPanelName.EAST, is visible if not display it and focus on Cytocopter result pane.
	 * 
	 */
	public void showPanel () {
		CytoPanel cytoPanel = cyServiceRegistrar.getService(CySwingApplication.class).getCytoPanel(CytoPanelName.EAST);

		if (cytoPanel.getState() == CytoPanelState.HIDE) 
			cytoPanel.setState(CytoPanelState.DOCK);

		int index = cytoPanel.indexOfComponent(this);

		if (index != -1) 
			cytoPanel.setSelectedIndex(index);
	}

	/**
	 * Receives a SVG plot file and appends it to the plot list and it is added as the current plot.
	 * 
	 * @param plotFile
	 * @throws Exception
	 */
	
	public void appendJPanelPlot(JPanel jpanelPlot) throws Exception
	{
		plotList.add(jpanelPlot);
		currentPlotIndex = plotList.size() - 1;
		showPlot(jpanelPlot);
		
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
