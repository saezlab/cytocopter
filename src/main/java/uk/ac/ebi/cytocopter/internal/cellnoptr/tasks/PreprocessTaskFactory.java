package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import javax.swing.JComboBox;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;

public class PreprocessTaskFactory implements TaskFactory {

	private CyServiceRegistrar cyServiceRegistrar;
	private RserveHandler connection;
	private String midasFile;
	private String networkName;
	private JComboBox dataPointCombo;
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar) {
		this(cyServiceRegistrar, null, null, null, null);
	}
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection) {
		this(cyServiceRegistrar, connection, null, null, null);
	}
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar, String midasFile, String networkName, JComboBox dataPointCombo) {
		this(cyServiceRegistrar, null, midasFile, networkName, dataPointCombo);
	}
	
	public PreprocessTaskFactory (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection, String midasFile, String networkName, JComboBox dataPointCombo) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.connection = connection;
		this.midasFile = midasFile;
		this.networkName = networkName;
		this.dataPointCombo = dataPointCombo;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		PreprocessTask task = new PreprocessTask(cyServiceRegistrar, connection, midasFile, networkName, dataPointCombo);
		return new TaskIterator(task);
	}

	@Override
	public boolean isReady() {
		return false;
	}

}
