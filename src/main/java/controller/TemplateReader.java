/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.exceptions.TemplateReaderException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JMenuItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author colin
 */
public class TemplateReader {
    private static File templateFile = null;
    private static String entityId = null;
    private static String entityTag = null;
    private static String sectionId = null;
    private static String sectionTag = null;
    private static Patient patient = null;
    private static Appointment appointment = null;
    
        public static void setTemplateFile(File file){
            templateFile = file;
        }
        
        public static String getEntityTag(){
            return entityTag;
        }
        public static void setEntityTag(String value){
            entityTag = value;
        }

        public static String getEntityId(){
            return entityId;
        }
        public static void setEntityId(String value){
            entityId = value;
        }

        public static String getSectionTag(){
            return sectionTag;
        }
        public static void setSectionTag(String value){
            sectionTag = value;
        }
        
        public static String getSectionId(){
            return sectionId;
        }
        public static void setSectionId(String value){
            sectionId = value;
        }
        
        public static File getTemplateFile(){
            return templateFile;
        }
        
        private Patient getPatient(){
            return patient;
        }
        
        private Appointment getAppointment(){
            return appointment;
        }
        
        /*
        public TemplateReader(File templateFile, 
                String entityId, String sectionId){
            this.entityId = entityId;
            this.sectionId = sectionId; 
            this.templateFile = templateFile;
        }
        */

        /*
        public TemplateReader(Patient patient,String sectionId) {
            
        }
        
        public TemplateReader(Appointment appointment, String sectionId){

        }
        */

        private static Element getTemplate()throws TemplateReaderException{
            Element result = null;
            try{
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(getTemplateFile());
                doc.getDocumentElement().normalize();  
                
                return doc.getDocumentElement();
            }
            catch(ParserConfigurationException ex){
                String message = ex.getMessage() + "\n"
                        + "ParserConfigurationException raised in getTemplate() method";
                throw new TemplateReaderException(message, 
                        TemplateReaderException
                                .ExceptionType.PARSER_CONFIGURATION_ERROR);
            }
            catch(SAXException ex){
                String message = ex.getMessage() + "\n"
                        + "Raised in MenuMaker.getTemplate() method";
                throw new TemplateReaderException(message,
                        TemplateReaderException.ExceptionType.SAX_EXCEPTION);
            }
            catch(IOException ex){
                String message = ex.getMessage() + "\n"
                        + "Raised in MenuMaker.getTemplate() method";
                throw new TemplateReaderException(message,
                        TemplateReaderException.ExceptionType.IO_EXCEPTION);
            }
        }
        
