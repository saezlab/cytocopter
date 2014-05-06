package uk.ac.ebi.cytocopter.internal.cellnoptr.utils;

import java.util.Collection;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;

import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.NodeTypeAttributeEnum;
import uk.ac.ebi.cytocopter.internal.utils.CyNetworkUtils;

public class NetworkAttributes {

	// Edge Methods
	public static void createEdgeAttribute (CyNetwork network, String attribute, CyServiceRegistrar cyServiceRegistrar) {
		if (!checkEdgeAttributeExists(network, attribute)) {
			network.getDefaultEdgeTable().createColumn(attribute, String.class, false);
		}
	}
	
	public static boolean checkEdgeAttributeExists (CyNetwork network, String attribute) {
		return (network.getDefaultEdgeTable().getColumn(attribute) != null);
	}
	
	public static void addEdgeAttribute (CyNetwork network, CyEdge edge, String attributeName, String attributeValue, CyServiceRegistrar cyServiceRegistrar) {
		if (edge != null && network != null && attributeName != null && attributeValue != null) {
			NetworkAttributes.createEdgeAttribute(network, attributeName, cyServiceRegistrar);
			network.getRow(edge).set(attributeName, attributeValue);
		}
	}
	
	public static void addEdgeAttribute (String networkName, String edgeName, String attributeName, String attributeValue, CyServiceRegistrar cyServiceRegistrar) {
		CyNetwork network = CyNetworkUtils.getCyNetwork(cyServiceRegistrar, networkName);
		CyEdge edge = CyNetworkUtils.getCyEdge(network, edgeName);
		addEdgeAttribute(network, edge, attributeName, attributeValue, cyServiceRegistrar);
	}
	
	public static void addEdgeAttribute (String networkName, String[] edgesName, String attributeName, String[] attributesValue, CyServiceRegistrar cyServiceRegistrar) {
		for (int i = 0; i < edgesName.length; i++)
			addEdgeAttribute(networkName, edgesName[i], attributeName, attributesValue[i], cyServiceRegistrar);
	}

	
	// Node Methods
	public static void createNodeAttribute (CyNetwork network, String attribute, CyServiceRegistrar cyServiceRegistrar) {
		if (!checkNodeAttributeExists(network, attribute)) {
			network.getDefaultNodeTable().createColumn(attribute, String.class, false);
		}
	}
	
	public static void addNodeAttribute (CyNetwork network, CyNode node, String attributeName, String attributeValue, CyServiceRegistrar cyServiceRegistrar) {
		if (node != null && network != null && attributeName != null && attributeValue != null) {
			NetworkAttributes.createNodeAttribute(network, attributeName, cyServiceRegistrar);
			network.getRow(node).set(attributeName, attributeValue);
		}
	}
	
	public static boolean checkNodeAttributeExists (CyNetwork network, String attribute) {
		return (network.getDefaultNodeTable().getColumn(attribute) != null);
	}
	
	public static boolean checkNodeAttributeExists (String networkName, String attribute, CyServiceRegistrar cyServiceRegistrar) {
		CyNetwork network = CyNetworkUtils.getCyNetwork(cyServiceRegistrar, networkName);
		return (network.getDefaultNodeTable().getColumn(attribute) != null);
	}

	// Node Type Attribute Methods
	public static void addNodeTypeAttribute (String networkName, String nodeName, NodeTypeAttributeEnum type, CyServiceRegistrar cyServiceRegistrar) {
		CyNetwork network = CyNetworkUtils.getCyNetwork(cyServiceRegistrar, networkName);
		CyNode node = CyNetworkUtils.getCyNode(network, nodeName);
		addNodeAttribute (network, node, type.getAttributeName(), type.getAttributeValue(), cyServiceRegistrar);
	}
	
	public static void addNodeTypeAttribute (String networkName, String[] nodesName, NodeTypeAttributeEnum type, CyServiceRegistrar cyServiceRegistrar) {
		for (String nodeName : nodesName)
			addNodeTypeAttribute(networkName, nodeName, type, cyServiceRegistrar);
	}
	
	public static void addNodeTypeAttribute (String networkName, Collection<String> nodesName, NodeTypeAttributeEnum type, CyServiceRegistrar cyServiceRegistrar) {
		for (String nodeName : nodesName)
			addNodeTypeAttribute(networkName, nodeName, type, cyServiceRegistrar);
	}
}
