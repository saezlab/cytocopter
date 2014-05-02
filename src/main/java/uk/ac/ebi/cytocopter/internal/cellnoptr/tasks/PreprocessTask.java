package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import java.io.File;
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.apache.commons.io.FilenameUtils;
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
import uk.ac.ebi.cytocopter.internal.ui.CytocopterResultsPanel;
import uk.ac.ebi.cytocopter.internal.utils.CytoPanelUtils;

public class PreprocessTask extends AbstractTask implements ObservableTask {

	private CyServiceRegistrar cyServiceRegistrar;
	private RserveHandler connection;
	private StringBuilder outputString;
	private JComboBox dataPointCombo;

	@Tunable(description="midasFile", context="nogui")
    public String midasFile = "";
	
	@Tunable(description="networkName", context="nogui")
    public String networkName = "";
	
	
	public PreprocessTask (CyServiceRegistrar cyServiceRegistrar) {
		this (cyServiceRegistrar, null, null, null, null);
	}
	
	public PreprocessTask (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection) {
		this(cyServiceRegistrar, connection, null, null, null);
	}
	
	public PreprocessTask (CyServiceRegistrar cyServiceRegistrar, String midasFile, String networkName, JComboBox dataPointCombo) {
		this(cyServiceRegistrar, null, midasFile, networkName, dataPointCombo);
	}
	
	public PreprocessTask (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection, String midasFile, String networkName, JComboBox dataPointCombo) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.outputString = new StringBuilder();
		this.connection = connection;
		this.midasFile = midasFile;
		this.networkName = networkName;
		this.dataPointCombo = dataPointCombo;
	}
	
	// cytocopter preprocess midasFile=/Users/emanuel/files.cytocopter/ToyModelPB.csv networkName=PKN-ToyPB.sif

	@Override
	public void run (TaskMonitor taskMonitor) throws Exception {
		
		taskMonitor.setTitle("Cytocopter - Preprocessing...");
		
		// Check if connection is established
		if (connection == null) connection = new RserveHandler(cyServiceRegistrar);

		// Configure CellNOptR R package
		ConfigureCellnoptrTaskFactory configureCellnoptr = new ConfigureCellnoptrTaskFactory(cyServiceRegistrar, connection);
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
		
		CytocopterResultsPanel resultsPanel = (CytocopterResultsPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, CytocopterResultsPanel.class);
		resultsPanel.appendSVGPlot(cnolistPlot);
		
		// Get node types
		String[] stimuliArray = connection.executeReceiveStrings("cnolist$namesStimuli");
		NetworkAttributes.addAttribute(networkName, stimuliArray, NodeTypeAttributeEnum.STIMULATED, cyServiceRegistrar);
		
		String[] inhibitorsArray = connection.executeReceiveStrings("cnolist$namesInhibitors");
		NetworkAttributes.addAttribute(networkName, inhibitorsArray, NodeTypeAttributeEnum.INHIBITED, cyServiceRegistrar);
		
		String[] readoutArray = connection.executeReceiveStrings("cnolist$namesSignals");
		NetworkAttributes.addAttribute(networkName, readoutArray, NodeTypeAttributeEnum.READOUT, cyServiceRegistrar);
		
		String[] compressedArray = connection.executeReceiveStrings("cutcompexp$speciesCompressed");
		NetworkAttributes.addAttribute(networkName, compressedArray, NodeTypeAttributeEnum.COMPRESSED, cyServiceRegistrar);
		
		Collection<String> inhibitedReadouts = intersect(inhibitorsArray, readoutArray);
		NetworkAttributes.addAttribute(networkName, inhibitedReadouts, NodeTypeAttributeEnum.INHIBITED_READOUT, cyServiceRegistrar);
		
		// Get time signals
		double[] timeSignals = connection.executeReceiveDoubles("cnolist$timeSignals");
		
		// Add time signals to ComboBoxModel if in GUI context
		if (dataPointCombo != null) {
			DefaultComboBoxModel dataPointModel = new DefaultComboBoxModel();

			for (int i = 1; i < timeSignals.length; i++) {
				dataPointModel.addElement(timeSignals[i]);
			}
			
			dataPointCombo.setModel(dataPointModel);
		}
		
		// Apply visual style
		String applyVisualStyleCommand = "vizmap apply styles=" + CyActivator.visualStyleName;
		CommandExecutor.execute(applyVisualStyleCommand, cyServiceRegistrar);
		
		// Add output
		outputString.append("---- Cytocopter Preprocessing" + "\n");
		outputString.append("Network: " + networkName + ", MIDAS: " + FilenameUtils.getName(midasFile) + "\n"); 
		outputString.append(loadModelOutput);
		outputString.append(loadMidasOutput);
		outputString.append(createCNOListOutput);
		outputString.append(finderIndicesOutput);
		outputString.append(findNONCOutput);
		
		// Append output to log panel in CytocopterResultsPanel
		resultsPanel.appendLog(outputString.toString());
	}
	
	private Collection<String> intersect (String[] list1, String[] list2) {
		Collection<String> overlap = new TreeSet<String>();
		
		for (String element : list1)
			for (String element2 : list2)
				if (element.equals(element2))
					overlap.add(element2);
		
		return overlap;
	}

	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
