package uk.ac.ebi.cytocopter.internal.cellnoptr.io.sbmlqual;

import java.io.File;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class SbmlQualNetworkWriterFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private File sbmlQualFile;
	private File sifFile;
	
	public SbmlQualNetworkWriterFactory (CyServiceRegistrar cyServiceRegistrar, File sbmlQualFile, File sifFile) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.sbmlQualFile = sbmlQualFile;
		this.sifFile = sifFile;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SbmlQualNetworkWriter(sbmlQualFile, cyServiceRegistrar, sifFile));
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
