package uk.ac.ebi.cytocopter.internal.mahdimidasexceptions;

public class MidasGeneralException extends Exception
{
	String message;

	public MidasGeneralException(String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return message + "\n";
	}

}
