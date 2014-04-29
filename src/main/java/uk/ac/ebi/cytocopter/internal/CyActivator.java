package uk.ac.ebi.cytocopter.internal;

import java.util.Properties;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

import uk.ac.ebi.cytocopter.internal.tasks.cellnoptr.ConfigureCellnoptrTaskFactory;
import uk.ac.ebi.cytocopter.internal.tasks.cellnoptr.PreprocessTaskFactory;
import uk.ac.ebi.cytocopter.internal.tasks.enums.CytocopterCommandsEnum;
import uk.ac.ebi.cytocopter.internal.ui.CytocopterControlPanel;
import uk.ac.ebi.cytocopter.internal.ui.CytocopterResultsPanel;

public class CyActivator extends AbstractCyActivator {

	public BundleContext bundleContext;
	public CyServiceRegistrar cyServiceRegistrar;
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {	
		this.bundleContext = bundleContext;
		cyServiceRegistrar = getService(bundleContext, CyServiceRegistrar.class);
		
		CytocopterMenuAction action = new CytocopterMenuAction(cyServiceRegistrar, "Cytocopter");
		registerAllServices(bundleContext, action, new Properties());

		registerPanels();
		registerCyrfaceCommands();
	}
		
	private void registerPanels () {
		registerService(bundleContext, new CytocopterControlPanel(cyServiceRegistrar), CytoPanelComponent.class, new Properties());
		registerService(bundleContext, new CytocopterResultsPanel(cyServiceRegistrar), CytoPanelComponent.class, new Properties());
	}
	
	private void registerCyrfaceCommands () {
		Properties props = new Properties();
		
		props.setProperty(ServiceProperties.COMMAND, CytocopterCommandsEnum.CONFIGURE.getName());
		props.setProperty(ServiceProperties.COMMAND_NAMESPACE, CytocopterCommandsEnum.CYTOCOPTER_NAME_SPACE);
		registerService(bundleContext, new ConfigureCellnoptrTaskFactory(cyServiceRegistrar), TaskFactory.class, props);
		
		props.setProperty(ServiceProperties.COMMAND, CytocopterCommandsEnum.PREPROCESS.getName());
		props.setProperty(ServiceProperties.COMMAND_NAMESPACE, CytocopterCommandsEnum.CYTOCOPTER_NAME_SPACE);
		registerService(bundleContext, new PreprocessTaskFactory(cyServiceRegistrar), TaskFactory.class, props);
	}

}
