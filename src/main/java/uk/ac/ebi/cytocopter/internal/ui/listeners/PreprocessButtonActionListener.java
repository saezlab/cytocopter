package uk.ac.ebi.cytocopter.internal.ui.listeners;

import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.apache.commons.lang.NullArgumentException;
import org.cytoscape.work.swing.DialogTaskManager;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.Observer;

import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.PreprocessTaskFactory;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;

public class PreprocessButtonActionListener implements ActionListener
{

	private ControlPanel controlPanel;

	public PreprocessButtonActionListener(ControlPanel controlPanel)
	{
		this.controlPanel = controlPanel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{

		String networkValue;
		String midasFilePath;

		try
		{
			networkValue = controlPanel.getNetworkValue();

		}
		catch (Exception e)
		{
			networkValue = null;
		}

		try
		{
			midasFilePath = controlPanel.getMidasFilePath();
		}
		catch (Exception e)
		{
			midasFilePath = null;
		}

		if (networkValue == null)
		{
			JOptionPane.showMessageDialog(null, "You should import a network", "No Network Error",
					JOptionPane.ERROR_MESSAGE);
			controlPanel.optimiseButton.setEnabled(false);
		}

		else if (midasFilePath == null)
		{
			JOptionPane.showMessageDialog(null, "You should pick a csv file", "No CNO Error",
					JOptionPane.ERROR_MESSAGE);
			controlPanel.optimiseButton.setEnabled(false);
		}

		else
		{
                        Observer taskObserver = new Observer();
			PreprocessTaskFactory preprocessTaskFactory = new PreprocessTaskFactory(controlPanel.cyServiceRegistrar,
					true, true, true);
			controlPanel.cyServiceRegistrar.getService(DialogTaskManager.class)
					.execute(preprocessTaskFactory.createTaskIterator(), taskObserver);
			controlPanel.optimiseButton.setEnabled(true);
		}
	}

}
