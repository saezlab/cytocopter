/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import java.util.ArrayList;

/**
 *
 * @author francescoceccarelli
 */
public class Transition {
    
    private ArrayList<String> inputs = new ArrayList<>();
    private String output;
    private ArrayList<Integer> interactions = new ArrayList<>();
    private String transitionID;
    private boolean ANDTransition = false; 

    public ArrayList<String> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<String> inputs) {
        this.inputs = inputs;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public ArrayList<Integer> getInteractions() {
        return interactions;
    }

    public void setInteractions(ArrayList<Integer> interactions) {
        this.interactions = interactions;
    }

    public String getTransitionID() {
        return transitionID;
    }

    public void setTransitionID(String transitionID) {
        this.transitionID = transitionID;
    }
    
    public void setANDTransition () {
        this.ANDTransition = true; 
    }
    
    public boolean getANDTransition(){
        return ANDTransition;
    }
    
}
