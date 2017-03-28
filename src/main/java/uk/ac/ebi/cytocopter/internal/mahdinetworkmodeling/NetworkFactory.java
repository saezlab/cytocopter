package uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling;

import java.util.ArrayList;
import java.util.Scanner;

import uk.ac.ebi.cytocopter.internal.mahdiexceptions.EdgeException;

public class NetworkFactory
{
	private static Scanner inputStream;

	// Import Version 02
	// *********************************************************************************
	public CNONetwork importNetwork(String fileAddress) throws EdgeException
	{
		FileInMemory fileInMemory = new FileInMemory(fileAddress);
		CNONetwork cnoNetwork = new CNONetwork();

		for (int i = 0; i < fileInMemory.getLastIndex(); i++)
		{
			FileLine oneLine;
			oneLine = fileInMemory.readLine(i);
			if (!oneLine.getSource().startsWith("and") && !oneLine.getTarget().startsWith("and"))
			{
				Node sourceNode = new Node(oneLine.getSource());
				cnoNetwork.addNode(sourceNode);
			}
		}

		for (int i = 0; i < fileInMemory.getLastIndex(); i++)
		{
			FileLine oneLine;
			oneLine = fileInMemory.readLine(i);
			if (!oneLine.getSource().startsWith("and") && !oneLine.getTarget().startsWith("and"))
			{
				Node sourceNode = new Node(oneLine.getTarget());
				cnoNetwork.addNode(sourceNode);
			}
		}

		for (int i = 0; i < fileInMemory.getLastIndex(); i++)
		{
			FileLine oneLine;
			oneLine = fileInMemory.readLine(i);

			// If both sourceNode and targetNode are not hyper nodes
			// It simply adds the nodes to the network
			if (!oneLine.getSource().startsWith("and") && !oneLine.getTarget().startsWith("and"))
			{
				Node sourceNode = new Node(oneLine.getSource());
				Node targetNode = new Node(oneLine.getTarget());
				int sign;

				sourceNode = cnoNetwork.addNode(sourceNode);
				targetNode = cnoNetwork.addNode(targetNode);
				sign = oneLine.getSign();

				cnoNetwork.addEdge(sourceNode, targetNode, sign);
			}
			else // if(oneLine.getTarget().startsWith("and") ||
					// oneLine.getSource().startsWith("and"))
			{
				String andName = null;
				if (oneLine.getTarget().startsWith("and"))
				{
					andName = oneLine.getTarget();
				}
				else if (oneLine.getSource().startsWith("and"))
				{
					andName = oneLine.getSource();
				}

				Node targetNode = cnoNetwork.addNode(new Node(fileInMemory.targetOfAnd(andName)));

				ArrayList<String> sourcesNamesofThisAnd = fileInMemory.sourcesOfAnd(andName);
				ArrayList<Integer> sourceSignsofThisAnd = fileInMemory.signsOfAnd(andName);
				ArrayList<Node> sourcesNodes = new ArrayList<Node>();

				for (String s : sourcesNamesofThisAnd)
				{
					sourcesNodes.add(cnoNetwork.addNode(new Node(s)));
				}

				cnoNetwork.addEdge(sourcesNodes, targetNode, sourceSignsofThisAnd);
			}
		}

		return cnoNetwork;
	}

	// Import Version 02 (End)
	// *********************************************************************************

	// when we want to write the Network to another file.
	public void exportNetwork(String addressFile, CNONetwork network)
	{
		return;
	}

}
