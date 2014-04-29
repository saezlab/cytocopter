package uk.ac.ebi.cytocopter.internal.ui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.cytoscape.util.swing.FileChooserFilter;

public class DataMouseListener implements MouseListener {
	
	private JTextField dataTextField;
	
	public DataMouseListener (JTextField dataTextField) {
		this.dataTextField = dataTextField;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileChooserFilter("MIDAS", "csv"));
		
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            dataTextField.setText(file.getName());
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