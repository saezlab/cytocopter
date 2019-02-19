package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import java.io.File;
import static java.lang.System.exit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import uk.ac.ebi.cytocopter.internal.CyActivator;
import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.FormalismEnum;
import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.NodeTypeAttributeEnum;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.CommandExecutor;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.NetworkAttributes;
import uk.ac.ebi.cytocopter.internal.mahdimidas.CNO;
import uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling.CNONetwork;
import uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling.Edge;
import uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling.NetworkFactory;
import uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling.NetworkOptimizer;
import uk.ac.ebi.cytocopter.internal.mahdiplotting.ContainerPanelSimulation;
import uk.ac.ebi.cytocopter.internal.ui.enums.AlgorithmConfigurationsEnum;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.LogPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.ResultsPanel;
import uk.ac.ebi.cytocopter.internal.utils.CyNetworkUtils;
import uk.ac.ebi.cytocopter.internal.utils.CytoPanelUtils;
import uk.ac.ebi.cytocopter.internal.utils.MSutils;

public class OptimiseTask extends AbstractTask implements ObservableTask {

	private boolean useControlPanel;
	
	private CyServiceRegistrar cyServiceRegistrar;
	
	//private RserveHandler connection;
	
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
			
			
		}
		
		
		
		
		// Focus selected network
				CommandExecutor.execute("network set current network=" + networkName,
						cyServiceRegistrar);
				

				// Export selected network to sif
                                File networkFile2 = File.createTempFile(networkName + "_" + "temp", ".sif");
                                String networkFile = networkFile2.getAbsolutePath();
                                networkFile2.delete();
                                CommandExecutor.execute("network export OutputFile=\""
                                               + MSutils.getWindowsCorrectPath(networkFile) + "\"" + " options=sif",
                                               cyServiceRegistrar);
				
				midasFile = controlPanel.getMidasFilePath();
				
				
				NetworkFactory networkFactory = new NetworkFactory();
				CNONetwork cnoNetwork= networkFactory.importNetwork(networkFile.toString());

				CNO midas = new CNO(midasFile);
				cnoNetwork.setMidas(midas);
				cnoNetwork.compress();
                                JCheckBox checkbox = controlPanel.getJCheckBox();
                                boolean checked = checkbox.isSelected();
                                if (checked == true){
                                    cnoNetwork.expand();
                                    //JOptionPane.showMessageDialog(null, "Expanding");
                                }
                                
				
				int p_TimePoint = Integer.parseInt(timePoint.substring(0,timePoint.indexOf(".")));
				double p_SizeFac = controlPanel.getAlgorithmPropertyValue(AlgorithmConfigurationsEnum.SIZE_FAC);
				double p_NAFac = controlPanel.getAlgorithmPropertyValue(AlgorithmConfigurationsEnum.NA_FAC);
				int p_PopSize = controlPanel.getAlgorithmPropertyValue(AlgorithmConfigurationsEnum.POP_SIZE).intValue();
				double p_MaxTime = controlPanel.getAlgorithmPropertyValue(AlgorithmConfigurationsEnum.MAX_TIME);
				int p_MaxGen = controlPanel.getAlgorithmPropertyValue(AlgorithmConfigurationsEnum.MAX_GENS).intValue();
				double p_RelTol= controlPanel.getAlgorithmPropertyValue(AlgorithmConfigurationsEnum.REL_TOL);
				
				
                                //Export all models to SIF
			
                                NetworkOptimizer networkOptimizer =  new NetworkOptimizer(cnoNetwork, p_TimePoint,p_SizeFac,p_NAFac,p_PopSize,p_MaxTime,p_MaxGen,p_RelTol);
				networkOptimizer.runs();
                                File optimisedNetworkFile = File.createTempFile("Scaffold", ".sif");
				cnoNetwork.exportNetwork(optimisedNetworkFile);
                                
                                //TEST
                                ArrayList<Integer> bestFit = networkOptimizer.run();
                                
                                
                               
		// Generate a unique name for the optimised network
		String optimisedNetworkName = CyNetworkUtils.getUniqueNetworkName(cyServiceRegistrar, networkName + "_" + "Optimised");
		
		// Import optimised network
		CyNetwork optimisedCyNetwork = CyNetworkUtils.readCyNetworkFromFile(cyServiceRegistrar, optimisedNetworkFile);
		optimisedCyNetwork.getRow(optimisedCyNetwork).set(CyNetwork.NAME, optimisedNetworkName);
		CyNetworkUtils.createViewAndRegister(cyServiceRegistrar, optimisedCyNetwork);
		
		
		
		
		ArrayList<Double> DesiredResultsWeights = networkOptimizer.getAdaptedDesiredResultsWeights();
		int DesiredResultsWeightsLength = DesiredResultsWeights.size();
		double[] edgesWeights = new double [DesiredResultsWeightsLength];
		for(int i=0;i<DesiredResultsWeightsLength;i++)
		{
			edgesWeights[i] = DesiredResultsWeights.get(i);
		}
		
		String[] edgesNames = networkOptimizer.getAdaptedEdgeNames().toArray(new String[0]);
		
		String[] stimuliArray = midas.namesStimuli().toArray(new String[0]);
		String[] inhibitorsArray = midas.namesInhibitors().toArray(new String[0]);
		String[] readoutArray = midas.namesSignals().toArray(new String[0]);
		String[] compressedArray = cnoNetwork.compressedNodes().toArray(new String[0]);
		
		
		

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
		
		optimisedNetwork.getTable(CyEdge.class, CyNetwork.LOCAL_ATTRS).createColumn(edgeWeightAttribute, Double.class, false);
                	
		
		
		for (int i = 0; i < edgesNames.length; i++) {
			CyEdge edge = CyNetworkUtils.getCyEdge(optimisedNetwork, edgesNames[i]);
  
			optimisedNetwork.getRow(edge).set(edgeWeightAttribute, edgesWeights[i]);
		}
		
		
		// Write log
		outputString.append("[" + dateFormat.format(Calendar.getInstance().getTime()) + "] " + "Cytocopter Optimising" + "\n");
		
		// Append log to Log panel
		logPanel.appendLog(outputString.toString());
		
		
		List<HashMap<String, Integer>> simulationResultsBeginTime = cnoNetwork.simulateForAllTreatmentCombination(0);
		List<HashMap<String, Integer>> simulationResultEndTime = cnoNetwork.simulateForAllTreatmentCombination(p_TimePoint);
		
		JPanel optimizationJPanel = new ContainerPanelSimulation(midas,0,p_TimePoint, simulationResultsBeginTime,simulationResultEndTime);
		
		resultsPanel.appendJPanelPlot(optimizationJPanel);
                
                
                
                
                //Export the best fit to SIF file 
                cnoNetwork.restoreEdges();
                cnoNetwork.removeEdges(bestFit);
                File optimisedNetworkFileSBML = File.createTempFile("Scaffold", ".sif");
                cnoNetwork.exportNetwork2(optimisedNetworkFileSBML);
                String filename = SBMLFileString.getInstance();
                filename = optimisedNetworkFileSBML.toString();
                SBMLFileString.setInstance(filename);
                
                
                
		// Apply layout
		String layoutCommand = "layout hierarchical";
		CommandExecutor.execute(layoutCommand, cyServiceRegistrar);
                
                // Apply visual style
		String applyVisualStyleCommand = "vizmap apply styles=" + CyActivator.visualStyleName;
                //JOptionPane.showMessageDialog(null, applyVisualStyleCommand);
                try {
                    Thread.sleep(1000);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
		CommandExecutor.execute(applyVisualStyleCommand, cyServiceRegistrar);
               

	}
	
	
	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
