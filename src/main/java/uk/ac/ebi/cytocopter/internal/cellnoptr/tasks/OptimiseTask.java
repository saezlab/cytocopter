package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import java.io.File;

import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;
import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.FormalismEnum;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.CommandExecutor;
import uk.ac.ebi.cytocopter.internal.ui.ControlPanel;
import uk.ac.ebi.cytocopter.internal.ui.ResultsPanel;
import uk.ac.ebi.cytocopter.internal.ui.enums.AlgorithmConfigurationsEnum;
import uk.ac.ebi.cytocopter.internal.utils.CytoPanelUtils;

public class OptimiseTask extends AbstractTask implements ObservableTask {

	private boolean useControlPanel;
	
	private CyServiceRegistrar cyServiceRegistrar;
	
	private RserveHandler connection;
	
	private ControlPanel controlPanel;
	private ResultsPanel resultsPanel;

	private StringBuilder outputString;
	
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
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Cytocopter - Optimising...");

		// Get necessary attributes from control panel otherwise from tunables.
		if (useControlPanel) {
			controlPanel = (ControlPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, ControlPanel.class, CytoPanelName.WEST);
			resultsPanel = (ResultsPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, ResultsPanel.class, CytoPanelName.EAST);
			
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
		
		// Add plot to results panel
		resultsPanel.appendSVGPlot(cnolistFitPlot);
	}
	
	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
