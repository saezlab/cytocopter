package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;

import org.apache.commons.io.FilenameUtils;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;
import uk.ac.ebi.cytocopter.internal.CyActivator;
import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.NodeTypeAttributeEnum;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.CommandExecutor;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.NetworkAttributes;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.LogPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.ResultsPanel;
import uk.ac.ebi.cytocopter.internal.utils.CytoPanelUtils;

public class PreprocessTask extends AbstractTask implements ObservableTask {

	private boolean useControlPanel;
	private boolean displayResults;
	private boolean displayNetworkAnnotation;
	
	private CyServiceRegistrar cyServiceRegistrar;
	private RserveHandler connection;
	
	private ControlPanel controlPanel;
	private ResultsPanel resultsPanel;
	private LogPanel logPanel;

	private StringBuilder outputString;
	private DateFormat dateFormat;

	@Tunable(description="midasFile", context="nogui")
    public String midasFile = "";
	
	@Tunable(description="networkName", context="nogui")
    public String networkName = "";
	
	
	public PreprocessTask (CyServiceRegistrar cyServiceRegistrar, boolean useControlPanel, boolean displayResults, boolean displayNetworkAnnotation) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.useControlPanel = useControlPanel;
		this.displayResults = displayResults;
		this.displayNetworkAnnotation = displayNetworkAnnotation;
		this.outputString = new StringBuilder();
		this.dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	}
	
	// cytocopter preprocess midasFile=/Users/emanuel/files.cytocopter/ToyModelPB.csv networkName=PKN-ToyPB.sif

	@Override
	public void run (TaskMonitor taskMonitor) throws Exception {
		
		taskMonitor.setTitle("Cytocopter - Preprocessing...");
		
		// Get necessary attributes from control panel otherwise from tunables.
		if (useControlPanel) {
			controlPanel = (ControlPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, ControlPanel.class, CytoPanelName.WEST);
			resultsPanel = (ResultsPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, ResultsPanel.class, CytoPanelName.EAST);
			logPanel = (LogPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, LogPanel.class, CytoPanelName.SOUTH);
			
			connection = controlPanel.connection;
			
			networkName = controlPanel.getNetworkValue();
			midasFile = controlPanel.getMidasFilePath();
		}
		
		// Check if connection is established
		if (connection == null) {
			connection = new RserveHandler(cyServiceRegistrar);
			
			// Save connection in control panel
			if (useControlPanel) controlPanel.connection = connection;
		}

		// Configure CellNOptR R package
		ConfigureCellnoptrTaskFactory configureCellnoptr = new ConfigureCellnoptrTaskFactory(cyServiceRegistrar, true);
		CommandExecutor.execute(configureCellnoptr.createTaskIterator(), cyServiceRegistrar);
		
		// Focus selected network
		CommandExecutor.execute("network set current network=" + networkName, cyServiceRegistrar);
		
		// Export selected network to sif
		File networkFile = File.createTempFile(networkName + "_" + "temp", ".sif");
		CommandExecutor.execute("network export OutputFile=" + networkFile.getAbsolutePath() + " options=sif", cyServiceRegistrar);
		
		// Load model network
		String loadModelCommand = "model <- readSIF(sifFile = '" + networkFile.getAbsolutePath() + "')";
		String loadModelOutput = connection.executeParseOutput(loadModelCommand);
		
		// Load midas file
		String loadMidasCommand = "data <- readMIDAS(MIDASfile = '" + midasFile + "')";
		String loadMidasOutput = connection.executeParseOutput(loadMidasCommand);
		
		// Create CNO List
		String createCNOListCommand = "cnolist <- makeCNOlist(dataset = data, subfield = F)";
		String createCNOListOutput = connection.executeParseOutput(createCNOListCommand);
		
		// Check if data and model matches
		String checkSignalsCommand = "checkSignals(cnolist, model)";
		String checkSignalsOutput = connection.executeParseOutput(checkSignalsCommand);
		
		// Finder indices
		String finderIndicesCommand = "indices <- indexFinder(cnolist, model, verbose = T)";
		String finderIndicesOutput = connection.executeParseOutput(finderIndicesCommand);
		
		// Finding and cutting the non observable and non controllable species
		String findNONCCommand = "noncindices <- findNONC(model, indices, verbose = T)";
		String findNONCOutput = connection.executeParseOutput(findNONCCommand);
		
		String cutNONCCommand = "ncnocut <- cutNONC(model, noncindices)";
		String cutNONCOutput = connection.executeParseOutput(cutNONCCommand);
		
		String indexFinderCommand = "cutindices <- indexFinder(cnolist, ncnocut)";
		String indexFinderOutput = connection.executeParseOutput(indexFinderCommand);
		
		// Compressing the model
		String compressModelCommand = "cutcomp <- compressModel(ncnocut, cutindices)";
		String compressModelOutput = connection.executeParseOutput(compressModelCommand);
		
		String indexFinderCutCommand = "indicescutcomp <- indexFinder(cnolist, cutcomp)";
		String indexFinderCutOutput = connection.executeParseOutput(indexFinderCutCommand);

		// Expanding the gates
		String expandGatesCommand = "cutcompexp <- expandGates(cutcomp)";
		String expandGatesOutput = connection.executeParseOutput(expandGatesCommand);
		
		// Preparing for model and data training
		String residualErrorCommand = "errorcnolist <- residualError(cnolist)";
		String residualErrorOutput = connection.executeParseOutput(residualErrorCommand);
		
		String prepForSimCommand = "fields4Sim <- prep4sim(cutcompexp)";
		String prepForSimOutput = connection.executeParseOutput(prepForSimCommand);
		
		String bStringCommand = "bstring <- rep(1, length(cutcompexp$reacID))";
		String bStringOuput = connection.executeParseOutput(bStringCommand);
		
		// Plot cno list
		String plotCnolistCommand = "plotCNOlist(cnolist)";
		File cnolistPlot = connection.executeReceivePlotFile(plotCnolistCommand, "cnolist");
		
		// Get node types
		String[] stimuliArray = connection.executeReceiveStrings("cnolist$namesStimuli");
		String[] inhibitorsArray = connection.executeReceiveStrings("cnolist$namesInhibitors");
		String[] readoutArray = connection.executeReceiveStrings("cnolist$namesSignals");
		String[] compressedArray = connection.executeReceiveStrings("cutcompexp$speciesCompressed");
		
		// Get time signals
		double[] timeSignals = connection.executeReceiveDoubles("cnolist$timeSignals");
		
		// Add output
		outputString.append("[" + dateFormat.format(Calendar.getInstance().getTime()) + "] " + "Cytocopter Preprocessing" + "\n");
		outputString.append("Network: " + networkName + "\n");
		outputString.append("MIDAS: " + FilenameUtils.getName(midasFile) + "\n");
		outputString.append(loadModelOutput);
		outputString.append(loadMidasOutput);
		outputString.append(createCNOListOutput);
		outputString.append(finderIndicesOutput);
		outputString.append(findNONCOutput);
		outputString.append("\n");
		
		// Annotate selected network
		if (displayNetworkAnnotation) {
			// Remove Node Type attribute in case it already exists to reset the existing values
			NetworkAttributes.removeNodeTypeAttribute(networkName, NodeTypeAttributeEnum.NA, cyServiceRegistrar);
			
			// Identify inhibited readouts
			Collection<String> inhibitedReadouts = NodeTypeAttributeEnum.intersect(inhibitorsArray, readoutArray);
			
			// Add aatributes to nodes
			NetworkAttributes.addNodeTypeAttribute(networkName, stimuliArray, NodeTypeAttributeEnum.STIMULATED, cyServiceRegistrar);
			NetworkAttributes.addNodeTypeAttribute(networkName, inhibitorsArray, NodeTypeAttributeEnum.INHIBITED, cyServiceRegistrar);
			NetworkAttributes.addNodeTypeAttribute(networkName, readoutArray, NodeTypeAttributeEnum.READOUT, cyServiceRegistrar);
			NetworkAttributes.addNodeTypeAttribute(networkName, compressedArray, NodeTypeAttributeEnum.COMPRESSED, cyServiceRegistrar);
			NetworkAttributes.addNodeTypeAttribute(networkName, inhibitedReadouts, NodeTypeAttributeEnum.INHIBITED_READOUT, cyServiceRegistrar);
			
			// Apply visual style
			String applyVisualStyleCommand = "vizmap apply styles=" + CyActivator.visualStyleName;
			CommandExecutor.execute(applyVisualStyleCommand, cyServiceRegistrar);
		}
		
		// Display results if in gui context
		if (displayResults) {
			// Create model for data point combo box
			DefaultComboBoxModel dataPointModel = new DefaultComboBoxModel();
			for (int i = 1; i < timeSignals.length; i++)
				dataPointModel.addElement(timeSignals[i]);
			
			// Set combo box model and check status of time point combo box
			controlPanel.dataPointCombo.setModel(dataPointModel);
			controlPanel.setTimePointComboBoxStatus();
			
			// Add plot to results panel
			resultsPanel.appendSVGPlot(cnolistPlot);
			
			// Append output to log panel
			logPanel.appendLog(outputString.toString());
		}
			
	}
	
	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
