package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;

import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;
import uk.ac.ebi.cyrface.internal.utils.BioconductorPackagesEnum;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;
import uk.ac.ebi.cytocopter.internal.utils.CytoPanelUtils;

public class ConfigureCellnoptrTask extends AbstractTask implements ObservableTask {

	private boolean useControlPanelModel;
	private ControlPanel controlPanel;
	
	private CyServiceRegistrar cyServiceRegistrar;
	
	private RserveHandler connection;
	
	private StringBuilder outputString;
	
	
	public ConfigureCellnoptrTask (CyServiceRegistrar cyServiceRegistrar, boolean useControlPanel) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.useControlPanelModel = useControlPanel;
		this.outputString = new StringBuilder();
	}
	
	@Override
	public void run (TaskMonitor taskMonitor) throws Exception {
		
		// Check if to use control panel model
		if (useControlPanelModel) {
			controlPanel = (ControlPanel) CytoPanelUtils.getCytoPanel(cyServiceRegistrar, ControlPanel.class, CytoPanelName.WEST);
			
			if (controlPanel.connection == null) 
				controlPanel.connection = new RserveHandler(cyServiceRegistrar);
			
			connection = controlPanel.connection;
		}
		
		// Check if connection is established
		if (connection == null) connection = new RserveHandler(cyServiceRegistrar);
		
		// Check if graph is installed		
		connection.installBioconductorPackage(BioconductorPackagesEnum.GRAPH);
		
		// Check if RBGL is installed
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
