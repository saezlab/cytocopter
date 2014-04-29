package uk.ac.ebi.cytocopter.internal.tasks.enums;

public enum CytocopterCommandsEnum {
	CONFIGURE("configure"),
	PREPROCESS("preprocess");
	
	public static final String CYTOCOPTER_NAME_SPACE = "cytocopter";
	
	private String name;
	
	private CytocopterCommandsEnum (String name) {
		this.name = name;
	}
	
	public String getName () {
		return this.name;
	}
}
