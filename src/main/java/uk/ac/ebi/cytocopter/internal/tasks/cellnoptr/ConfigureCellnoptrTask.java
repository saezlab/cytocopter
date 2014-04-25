package uk.ac.ebi.cytocopter.internal.tasks.cellnoptr;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;
import uk.ac.ebi.cyrface.internal.utils.BioconductorPackagesEnum;

public class ConfigureCellnoptrTask extends AbstractTask implements ObservableTask {

	private CyServiceRegistrar cyServiceRegistrar;
	private RserveHandler connection;
	private StringBuilder outputString;
	
	
	public ConfigureCellnoptrTask (CyServiceRegistrar cyServiceRegistrar) {
		this(cyServiceRegistrar, null);
	}
	
	public ConfigureCellnoptrTask (CyServiceRegistrar cyServiceRegistrar, RserveHandler connection) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.connection = connection;
		this.outputString = new StringBuilder();
	}
	
	@Override
	public void run (TaskMonitor taskMonitor) throws Exception {
		
		// Check if connection is established
		if (connection == null) connection = new RserveHandler(cyServiceRegistrar);
		
		// Check if graph is installed		
		connection.installBioconductorPackage(BioconductorPackagesEnum.GRAPH);
		
		// Check if RBGL is installed (RBGL depends on graph, therefore it must be imported after)
		connection.installBioconductorPackage(BioconductorPackagesEnum.RBGL);
		
		// Check if Cairo is installed
		connection.installBioconductorPackage(BioconductorPackagesEnum.CAIRO);
		
		// Check if CellNOptR is installed
		connection.installBioconductorPackage(BioconductorPackagesEnum.CELLNOPTR);
		
		outputString.append("CellNOptR configured successfully");
	}
	
	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
