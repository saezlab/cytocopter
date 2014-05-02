package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.CommandExecutor;

public class OptimiseTask extends AbstractTask implements ObservableTask {

	private CyServiceRegistrar cyServiceRegistrar;
	private RserveHandler connection;
	private StringBuilder outputString;
	
	@Tunable(description="midasFile", context="nogui")
    public String midasFile = "";
	
	@Tunable(description="networkName", context="nogui")
    public String networkName = "";
	
	@Tunable(description="formalism", context="nogui")
	public String formalism;
	
	@Tunable(description="timePoint", context="nogui")
	public String timePoint;
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Cytocopter - Optimising...");
		
		// Check if connection is established
		if (connection == null) connection = new RserveHandler(cyServiceRegistrar);
		
		// Configure CellNOptR R package
		ConfigureCellnoptrTaskFactory configureCellnoptr = new ConfigureCellnoptrTaskFactory(cyServiceRegistrar, connection);
		CommandExecutor.execute(configureCellnoptr.createTaskIterator(), cyServiceRegistrar);
		
		// Configure CellNOptR R package
		PreprocessTaskFactory preprocess = new PreprocessTaskFactory(cyServiceRegistrar, connection, midasFile, networkName);
		CommandExecutor.execute(preprocess.createTaskIterator(), cyServiceRegistrar);
		
		
	}
	
	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
