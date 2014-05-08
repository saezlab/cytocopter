package uk.ac.ebi.cytocopter.internal.cellnoptr.io.sbmlqual;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.util.StreamUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SbmlQualCyFileFilter extends BasicCyFileFilter {

	private static final String SBML_ATTRIBUTE = "sbml";
	private static final String SBML_QUAL_ATTRIBUTE = "xmlns:qual";
	private static final String SBML_QUAL_ATTRIBUTE_VALUE = "http://www.sbml.org/sbml/level3/version1/qual/version1";
	
	public SbmlQualCyFileFilter (StreamUtil streamUtil) {
		super(
				new String[] {"xml", "sbml"}, 
				new String[] {"text/xml", "application/rdf+xml", "application/xml", "text/plain"},
				"SBML-Qual",
				DataCategory.NETWORK,
				streamUtil
			);
	}

	
	@Override
	public boolean accepts (InputStream stream, DataCategory category) {
		boolean isSbmlQualFile = false; 
		
		try {
			// Read xml file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stream);
			doc.getDocumentElement().normalize();
		
			Element species = (Element) doc.getElementsByTagName(SBML_ATTRIBUTE).item(0);
			
			if (species.hasAttribute(SBML_QUAL_ATTRIBUTE))
				isSbmlQualFile = true;
			
			System.out.println("SBML-Qual filter");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isSbmlQualFile;
	}
}
