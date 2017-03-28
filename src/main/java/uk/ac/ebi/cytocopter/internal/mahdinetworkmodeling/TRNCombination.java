package uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling;

public class TRNCombination
{
	private Node node;
	private String treatment;
	private Integer value;
	
	public TRNCombination(Node node, Integer value)
	{
		this.node = node;
		this.treatment = node.getName();
		this.value = value;
	}
	
	public TRNCombination(String treatment, Integer value)
	{
		this.treatment = treatment;
		this.node = new Node(treatment);
		this.value = value;
	}
	
	public Node getNode()
	{
		return node;
	}

	public String getTreatment()
	{
		return treatment;
	}

	public Integer getValue()
	{
		return value;
	}
	public void setValue(Integer value)
	{
		this.value = value;
	}
}
