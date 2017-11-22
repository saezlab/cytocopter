package uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;

import uk.ac.ebi.cytocopter.internal.mahdiexceptions.EdgeException;
import uk.ac.ebi.cytocopter.internal.mahdiexceptions.NetworkFactoryException;
import uk.ac.ebi.cytocopter.internal.mahdiexceptions.NodeException;
import uk.ac.ebi.cytocopter.internal.mahdimidas.CNO;
import uk.ac.ebi.cytocopter.internal.mahdimidasexceptions.MidasGeneralException;

public class CNONetwork
{
	private String fileAddress;
	final ArrayList<Node> nodes;
	public final ArrayList<Edge> edges;
	public boolean IsASimulatedNetwork;
	public boolean IsAStableNetworkAfterSimulation;
	public double EdgePenaltyParameter = 0.0001d;
	public double NANodePenaltyParameter = 200d;

	public double getEdgePenaltyParameter()
	{
		return EdgePenaltyParameter;
	}

	public void setEdgePenaltyParameter(double edgePenaltyParameter)
	{
		EdgePenaltyParameter = edgePenaltyParameter;
	}

	public double getNANodePenaltyParameter()
	{
		return NANodePenaltyParameter;
	}

	public void setNANodePenaltyParameter(double nANodePenaltyParameter)
	{
		NANodePenaltyParameter = nANodePenaltyParameter;
	}

	private int hyperEdgesNumbers;

	private CNO midas;

	public CNO getMidas()
	{
		return midas;
	}

	public int numberOfEdges()
	{

		return availableEdges();
	}

	public int numberOfNodes()
	{
		return nodes.size();
	}

	public void setMidas(CNO midas)
	{
		this.midas = midas;
	}

	int lastEdgeIndex;

	public CNONetwork()
	{
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
		lastEdgeIndex = 0;
		hyperEdgesNumbers = 0;
	}

	// correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	// If the node is already existed in the network, it simply return that node
	// If the node is not existed in the network, it adds the argument node to
	// the network, and
	// returns the newly added node.
	public Node addNode(Node node)
	{
		Node t = isExistNode(node);
		if (t == null)
		{
			nodes.add(node);
			return node;
		}
		else
		{
			return t;
		}
	}

	public boolean removeNode(String name)
	{

		return false;
	}

	public Edge addEdge(ArrayList<Node> sourceNodes, Node targetNode, ArrayList<Integer> sourceSigns)
			throws EdgeException
	{

		if (sourceNodes.size() != sourceSigns.size())
			throw new EdgeException("The size of sourceNodes and sourcesSign has to be equal");

		// check for existence of each node in the Network
		for (int i = 0; i < sourceNodes.size(); i++)
		{
			Node existedNode = isExistNode(sourceNodes.get(i));

			if (existedNode == null)
			{
				throw new EdgeException("At least, one of source nodes does not exist in the Network");
			}
			else
			{
				sourceNodes.set(i, existedNode);
			}
		}

		Node existedTarget = isExistNode(targetNode);
		if (existedTarget == null)
		{
			throw new EdgeException("The target node does not exist in the Network");
		}
		else
		{
			targetNode = existedTarget;
		}

		Edge e = new Edge(sourceNodes, targetNode, sourceSigns);

		if (isExistEdge(e) == null)
		{
			e.setID(edges.size());
			edges.add(e);
		}
		else
		{
			e = isExistEdge(e);
		}

		e.setVisible(Edge.VISIBLE);

		return e;
	}

	public Edge addEdge(Node sourceNode, Node targetNode, int source_sign) throws EdgeException
	{
		Edge edge = null;
		if (isExistNode(sourceNode) == null)
		{
			throw new EdgeException("The source node does not exist in the Network");
		}
		else if (isExistNode(targetNode) == null)
		{
			throw new EdgeException("The target node does not exist in the Network");
		}
		else
		{
			for (Edge e : edges)
			{
				// this equality uses source name and target name
				if (e.equals(sourceNode, targetNode, source_sign))
					return e;
			}

			sourceNode = isExistNode(sourceNode);
			targetNode = isExistNode(targetNode);
			edge = new Edge(sourceNode, targetNode, source_sign);
			edge.setID(edges.size());
			edges.add(edge);
		}
		edge.setVisible(Edge.VISIBLE);
		return edge;
	}

	// correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	// If a node is already existed in the network, it returns that node
	// Else: it returns null
	//
	// How to check nodes for equality?
	// Each node has a unique identifier, i.e. its name
	private Node isExistNode(Node b)
	{
		for (Node a : nodes)
		{
			if (a.equals(b))
				return a;
		}
		return null;
	}

	// correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	// Overloaded version of the previous function (different argument types)
	public Node isExistNode(String nodeName)
	{
		for (Node a : nodes)
		{
			if (a.getName().equals(nodeName))
				return a;
		}
		return null;
	}

	// correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	// If an edge is already existed in the network, it returns that edge
	// Else: it returns null
	private Edge isExistEdge(Edge b)
	{
		for (Edge e : edges)
		{
			if (e.equals(b))
			{
				return e;
			}
		}
		return null;
	}

	public Edge findEdge(int id)
	{
		int result = 0;
		for (Edge e : edges)
		{
			if (e.isAvailabe())
			{
				if (result == id)
				{
					return e;
				}
				result++;

			}
		}

		return null;
	}

	public Edge removeEdge(int id)
	{
		// keep the source and target nodes in the network
		Edge edge = findEdge(id);
		edge.setVisible(Edge.HIDDEN);

		return edge;
	}

	public void removeEdges(ArrayList<Integer> bitStream) throws EdgeException
	{
		if (bitStream.size() != this.availableEdges())
		{
			throw new EdgeException("The number of input bitStream should be equal to the number of available edges");
		}

		for (int i = 0; i < this.availableEdges(); i++)
		{
			Edge edge = findEdge(i);
			if (edge.isAvailabe())
			{
				if (bitStream.get(i) == 0)
				{
					edge.setVisible(Edge.HIDDEN);
				}
				else if (bitStream.get(i) == 1)
				{
					edge.setVisible(Edge.VISIBLE);
				}
				else
				{
					throw new EdgeException("The input bitStream must contain only 0s and 1s");
				}
			}
		}

		return;
	}
        public void removeEdges2(ArrayList<Integer> bitStream) throws EdgeException
	{
		if (bitStream.size() != this.availableEdges())
		{
			throw new EdgeException("The number of input bitStream should be equal to the number of available edges");
		}

		for (int i = 0; i < this.availableEdges(); i++)
		{
			Edge edge = findEdge(i);
			if (edge.isAvailabe())
			{
				if (bitStream.get(i) == 0)
				{
					edge.setAvailabe(false);
				}
				else if (bitStream.get(i) == 1)
				{
					edge.setAvailabe(true);
				}
				else
				{
					throw new EdgeException("The input bitStream must contain only 0s and 1s");
				}
			}
		}

		return;
	}

	public int availableEdges()
	{
		int result = 0;
		for (int i = 0; i < edges.size(); i++)
		{
			if (edges.get(i).isAvailabe())
			{
				result++;
			}
		}
		return result;
	}

