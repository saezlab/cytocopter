/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.cytocopter.internal.ui.listeners;

/**
 *
 * @author francescoceccarelli
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.FileDialog;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.SBMLFileString;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.xml.sax.SAXException;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.SBMLExport;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.SBMLImport;

/**
 *
 * @author francescoceccarelli
 */
public class SBMLImportButtonActionListener implements ActionListener{
    
    private ControlPanel controlPanel;
    JFileChooser fc;
    private CyServiceRegistrar cyServiceRegistrar;


	public SBMLImportButtonActionListener(ControlPanel controlPanel, CyServiceRegistrar cyServiceRegistrar)
	{
		this.controlPanel = controlPanel;
                this.cyServiceRegistrar = cyServiceRegistrar;
	}

	public void actionPerformed(ActionEvent e)
	{
                String path="";
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
                fileChooser.setFileFilter(xmlFilter);
                int result = fileChooser.showOpenDialog(controlPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    
                    File selectedFile = fileChooser.getSelectedFile();
                    path = selectedFile.getAbsolutePath();
                
                }
                try {
                    SBMLImport importer = new SBMLImport(path, cyServiceRegistrar);
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(SBMLImportButtonActionListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SAXException ex) {
                    Logger.getLogger(SBMLImportButtonActionListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SBMLImportButtonActionListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
               
                
        }
}
