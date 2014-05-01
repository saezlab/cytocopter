package uk.ac.ebi.cytocopter.internal.cellnoptr.enums;

public enum FormalismEnum {
	BOOLEAN ("boolean");

	private String name;
	
	private FormalismEnum (String name) {
		this.name = name;
	}
	
	public String getName () {
		return name;
	}
	
}
