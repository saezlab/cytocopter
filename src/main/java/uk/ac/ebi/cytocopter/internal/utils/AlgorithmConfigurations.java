package uk.ac.ebi.cytocopter.internal.utils;

public enum AlgorithmConfigurations {

	SIZE_FAC("Size fac", "Size scalling factor", 1E-4),
	NA_FAC("NA fac", "NA scalling factor", 1.0),
	POP_SIZE("Pop size", "Population size", 50.0),
	P_MUTATION("P mutation", "Mutation probability", 50.0),
	MAX_TIME("Max time", "Maximum optimisation time", 15.0),
	MAX_GENS("Max gen", "Maximum number of generations", 500.0),
	STALL_GEN_MAX("Stall gen max", "Maximum number of stall generations", 100.0),
	SEL_PRES("Sel press", "Selective pressure", 1.2),
	ELITISM("Elistism", "Elistism", 5.0),
	REL_TOL("Rel tol", "Relative tolerance", 0.1);
	
	private String name;
	private String description;
	private Double defaultValue;
	
	private AlgorithmConfigurations (String name, String description, Double defaultValue) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
	}
	
	public String getDescription () {
		return description;
	}
	
	public Double getDefaultValue () {
		return defaultValue;
	}
	
	public String getName () {
		return name;
	}
	
}
