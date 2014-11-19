package uk.ac.ebi.cytocopter.internal.ui.menus;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;

import uk.ac.ebi.cytocopter.internal.cellnoptr.io.sbmlqual.SbmlQualNetworkWriterFactory;

@SuppressWarnings("serial")
public class SbmlQualExportMenu extends AbstractCyAction {

	private CyServiceRegistrar cyServiceRegistrar;
	private File selectedFile;

	
	public SbmlQualExportMenu (CyServiceRegistrar cyServiceRegistrar) {
		super("Export to SBML-Qual...");
		setPreferredMenu("Apps.Cytocopter");
		this.cyServiceRegistrar = cyServiceRegistrar;
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(selectedFile);
		fc.addChoosableFileFilter(new FileChooserFilter("SBML", "sbml"));
		fc.addChoosableFileFilter(new FileChooserFilter("SBML xml", "xml"));

		int chooseFileReturn = fc.showSaveDialog(null);
		if (chooseFileReturn == JFileChooser.APPROVE_OPTION) {
			selectedFile  = fc.getSelectedFile();
			
			try {
				File tempNetworkSif = File.createTempFile("current_cytoscape_network" + "_" + "temp", ".sif");
				CyNetwork currentNetwork = cyServiceRegistrar.getService(CyAppAdapter.class).getCyApplicationManager().getCurrentNetwork();
				CyWriter exportNetwork = cyServiceRegistrar.getService(CyNetworkViewWriterFactory.class).createWriter(new FileOutputStream(tempNetworkSif.getAbsolutePath()), currentNetwork);
				cyServiceRegistrar.getService(SynchronousTaskManager.class).execute(new TaskIterator(exportNetwork));
				
				SbmlQualNetworkWriterFactory factory = new SbmlQualNetworkWriterFactory(cyServiceRegistrar, selectedFile, tempNetworkSif);
				cyServiceRegistrar.getService(SynchronousTaskManager.class).execute(factory.createTaskIterator());
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

}
