package uk.ac.ebi.cytocopter.internal.tasks.cellnoptr;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;

public class ConfigureCellnoptrTaskFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private RserveHandler connection;
	
	public ConfigureCellnoptrTaskFactory (CyServiceRegistrar cyServiceRegistrar) {
		this(cyServiceRegistrar, null);
	}
	
	public ConfigureCellnoptrTaskFactory (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.connection = connection;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		ConfigureCellnoptrTask task = new ConfigureCellnoptrTask(cyServiceRegistrar, connection);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
