package uk.ac.ebi.cytocopter.internal.cellnoptr.utils;

import java.util.Collection;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;

import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.NodeTypeAttributeEnum;
import uk.ac.ebi.cytocopter.internal.utils.CyNetworkUtils;

public class NetworkAttributes {

	public static void addAttribute (CyNetwork network, CyNode node, NodeTypeAttributeEnum type, CyServiceRegistrar cyServiceRegistrar) {
		NetworkAttributes.createAttribute(network, type.getAttributeName(), cyServiceRegistrar);
		
		if (network != null && node != null && type != null)
			network.getRow(node).set(type.getAttributeName(), type.getAttributeValue());
	}
	
	public static void addAttribute (String networkName, CyNode node, NodeTypeAttributeEnum type, CyServiceRegistrar cyServiceRegistrar) {
		CyNetwork network = CyNetworkUtils.getCyNetwork(cyServiceRegistrar, networkName);
		addAttribute(network, node, type, cyServiceRegistrar);
	}
	
	public static void addAttribute (String networkName, String nodeName, NodeTypeAttributeEnum type, CyServiceRegistrar cyServiceRegistrar) {
		CyNetwork network = CyNetworkUtils.getCyNetwork(cyServiceRegistrar, networkName);
		
		CyNode node = CyNetworkUtils.getCyNode(network, nodeName);
		
		addAttribute(network, node, type, cyServiceRegistrar);
	}
	
	public static void addAttribute (String networkName, String[] nodesName, NodeTypeAttributeEnum type, CyServiceRegistrar cyServiceRegistrar) {
		for (String nodeName : nodesName)
			addAttribute(networkName, nodeName, type, cyServiceRegistrar);
	}
	
	public static void addAttribute (String networkName, Collection<String> nodesName, NodeTypeAttributeEnum type, CyServiceRegistrar cyServiceRegistrar) {
		for (String nodeName : nodesName)
			addAttribute(networkName, nodeName, type, cyServiceRegistrar);
	}
	
	public static boolean checkNodeAttributeExists (CyNetwork network, String attribute) {
		boolean exists = network.getDefaultNodeTable().getColumn(attribute) != null;
		return exists;
	}
	
	public static void createAttribute (CyNetwork network, String attribute, CyServiceRegistrar cyServiceRegistrar) {
		if (!checkNodeAttributeExists(network, attribute)) {
			network.getDefaultNodeTable().createColumn(attribute, String.class, false);
		}
	}
}
