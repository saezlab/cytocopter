package uk.ac.ebi.cytocopter.internal.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;

//import uk.ac.ebi.cyrface.internal.rinterface.rserve.RserveHandler;
import uk.ac.ebi.cytocopter.internal.cellnoptr.enums.FormalismEnum;
import uk.ac.ebi.cytocopter.internal.ui.enums.AlgorithmConfigurationsEnum;
import uk.ac.ebi.cytocopter.internal.ui.listeners.DataMouseListener;
import uk.ac.ebi.cytocopter.internal.ui.listeners.NetworkComboBoxAddedNetwork;
import uk.ac.ebi.cytocopter.internal.ui.listeners.NetworkComboBoxRemovedNetwork;
import uk.ac.ebi.cytocopter.internal.ui.listeners.OptimiseButtonActionListener;
import uk.ac.ebi.cytocopter.internal.ui.listeners.PreprocessButtonActionListener;
import uk.ac.ebi.cytocopter.internal.ui.listeners.SBMLExportButtonActionListener;
import uk.ac.ebi.cytocopter.internal.ui.listeners.SBMLImportButtonActionListener;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements CytoPanelComponent {

	public CyServiceRegistrar cyServiceRegistrar;
//	public RserveHandler connection;
	
	public JLabel networkLabel;
	public JComboBox networkCombo;
	public DefaultComboBoxModel networkModel;
	
	public JLabel dataLabel;
	public JTextField dataTextField;
	public File dataFile;
	
	public JButton preprocessButton;
        
        public JButton SBMLExportButton;
        public JButton SBMLImportButton;
	
	public JLabel formalismLabel;
	public JComboBox formalismCombo;
	public DefaultComboBoxModel formalismModel;
	
	public JLabel dataTimePointLabel;
	public JComboBox dataPointCombo;
	
	public JButton optimiseButton;
	
	public JPanel algorithmPanel;
	public Map<String, JTextField> configurationsMap;
        
        public JCheckBox checkbox;
	
	
	public ControlPanel (CyServiceRegistrar cyServiceRegistrar) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.configurationsMap = new HashMap<String, JTextField>();
		
		// Create panel layout
		createPanelLayout();
		
		// Show panel
		this.setVisible(true);
	}
	
	private void createPanelLayout () {
		// Define panel layout
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]{70, 110, 110};

		setLayout(layout);
		setSize(new Dimension(450, 400));
		setPreferredSize(new Dimension(450, 400));

		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;

		// Initialise panel components
                
                c.gridy = 0;
                createSBMLImportButton (c);
                
                
		c.gridy = 1;
		createNetworkRow (c);

		c.gridy = 2;
		createDataRow (c);
                
                c.gridy = 3;
                createExpandBox (c);
		
		c.gridy = 4;
		createPreprocessButtonRow (c);

		c.gridy = 5;
		createFormalismRow (c);

		c.gridy = 6;
		createTimePointsRows (c);

		c.gridy = 7;
		createOptimiseButtonRow (c);
                
                c.gridy = 7;
                createSBMLExportButton (c);
                
		c.gridy = 9;
		createAlgorithmConfigurations (c);

		// Add components listeners
		initialiseNetworkRow();
		initialiseDataRow();
		initialisePreprocessButtonRow();
		intialiseFormalismRow();
		initialiseTimePointsRows();
		initialiseOptimiseButtonRow();
                initialiseSBMLExportButton();
                initialiseSBMLImportButton();
		initialiseAlgorithmConfigurations();
	}
	
	// Create methods
	private void createNetworkRow (GridBagConstraints c) {
		networkLabel = new JLabel("Network");
		networkLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
		c.gridx = 0;
		c.gridwidth = 1;
		add(networkLabel, c);
		
		networkCombo = new JComboBox();
		c.gridx = 1;
		c.gridwidth = 2;
		add(networkCombo, c);
	}
	
	
	private void createDataRow (GridBagConstraints c) {
		dataLabel = new JLabel("Data");
		dataLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
		c.gridx = 0;
		c.gridwidth = 1;
		add(dataLabel, c);
		
		dataTextField = new JTextField();
		c.gridx = 1;
		c.gridwidth = 2;
		add(dataTextField, c);
	}
	
	private void createPreprocessButtonRow (GridBagConstraints c) {
		c.gridx = 2;
		c.gridwidth = 1;
		preprocessButton = new JButton("Preprocess");
		add(preprocessButton, c);
	}
	
	private void createFormalismRow (GridBagConstraints c) {
		formalismLabel = new JLabel("Formalism");
		formalismLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
		c.gridx = 0;
		c.gridwidth = 1;
		add(formalismLabel, c);
		
		formalismCombo = new JComboBox();
		c.gridx = 1;
		c.gridwidth = 2;
		add(formalismCombo, c);
	}
	
	private void createTimePointsRows (GridBagConstraints c) {
		c.gridx = 1;
		c.gridwidth = 1;
		dataTimePointLabel = new JLabel("Time point");
		dataTimePointLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
		add(dataTimePointLabel, c);
		
		c.gridx = 2;
		dataPointCombo = new JComboBox();
		add(dataPointCombo, c);
	}
	
	private void createOptimiseButtonRow (GridBagConstraints c) {
		c.gridx = 2;
		c.gridwidth = 1;
		optimiseButton = new JButton("Optimise");
		optimiseButton.setEnabled(false);
		add(optimiseButton, c);
	}
        
        
        private void createSBMLExportButton (GridBagConstraints c) {
		c.gridx = 1;
		c.gridwidth = 1;
		SBMLExportButton = new JButton("Export to SBML");
                SBMLExportButton.setEnabled(false);
		add(SBMLExportButton, c);
	}
        
        private void createSBMLImportButton (GridBagConstraints c) {
		c.gridx = 2;
		c.gridwidth = 1;
		SBMLImportButton = new JButton("Import from SBML");
                SBMLImportButton.setEnabled(true);
		add(SBMLImportButton, c);
	}
        
        private void createExpandBox (GridBagConstraints c) {
		c.gridx = 1;
		c.gridwidth = 1;
		checkbox = new JCheckBox("Expand gates");
		add(checkbox, c);
	}
        
        public JCheckBox getJCheckBox(){
            return checkbox;
        }
        
        
	
	private void createAlgorithmConfigurations (GridBagConstraints c) {
		c.gridx = 2;
		c.gridwidth = 1;
		
		GridBagLayout algorithmLayout = new GridBagLayout();
		algorithmLayout.columnWidths = new int[]{70, 75, 70, 75};
		
		algorithmPanel = new JPanel(algorithmLayout);
		algorithmPanel.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Configurations"));
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.ipadx = 5;
		constraints.ipady = 6;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		for (AlgorithmConfigurationsEnum conf : AlgorithmConfigurationsEnum.values()) {
			JLabel label = new JLabel(conf.getName());
			label.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			JTextField textField = new JTextField(conf.getDefaultValue().toString());
			
			// Add components to the panel
			algorithmPanel.add(label, constraints);
			constraints.gridx++;
			algorithmPanel.add(textField, constraints);
			
			// Add algorithm configuration to configurations map
			configurationsMap.put(conf.getRArgName(), textField);
			
			// Update grid bag indices
			if (constraints.gridx == 3) {
				constraints.gridx = 0;
				constraints.gridy++;
				
			} else {
				constraints.gridx++;
			}
		}
		
		c.gridy = 8;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weighty = 0.1;
		
		add(algorithmPanel, c);
	}
        
        
	
	
	// Initialise methods  
	private void initialiseNetworkRow () {
		// Fill combo box
		networkModel = new DefaultComboBoxModel();
 		
		for (CyNetwork cyNetwork : cyServiceRegistrar.getService(CyNetworkManager.class).getNetworkSet()) {
			String cyNetworkName = cyNetwork.getRow(cyNetwork).get(CyNetwork.NAME, String.class);
			networkModel.addElement(cyNetworkName);
		}
		
		networkCombo.setModel(networkModel);
		
		// Add combo box listeners
		NetworkComboBoxAddedNetwork addNetworkListener = new NetworkComboBoxAddedNetwork(networkCombo);
		cyServiceRegistrar.registerAllServices(addNetworkListener, new Properties());
		
		NetworkComboBoxRemovedNetwork removeNetworkListener = new NetworkComboBoxRemovedNetwork(networkCombo);
		cyServiceRegistrar.registerAllServices(removeNetworkListener, new Properties());
	}
	
	private void initialiseDataRow () {
		dataTextField.addMouseListener(new DataMouseListener(this));
	}
	
	private void initialisePreprocessButtonRow () {
		preprocessButton.addActionListener(new PreprocessButtonActionListener(this));
	}
	
	private void intialiseFormalismRow () {		
		// Fill combo box
		formalismModel = new DefaultComboBoxModel();
		
		for (FormalismEnum formalism : FormalismEnum.values())
			formalismModel.addElement(formalism.getName());
		
		formalismCombo.setModel(formalismModel);
		formalismCombo.setSelectedItem(FormalismEnum.BOOLEAN.name());
		
		// Add Listener
		formalismCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTimePointComboBoxStatus();
			}
		});
	}
	
	private void initialiseTimePointsRows () {
		DefaultComboBoxModel dataPointModel = new DefaultComboBoxModel();
		dataPointModel.addElement("--");
		dataPointCombo.setModel(dataPointModel);
		
		dataPointCombo.setEnabled(false);
	}
	
	private void initialiseOptimiseButtonRow () {
		optimiseButton.addActionListener(new OptimiseButtonActionListener(this));
	}
        
        private void initialiseSBMLExportButton () {
		SBMLExportButton.addActionListener(new SBMLExportButtonActionListener(this));
	}
        private void initialiseSBMLImportButton () {
		SBMLImportButton.addActionListener(new SBMLImportButtonActionListener(this,cyServiceRegistrar));
	}
        
	
	private void initialiseAlgorithmConfigurations () {}
	
	// Behaviour methods
	public void setTimePointComboBoxStatus () {
		if (dataPointCombo.getModel().getSize() > 1 && formalismCombo.getSelectedItem().toString().equals(FormalismEnum.BOOLEAN.getName())) {
			dataPointCombo.setEnabled(true);
		} else {
			dataPointCombo.setEnabled(false);
		}
	}
	
	// Get components values
	
	/**
	 * Get selected network from the JComboBox in the Cytocopter control panel
	 * 
	 * @return
	 */
	public String getNetworkValue () {
		String network = (String) networkCombo.getSelectedItem();
		return network;
	}
	
	/**
	 * Get selected timePoint from the JComboBox in the Cytocopter control panel
	 * 
	 * @return
	 */
	public String getTimePointValue () {
		String network = dataPointCombo.getSelectedItem().toString();
		return network;
	}
	
	/**
	 * Get selected formalism from the JComboBox in the Cytocopter control panel
	 * 
	 * @return
	 */
	public String getFormalismValue () {
		String network = (String) formalismCombo.getSelectedItem();
		return network;
	}
	
	/**
	 * Get number of time points existing in the selected MIDAS file
	 * 
	 * @return
	 */
	public int getNumberOfDataTimePoints() {
		return dataPointCombo.getModel().getSize();
	}
	
	/**  
	 * Get MIDAS selected file absolute path
	 * 
	 * @return
	 */
	public String getMidasFilePath () {
		return dataFile.getAbsolutePath();
	}
	
	/**
	 * Get Algorithm configuration property value defined in the control panel. If the value defined by the user is not a valid double
	 * the default value of the property is used instead and the text field value is reseted.
	 * 
	 * @param property
	 * @return
	 */
	public Double getAlgorithmPropertyValue (AlgorithmConfigurationsEnum property) {
		Double value;
		
		try {
			value = Double.valueOf(configurationsMap.get(property.getRArgName()).getText());
			
		} catch (Exception e) {
			value = property.getDefaultValue();
			configurationsMap.get(property.getRArgName()).setText(value.toString());
		}
		
		return value;
	}
	
	
	// Cytopanel methods
	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public String getTitle() {
		return "Cytocopter";
	}
	
}
