package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import java.io.File;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;
import uk.ac.ebi.cytocopter.internal.ui.CytocopterControlPanel;

public class PreprocessTaskFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private RserveHandler connection;
	private String midasFile;
	private String networkName;
	private CytocopterControlPanel contorlPanel;
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar) {
		this(cyServiceRegistrar, null, null, null);
	}
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection) {
		this(cyServiceRegistrar, connection, null, null);
	}
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar, String midasFile, String networkName) {
		this(cyServiceRegistrar, null, midasFile, networkName);
	}
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection, String midasFile, String networkName) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.connection = connection;
		this.midasFile = midasFile;
		this.networkName = networkName;
	}
	
	public PreprocessTaskFactory (CytocopterControlPanel contorlPanel, File selectedFile, String network) {
		this.cyServiceRegistrar = contorlPanel.cyServiceRegistrar;
		this.connection = null;
		this.midasFile = selectedFile.getAbsolutePath();
		this.networkName = network;
		this.contorlPanel = contorlPanel;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		PreprocessTask task = new PreprocessTask(cyServiceRegistrar, connection, midasFile, networkName, contorlPanel);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
