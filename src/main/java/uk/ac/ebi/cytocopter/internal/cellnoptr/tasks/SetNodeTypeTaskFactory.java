package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class SetNodeTypeTaskFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	
	public SetNodeTypeTaskFactory (CyServiceRegistrar cyServiceRegistrar) {
		this.cyServiceRegistrar = cyServiceRegistrar;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		SetNodeTypeTask task = new SetNodeTypeTask(cyServiceRegistrar);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
