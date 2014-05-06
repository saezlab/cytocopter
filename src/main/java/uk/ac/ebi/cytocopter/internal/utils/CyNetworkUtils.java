package uk.ac.ebi.cytocopter.internal.utils;

import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;

public class CyNetworkUtils {

	public static CyNetwork getCyNetwork (CyServiceRegistrar cyServiceRegistrar, String networkName) {
		Set<CyNetwork> networks = cyServiceRegistrar.getService(CyNetworkManager.class).getNetworkSet();
		
		for (CyNetwork network : networks)
			if (network.getRow(network).get(CyNetwork.NAME, String.class).equals(networkName))
				return network; 
		
		return null;
	}
	
	public static CyNode getCyNode (CyNetwork cyNetwork, String nodeName) {
		for (CyNode node : cyNetwork.getNodeList())
			if (cyNetwork.getRow(node).get(CyNetwork.NAME, String.class).equals(nodeName))
				return node;
				
		return null;
	}
	
	public static CyEdge getCyEdge (CyNetwork cyNetwork, String edgeName) {
		for (CyEdge edge : cyNetwork.getEdgeList())
			if (cyNetwork.getRow(edge).get(CyNetwork.NAME, String.class).equals(edgeName))
				return edge;
		
		return null;
	}
	
}
