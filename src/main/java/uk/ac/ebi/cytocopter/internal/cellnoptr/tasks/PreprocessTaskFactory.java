package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class PreprocessTaskFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private boolean useControlPanel;
	private boolean displayResults;
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar, boolean useControlPanel, boolean displayResults) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.useControlPanel = useControlPanel;
		this.displayResults = displayResults;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		PreprocessTask task = new PreprocessTask(cyServiceRegistrar, useControlPanel, displayResults);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
