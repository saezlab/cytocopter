package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class PreprocessTaskFactory implements TaskFactory
{

	private CyServiceRegistrar cyServiceRegistrar;
	private boolean useControlPanel;
	private boolean displayResults;
	private boolean displayNetworkAnnotation;

	public PreprocessTaskFactory(CyServiceRegistrar cyServiceRegistrar, boolean useControlPanel, boolean displayResults,
			boolean displayNetworkAnnotation)
	{
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.useControlPanel = useControlPanel;
		this.displayResults = displayResults;
		this.displayNetworkAnnotation = displayNetworkAnnotation;
	}

	@Override
	public TaskIterator createTaskIterator()
	{
		PreprocessTask task = new PreprocessTask(cyServiceRegistrar, useControlPanel, displayResults,
				displayNetworkAnnotation);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady()
	{
		return false;
	}

}
