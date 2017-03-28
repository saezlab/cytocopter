package uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling;

import uk.ac.ebi.cytocopter.internal.mahdiexceptions.NodeException;

public class Node
{
	public final static int hSize = 50;

	public final static int STIMULUS = 1;
	public final static int INHIBITOR = 0;
	public final static int NOTSTIMULATED = -1;

	private Boolean compressed = null;

	public Boolean isCompressed()
	{
		return compressed;
	}

	public void setCompressed(Boolean compressed)
	{
		this.compressed = compressed;
	}

	private int nodeTime = -1;

	// States can be:
	// 1: active
	// 0: inactive
	// -1: unkown
	private int[] statesHistory = new int[hSize];

	String name;

	// true: measured (DV column exist in the Midas file)
	// false: not measured (DV column doesn't exist in the Midas file)
	boolean measured;

	// 1: if stimulated in MIDAS (TR:name or
	// 0: if inhibited (TR:iname))
	// -1: neither
	int stimulated;
	// Shows the
	int value_of_stimulated_node;

	public void setStimulatedNodes(int stimulus_or_inhibitor, int value) throws NodeException
	{
		stimulated = stimulus_or_inhibitor;
		if (value == 0 || value == 1)
		{
			value_of_stimulated_node = value;
		}
		else
		{
			throw new NodeException("value has to be 0 or 1");
		}
	}

	public boolean isInitializedNode()
	{
		if (stimulated == Node.STIMULUS)
			return true;
		else if (stimulated == Node.INHIBITOR && value_of_stimulated_node == 1)
			return true;
		else // if (stimulated == Node.INHIBITOR && value_of_stimulated_node ==
				// 0)
			return false;
	}

	private int getInitializedValue()
	{
		if (stimulated == Node.STIMULUS && value_of_stimulated_node == 1)
			return 1;
		else if (stimulated == Node.STIMULUS && value_of_stimulated_node == 0)
			return 0;
		else if (stimulated == Node.INHIBITOR && value_of_stimulated_node == 1)
			return 0;
		else // if (stimulated == Node.INHIBITOR && value_of_stimulated_node ==
				// 0)
			return -1;
	}

	public int finalResultAfterSimulation(boolean hasStableNetwork)
	{
		if (nodeTime == 0)
			return statesHistory[0];
		else if (nodeTime == 1)
			return statesHistory[1];

		else if (hasStableNetwork == true)
			return statesHistory[nodeTime % hSize];
		else
		{

			// find last nodeState
			int last_state = statesHistory[nodeTime % hSize];
			boolean flag = true;

			for (int i = 0; i < hSize; i++)
			{
				if (last_state != statesHistory[i])
					flag = false;
			}

			if (flag == true)
				return last_state;
			else
				return -1;
		}
	}

	public Node(String name)
	{
		// initial value for all the nodes
		this.name = name;
		for (int i = 0; i < hSize; i++)
		{
			statesHistory[i] = 0;
		}
		setState(0);
	}

	public int getTime()
	{
		return nodeTime;
	}

	public int getState(int time) throws NodeException
	{
		if (time <= nodeTime)
		{
			return statesHistory[time % hSize];
		}
		else
			throw new NodeException("There is no history for that time in the node");
	}

	public void setState(int state)
	{

		nodeTime++;

		if (isInitializedNode())
			statesHistory[nodeTime % hSize] = getInitializedValue();
		else
			statesHistory[nodeTime % hSize] = state;

	}

	public boolean isStable()
	{
		boolean flag = false;
		int previousNodeTime = nodeTime - 1;
		if (nodeTime == 0)
			return false;
		else if (nodeTime == 1)
			return false;
		else if (statesHistory[previousNodeTime % hSize] == statesHistory[nodeTime % hSize])
			return true;
		else
			return false;

	}

	public String getName()
	{
		return name;
	}

	private void setName(String name)
	{
		this.name = name;
	}

	public boolean isMeasured()
	{
		return measured;
	}

	public void setMeasured(boolean measured)
	{
		this.measured = measured;
	}

	public int getStimulated()
	{
		return stimulated;
	}

	public void setStimulated(int stimulated)
	{
		this.stimulated = stimulated;
	}

	public boolean equals(Node b)
	{
		if (b.getName().equals(this.getName()))
			return true;
		return false;
	}

	public void cleanNode()
	{
		nodeTime = 0;
		for (int i = 0; i < hSize; i++)
		{
			statesHistory[i] = -1;
		}

	}

}
