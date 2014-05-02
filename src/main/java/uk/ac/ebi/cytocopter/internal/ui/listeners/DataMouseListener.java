package uk.ac.ebi.cytocopter.internal.ui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFileChooser;

import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.work.swing.DialogTaskManager;

import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.PreprocessTaskFactory;
import uk.ac.ebi.cytocopter.internal.ui.CytocopterControlPanel;

public class DataMouseListener implements MouseListener {

	private CytocopterControlPanel contorlPanel;
	private File selectedFile;

	public DataMouseListener (CytocopterControlPanel controlPanel) {
		this.contorlPanel = controlPanel;
	}

	@Override
	public void mouseClicked (MouseEvent event) {
		try {
			JFileChooser fc = new JFileChooser(selectedFile);
			fc.addChoosableFileFilter(new FileChooserFilter("MIDAS", "csv"));

			String network = (String) contorlPanel.networkCombo.getSelectedItem();

			int chooseFileReturn = fc.showOpenDialog(null);
			if (network != null && chooseFileReturn == JFileChooser.APPROVE_OPTION) {
				selectedFile  = fc.getSelectedFile();
				contorlPanel.dataTextField.setText(selectedFile.getName());

				PreprocessTaskFactory preprocessTaskFactory = new PreprocessTaskFactory(contorlPanel, selectedFile, network);

				contorlPanel.cyServiceRegistrar.getService(DialogTaskManager.class).execute(preprocessTaskFactory.createTaskIterator());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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