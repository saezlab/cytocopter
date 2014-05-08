package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;
import uk.ac.ebi.cytocopter.internal.CyActivator;
import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.FormalismEnum;
import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.NodeTypeAttributeEnum;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.CommandExecutor;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.NetworkAttributes;
import uk.ac.ebi.cytocopter.internal.ui.enums.AlgorithmConfigurationsEnum;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.LogPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.ResultsPanel;
import uk.ac.ebi.cytocopter.internal.utils.CyNetworkUtils;
import uk.ac.ebi.cytocopter.internal.utils.CytoPanelUtils;

public class OptimiseTask extends AbstractTask implements ObservableTask {

	private boolean useControlPanel;
	
	private CyServiceRegistrar cyServiceRegistrar;
	
	private RserveHandler connection;
	
	private ControlPanel controlPanel;
	private ResultsPanel resultsPanel;
	private LogPanel logPanel;

	private StringBuilder outputString;
	private DateFormat dateFormat;
	
	@Tunable(description="midasFile", context="nogui")
    public String midasFile;
	
	@Tunable(description="networkName", context="nogui")
    public String networkName;
	
	@Tunable(description="formalism", context="nogui")
	public String formalism = FormalismEnum.BOOLEAN.getName();
	
	@Tunable(description="timePoint", context="nogui")
	public String timePoint;
	

	public OptimiseTask (CyServiceRegistrar cyServiceRegistrar, boolean useControlPanel) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		
		this.useControlPanel = useControlPanel;
		
		this.outputString = new StringBuilder();
		this.dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Cytocopter - Optimising...");

		// Get necessary attributes from control panel otherwise from tunables.
		if (useControlPanel) {
			controlPanel = (ControlPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, ControlPanel.class, CytoPanelName.WEST);
			resultsPanel = (ResultsPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, ResultsPanel.class, CytoPanelName.EAST);
			logPanel = (LogPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, LogPanel.class, CytoPanelName.SOUTH);
			
			networkName = controlPanel.getNetworkValue();
			formalism = controlPanel.getFormalismValue();
			timePoint = controlPanel.getTimePointValue();
			
			connection = controlPanel.connection;
		}
		
		// Check if connection is established if not run necessary commands for the optimisation
		if (connection == null) {
			// Initialise connection
			connection = new RserveHandler(cyServiceRegistrar);
			
			// Save connection in control panel
			if (useControlPanel) controlPanel.connection = connection;
		}
		
		// Run CellNOptR R package preprocess
		PreprocessTaskFactory preprocess = new PreprocessTaskFactory(controlPanel.cyServiceRegistrar, true, false, false);
		CommandExecutor.execute(preprocess.createTaskIterator(), cyServiceRegistrar);
		
		// Check number of time points in the data
		double numberOfTimePoints = connection.executeReceiveDouble("length(cnolist$timeSignals)");
		if (numberOfTimePoints > 2 && formalism.equals(FormalismEnum.BOOLEAN.getName()) && timePoint == null)
			throw new Exception("Time point undefined: Boolean formalism and data time points bigger than 2");
		
		// If time point is defined a subset of the cno list time points is used
		if (numberOfTimePoints > 2 && formalism.equals(FormalismEnum.BOOLEAN.getName())) {
			connection.execute("t <- " + timePoint);
			connection.execute("cnolistaux <- cnolist");
			connection.execute("tindex <- which(cnolistaux$timeSignals == t)");
			connection.execute("cnolistaux$timeSignals = c(0,t)");
			connection.execute("cnolistaux$valueSignals <- list(t0 = cnolist$valueSignals[[1]], cnolist$valueSignals[[tindex]])");
			connection.execute("cnolist <- cnolistaux");
		}
		
		// Run optimisation
		StringBuilder optimisationCommand = new StringBuilder("optresult <- gaBinaryT1(CNOlist = cnolist, model = cutcompexp, initBstring = bstring, ");
		for (AlgorithmConfigurationsEnum arg : AlgorithmConfigurationsEnum.values()) {
			optimisationCommand.append(arg.getRArgName());
			optimisationCommand.append(" = ");
			optimisationCommand.append(controlPanel.getAlgorithmPropertyValue(arg));
			optimisationCommand.append(", ");
		}
		optimisationCommand.append("verbose = F)");
		
		connection.execute(optimisationCommand.toString());
		
		// Retrive fitness plot
		String plotFitnessCommand = "plotFit(optRes = optresult)";
		File fitnessPlot = connection.executeReceivePlotFile(plotFitnessCommand, "cnolist");
		
		// Retrive fit plot
		String plotFitCommand = "cutAndPlotResultsT1(model = cutcompexp, bString = optresult$bString, simList = fields4Sim, CNOlist = cnolist, indexList = indicescutcomp, plotPDF = F)";
		File cnolistFitPlot = connection.executeReceivePlotFile(plotFitCommand, "cnolist");
		
		// Save optimised network to Scaffold.sif file
		String writeOptmisedNetworkCommand = "writeScaffold(modelComprExpanded = cutcompexp, optimResT1 = optresult, optimResT2 = NA, modelOriginal = model, CNOlist = cnolist)";
		connection.execute(writeOptmisedNetworkCommand);
		File optimisedNetworkFile = connection.getFile("Scaffold.sif");
		
		// Generate a unique name for the optimised network
		String optimisedNetworkName = CyNetworkUtils.getUniqueNetworkName(cyServiceRegistrar, networkName + "_" + "Optimised");
		
