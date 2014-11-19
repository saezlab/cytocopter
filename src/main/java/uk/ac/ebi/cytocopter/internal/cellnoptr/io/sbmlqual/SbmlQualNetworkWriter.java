package uk.ac.ebi.cytocopter.internal.cellnoptr.io.sbmlqual;

import java.io.File;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.sbfc.api.GeneralModel;

import uk.ac.ebi.cyrface.internal.sbml.sbfc.CellNOpt2Qual;
import uk.ac.ebi.cyrface.internal.sbml.sbfc.CellNOptModel;

public class SbmlQualNetworkWriter extends AbstractTask {

	private CyServiceRegistrar cyServiceRegistrar;
	private File qualFile;
	private File sifFile;
	
	
	public SbmlQualNetworkWriter(File sbmlQualNetworkFile, CyServiceRegistrar cyServiceRegistrar, File sifFile) {
		this.qualFile = sbmlQualNetworkFile;
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.sifFile = sifFile;
	}

	@Override
	public void run (TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Exporting network to SBML-Qual...");
		
		CellNOptModel model = new CellNOptModel();
		model.setModelFromFile(sifFile.getAbsolutePath());
		
		CellNOpt2Qual converter = new CellNOpt2Qual();
		GeneralModel convertedModel = converter.convert(model);
		
		convertedModel.modelToFile(qualFile.getAbsolutePath());
		
		taskMonitor.setTitle("Current network exported to SBML-Qual format!");
	}

}
