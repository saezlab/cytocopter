package uk.ac.ebi.cytocopter.internal;

import java.io.InputStream;
import java.util.Properties;

import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.CytocopterCommandsEnum;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.ConfigureCellnoptrTaskFactory;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.OptimiseTaskFactory;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.PreprocessTaskFactory;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.SetNodeTypeTaskFactory;
import uk.ac.ebi.cytocopter.internal.ui.menus.SbmlQualReaderCyMenu;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.LogPanel;
import uk.ac.ebi.cytocopter.internal.ui.panels.ResultsPanel;

public class CyActivator extends AbstractCyActivator {

	public BundleContext bundleContext;
	public CyServiceRegistrar cyServiceRegistrar;
	
	public static final String visualStyleFile = "/CytocopterVisualStyle.xml";
	public static final String visualStyleName = "Cytocopter";
	
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;
		cyServiceRegistrar = getService(bundleContext, CyServiceRegistrar.class);
		
		registerPanels();
		registerCytocopterCommands();
		loadVisualStyle();
		loadSbmlQualReader();
	}
		
	private void registerPanels () {
		registerService(bundleContext, new ControlPanel(cyServiceRegistrar), CytoPanelComponent.class, new Properties());
		registerService(bundleContext, new ResultsPanel(cyServiceRegistrar), CytoPanelComponent.class, new Properties());
		registerService(bundleContext, new LogPanel(cyServiceRegistrar), CytoPanelComponent.class, new Properties());
	}
	
	private void registerCytocopterCommands () {
		Properties props = new Properties();
		props.setProperty(ServiceProperties.COMMAND_NAMESPACE, CytocopterCommandsEnum.CYTOCOPTER_NAME_SPACE);
		
		props.setProperty(ServiceProperties.COMMAND, CytocopterCommandsEnum.CONFIGURE.getName());
		registerService(bundleContext, new ConfigureCellnoptrTaskFactory(cyServiceRegistrar, false), TaskFactory.class, props);
		
		props.setProperty(ServiceProperties.COMMAND, CytocopterCommandsEnum.PREPROCESS.getName());
		registerService(bundleContext, new PreprocessTaskFactory(cyServiceRegistrar, false, false, true), TaskFactory.class, props);
		
		props.setProperty(ServiceProperties.COMMAND, CytocopterCommandsEnum.OPTIMISE.getName());
		registerService(bundleContext, new OptimiseTaskFactory(cyServiceRegistrar, false), TaskFactory.class, props);
		
		props.setProperty(ServiceProperties.COMMAND, CytocopterCommandsEnum.NODETYPE.getName());
		registerService(bundleContext, new SetNodeTypeTaskFactory(cyServiceRegistrar), TaskFactory.class, props);
	}

	private void loadVisualStyle () {
		InputStream in = getClass().getResourceAsStream(visualStyleFile);
		LoadVizmapFileTaskFactory loadVizmapFileTaskFactory =  cyServiceRegistrar.getService(LoadVizmapFileTaskFactory.class);
		loadVizmapFileTaskFactory.loadStyles(in);
	}
	
	private void loadSbmlQualReader () {
		SbmlQualReaderCyMenu sbmlQualReaderMenu = new SbmlQualReaderCyMenu(cyServiceRegistrar);
		cyServiceRegistrar.registerService(sbmlQualReaderMenu, CyAction.class, new Properties());
		
//		HashSet<String> extensions = new HashSet<String>();
//		extensions.add("xml");
//		extensions.add("sbml");
//		
//		HashSet<String> contentTypes = new HashSet<String>();
//		contentTypes.add("xml");
//		
//		String description = "SBML-Qual file filter";
//		
//		DataCategory category = DataCategory.NETWORK;
//		
//		CySwingAppAdapter adapter = cyServiceRegistrar.getService(CySwingAppAdapter.class);
//		
//		BasicCyFileFilter filter = new BasicCyFileFilter(extensions,contentTypes, description, category, adapter.getStreamUtil());
//		
//		SbmlQualNetworkReaderFactory factory = new SbmlQualNetworkReaderFactory (cyServiceRegistrar, filter);
//		
//		Properties props = new Properties();
//		props.setProperty("readerDescription","Sbml-Qual reader");
//		props.setProperty("readerId","sbmlqual");
//		
//		cyServiceRegistrar.registerService(factory, InputStreamTaskFactory.class, props);
	}
}
