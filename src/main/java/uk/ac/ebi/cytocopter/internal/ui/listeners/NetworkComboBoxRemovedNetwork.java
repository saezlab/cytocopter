package uk.ac.ebi.cytocopter.internal.ui.listeners;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

public class NetworkComboBoxRemovedNetwork implements NetworkAboutToBeDestroyedListener {

	private JComboBox networkComboBox;
	
	public NetworkComboBoxRemovedNetwork (JComboBox networkComboBox) {
		this.networkComboBox = networkComboBox;
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent event) {
		CyNetwork cyNetwork = event.getNetwork();
		String cyNetworkName = cyNetwork.getRow(cyNetwork).get(CyNetwork.NAME, String.class);
		((DefaultComboBoxModel)networkComboBox.getModel()).removeElement(cyNetworkName);
	}
	
}
