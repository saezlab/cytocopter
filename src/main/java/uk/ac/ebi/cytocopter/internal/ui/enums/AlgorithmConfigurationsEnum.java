package uk.ac.ebi.cytocopter.internal.ui.enums;

public enum AlgorithmConfigurationsEnum
{

	SIZE_FAC("Size fac", "Size scalling factor", 0.0001, "sizeFac"),
	NA_FAC("NA fac", "NA scalling factor", 1.0, "NAFac"),
	POP_SIZE("Pop size", "Population size", 50.0, "popSize"),
	MAX_TIME("Max time", "Maximum optimisation time", 15.0, "maxTime"),
	MAX_GENS("Max gen", "Maximum number of generations", 500.0, "maxGens"),
	REL_TOL("Rel tol", "Relative tolerance", 0.1, "relTol");
	
	private String name;
	private String description;
	private Double defaultValue;
	private String rArgName;
	
	private AlgorithmConfigurationsEnum(String name, String description, Double defaultValue, String rArgName)
	{
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.rArgName = rArgName;
	}
	
	public String getDescription()
	{
		return description;
	}

	public Double getDefaultValue()
	{
		return defaultValue;
	}

	public String getName()
	{
		return name;
	}

	public String getRArgName()
	{
		return rArgName;
	}
}
