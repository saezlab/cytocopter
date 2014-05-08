package uk.ac.ebi.cytocopter.internal.cellnoptr.io.sbmlqual;

import java.io.File;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class SbmlQualNetworkReaderFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private File file;
	
	public SbmlQualNetworkReaderFactory (CyServiceRegistrar cyServiceRegistrar, File file) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.file = file;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SbmlQualNetworkReader(file, cyServiceRegistrar));
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
