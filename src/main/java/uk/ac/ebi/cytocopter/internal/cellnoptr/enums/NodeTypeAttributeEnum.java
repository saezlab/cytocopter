package uk.ac.ebi.cytocopter.internal.cellnoptr.enums;

import java.util.Collection;
import java.util.TreeSet;

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
	
	public static Collection<String> intersect (String[] list1, String[] list2) {
		Collection<String> overlap = new TreeSet<String>();
		
		for (String element : list1)
			for (String element2 : list2)
				if (element.equals(element2))
					overlap.add(element2);
		
		return overlap;
	}
	
	public static String isOperator (String nodeName) {
		if (nodeName.toLowerCase().startsWith("and"))
			return "AND";
		
		if (nodeName.toLowerCase().startsWith("or"))
			return "OR";
		
		return null;
	}
}
