package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import uk.ac.ebi.cytocopter.internal.CyActivator;
import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.NodeTypeAttributeEnum;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.CommandExecutor;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.NetworkAttributes;
import uk.ac.ebi.cytocopter.internal.mahdimidas.CNO;
import uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling.CNONetwork;
import uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling.NetworkFactory;
import uk.ac.ebi.cytocopter.internal.mahdiplotting.ContainerPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.LogPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.ResultsPanel;
import uk.ac.ebi.cytocopter.internal.utils.CytoPanelUtils;
import uk.ac.ebi.cytocopter.internal.utils.MSutils;

public class PreprocessTask extends AbstractTask implements ObservableTask
{

	private boolean useControlPanel;
	private boolean displayResults;
	private boolean displayNetworkAnnotation;

	private CyServiceRegistrar cyServiceRegistrar;

	private ControlPanel controlPanel;
	private ResultsPanel resultsPanel;
	private LogPanel logPanel;

	private StringBuilder outputString;
	private DateFormat dateFormat;

	@Tunable(description = "midasFile", context = "nogui")
	public String midasFile = "";

	@Tunable(description = "networkName", context = "nogui")
	public String networkName = "";

	public PreprocessTask(CyServiceRegistrar cyServiceRegistrar, boolean useControlPanel, boolean displayResults,
			boolean displayNetworkAnnotation)
	{
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.useControlPanel = useControlPanel;
		this.displayResults = displayResults;
		this.displayNetworkAnnotation = displayNetworkAnnotation;
		this.outputString = new StringBuilder();
		this.dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	}

	// cytocopter preprocess
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception
	{

		taskMonitor.setTitle("Cytocopter - Preprocessing...");

		// Get necessary attributes from control panel otherwise from tunables.
		if (useControlPanel)
		{
			controlPanel = (ControlPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, ControlPanel.class,
					CytoPanelName.WEST);
			resultsPanel = (ResultsPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, ResultsPanel.class,
					CytoPanelName.EAST);
			logPanel = (LogPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, LogPanel.class, CytoPanelName.SOUTH);


			networkName = MSutils.getJavaCorrectPath(MSutils.getWindowsCorrectPath(controlPanel.getNetworkValue()));
			midasFile = MSutils.getJavaCorrectPath(MSutils.getWindowsCorrectPath(controlPanel.getMidasFilePath()));
		}


		// Focus selected network
		CommandExecutor.execute("network set current network=" + networkName, cyServiceRegistrar);

		// Export selected network to sif
		File networkFile2 = File.createTempFile(networkName + "_" + "temp", ".sif");
                String networkFile = networkFile2.getAbsolutePath();
                networkFile2.delete();
		CommandExecutor.execute("network export OutputFile=\""
				+ MSutils.getWindowsCorrectPath(networkFile) + "\"" + " options=sif",
				cyServiceRegistrar);
                                

		NetworkFactory networkFactory = new NetworkFactory();
		CNONetwork cnoNetwork = networkFactory.importNetwork(networkFile.toString());

		CNO midas = new CNO(midasFile);
		cnoNetwork.setMidas(midas);
		cnoNetwork.compress();
		cnoNetwork.expand();

		String[] stimuliArray = midas.namesStimuli().toArray(new String[0]);
		String[] inhibitorsArray = midas.namesInhibitors().toArray(new String[0]);
		String[] readoutArray = midas.namesSignals().toArray(new String[0]);
		String[] compressedArray = cnoNetwork.compressedNodes().toArray(new String[0]);


		List<Double> timeSignalsList = midas.timeSignals();
		double[] timeSignals = new double[timeSignalsList.size()];
		for (int i = 0; i < timeSignals.length; i++)
		{
			timeSignals[i] = timeSignalsList.get(i).doubleValue();
		}

		String loadModelOutput = "\nYour data set comprises " + midas.combinations_of_timepoint_and_treatment()
				+ " conditions (i.e. combinations of time point and treatment)";
		String loadMidasOutput = "\nYour data set comprises measurements on " + midas.number_of_DV_columns()
				+ " different species";
		String createCNOListOutput = "\nYour data set comprises " + midas.number_of_TR_columns()
				+ " stimuli/inhibitors and 1 cell line(s) ( Cell )";
		String measuredNodeNames = "\nThe following species are measured: " + midas.namesSignals();
		String stimulatedNodeNames = "\nThe following species are stimulated: " + midas.namesStimuli();
		String inhibitedNodeNames = "\nThe following species are inhibited: " + midas.namesInhibitors();
		String compressedNodeNames = "\nThe following species are compressed: " + cnoNetwork.compressedNodes();

		// Add output
		outputString.append(
				"[" + dateFormat.format(Calendar.getInstance().getTime()) + "] " + "Cytocopter Preprocessing" + "\n");
		outputString.append("Network: " + networkName + "\n");
		outputString.append("MIDAS: " + FilenameUtils.getName(midasFile) + "\n");
		outputString.append(loadModelOutput);
		outputString.append(loadMidasOutput);
		outputString.append(createCNOListOutput);
		outputString.append(measuredNodeNames);
		outputString.append(stimulatedNodeNames);
		outputString.append(inhibitedNodeNames);
		outputString.append(compressedNodeNames);
		outputString.append("\n");

		// Mahdi
		ContainerPanel cnolistPlot = new ContainerPanel(midas);

		// Annotate selected network
		if (displayNetworkAnnotation)
		{
			// Remove Node Type attribute in case it already exists to reset the
			// existing values
			NetworkAttributes.removeNodeTypeAttribute(networkName, NodeTypeAttributeEnum.NA, cyServiceRegistrar);

			// Identify inhibited readouts
			Collection<String> inhibitedReadouts = NodeTypeAttributeEnum.intersect(inhibitorsArray, readoutArray);

			// Add aatributes to nodes
			NetworkAttributes.addNodeTypeAttribute(networkName, stimuliArray, NodeTypeAttributeEnum.STIMULATED,
					cyServiceRegistrar);
			NetworkAttributes.addNodeTypeAttribute(networkName, inhibitorsArray, NodeTypeAttributeEnum.INHIBITED,
					cyServiceRegistrar);
			NetworkAttributes.addNodeTypeAttribute(networkName, readoutArray, NodeTypeAttributeEnum.READOUT,
					cyServiceRegistrar);
			NetworkAttributes.addNodeTypeAttribute(networkName, compressedArray, NodeTypeAttributeEnum.COMPRESSED,
					cyServiceRegistrar);
			NetworkAttributes.addNodeTypeAttribute(networkName, inhibitedReadouts,
					NodeTypeAttributeEnum.INHIBITED_READOUT, cyServiceRegistrar);

			// Apply visual style
			String applyVisualStyleCommand = "vizmap apply styles=" + CyActivator.visualStyleName;
			CommandExecutor.execute(applyVisualStyleCommand, cyServiceRegistrar);
		}

		// Display results if in gui context
		if (displayResults)
		{
			// Create model for data point combo box
			DefaultComboBoxModel dataPointModel = new DefaultComboBoxModel();
			for (int i = 1; i < timeSignals.length; i++)
				dataPointModel.addElement(timeSignals[i]);

			// Set combo box model and check status of time point combo box
			controlPanel.dataPointCombo.setModel(dataPointModel);
			controlPanel.setTimePointComboBoxStatus();

			// Add plot to results panel
			resultsPanel.appendJPanelPlot(cnolistPlot);

			// Append output to log panel
			logPanel.appendLog(outputString.toString());
		}

	}

	@Override
	public <R> R getResults(Class<? extends R> type)
	{
		return type.cast(outputString.toString());
	}
}