		// Import optimised network
		CyNetwork optimisedCyNetwork = CyNetworkUtils.readCyNetworkFromFile(cyServiceRegistrar, optimisedNetworkFile);
		optimisedCyNetwork.getRow(optimisedCyNetwork).set(CyNetwork.NAME, optimisedNetworkName);
		CyNetworkUtils.createViewAndRegister(cyServiceRegistrar, optimisedCyNetwork);
		
		// Read optimised network edges weights
		String readWeightsCommands = "edgesWeights <- read.table(file = 'weightsScaffold.EA', sep=' ', header=F,  skip=1)";
		connection.execute(readWeightsCommands);
		
		double[] edgesWeights = connection.executeReceiveDoubles("edgesWeights$V5");
		String[] edgesNames = buildEdgesNames();
		
		// Get node types
		String[] stimuliArray = connection.executeReceiveStrings("cnolist$namesStimuli");
		String[] inhibitorsArray = connection.executeReceiveStrings("cnolist$namesInhibitors");
		String[] readoutArray = connection.executeReceiveStrings("cnolist$namesSignals");
		String[] compressedArray = connection.executeReceiveStrings("cutcompexp$speciesCompressed");

		// Remove Node Type attribute in case it already exists to reset the existing values
		NetworkAttributes.removeNodeTypeAttribute(optimisedNetworkName, NodeTypeAttributeEnum.NA, cyServiceRegistrar);
		
		// Identify inhibited readouts
		Collection<String> inhibitedReadouts = NodeTypeAttributeEnum.intersect(inhibitorsArray, readoutArray);
		
		// Add aatributes to nodes
		NetworkAttributes.addNodeTypeAttribute(optimisedNetworkName, stimuliArray, NodeTypeAttributeEnum.STIMULATED, cyServiceRegistrar);
		NetworkAttributes.addNodeTypeAttribute(optimisedNetworkName, inhibitorsArray, NodeTypeAttributeEnum.INHIBITED, cyServiceRegistrar);
		NetworkAttributes.addNodeTypeAttribute(optimisedNetworkName, readoutArray, NodeTypeAttributeEnum.READOUT, cyServiceRegistrar);
		NetworkAttributes.addNodeTypeAttribute(optimisedNetworkName, compressedArray, NodeTypeAttributeEnum.COMPRESSED, cyServiceRegistrar);
		NetworkAttributes.addNodeTypeAttribute(optimisedNetworkName, inhibitedReadouts, NodeTypeAttributeEnum.INHIBITED_READOUT, cyServiceRegistrar);
		
		// Set Optimised network node types
		CyNetwork optimisedNetwork = CyNetworkUtils.getCyNetwork(cyServiceRegistrar, optimisedNetworkName);
		List<CyNode> optimisedNetworkNodes = optimisedNetwork.getNodeList();
		
		for (CyNode node : optimisedNetworkNodes) {
			String nodeName = optimisedNetwork.getRow(node).get(CyNetwork.NAME, String.class);
			String operator = NodeTypeAttributeEnum.isOperator(nodeName);
			
			if (operator != null) {
				NetworkAttributes.addNodeTypeAttribute(optimisedNetworkName, nodeName, NodeTypeAttributeEnum.OPERATOR, cyServiceRegistrar);
				optimisedNetwork.getRow(node).set(CyNetwork.NAME, operator);
			}
		}
		
		// Set Optimised network edge weights
		String edgeWeightAttribute = "Cytocopter.EdgeWeight";
		
		optimisedNetwork.getDefaultEdgeTable().createColumn(edgeWeightAttribute, Double.class, false);

		for (int i = 0; i < edgesNames.length; i++) {
			CyEdge edge = CyNetworkUtils.getCyEdge(optimisedNetwork, edgesNames[i]);
			optimisedNetwork.getRow(edge).set(edgeWeightAttribute, edgesWeights[i]);
		}
		
		// Apply visual style
		String applyVisualStyleCommand = "vizmap apply styles=" + CyActivator.visualStyleName;
		CommandExecutor.execute(applyVisualStyleCommand, cyServiceRegistrar);
		
		// Apply layout
		String layoutCommand = "layout hierarchical";
		CommandExecutor.execute(layoutCommand, cyServiceRegistrar);
		
		// Write log
		outputString.append("[" + dateFormat.format(Calendar.getInstance().getTime()) + "] " + "Cytocopter Optimising" + "\n");
		outputString.append(optimisationCommand.toString());
		outputString.append("\n");
		
		// Append log to Log panel
		logPanel.appendLog(outputString.toString());
		
		// Add plot to results panel
		resultsPanel.appendSVGPlot(cnolistFitPlot);
	}
	
	/**
	 * Assembles from the edges properties, i.e source nodes, target nodes and interaction type,
	 * the Cytoscape edges names.
	 * 
	 * @return
	 * @throws Exception
	 */
	private String[] buildEdgesNames () throws Exception {
		String[] sourceNodes = connection.executeReceiveStrings("edgesWeights$V1");
		String[] interactionType = connection.executeReceiveStrings("edgesWeights$V2");
		String[] targetNodes = connection.executeReceiveStrings("edgesWeights$V3");
		
		int n = sourceNodes.length;
		String[] names = new String[n];
		
		for (int i = 0; i < n; i++) {
			names[i] = sourceNodes[i] + " " + interactionType[i] + " " + targetNodes[i];
		}
		
		return names;
	}
	
	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
