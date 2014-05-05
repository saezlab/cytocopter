package uk.ac.ebi.cytocopter.internal.ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cytoscape.work.swing.DialogTaskManager;

import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.PreprocessTaskFactory;
import uk.ac.ebi.cytocopter.internal.ui.CytocopterControlPanel;

public class PreprocessButtonActionListener implements ActionListener {

	private CytocopterControlPanel controlPanel;

	public PreprocessButtonActionListener (CytocopterControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		PreprocessTaskFactory preprocessTaskFactory = new PreprocessTaskFactory(controlPanel.cyServiceRegistrar, true, true);
		controlPanel.cyServiceRegistrar.getService(DialogTaskManager.class).execute(preprocessTaskFactory.createTaskIterator());
	}

}
