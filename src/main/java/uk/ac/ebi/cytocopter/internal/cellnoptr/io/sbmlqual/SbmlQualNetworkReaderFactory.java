package uk.ac.ebi.cytocopter.internal.cellnoptr.io.sbmlqual;

import java.io.InputStream;

import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.io.read.AbstractInputStreamTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskIterator;

public class SbmlQualNetworkReaderFactory extends AbstractInputStreamTaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;

	public SbmlQualNetworkReaderFactory (CyServiceRegistrar cyServiceRegistrar) {
		super(new SbmlQualCyFileFilter(cyServiceRegistrar.getService(CySwingAppAdapter.class).getStreamUtil()));
		this.cyServiceRegistrar = cyServiceRegistrar;
	}
	
	@Override
	public TaskIterator createTaskIterator(InputStream stream, String inputName) {
		return new TaskIterator(new SbmlQualNetworkReader(stream, inputName, cyServiceRegistrar));
	}
	
}
