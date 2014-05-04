package uk.ac.ebi.cytocopter.internal.ui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFileChooser;

import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.work.swing.DialogTaskManager;

import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.PreprocessTaskFactory;
import uk.ac.ebi.cytocopter.internal.ui.CytocopterControlPanel;

public class DataMouseListener implements MouseListener {

	private CytocopterControlPanel controlPanel;

	public DataMouseListener (CytocopterControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	@Override
	public void mouseClicked (MouseEvent event) {
		try {
			JFileChooser fc = new JFileChooser(controlPanel.dataFile);
			fc.addChoosableFileFilter(new FileChooserFilter("MIDAS", "csv"));

			int chooseFileReturn = fc.showOpenDialog(null);
			if (controlPanel.getNetworkValue() != null && chooseFileReturn == JFileChooser.APPROVE_OPTION) {
				controlPanel.dataFile  = fc.getSelectedFile();
				controlPanel.dataTextField.setText(controlPanel.dataFile.getName());

				PreprocessTaskFactory preprocessTaskFactory = new PreprocessTaskFactory(controlPanel.cyServiceRegistrar, true, true);
				controlPanel.cyServiceRegistrar.getService(DialogTaskManager.class).execute(preprocessTaskFactory.createTaskIterator());
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