	// int [] >> is has the length as number of edges
	// For example: [ 1 2 3 4 5]
	// [ 0 0 1 0 1]
	// It means edge number 3 and 5 should be removed!
	public CNONetwork subNetwork(int[] ids)
	{
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("*****NETWORK Configuration*****\n");
		result.append("Nodes:\n");
		for (Node n : nodes)
		{
			try
			{
				result.append(n.getName() + "(" + n + ") State:(" + n.getState(n.getTime()) + ") Stim("
						+ n.isInitializedNode() + ") T(" + n.getTime() + ")\n");
			}
			catch (NodeException e1)
			{
				e1.printStackTrace();
			}
		}

		result.append("\nEdges:\n");
		for (Edge e : edges)
		{

			result.append("id(" + e.getID() + ")[A:" + e.isAvailabe() + "][CompExp:" + e.isCompressedExpanded() + "][V:"
					+ e.isVisible() + "]");
			result.append("sourceNode(s):[");
			for (int i = 0; i < e.getSources().size(); i++)
			{
				Node n = e.getSources().get(i);
				int sign = e.getSourceSigns().get(i);

				result.append(n.getName() + "(" + n + ")" + "sgn(" + sign + ")" + ",");
			}

			result.deleteCharAt(result.length() - 1);

			result.append("] => targetNode:[");
			result.append(e.getTarget().getName() + "(" + e.getTarget() + ")");
			result.append("]\n");
		}
		result.append("*******************************");
		return result.toString();
	}

	public int computeNodeState(Node n) throws EdgeException, NodeException
	{

		n = isExistNode(n);

		if (n == null)
			throw new EdgeException("The target node does not exist in the Network");

		int currentTimePoint = n.getTime();
		int previousTimePoint = currentTimePoint - 1;

		if (currentTimePoint == 0)
		{
			return n.getState(0);
		}

		else if (n.isInitializedNode())
		{
			return n.getState(currentTimePoint);
		}

		else
		{
			int result = 0;
			for (int i = 0; i < edges.size(); i++)
			{
				if (edges.get(i).isAvailabe() && edges.get(i).getTarget().equals(n)
						&& edges.get(i).isVisible() == Edge.VISIBLE)
				{
					Edge edge = edges.get(i);
					int computed_value_based_on_previous_time_for_edge = edge.computeValue(currentTimePoint);

					result = orOptWithNA(result, edges.get(i).computeValue(currentTimePoint));
					result = result;

				}
			}
			return result;
		}
	}

	public HashMap<String, Integer> simulate() throws EdgeException, NodeException
	{
		boolean nothing_changed = false;

		for (int i = 0; i < 100 && nothing_changed == false; i++)
		{
			nothing_changed = true;

			for (Node n : nodes)
			{
				int newComputedState = computeNodeState(n);
				n.setState(newComputedState);
				if (n.isStable() == false)
				{
					nothing_changed = false;
				}
			}
		}

		HashMap<String, Integer> result = new HashMap<String, Integer>();

		if (nothing_changed == false)
		{
			result = postSimulation(false);
			IsAStableNetworkAfterSimulation = false;
		}
		else
		{
			result = postSimulation(true);
			IsAStableNetworkAfterSimulation = true;
		}

		return result;
	}

	public List<HashMap<String, Integer>> simulateForAllTreatmentCombination(int timePoint)
			throws EdgeException, NodeException, NetworkFactoryException
	{

		List<String> DVColumnNames = midas.namesDVColumns();
		int number_of_simulation = midas.Number_OF_TR_COMBINATIONS;
		ArrayList<String> treatmentColumnNames = (ArrayList<String>) midas.namesCues();
		List<ArrayList<Integer>> treatmentCombinations = midas.valueCuesAsInteger();

		if (timePoint == 0)
		{
			for (ArrayList<Integer> oneRow : treatmentCombinations)
			{
				for (int i = 0; i < oneRow.size(); i++)
				{
					oneRow.set(i, 0);
				}
			}
		}

		ArrayList<HashMap<String, Integer>> result = new ArrayList<HashMap<String, Integer>>();

		for (int i = 0; i < number_of_simulation; i++)
		{
			initializeNetwork(treatmentColumnNames, treatmentCombinations.get(i));
			result.add(simulate());
		}

		return result;

	}

	public HashMap<String, Integer> simulate(int rowNumber)
			throws EdgeException, NodeException, MidasGeneralException, NetworkFactoryException
	{
		this.initializeNetwork(rowNumber);
		return this.simulate();
	}

