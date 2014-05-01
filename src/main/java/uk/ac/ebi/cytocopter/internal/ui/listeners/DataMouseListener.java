package uk.ac.ebi.cytocopter.internal.ui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.work.swing.DialogTaskManager;

import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.PreprocessTaskFactory;

public class DataMouseListener implements MouseListener {
	
	private CyServiceRegistrar cyServiceRegistrar;
	private JTextField dataTextField;
	private JComboBox networkCombo;
	private File selectedFile;
	private JComboBox dataPointCombo;
	
	public DataMouseListener (JTextField dataTextField, JComboBox networkCombo, CyServiceRegistrar cyServiceRegistrar, JComboBox dataPointCombo) {
		this.dataTextField = dataTextField;
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.networkCombo = networkCombo;
		this.dataPointCombo = dataPointCombo;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		JFileChooser fc = new JFileChooser(selectedFile);
		fc.addChoosableFileFilter(new FileChooserFilter("MIDAS", "csv"));
		
		String network = (String) networkCombo.getSelectedItem();
		
		int chooseFileReturn = fc.showOpenDialog(null);
		if (chooseFileReturn == JFileChooser.APPROVE_OPTION) {
			selectedFile  = fc.getSelectedFile();
            dataTextField.setText(selectedFile.getName());
        }
		
		if (network != null) {
			PreprocessTaskFactory preprocessTaskFactory = new PreprocessTaskFactory(cyServiceRegistrar, selectedFile.getAbsolutePath(), network, dataPointCombo);    			
			cyServiceRegistrar.getService(DialogTaskManager.class).execute(preprocessTaskFactory.createTaskIterator());
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}