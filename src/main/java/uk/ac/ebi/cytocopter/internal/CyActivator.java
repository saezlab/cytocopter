package uk.ac.ebi.cytocopter.internal;

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {

	public BundleContext bundleContext;
	public CyServiceRegistrar cyServiceRegistrar;
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {	
		this.bundleContext = bundleContext;
		cyServiceRegistrar = getService(bundleContext, CyServiceRegistrar.class);
		
		CytocopterMenuAction action = new CytocopterMenuAction(cyServiceRegistrar, "Cytocopter");
		registerAllServices(bundleContext, action, new Properties());
		
	}

}