        private static Element getSelectedRootFromTemplate()throws TemplateReaderException{
            Element result = null;
            Element element = null;
            NodeList nodes = null;
            Node node = null;
            boolean isElementFound = false;
            
            Element template = getTemplate();
            //node = template.getFirstChild();
            
            nodes = template.getElementsByTagName(getEntityTag());
            
            if (nodes.getLength() == 0){
                String message = "Template element tagged 'entity' not found in '"
                        + getTemplateFile() + "'\n"
                        + "Raised in getSelectedRootFromTemplate() method";
                throw new TemplateReaderException(message,
                        TemplateReaderException.ExceptionType.ELEMENT_NOT_FOUND_IN_TEMPLATE);
            }
            
            for (int temp = 0; temp < nodes.getLength(); temp++) {
                node = nodes.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    element = (Element)node;
                    if (element.getAttribute("id").equals(getEntityId())){
                        isElementFound = true;
                        break;
                    }
                }
            }
            if (!isElementFound){
                String message = "Template entity element with id '" + getEntityId() + "' not found in '"
                        + getTemplateFile() + "'\n"
                        + "Raised in getSelectedRootFromTemplate() method";
                throw new TemplateReaderException(message,
                        TemplateReaderException.ExceptionType.ELEMENT_NOT_FOUND_IN_TEMPLATE);
            }
            return element;
        }
        
        public static ArrayList<String> extract(Patient.MedicalHistory mh){
            setEntityTag("entity");
            setEntityId("Patient");
            ArrayList<String> result = new ArrayList<>();
            Element element = null;
            
            try{
                element = getSelectedRootFromTemplate();
                NodeList nodes = element.getElementsByTagName("section");
                for(int index = 0; index < nodes.getLength(); index++){
                    if((nodes.item(index).getNodeType() == Node.ELEMENT_NODE)){
                        element = (Element)nodes.item(index);
                        String item = element.getAttribute("id");
                        result.add(item);
                    }
                }
            }catch (TemplateReaderException ex){
                
            }
            return result;
        }
        
        public static HashMap extract(HashMap<String,String> map)throws TemplateReaderException{
            //Map<String,String> myMap = new HashMap<>();
            Element eSection;
            Element ePrimary;
            
            Element element = getSelectedRootFromTemplate();
            NodeList sNodes = element.getElementsByTagName("section");
            for(int sIndex = 0; sIndex < sNodes.getLength(); sIndex++){
                if((sNodes.item(sIndex).getNodeType() == Node.ELEMENT_NODE)){
                    eSection = (Element)sNodes.item(sIndex);
                    NodeList pNodes = eSection.getElementsByTagName("primary");
                    for(int pIndex = 0; pIndex < pNodes.getLength(); pIndex++){
                        if((pNodes.item(pIndex).getNodeType() == Node.ELEMENT_NODE)){
                            ePrimary = (Element)pNodes.item(pIndex);
                            map.put(eSection.getAttribute("id"), 
                                      ePrimary.getAttribute("id"));
                        }
                    }
                }
            }
            return map;
        }
        
        /**
         * 
         * @param thePrimaryCondition 
         * @return PrimaryCondition; the extracted 'thePrimaryConditiom' PrimaryCondition object
         * -- 'thePrimaryCondition'.get() returns the collection of all the extracted PrimaryCondition objects
         * -- each extracted PrimaryCondition.getSecondaryCondition.get() returns all the SecondaryCondition objects for that PrimaryCondition
         * @throws TemplateReaderException 
         */
        public static PrimaryCondition extract(PrimaryCondition thePrimaryCondition)throws TemplateReaderException{
            PrimaryCondition pc = null;
            SecondaryCondition sc;
            boolean isElementFound = false;
            Element eSection = null;
            Element pElement;
            Element sElement;

            Element element = getSelectedRootFromTemplate();
            NodeList pNodes = element.getElementsByTagName("section");
            if (pNodes.getLength() == 0){
                String message = "Template element tagged 'section' not found in "
                        + "TemplateReader::extract(PrimaryCondition) method";
                throw new TemplateReaderException(message,
                        TemplateReaderException.ExceptionType.ELEMENT_NOT_FOUND_IN_TEMPLATE);
            }
            
            for(int pIndex = 0; pIndex < pNodes.getLength(); pIndex++){
                if((pNodes.item(pIndex).getNodeType() == Node.ELEMENT_NODE)){
                    eSection = (Element)pNodes.item(pIndex);
                    if (eSection.getAttribute("id").equals(getSectionId())){
                        isElementFound = true;
                        break;
                    }
                }
            }
            if (!isElementFound){
                String message = "Unable to locate sectionId = '" + getSectionId() 
                        + "' in TemplateReader::extract(PrimaryCondition)";
                throw new TemplateReaderException(message, 
                        TemplateReaderException.ExceptionType.ELEMENT_NOT_FOUND_IN_TEMPLATE);
            }
            
            pNodes = eSection.getElementsByTagName("primary");
            if (pNodes.getLength() == 0){
                String message = "Template element tagged 'primary' not found in "
                        +  "TemplateReader::extract(PrimaryCondition) method";
                throw new TemplateReaderException(message,
                        TemplateReaderException.ExceptionType.ELEMENT_NOT_FOUND_IN_TEMPLATE);
            }
            for(int pIndex = 0; pIndex < pNodes.getLength(); pIndex++){
                if((pNodes.item(pIndex).getNodeType() == Node.ELEMENT_NODE)){
                    pElement = (Element)pNodes.item(pIndex);
                    pc = new PrimaryCondition();
                    pc.setDescription(pElement.getAttribute("id"));
                    SecondaryCondition theSecondaryCondition = 
                            new SecondaryCondition(pc);
                    NodeList sNodes = pElement.getElementsByTagName("secondary");
                    for(int sIndex = 0; sIndex < sNodes.getLength(); sIndex++){
                        if((sNodes.item(sIndex).getNodeType() == Node.ELEMENT_NODE)){
                            sElement = (Element)sNodes.item(sIndex);
                            sc = new SecondaryCondition();
                            sc.setDescription(sElement.getAttribute("id"));
                            //adds each secondary object extracted to the collection
                            //of secondary conditions for a given primary condition 
                            theSecondaryCondition.get().add(sc);
                        }
                    }
                    // links secondary condition objectn collection to parent primary condition object
                    pc.setSecondaryCondition(theSecondaryCondition);
                }
                //adds this primary condition to the collection of extracted primary condition objects
                thePrimaryCondition.get().add(pc);
            }
            return thePrimaryCondition;
        }
    }
