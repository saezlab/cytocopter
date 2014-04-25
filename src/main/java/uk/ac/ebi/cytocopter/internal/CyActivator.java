package uk.ac.ebi.cytocopter.internal;

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

import uk.ac.ebi.cytocopter.internal.tasks.cellnoptr.ConfigureCellnoptrTaskFactory;

public class CyActivator extends AbstractCyActivator {

	public BundleContext bundleContext;
	public CyServiceRegistrar cyServiceRegistrar;
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {	
		this.bundleContext = bundleContext;
		cyServiceRegistrar = getService(bundleContext, CyServiceRegistrar.class);
		
		CytocopterMenuAction action = new CytocopterMenuAction(cyServiceRegistrar, "Cytocopter");
		registerAllServices(bundleContext, action, new Properties());
		
		registerCyrfaceCommands();
	}
		
	private void registerCyrfaceCommands () {
		/* Custom Command */
		Properties configureProps = new Properties();
		configureProps.setProperty(ServiceProperties.COMMAND, "configure");
		configureProps.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cytocopter");
		registerService(bundleContext, new ConfigureCellnoptrTaskFactory(cyServiceRegistrar), TaskFactory.class, configureProps);
	}

}
