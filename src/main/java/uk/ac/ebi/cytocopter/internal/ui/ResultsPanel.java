package uk.ac.ebi.cytocopter.internal.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;

import uk.ac.ebi.cyrface.internal.utils.PlotsDialog.Attributes;
import uk.ac.ebi.cyrface.internal.utils.SVGPlots;

@SuppressWarnings("serial")
public class ResultsPanel extends JPanel implements CytoPanelComponent {

	private CyServiceRegistrar cyServiceRegistrar;

	private JPanel toolBarPanel;
	private JButton savePlot;
	private JButton previousPlot;
	private JButton nextPlot;

	private JPanel plotPanel;
	private JSVGCanvas canvas;
	private SVGPlots plot;

	private List<File> plotList;
	private int currentPlotIndex;


	public ResultsPanel (CyServiceRegistrar cyServiceRegistrar) {
		this.cyServiceRegistrar = cyServiceRegistrar;

		plotList = new ArrayList<File>();

		// Define Panel properties
		setLayout(new BorderLayout());
		setSize(new Dimension(650, 400));
		setPreferredSize(new Dimension(650, 400));

		// Create Panels
		createNorthPanel();
		createCentrePanel();

		// Initialise Panels
		initialiseNorthPanel();
	}

	private void createNorthPanel () {
		toolBarPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		savePlot = new JButton("Save");
		previousPlot = new JButton("<");
		nextPlot = new JButton(">");

		toolBarPanel.add(savePlot);
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

		savePlot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					JFileChooser fc = new JFileChooser();
					fc.setDialogTitle(Attributes.SAVE_R_PLOT_MENU_NAME.text);
					int browserReturn = fc.showSaveDialog(null);

					if (browserReturn == JFileChooser.APPROVE_OPTION) {
						String savePath = fc.getSelectedFile().getAbsolutePath();
						File plotFile = plotList.get(currentPlotIndex);

						savePath = savePath + "." + FilenameUtils.getExtension(plotFile.getName());

						File destinationFile = new File(savePath);
						FileUtils.copyFile(plotFile, destinationFile);
					}
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
	public void showPlot (File plotFile) throws Exception {
		clearPlotPanel();
		
		plot = new SVGPlots(plotFile);
		canvas = plot.createPlotPanel();
		
		plotPanel.add(canvas, BorderLayout.CENTER);		

		showPanel();
	}
	
	public void clearPlotPanel () {
		if (canvas != null) {
			plotPanel.remove(canvas);
			canvas.removeAll();
			canvas.dispose();
		}
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
	public void appendSVGPlot (File plotFile) throws Exception {
		plotList.add(plotFile);
		currentPlotIndex = plotList.size() - 1;
		showPlot(plotFile);
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
