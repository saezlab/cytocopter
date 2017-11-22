package uk.ac.ebi.cytocopter.internal.mahdiexceptions;

public class NodeException extends Exception
{
	String message;

	public NodeException(String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return message + "\n";
	}

}
