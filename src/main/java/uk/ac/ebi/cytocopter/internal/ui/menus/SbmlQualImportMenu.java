package uk.ac.ebi.cytocopter.internal.ui.menus;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.work.swing.DialogTaskManager;

import uk.ac.ebi.cytocopter.internal.cellnoptr.io.sbmlqual.SbmlQualNetworkReaderFactory;

@SuppressWarnings("serial")
public class SbmlQualImportMenu extends AbstractCyAction {

	private CyServiceRegistrar cyServiceRegistrar;
	private File selectedFile;

	
	public SbmlQualImportMenu (CyServiceRegistrar cyServiceRegistrar) {
		super("Import SBML-Qual...");
		setPreferredMenu("Apps.Cytocopter");
		this.cyServiceRegistrar = cyServiceRegistrar;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(selectedFile);
		fc.addChoosableFileFilter(new FileChooserFilter("SBML", "sbml"));
		fc.addChoosableFileFilter(new FileChooserFilter("SBML xml", "xml"));

		int chooseFileReturn = fc.showOpenDialog(null);
		if (chooseFileReturn == JFileChooser.APPROVE_OPTION) {
			selectedFile  = fc.getSelectedFile();
			
			SbmlQualNetworkReaderFactory factory = new SbmlQualNetworkReaderFactory(cyServiceRegistrar, selectedFile);
			cyServiceRegistrar.getService(DialogTaskManager.class).execute(factory.createTaskIterator());
		}
	}

}
