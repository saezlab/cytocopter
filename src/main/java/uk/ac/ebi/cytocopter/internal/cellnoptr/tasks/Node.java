/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

/**
 *
 * @author francescoceccarelli
 */
public class Node {
    
    private String input;
    private String output;
    private int interaction;
    
    public Node(String source,  int interaction, String target){
        this.input = source;
        this.output = target;
        this.interaction = interaction;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String source) {
        this.input = source;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String target) {
        this.output = target;
    }

    public int getInteraction() {
        return interaction;
    }

    public void setInteraction(int interaction) {
        this.interaction = interaction;
    }
    
}
