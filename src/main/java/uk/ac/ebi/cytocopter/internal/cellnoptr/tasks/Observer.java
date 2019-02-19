/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import javax.swing.JOptionPane;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;

/**
 *
 * @author francescoceccarelli
 */
public class Observer implements TaskObserver{
    
    private boolean taskComplete = false;


    @Override
    public void taskFinished(ObservableTask task) {
        

    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        // TODO Auto-generated method stub
        //JOptionPane.showMessageDialog(null, "HERE");
        if (!FlagClass.getStringInstance().equals("")){
            JOptionPane.showMessageDialog(null, "The following nodes may have inconsistent outgoing edge signs:\n "+ FlagClass.getStringInstance());
            FlagClass.resetStringInstance();
        }
        
    }
    public boolean isComplete() { return taskComplete; }
	 
    public void reset() { taskComplete = false; }
    
}
