package uk.ac.ebi.cytocopter.internal.mahdiexceptions;

public class NetworkFactoryException extends Exception
{
	String message;

	public NetworkFactoryException(String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return message + "\n";
	}

}
