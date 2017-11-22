/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.cytocopter.internal.ui.listeners;

import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.SBMLFileString;
import uk.ac.ebi.cytocopter.internal.ui.panels.ControlPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.SBMLExport;

/**
 *
 * @author francescoceccarelli
 */
public class SBMLExportButtonActionListener implements ActionListener{
    
    private ControlPanel controlPanel;
    JFileChooser fc;


	public SBMLExportButtonActionListener(ControlPanel controlPanel)
	{
		this.controlPanel = controlPanel;
	}

	public void actionPerformed(ActionEvent e)
	{
                String filename = SBMLFileString.getInstance();
                fc = new JFileChooser();
                FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
                // add filters
                fc.addChoosableFileFilter(xmlFilter);
                fc.setFileFilter(xmlFilter);
                int returnVal = fc.showSaveDialog(controlPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
               
                String pathTosave = file.toString();
                if (!pathTosave .endsWith(".xml"))
                     pathTosave += ".xml";
                    try {
                        SBMLExport ex = new SBMLExport(filename, pathTosave);
                    } catch (IOException ex1) {
                        Logger.getLogger(SBMLExportButtonActionListener.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (ParserConfigurationException ex1) {
                        Logger.getLogger(SBMLExportButtonActionListener.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (TransformerException ex1) {
                        Logger.getLogger(SBMLExportButtonActionListener.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
        }
}
