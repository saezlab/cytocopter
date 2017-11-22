package uk.ac.ebi.cytocopter.internal.cellnoptr.utils;

import java.util.Collection;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;

import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.NodeTypeAttributeEnum;
import uk.ac.ebi.cytocopter.internal.utils.CyNetworkUtils;

public class NetworkAttributes
{

	public static void addNodeTypeAttribute(String networkName, Collection<String> nodesName,
			NodeTypeAttributeEnum nodeTypeAttribute, CyServiceRegistrar cyServiceRegistrar)
	{
		for (String nodeName : nodesName)
			addNodeTypeAttribute(networkName, nodeName, nodeTypeAttribute, cyServiceRegistrar);
	}

	public static void addNodeTypeAttribute(String networkName, String[] nodesName,
			NodeTypeAttributeEnum nodeTypeAttribute, CyServiceRegistrar cyServiceRegistrar)
	{
		for (String nodeName : nodesName)
			addNodeTypeAttribute(networkName, nodeName, nodeTypeAttribute, cyServiceRegistrar);
	}

	public static void addNodeTypeAttribute(String networkName, String nodeName,
			NodeTypeAttributeEnum nodeTypeAttribute, CyServiceRegistrar cyServiceRegistrar)
	{
		CyNetwork network = CyNetworkUtils.getCyNetwork(cyServiceRegistrar, networkName);
		CyNode node = CyNetworkUtils.getCyNode(network, nodeName);

		if (network != null && node != null && nodeTypeAttribute != null && cyServiceRegistrar != null)
		{

			if (network.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS)
					.getColumn(nodeTypeAttribute.getAttributeName()) == null)
			{
				network.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS).createColumn(nodeTypeAttribute.getAttributeName(),
						String.class, false);
			}

			network.getRow(node).set(nodeTypeAttribute.getAttributeName(), nodeTypeAttribute.getAttributeValue());
		}
	}

	public static void removeNodeTypeAttribute(String networkName, NodeTypeAttributeEnum nodeTypeAttribute,
			CyServiceRegistrar cyServiceRegistrar)
	{

		CyNetwork network = CyNetworkUtils.getCyNetwork(cyServiceRegistrar, networkName);

		if (network.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS)
				.getColumn(nodeTypeAttribute.getAttributeName()) != null)
			network.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS).deleteColumn(nodeTypeAttribute.getAttributeName());
	}
}