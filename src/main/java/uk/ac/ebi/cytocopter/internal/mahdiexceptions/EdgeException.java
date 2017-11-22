package uk.ac.ebi.cytocopter.internal.mahdiexceptions;

public class EdgeException extends Exception
{
	String message;

	public EdgeException(String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return message + "\n";
	}
}
