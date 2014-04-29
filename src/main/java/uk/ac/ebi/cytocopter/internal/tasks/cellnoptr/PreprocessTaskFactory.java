package uk.ac.ebi.cytocopter.internal.tasks.cellnoptr;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;

public class PreprocessTaskFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private RserveHandler connection;
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar) {
		this(cyServiceRegistrar, null);
	}
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.connection = connection;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		PreprocessTask task = new PreprocessTask(cyServiceRegistrar, connection);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
