package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ConfigureCellnoptrTaskFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private boolean useControlPanel;
	
	public ConfigureCellnoptrTaskFactory (CyServiceRegistrar cyServiceRegistrar, boolean useControlPanel) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.useControlPanel = useControlPanel;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		ConfigureCellnoptrTask task = new ConfigureCellnoptrTask(cyServiceRegistrar, useControlPanel);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