	private HashMap<String, Integer> postSimulation(boolean isStableNetwork)
	{

		HashMap<String, Integer> result = new HashMap<String, Integer>();

		for (Node n : nodes)
		{
			int final_last_state = n.finalResultAfterSimulation(isStableNetwork);

			n.setState(final_last_state);
			result.put(n.getName(), final_last_state);
		}

		IsASimulatedNetwork = true;
		return result;
	}

	public List<HashMap<String, Double>> getDVDeviation(int timePoint)
			throws EdgeException, NodeException, NetworkFactoryException
	{
		List<HashMap<String, Double>> result = new ArrayList<HashMap<String, Double>>();
		List<HashMap<String, Integer>> result_of_simulation = this.simulateForAllTreatmentCombination(timePoint);
		List<HashMap<String, Double>> dvCube = midas.getDVCubeofThisTimePoint(timePoint);
		List<String> desiredDVColumnNames = midas.namesDVColumns();

		for (int i = 0; i < dvCube.size(); i++)
		{
			HashMap<String, Double> one_row_in_result = new HashMap<String, Double>();
			for (String s : desiredDVColumnNames)
			{

				HashMap<String, Double> one_row_in_dv_cube = dvCube.get(i);
				HashMap<String, Integer> one_row_in_simulation_result = result_of_simulation.get(i);

				Double current_value_in_one_row_in_dv_cube = one_row_in_dv_cube.get(s);
				Double curent_value_in_one_row_in_simulation_result = (double) (one_row_in_simulation_result.get(s));

				if (curent_value_in_one_row_in_simulation_result == -1d)
				{
					one_row_in_result.put(s, -1d);
				}
				else
				{
					one_row_in_result.put(s, Math.pow(
							(curent_value_in_one_row_in_simulation_result - current_value_in_one_row_in_dv_cube), 2));
				}

			}
			result.add(one_row_in_result);
		}

		return result;
	}

	public double getFitnessNumber(int timePoint) throws EdgeException, NodeException, NetworkFactoryException
	{
		Double result = 0d;
		List<HashMap<String, Double>> deviationMatrixTN = this.getDVDeviation(timePoint);
		List<HashMap<String, Double>> deviationMatrixT0 = this.getDVDeviation(0);

		int NA = 0;
		int NAt0 = 0;
		Double numTN = 0d;
		Double numT0 = 0d;

		for (HashMap<String, Double> oneRow : deviationMatrixTN)
		{
			for (Double aValue : oneRow.values())
			{
				if (aValue == -1)
				{
					NA++;
					result = result + 1 * NANodePenaltyParameter;
				}
				else
				{
					numTN = numTN + (aValue / 2);
					result = result + (aValue / 2);
				}
			}
		}

		for (HashMap<String, Double> oneRow : deviationMatrixT0)
		{
			for (Double aValue : oneRow.values())
			{
				if (aValue == -1)
				{
					NAt0++;
					result = result + 1 * NANodePenaltyParameter;
				}
				else
				{
					numT0 = numT0 + (aValue / 2);
					result = result + (aValue / 2);
				}
			}
		}

		int NdataPoints = deviationMatrixT0.size() * deviationMatrixT0.get(0).size();

		// Compute Edges Penalties
		Double edgePenalties = 0d;
		Double edgePenaltiesTotal = 0d;
		for (Edge e : edges)
		{
			if (e.isVisible() == Edge.VISIBLE && e.isAvailabe())
			{
				edgePenalties = edgePenalties + e.penalty();
			}

			edgePenaltiesTotal = edgePenaltiesTotal + e.penalty();
		}

		edgePenalties = edgePenalties * EdgePenaltyParameter * NdataPoints / edgePenaltiesTotal;


		return (result + edgePenalties) / NdataPoints;
	}

	// Crazy estimation of the worst case! (Ask Attila!)
	public double getWorstCaseFitnessFunction()
	{
		double result = 2 * nodes.size() * 10 * (1 + NANodePenaltyParameter);
		for (Edge edge : edges)
		{
			if (edge.isAvailabe())
			{
				result = result + edge.penalty() * EdgePenaltyParameter;
			}
		}

		return result;
	}

