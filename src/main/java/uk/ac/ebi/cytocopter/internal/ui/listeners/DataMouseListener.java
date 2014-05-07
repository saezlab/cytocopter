package uk.ac.ebi.cytocopter.internal.ui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFileChooser;

import org.cytoscape.util.swing.FileChooserFilter;

import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;

public class DataMouseListener implements MouseListener {

	private ControlPanel controlPanel;

	public DataMouseListener (ControlPanel controlPanel) {
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