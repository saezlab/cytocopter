package uk.ac.ebi.cytocopter.internal.ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cytoscape.work.swing.DialogTaskManager;

import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.PreprocessTaskFactory;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;

public class PreprocessButtonActionListener implements ActionListener {

	private ControlPanel controlPanel;

	public PreprocessButtonActionListener (ControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		PreprocessTaskFactory preprocessTaskFactory = new PreprocessTaskFactory(controlPanel.cyServiceRegistrar, true, true, true);
		controlPanel.cyServiceRegistrar.getService(DialogTaskManager.class).execute(preprocessTaskFactory.createTaskIterator());
	}

}
