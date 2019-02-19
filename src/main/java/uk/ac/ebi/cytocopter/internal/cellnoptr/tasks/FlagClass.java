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
public class FlagClass {
    
    private static String warning;
    
    private FlagClass(){}
    
    public static String getStringInstance() {
       if(warning == null){
            warning = new String("");
        }
        return warning;
    }
    public static void setStringInstance(String file){
        warning = warning + file;
    }
    public static void resetStringInstance(){
        warning = "";
    }
    
    

}
