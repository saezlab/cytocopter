/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.cytocopter.internal.cellnoptr.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author francescoceccarelli
 */
public class SBMLExport {

   

    /**
     * @param args the command line arguments
     */
    public SBMLExport(String file, String pathToSave) throws FileNotFoundException, IOException, ParserConfigurationException, TransformerException {
        // TODO code application logic here
        
        ArrayList<Node> nodes = new ArrayList<>();
        // read sif file after it has been preprocessed
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
               // process the line.
               line = line.replaceAll("\\s+"," ");
               String words[] = line.split(" ");
               Node node = new Node(words[0], Integer.parseInt(words[1]), words[2]);
               nodes.add(node);
            }
        }
        writeSpecies(nodes, file, pathToSave);
        
    }
    
    
    public void writeSpecies (ArrayList<Node> nodes, String file, String pathToSave) throws ParserConfigurationException, TransformerConfigurationException, TransformerException{
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        // root elements 
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("sbml");
	doc.appendChild(rootElement);
        // set attributes for sbml 
        Attr attr1 = doc.createAttribute("xmlns");
	attr1.setValue("http://www.sbml.org/sbml/level3/version1/core");
        Attr attr2 = doc.createAttribute("level");
	attr2.setValue("3");
        Attr attr3 = doc.createAttribute("version");
	attr3.setValue("1");
        Attr attr4 = doc.createAttribute("xmlns:qual");
	attr4.setValue("http://www.sbml.org/sbml/level3/version1/qual/version1");
        Attr attr5 = doc.createAttribute("qual:required");
	attr5.setValue("true");
	rootElement.setAttributeNode(attr1);
        rootElement.setAttributeNode(attr2);
        rootElement.setAttributeNode(attr3);
        rootElement.setAttributeNode(attr4);
        rootElement.setAttributeNode(attr5);
        Element modelTag = doc.createElement("model");
        rootElement.appendChild(modelTag);
        Attr modelAttr = doc.createAttribute("id");
        
        //Use the file name to generate a model ID
        String OS = System.getProperty("os.name").toLowerCase();
        String name = "";
        if (OS.indexOf("win") >= 0){
            name = file.substring(file.lastIndexOf("\\")+1, file.length());
        }else name = file.substring(file.lastIndexOf("/")+1, file.length());
        name = name.replaceAll("[0-9]","");
        String modelID = name+".sbmlQual";
        modelAttr.setValue(modelID);
        modelTag.setAttributeNode(modelAttr);
        Element listOfComp = doc.createElement("listOfCompartments");
        modelTag.appendChild(listOfComp);
        Element comp = doc.createElement("compartment");
        listOfComp.appendChild(comp);
        Attr compID = doc.createAttribute("id");
        compID.setValue("default");
        Attr con = doc.createAttribute("constant");
        con.setValue("true");
        comp.setAttributeNode(compID);
        comp.setAttributeNode(con);
        Element listOfSpecies = doc.createElement("qual:listOfQualitativeSpecies");
        modelTag.appendChild(listOfSpecies);
        ArrayList<String> uniqueNodes = new ArrayList<>();
        for (int i = 0; i<nodes.size();i++){
            
                if (!uniqueNodes.contains(nodes.get(i).getInput()) && !nodes.get(i).getInput().startsWith("and")){
                    uniqueNodes.add(nodes.get(i).getInput());            
                if (!uniqueNodes.contains(nodes.get(i).getOutput()) && !nodes.get(i).getOutput().startsWith("and")){
                    uniqueNodes.add(nodes.get(i).getOutput()); 
                }
            }
        }
        for (int i = 0; i < uniqueNodes.size(); i++){
            
            Element qualSpecies = doc.createElement("qual:qualitativeSpecies");
            listOfSpecies.appendChild(qualSpecies);
            Attr qualID = doc.createAttribute("qual:id");
            qualID.setValue(uniqueNodes.get(i));
            Attr qualComp = doc.createAttribute("qual:compartment");
            qualComp.setValue("main");
            Attr qualConstant = doc.createAttribute("qual:constant");
            qualConstant.setValue("false");
            qualSpecies.setAttributeNode(qualID);
            qualSpecies.setAttributeNode(qualComp);
            qualSpecies.setAttributeNode(qualConstant);
        
        }
        ArrayList<Transition> allTransitions = findTransitions(nodes);
        Element lisOfTransiton = doc.createElement("qual:listOfTransitions");
        modelTag.appendChild(lisOfTransiton);
        for (int i=0;i<allTransitions.size();i++){
            Element trans = doc.createElement("qual:transition");
            lisOfTransiton.appendChild(trans);
            Attr transID = doc.createAttribute("qual:id");
            transID.setValue(allTransitions.get(i).getTransitionID());
            trans.setAttributeNode(transID);
            Element listOfInputs = doc.createElement("qual:listOfInputs");
            trans.appendChild(listOfInputs);
            
            for (int j = 0; j < allTransitions.get(i).getInputs().size();j++){
                
                Element in = doc.createElement("qual:input");
                listOfInputs.appendChild(in);
                Attr qualID = doc.createAttribute("qual:id");
                String thetaValue = "theta_" + allTransitions.get(i).getTransitionID() + "_"+allTransitions.get(i).getInputs().get(j);
                qualID.setValue(thetaValue);
                Attr qualSpecies = doc.createAttribute("qual:qualitativeSpecies");
                qualSpecies.setValue(allTransitions.get(i).getInputs().get(j));
                Attr qualEffect = doc.createAttribute("qual:transitionEffect");
                qualEffect.setValue("none");
                Attr sign = doc.createAttribute("qual:sign");
                sign.setValue(allTransitions.get(i).getInteractions().get(j) == 1 ? "positive" : "negative");
                Attr thresholdLevel = doc.createAttribute("qual:thresholdLevel");
                thresholdLevel.setValue("1");
                in.setAttributeNode(qualID);
                in.setAttributeNode(qualSpecies);
                in.setAttributeNode(qualEffect);
                in.setAttributeNode(sign);
                in.setAttributeNode(thresholdLevel);
                
            }
            
            Element listOfOutputs = doc.createElement("qual:listOfOutputs");
            trans.appendChild(listOfOutputs);
            Element out = doc.createElement("qual:output");
            listOfOutputs.appendChild(out);
            Attr qualSpecies = doc.createAttribute("qual:qualitativeSpecies");
            qualSpecies.setValue(allTransitions.get(i).getOutput());
            Attr qualEffect = doc.createAttribute("qual:transitionEffect");
            qualEffect.setValue("assignmentLevel");
            out.setAttributeNode(qualSpecies);
            out.setAttributeNode(qualEffect);
            
            Element listOfTerms = doc.createElement("qual:listOfFunctionTerms");
            trans.appendChild(listOfTerms);
            Element defaultTerm = doc.createElement("qual:defaultTerm");
            listOfTerms.appendChild(defaultTerm);
            Element funtionTerm = doc.createElement("qual:functionTerm");
            listOfTerms.appendChild(funtionTerm);
            Attr defLevel = doc.createAttribute("qual:resultLevel");
            defLevel.setValue("0");
            defaultTerm.setAttributeNode(defLevel);
            Attr funLevel = doc.createAttribute("qual:resultLevel");
            funLevel.setValue("1");
            funtionTerm.setAttributeNode(funLevel);
            
            Element math = doc.createElement("math");
            funtionTerm.appendChild(math);
            Attr xmlns = doc.createAttribute("xmlns");
            xmlns.setValue("http://www.w3.org/1998/Math/MathML");
            math.setAttributeNode(xmlns);
            
            Element apply = doc.createElement("apply");
            math.appendChild(apply);
            
            
           
            
            if (allTransitions.get(i).getInputs().size() > 1){
                if (allTransitions.get(i).getANDTransition()==false){
                    Element ORLogic = doc.createElement("or");
                    apply.appendChild(ORLogic);
                }else {
                    Element ANDLogic = doc.createElement("and");
                    apply.appendChild(ANDLogic);
                }
            }
            
            for (int k = 0; k < allTransitions.get(i).getInputs().size(); k++){
                Element applyLogic = doc.createElement("apply");
                apply.appendChild(applyLogic);
                
                Element geq = doc.createElement("geq");
                applyLogic.appendChild(geq);
                
                Element ci = doc.createElement("ci");
                ci.appendChild(doc.createTextNode(allTransitions.get(i).getInputs().get(k)));
                applyLogic.appendChild(ci);
                
                String thetaValue = "theta_" + allTransitions.get(i).getTransitionID() + "_"+allTransitions.get(i).getInputs().get(k);
                Element ci2 = doc.createElement("ci");
                ci2.appendChild(doc.createTextNode(thetaValue));
                applyLogic.appendChild(ci2);
                
            }
            
        
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new File(pathToSave));
        transformer.transform(source, result);
    }
    
    public ArrayList<Transition> findTransitions(ArrayList<Node> nodes){
        
        ArrayList<Transition> allTransitions;
        allTransitions = new ArrayList<>();
        int count = 1;
        ArrayList<String> checkedTarget = new ArrayList<>();
        for (Node node : nodes){
            Transition transition = new Transition();
            transition.setTransitionID("t"+count);
            
            String target = node.getOutput();
            if (checkedTarget.contains(target)){
                continue;
            }
            count ++;
            ArrayList<String> inputs = new ArrayList<>();
            ArrayList<Integer> interactions = new ArrayList<>();
            for (Node n : nodes){
          
               if (n.getOutput().equals(target)){
                   inputs.add(n.getInput());
                   interactions.add(n.getInteraction());  
               } 
            }
            
            transition.setInputs(inputs);
            transition.setInteractions(interactions);
            transition.setOutput(target);
            allTransitions.add(transition);
            checkedTarget.add(target);
        }
        
        allTransitions = findANDTransitions(allTransitions);
        
        return allTransitions;
    }
    
    public ArrayList<Transition> findANDTransitions(ArrayList<Transition> transitions){
        
        ArrayList<Transition> toRemove = new ArrayList<Transition>();
        for (Transition tr : transitions){
            if (tr.getOutput().startsWith("and")){
               String andTransitions = tr.getOutput();
               for (Transition trans : transitions){
                   if (trans.getInputs().contains(andTransitions)){
                       tr.setOutput(trans.getOutput());
                       tr.setANDTransition();
                       if (trans.getInputs().size() == 1){
                           toRemove.add(trans);
                       }else {
                           trans.getInputs().remove(andTransitions);
                       }
              
                   }
               }
            }
        }
        //remove extra non necessary AND transitions 
        for (int i = 0;i<toRemove.size();i++){
            transitions.remove(toRemove.get(i));
        }
        
        return transitions;
        
    }

    
}