	public static int orOptWithNA(int i, int j)
	{
		if (i == 1 || j == 1)
			return 1;
		else if (i == 0 || j == 0)
			return 0;
		else // if ( i==-1 || j==-1)
			return -1;
	}

	// correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	private boolean state_sign_2Bool(int state, int sign)
	{
		if (state == 1 && sign == 1)
			return true;
		else if (state == 1 && sign == 0)
			return false;
		else if (state == 0 && sign == 1)
			return false;
		else // state == 0 && sign == 0
			return true;

	}

	// correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	private boolean int2Bool(int i)
	{
		return i > 0 ? true : false;
	}

	// correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	private int bool2Int(boolean b)
	{
		return b ? 1 : 0;
	}

	private void cleanNetwork()
	{
		for (Node n : nodes)
		{
			n.cleanNode();
		}
	}

	// For the midasTreatmentColumns, you should pass "pi3k" (NOT TR:pi3ki) as
	// one of the entries for example
	public void initializeNetwork(ArrayList<String> midasTreatmentColumns,
			ArrayList<Integer> miadsOneTreatmentCombination) throws NetworkFactoryException, NodeException
	{
		cleanNetwork();

		if (midasTreatmentColumns.size() != miadsOneTreatmentCombination.size())
		{
			throw new NetworkFactoryException(
					"The size of the column arrays has to be equal to the size of the treatment combination array");
		}

		for (int i = 0; i < midasTreatmentColumns.size(); i++)
		{
			String nodeName = midasTreatmentColumns.get(i);

			
			Integer nodeState = miadsOneTreatmentCombination.get(i);

			Node currentNode = this.isExistNode(nodeName);
			if (currentNode != null)
			{
				if (midas.isStimuli(nodeName))
				{
					if (nodeState == 1)
					{
						currentNode.setState(1);
						currentNode.setStimulatedNodes(Node.STIMULUS, 1);
					}
					else if (nodeState == 0)
					{
						currentNode.setState(0);
						currentNode.setStimulatedNodes(Node.STIMULUS, 0);
					}
				}
				else if (midas.isInhibitor(nodeName))
				{
					if (nodeState == 1)
					{
						currentNode.setStimulatedNodes(Node.INHIBITOR, 1);
						currentNode.setState(0);
					}
					else if (nodeState == 0)
					{
						currentNode.setStimulatedNodes(Node.INHIBITOR, 0);
						currentNode.setState(-1);
					}
				}

			}
		}

		for (Node n : this.nodes)
		{
			if (n.getTime() == 0)
			{
				n.setState(-1);
			}

		}
		return;
	}

	// If row = 0, it points to the the row number 2 of the midas file
	public void initializeNetwork(int rowNumber) throws MidasGeneralException, NetworkFactoryException, NodeException
	{
		ArrayList<String> midasTreatmentColumns = (ArrayList<String>) midas.namesCues();
		ArrayList<Integer> miadsOneTreatmentCombination = (ArrayList<Integer>) midas.getThisRowOfTR(rowNumber);
		this.initializeNetwork(midasTreatmentColumns, miadsOneTreatmentCombination);

		return;
	}

	public ArrayList<Float> getDeviation(int timePoint)
	{
		System.out.println(midas.getNamesCuesIndices());

		return null;
	}

	public void restoreEdges()
	{
		for (Edge edge : edges)
		{
			edge.setVisible(Edge.VISIBLE);
		}
	}

	public void compress() throws EdgeException
	{
		Boolean compressionResult = null;
		for (int i = 0; i < nodes.size(); i++)
		{

			compressionResult = compressNode(nodes.get(i));

			if (compressionResult == false)
			{
				nodes.get(i).setCompressed(compressionResult);
			}
			else if (compressionResult == true)
			{
				nodes.get(i).setCompressed(compressionResult);
			}
		}
	}

