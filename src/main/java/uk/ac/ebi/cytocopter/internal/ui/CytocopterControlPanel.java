package uk.ac.ebi.cytocopter.internal.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.FileChooserFilter;

import uk.ac.ebi.cytocopter.internal.utils.AlgorithmConfigurations;

@SuppressWarnings("serial")
public class CytocopterControlPanel extends JPanel implements CytoPanelComponent {

	public CyServiceRegistrar cyServiceRegistrar;
	
	public CytocopterControlPanel (CyServiceRegistrar cyServiceRegistrar) {
		this.cyServiceRegistrar = cyServiceRegistrar;
		
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]{70, 130, 130};
		
		setLayout(layout);
		setSize(new Dimension(400, 400));
		setPreferredSize(new Dimension(400, 400));
		
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		
		// Network panel
		c.gridy = 0;

		JLabel networkLabel = new JLabel("Network");
		c.gridx = 0;
		c.gridwidth = 1;
		add(networkLabel, c);
		
		DefaultComboBoxModel networkComboModel = new DefaultComboBoxModel();
		networkComboModel.addElement("Network1");
		
		JComboBox networkCombo = new JComboBox(networkComboModel);
		c.gridx = 1;
		c.gridwidth = 2;
		add(networkCombo, c);
		
		// Data panel
		c.gridy = 1;
		
		JLabel dataLabel = new JLabel("Data");
		c.gridx = 0;
		c.gridwidth = 1;
		add(dataLabel, c);
		
		JTextField dataTextField = new JTextField();
		c.gridx = 1;
		c.gridwidth = 2;
		add(dataTextField, c);
		
		dataTextField.addMouseListener(new DataMouseListener(dataTextField));
		
		// Formalism panel
		c.gridy = 2;
		
		JLabel formalismLabel = new JLabel("Formalism");
		c.gridx = 0;
		c.gridwidth = 1;
		add(formalismLabel, c);
		
		DefaultComboBoxModel formalismComboModel = new DefaultComboBoxModel();
		formalismComboModel.addElement("Boolean");
		
		JComboBox formalismCombo = new JComboBox(formalismComboModel);
		c.gridx = 1;
		c.gridwidth = 2;
		add(formalismCombo, c);

		// Run button
		c.gridy = 3;
		c.gridx = 2;
		c.gridwidth = 1;
		
		JButton runButton = new JButton("Run");
		add(runButton, c);
		
		// Algorithm panel
		GridBagLayout algorithmLayout = new GridBagLayout();
		algorithmLayout.columnWidths = new int[]{70, 95, 70, 95};
		
		JPanel algorithmPanel = new JPanel(algorithmLayout);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.ipadx = 5;
		constraints.ipady = 5;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		for (AlgorithmConfigurations conf : AlgorithmConfigurations.values()) {
			JLabel label = new JLabel(conf.getName());
			JTextField textField = new JTextField(conf.getDefaultValue().toString());
			
			algorithmPanel.add(label, constraints);
			
			constraints.gridx++;
			algorithmPanel.add(textField, constraints);
			
			if (constraints.gridx == 3) {
				constraints.gridx = 0;
				constraints.gridy++;
				
			} else {
				constraints.gridx++;
			}
		}
		
		c.gridy = 4;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weighty = 0.5;
		add(algorithmPanel, c);
		
		this.setVisible(true);
	}
	
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
	
	public class DataMouseListener implements MouseListener {
		
		private JTextField dataTextField;
		
		public DataMouseListener (JTextField dataTextField) {
			this.dataTextField = dataTextField;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new FileChooserFilter("MIDAS", "csv"));
			
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            dataTextField.setText(file.getName());
	        }
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}

}
