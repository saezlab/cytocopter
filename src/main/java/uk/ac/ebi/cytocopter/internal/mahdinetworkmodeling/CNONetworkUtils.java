package uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling;

import java.util.ArrayList;

import uk.ac.ebi.cytocopter.internal.mahdiexceptions.NetworkFactoryException;
import uk.ac.ebi.cytocopter.internal.mahdiexceptions.NodeException;
import uk.ac.ebi.cytocopter.internal.mahdimidas.CNO;

public class CNONetworkUtils
{
	public static CNONetwork initializeNetwork(CNO midas, CNONetwork network,ArrayList<String> midasTreatmentColumns, ArrayList<Integer> miadsOneTreatmentCombination) throws NetworkFactoryException, NodeException
	{
		
		if(midasTreatmentColumns.size() != miadsOneTreatmentCombination.size())
		{
			throw new NetworkFactoryException("The size of the column arrays has to be equal to the size of the treatment combination array");
		}
		
		for(int i = 0; i<midasTreatmentColumns.size(); i++)
		{
			String nodeName = midasTreatmentColumns.get(i);
			Integer nodeState = miadsOneTreatmentCombination.get(i);
			
			Node currentNode = network.isExistNode(nodeName);
			if(currentNode != null)
			{
				if(midas.isStimuli(nodeName))
				{
					if(nodeState == 1)
					{
						currentNode.setState(1);
						currentNode.setStimulatedNodes(Node.STIMULUS, 1);
					}
					else if(nodeState == 0)
					{
						currentNode.setState(0);
						currentNode.setStimulatedNodes(Node.STIMULUS, 0);
					}
				}
				else if(midas.isInhibitor(nodeName))
				{
					if(nodeState == 1)
					{
						currentNode.setStimulatedNodes(Node.INHIBITOR, 1);
						currentNode.setState(0);
					}
					else if(nodeState == 0)
					{
						currentNode.setStimulatedNodes(Node.INHIBITOR, 0);
						currentNode.setState(-1);
					}
				}
				
			}
		}
		
		for(Node n:network.nodes)
		{
			if(n.getTime()==0)
			{
				n.setState(-1);
			}
			
		}
		return network;
	}

}
