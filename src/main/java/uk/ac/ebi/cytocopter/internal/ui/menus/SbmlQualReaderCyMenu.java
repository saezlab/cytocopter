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
public class SbmlQualReaderCyMenu extends AbstractCyAction {

	private static final String PREFERRED_MENU = "Apps.Cytocopter";
	private static final String MENU_NAME = "Import SBML-Qual...";
	
	private CyServiceRegistrar cyServiceRegistrar;
	private File selectedFile;
	
	
	public SbmlQualReaderCyMenu (CyServiceRegistrar cyServiceRegistrar) {
		super(MENU_NAME);
		this.cyServiceRegistrar = cyServiceRegistrar;
		setPreferredMenu(PREFERRED_MENU);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			JFileChooser fc = new JFileChooser(selectedFile);
			fc.addChoosableFileFilter(new FileChooserFilter("sbml", "sbml"));
			fc.addChoosableFileFilter(new FileChooserFilter("xml", "xml"));
			
			int chooseFileReturn = fc.showOpenDialog(null);
			if (chooseFileReturn == JFileChooser.APPROVE_OPTION) {
				selectedFile = fc.getSelectedFile();
				
				SbmlQualNetworkReaderFactory factory = new SbmlQualNetworkReaderFactory (cyServiceRegistrar, selectedFile);
				cyServiceRegistrar.getService(DialogTaskManager.class).execute(factory.createTaskIterator());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
