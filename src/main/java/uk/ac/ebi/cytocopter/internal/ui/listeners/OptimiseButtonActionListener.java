package uk.ac.ebi.cytocopter.internal.ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cytoscape.work.swing.DialogTaskManager;

import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.OptimiseTaskFactory;
import uk.ac.ebi.cytocopter.internal.ui.CytocopterControlPanel;

public class OptimiseButtonActionListener implements ActionListener {

	private CytocopterControlPanel controlPanel;
	
	public OptimiseButtonActionListener (CytocopterControlPanel controlPanel) {
		this.controlPanel= controlPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		OptimiseTaskFactory optimiseTaskFactory = new OptimiseTaskFactory(controlPanel.cyServiceRegistrar, true);
		controlPanel.cyServiceRegistrar.getService(DialogTaskManager.class).execute(optimiseTaskFactory.createTaskIterator());
	}
}
