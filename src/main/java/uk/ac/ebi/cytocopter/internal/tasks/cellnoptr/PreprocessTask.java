package uk.ac.ebi.cytocopter.internal.tasks.cellnoptr;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.Tunable;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;

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
		
		CommandExecutorTaskFactory asd = new CommandExecutorTaskFactory() {
			
			@Override
			public TaskIterator createTaskIterator(String arg0, String arg1,
					Map<String, Object> arg2, TaskObserver arg3) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TaskIterator createTaskIterator(List<String> arg0, TaskObserver arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TaskIterator createTaskIterator(TaskObserver arg0, String... arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TaskIterator createTaskIterator(File arg0, TaskObserver arg1) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
