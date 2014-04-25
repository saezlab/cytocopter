package uk.ac.ebi.cytocopter.internal.tasks.cellnoptr;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ConfigureCellnoptrTaskFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar; 
	
	public ConfigureCellnoptrTaskFactory (CyServiceRegistrar cyServiceRegistrar) {
		this.cyServiceRegistrar = cyServiceRegistrar;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		ConfigureCellnoptrTask task = new ConfigureCellnoptrTask(cyServiceRegistrar);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
