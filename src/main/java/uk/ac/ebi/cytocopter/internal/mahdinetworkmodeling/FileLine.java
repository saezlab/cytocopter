package uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling;

class FileLine
{
	Integer lineNumber;
	String source;
	Integer sign;
	String target;
	public String getSource()
	{
		return source;
	}
	public void setSource(String source)
	{
		this.source = source;
	}
	public Integer getLineNumber()
	{
		return lineNumber;
	}
	public void setLineNumber(Integer lineNumber)
	{
		this.lineNumber = lineNumber;
	}
	public Integer getSign()
	{
		return sign;
	}
	public void setSign(Integer sign)
	{
		this.sign = sign;
	}
	public String getTarget()
	{
		return target;
	}
	public void setTarget(String target)
	{
		this.target = target;
	}
	
}