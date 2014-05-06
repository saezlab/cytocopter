package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.NodeTypeAttributeEnum;
import uk.ac.ebi.cytocopter.internal.cellnoptr.utils.NetworkAttributes;

public class SetNodeTypeTask extends AbstractTask implements ObservableTask {

	private CyServiceRegistrar cyServiceRegistrar;
	private StringBuilder outputString;
	
	@Tunable(description="names")
	public String names = "";
	
	@Tunable(description="network")
	public String networkName = "";
	
	@Tunable(description="type")
	public String type = "";

	public SetNodeTypeTask (CyServiceRegistrar cyServiceRegistrar) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		outputString = new StringBuilder();
	}
	
	@Override
	public void run (TaskMonitor taskMonitor) throws Exception {
		NodeTypeAttributeEnum attribute = NodeTypeAttributeEnum.mapAttributeString(type);
		
		String[] namesList = names.split(";");
		for (String name : namesList) {
			NetworkAttributes.addNodeTypeAttribute(networkName, name, attribute, cyServiceRegistrar);
		}
	}

	@Override
	public <R> R getResults(Class<? extends R> type) {
		return type.cast(outputString.toString());
	}
}
