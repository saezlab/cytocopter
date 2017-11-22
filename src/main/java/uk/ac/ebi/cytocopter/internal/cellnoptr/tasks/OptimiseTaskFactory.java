package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class OptimiseTaskFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private boolean useControlPanel;
	
	public OptimiseTaskFactory (CyServiceRegistrar cyServiceRegistrar, boolean useControlPanel) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.useControlPanel = useControlPanel;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		OptimiseTask task = new OptimiseTask(cyServiceRegistrar, useControlPanel);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady() {
		return false;
	}
}
