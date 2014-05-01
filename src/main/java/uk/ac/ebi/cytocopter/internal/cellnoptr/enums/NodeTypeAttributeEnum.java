package uk.ac.ebi.cytocopter.internal.cellnoptr.enums;

public enum NodeTypeAttributeEnum {
	STIMULATED ("stimulated"),
	INHIBITED ("inhibited"),
	READOUT ("readout"),
	COMPRESSED ("compressed"),
	INHIBITED_READOUT ("inhibited readout"),
	OPERATOR ("operator"),
	NA ("na");
	
	private String attributeName = "Cytocopter.NodeType";
	private String value;
	
	private NodeTypeAttributeEnum (String value) {
		this.value = value;
	}
	
	public String getAttributeValue () {
		return value;
	}
	
	public String getAttributeName () {
		return attributeName;
	}
	
	public static NodeTypeAttributeEnum mapAttributeString (String attributeString) {
		for (NodeTypeAttributeEnum attribute : NodeTypeAttributeEnum.values())
			if (attribute.getAttributeValue().equals(attributeString))
				return attribute;
		
		return null;
	}
}
