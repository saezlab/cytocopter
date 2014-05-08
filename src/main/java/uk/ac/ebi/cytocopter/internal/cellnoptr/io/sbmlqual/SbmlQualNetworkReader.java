package uk.ac.ebi.cytocopter.internal.cellnoptr.io.sbmlqual;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SbmlQualNetworkReader extends AbstractTask implements CyNetworkReader {

	private CyServiceRegistrar cyServiceRegistrar;
	private HashMap<String, CyNode> nodeNameMap;

	private InputStream networkFile;
	private String inputName;

	private CyNetwork cyNetwork;
	private CyNetworkView cyNetworkView;
	
	
	public SbmlQualNetworkReader (InputStream networkFile, String inputName, CyServiceRegistrar cyServiceRegistrar) {
		this.networkFile = networkFile;
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.nodeNameMap = new HashMap<String, CyNode>();
		this.inputName = inputName;
	}

	@Override
	public void run (TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Importing SBML-Qual...");
		
		// Read SBML-Qual xml 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(networkFile);
		doc.getDocumentElement().normalize();
		
		// Create network
		cyNetwork = cyServiceRegistrar.getService(CyNetworkFactory.class).createNetwork(); 
		
		String networkName = FilenameUtils.getBaseName(FilenameUtils.getBaseName(inputName));
		cyNetwork.getRow(cyNetwork).set(CyNetwork.NAME, networkName);
		
		// Read nodes
		NodeList species = doc.getElementsByTagName("qual:qualitativeSpecies");
		
		for (int i = 0; i < species.getLength(); i++) {
			Node specie = species.item(i);
			
			if (specie.getNodeType() == Node.ELEMENT_NODE) {
				String specieName = ((Element)specie).getAttribute("qual:id");
				
				CyNode node = cyNetwork.addNode();
				cyNetwork.getRow(node).set(CyNetwork.NAME, specieName);
				
				nodeNameMap.put(specieName, node);
			}
		}
		
		// Read edges
		NodeList edges = doc.getElementsByTagName("qual:transition");
		
		for (int i = 0; i < edges.getLength(); i++) {
			Element transition = (Element) edges.item(i);
			
			NodeList inputs = transition.getElementsByTagName("qual:input");
			
			Element output = (Element) transition.getElementsByTagName("qual:output").item(0);
			String outputNodeName = output.getAttribute("qual:qualitativeSpecies");
			
			for (int j = 0; j < inputs.getLength(); j++) {
				String inputNodeName = ((Element) inputs.item(j)).getAttribute("qual:qualitativeSpecies");
				
				String interaction = ((Element) inputs.item(j)).getAttribute("qual:sign");
				interaction = (interaction.equals("negative") ? "-1" : "1");
				
				String name = inputNodeName + " (" + interaction + ") " + outputNodeName;
				
				CyEdge edge = cyNetwork.addEdge(nodeNameMap.get(inputNodeName), nodeNameMap.get(outputNodeName), true);
				
				CyRow attr = cyNetwork.getRow(edge);
				attr.set(CyNetwork.NAME, name);
				attr.set(CyEdge.INTERACTION, interaction);
			}
		}
	}
	
	@Override
	public CyNetworkView buildCyNetworkView(CyNetwork arg0) {
		cyNetworkView = cyServiceRegistrar.getService(CyNetworkViewFactory.class).createNetworkView(cyNetwork);
		
		return cyNetworkView;
	}

	@Override
	public CyNetwork[] getNetworks() {
		return new CyNetwork[] { cyNetwork };
	}

}
