package uk.ac.ebi.cytocopter.internal.tasks.cellnoptr;

import java.io.File;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;
import uk.ac.ebi.cytocopter.internal.tasks.utils.CommandExecutor;

public class PreprocessTask  extends AbstractTask implements ObservableTask {

	private CyServiceRegistrar cyServiceRegistrar;
	private RserveHandler connection;
	private StringBuilder outputString;

	@Tunable(description="midasFile")
    public String midasFile = "";
	
	@Tunable(description="networkName")
    public String networkName = "";
	
	public PreprocessTask (CyServiceRegistrar cyServiceRegistrar) {
		this (cyServiceRegistrar, null);
	}
	
	public PreprocessTask (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.outputString = new StringBuilder();
		this.connection = connection;
	}
	

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
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
		
		// Get node types
		String[] stimuliArray = connection.executeReceiveStrings("cnolist$namesStimuli");
		String[] inhibitorsArray = connection.executeReceiveStrings("cnolist$namesInhibitors");
		String[] signalsArray = connection.executeReceiveStrings("cnolist$namesSignals");
		String[] compressedArray = connection.executeReceiveStrings("cutcompexp$speciesCompressed");
		
		// Get time signals
		double[] timeSignals = connection.executeReceiveDoubles("cnolist$timeSignals");
		
		// Add output
		outputString.append(loadModelOutput);
		outputString.append(loadMidasOutput);
		outputString.append(createCNOListOutput);
		outputString.append(finderIndicesOutput);
		outputString.append(findNONCOutput);
	}

	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