	public List<String> compressedNodes()
	{
		List<String> compressedNodes = new ArrayList<String>();
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.get(i).isCompressed())
			{
				compressedNodes.add(nodes.get(i).getName());
			}
		}

		return compressedNodes;

	}

	boolean compressNode(Node n) throws EdgeException
	{
		n = isExistNode(n);

		boolean noLoop = noloop(n);

		int number_of_in_edges = numberOfInputEdges(n);
		int number_of_out_edges = numberOfOutputEdges(n);

		boolean exist_in_tr_columns = midas.namesCues().contains(n.getName());
		boolean exist_in_dv_columns = midas.namesDVColumns().contains(n.getName());

		if (number_of_in_edges == 0 && !exist_in_tr_columns)
		{
			for (int i = 0; i < edges.size(); i++)
			{
				Edge currentEdge = edges.get(i);
				if (currentEdge.isAvailabe() && currentEdge.getSources().contains(n))
				{
					currentEdge.setAvailabe(false);
				}
			}

			return true;
		}
		else if (number_of_out_edges == 0 && !exist_in_dv_columns)
		{
			for (int i = 0; i < edges.size(); i++)
			{
				Edge currentEdge = edges.get(i);
				if (currentEdge.isAvailabe() && currentEdge.getTarget().equals(n))
				{
					currentEdge.setAvailabe(false);
				}
			}
			return true;
		}

		else if (!exist_in_tr_columns && !exist_in_dv_columns && number_of_in_edges == 1 && number_of_out_edges > 1
				&& noLoop)
		{
			Node newSource = null;
			ArrayList<Node> newTargets = new ArrayList<Node>();
			Integer firstSign = null;
			ArrayList<Integer> secondarySigns = new ArrayList<Integer>();

			for (int i = 0; i < edges.size(); i++)
			{
				Edge currentEdge = edges.get(i);
				if (currentEdge.isAvailabe() && currentEdge.getTarget().equals(n))
				{
					newSource = currentEdge.getSources().get(0);
					firstSign = currentEdge.getSourceSigns().get(0);
					currentEdge.setAvailabe(false);
				}
				if (currentEdge.isAvailabe() && currentEdge.getSources().contains(n))
				{
					// In this step we know that each edges can only have one
					// source and one target,
					// since we do not have any hyper edges before expansion.
					newTargets.add(currentEdge.getTarget());
					secondarySigns.add(currentEdge.getSourceSigns().get(0));
					currentEdge.setAvailabe(false);
				}
			}

			for (int i = 0; i < newTargets.size(); i++)
			{
				Edge newEdge = addEdge(newSource, newTargets.get(i), firstSign * secondarySigns.get(i));
				newEdge.setCompressedExpanded(true);
			}

			return true;
		}

		else if (!exist_in_tr_columns && !exist_in_dv_columns && number_of_in_edges > 1 && number_of_out_edges == 1
				&& noLoop)
		{
			ArrayList<Node> newSourceNodes = new ArrayList<Node>();
			ArrayList<Integer> firstSigns = new ArrayList<Integer>();

			Integer secondSign = null;
			Node newTarget = null;

			for (int i = 0; i < edges.size(); i++)
			{
				Edge currentEdge = edges.get(i);
				if (currentEdge.isAvailabe() && currentEdge.getTarget().equals(n))
				{
					newSourceNodes.add(currentEdge.getSources().get(0));
					firstSigns.add(currentEdge.getSourceSigns().get(0));
					currentEdge.setAvailabe(false);
				}
				if (currentEdge.isAvailabe() && currentEdge.getSources().contains(n))
				{
					newTarget = currentEdge.getTarget();
					secondSign = currentEdge.getSourceSigns().get(0);
					currentEdge.setAvailabe(false);
				}
			}

			for (int i = 0; i < newSourceNodes.size(); i++)
			{

				Edge newEdge = addEdge(newSourceNodes.get(i), newTarget, firstSigns.get(i) * secondSign);
				newEdge.setCompressedExpanded(true);
			}

			return true;
		}

		else if (!exist_in_tr_columns && !exist_in_dv_columns && number_of_in_edges == 1 && number_of_out_edges == 1
				&& noLoop)
		{
			Node newSource = null;
			Integer firstSign = null;

			Node newTarget = null;
			Integer secondSign = null;

			for (int i = 0; i < edges.size(); i++)
			{
				Edge currentEdge = edges.get(i);
				if (currentEdge.isAvailabe() && currentEdge.getTarget().equals(n))
				{
					newSource = currentEdge.getSources().get(0);
					firstSign = currentEdge.getSourceSigns().get(0);
					currentEdge.setAvailabe(false);
				}
				if (currentEdge.isAvailabe() && currentEdge.getSources().contains(n))
				{
					newTarget = currentEdge.getTarget();
					secondSign = currentEdge.getSourceSigns().get(0);
					currentEdge.setAvailabe(false);
				}
			}

			Edge newEdge = addEdge(newSource, newTarget, firstSign * secondSign);
			newEdge.setCompressedExpanded(true);

			return true;
		}
		else if (number_of_in_edges > 1 && number_of_out_edges > 1)
		{
			return false;
		}
		else
		{
			return false;
		}
	}

	boolean noloop(Node n)
	{
		ArrayList<Node> inputnodes = new ArrayList<Node>();
		ArrayList<Node> outputnodes = new ArrayList<Node>();

		for (int i = 0; i < edges.size(); i++)
		{
			if (edges.get(i).isAvailabe() && edges.get(i).getTarget().equals(n))
			{
				inputnodes.add(edges.get(i).getSources().get(0));
			}
			else if (edges.get(i).isAvailabe() && edges.get(i).getSources().contains(n))
			{
				outputnodes.add(edges.get(i).getTarget());
			}
		}

		for (int i = 0; i < inputnodes.size(); i++)
		{
			if (outputnodes.contains(inputnodes.get(i)))
			{
				return false;
			}
		}

		return true;
	}

	int numberOfInputEdges(Node n)
	{
		return inputEdges(n).size();
	}

	List<Edge> inputEdges(Node n)
	{
		// n=isExistNode(n);

		if (n == null)
			return null;

		List<Edge> result = new ArrayList<Edge>();

		for (Edge e : edges)
		{
			if (e.getTarget().equals(n) && e.isAvailabe())
			{
				result.add(e);
			}
		}

		return result;
	}

	int numberOfOutputEdges(Node n)
	{
		return outputEdges(n).size();
	}

	List<Edge> outputEdges(Node n)
	{

		if (n == null)
			return null;

		List<Edge> result = new ArrayList<Edge>();

		for (Edge e : edges)
		{
			if (e.getSources().contains(n) && e.isAvailabe())
			{
				result.add(e);
			}
		}

		return result;

	}

	public void expand() throws EdgeException
	{
		for (int i = 0; i < nodes.size(); i++)
		{

			expandNode(nodes.get(i));
		}
	}

	boolean expandNode(Node n) throws EdgeException
	{
		n = isExistNode(n);

		int number_of_in_edges = numberOfInputEdges(n);

		if (number_of_in_edges >= 2)
		{
			ArrayList<Node> sourceNodes = new ArrayList<Node>();
			ArrayList<Integer> sourceSigns = new ArrayList<Integer>();
			Node targetNode = n;

			for (int i = 0; i < edges.size(); i++)
			{
				Edge currentEdge = edges.get(i);

				if (currentEdge.isAvailabe() && currentEdge.getTarget().equals(n))
				{
					sourceNodes.add(currentEdge.getSources().get(0));
					sourceSigns.add(currentEdge.getSourceSigns().get(0));
				}
			}

			Edge newHyperEdge = addEdge(sourceNodes, targetNode, sourceSigns);
			newHyperEdge.setCompressedExpanded(true);

			return true;
		}

		return false;
	}

	public File exportNetwork(File exportFile) throws IOException
	{

		OutputStream os = new FileOutputStream(exportFile);
		OutputStreamWriter writer = new OutputStreamWriter(os);
		int hyperEdgeAndNode = 0;

		for (int i = 0; i < edges.size(); i++)
		{
			Edge edge = edges.get(i);
			if (edge.isAvailabe() && edge.sources.size() == 1)
			{
				writer.write(edge.getSources().get(0).getName());
				writer.write("\t" + edge.getSourceSigns().get(0) + "\t");
				writer.write(edge.getTarget().getName() + "\r\n");
			}
			else if (edge.isAvailabe() && edge.sources.size() > 1)
			{
				hyperEdgeAndNode++;
				for (int j = 0; j < edge.sources.size(); j++)
				{
					writer.write(edge.getSources().get(j).getName());
					writer.write("\t" + edge.getSourceSigns().get(j) + "\t");
					writer.write("and" + hyperEdgeAndNode + "\r\n");
				}
				writer.write("and" + hyperEdgeAndNode);
				writer.write("\t" + 1 + "\t");
				writer.write(edge.getTarget().getName() + "\r\n");
			}
		}

		writer.close();

		return exportFile;
	}
        
        public File exportNetwork2(File exportFile) throws IOException
	{

		OutputStream os = new FileOutputStream(exportFile);
		OutputStreamWriter writer = new OutputStreamWriter(os);
		int hyperEdgeAndNode = 0;

		for (int i = 0; i < edges.size(); i++)
		{
			Edge edge = edges.get(i);
			if (edge.sources.size() == 1 && edge.isVisible() == true && edge.isAvailabe())
			{
				writer.write(edge.getSources().get(0).getName());
				writer.write("\t" + edge.getSourceSigns().get(0) + "\t");
				writer.write(edge.getTarget().getName() + "\r\n");
			}
			else if (edge.sources.size() > 1 && edge.isVisible() == true && edge.isAvailabe())
			{
				hyperEdgeAndNode++;
				for (int j = 0; j < edge.sources.size(); j++)
				{
					writer.write(edge.getSources().get(j).getName());
					writer.write("\t" + edge.getSourceSigns().get(j) + "\t");
					writer.write("and" + hyperEdgeAndNode + "\r\n");
				}
				writer.write("and" + hyperEdgeAndNode);
				writer.write("\t" + 1 + "\t");
				writer.write(edge.getTarget().getName() + "\r\n");
			}
		}

		writer.close();

		return exportFile;
	}

	public ArrayList<String> getAdaptedEdgeNames()
	{

		ArrayList<String> result = new ArrayList<String>();

		int hyperEdgeAndNode = 0;

		for (int i = 0; i < edges.size(); i++)
		{
			Edge edge = edges.get(i);
			if (edge.isAvailabe() && edge.sources.size() == 1)
			{
				result.add(edge.getSources().get(0).getName() + " (" + edge.getSourceSigns().get(0) + ") "
						+ edge.getTarget().getName());
			}
			else if (edge.isAvailabe() && edge.sources.size() > 1)
			{
				hyperEdgeAndNode++;
				for (int j = 0; j < edge.sources.size(); j++)
				{
					result.add(edge.getSources().get(j).getName() + " (" + edge.getSourceSigns().get(j) + ") " + "and"
							+ hyperEdgeAndNode);
				}

				result.add("and" + hyperEdgeAndNode + " (" + "1" + ") " + edge.getTarget().getName());
			}
		}

		return result;

	}

	public Edge getAvailableEdge(int index)
	{
		int count = -1;
		int adapted_new_index = -1;
		for (int i = 0; i < edges.size() && adapted_new_index < 0; i++)
		{
			if (edges.get(i).isAvailabe() && index != count)
			{
				count++;
				if (index == count)
				{
					adapted_new_index = i;
				}
			}
		}

		if (adapted_new_index < 0)
		{
			return null;
		}
		else
		{
			return edges.get(adapted_new_index);
		}

	}

}
