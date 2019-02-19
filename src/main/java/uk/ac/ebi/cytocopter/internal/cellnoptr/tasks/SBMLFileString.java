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
public class SBMLFileString {
    
    private static String fileName;
    
    private SBMLFileString(){}
    

    public static String getInstance() {
       if(fileName == null){
            fileName = new String();
        }
        return fileName;
    }
    
    public static void setInstance(String file){
        fileName = file;
    }
    
    
}
