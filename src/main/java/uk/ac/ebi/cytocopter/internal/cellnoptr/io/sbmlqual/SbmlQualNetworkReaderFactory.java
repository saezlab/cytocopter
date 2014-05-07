package uk.ac.ebi.cytocopter.internal.cellnoptr.io.sbmlqual;

import java.io.File;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class SbmlQualNetworkReaderFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private File sbmlQualFile;
	
	
	public SbmlQualNetworkReaderFactory (CyServiceRegistrar cyServiceRegistrar, File sbmlQualFile) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.sbmlQualFile = sbmlQualFile;
	}

	@Override
	public TaskIterator createTaskIterator() {
		SbmlQualNetworkReader reader = new SbmlQualNetworkReader(sbmlQualFile, cyServiceRegistrar);
		return new TaskIterator(reader);
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
