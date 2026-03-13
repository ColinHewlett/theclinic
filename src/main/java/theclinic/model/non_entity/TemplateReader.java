/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.non_entity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
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

    /**
     * validates specified XML document against its specified XSD stylesheet
     * @param xsd
     * @param xml
     * @return boolean; true if validated
     */
    public boolean validateXMLSchema(File xsd, File xml) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch (Exception e) {
            System.out.println("Validation error: " + e.getMessage());
            return false;
        }
    }
    
    private File templateFile = null;
    public void setTemplateFile(File file){
        templateFile = file;
    }
    public File getTemplateFile(){
        return templateFile;
    }
    
    private String sectionId = null;
    public String getSectionId(){
        return sectionId;
    }
    public void setSectionId(String value){
        sectionId = value;
    }

    private String projectId = null;
    public String getProjectId(){
        return projectId;
    }
    public void setProjectId(String value){
        projectId = value;
    }

    public Element getRootElement()throws TemplateReaderException{
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
    
    /**
     * Locate <project> element with specified id attribute
     * @param eSelectedSection; the specified parent <section> 
     * @return specified <project>
     * @throws _TemplateReaderException; 
     * -- if <project> to locate's attribute is null, 
     * -- or if a <project> with this attribute cannot be found
     */
    private Element getSelectedProjectFrom(Element eSelectedSection, String projectId) throws TemplateReaderException{
        Element result = null;
        Element projectElement;
        NodeList projectNodes;
        Node node;
        
        projectNodes = eSelectedSection.getElementsByTagName("project");
        for (int index = 0; index < projectNodes.getLength(); index++) {
            node = projectNodes.item(index);
            projectElement = (Element)node;
            if (projectElement.getAttribute("id").equals(projectId)){
                result = projectElement;
                break;
            }
        }
        if ((!projectId.equals("UNIVERSAL")) && 
                (result==null)){
            String message = "<project> element not found with an id attribute = '" + projectId + "'\n"
                    + "Raised in getSelectedProjectFrom(<section id = '" + eSelectedSection.getAttribute("id") + "') method";
            throw new TemplateReaderException(message,
                    TemplateReaderException.ExceptionType.ELEMENT_NOT_FOUND_IN_TEMPLATE);
        }
        return result;
    }

    /**
     * Locates <section> whose attribute has been specified
     * @return <section> with specified attribute
     * @throws _TemplateReaderException; 
     * -- if <section> id attribute has not been defined
     */
    private Element getSelectedSectionFromDocument()throws TemplateReaderException{
        Element result = null;
        Element sectionsElement, sectionElement;
        NodeList nodes;
        Node node;

        sectionsElement = getRootElement();
        if (getSectionId()!=null){
            nodes = sectionsElement.getElementsByTagName("section");
            for (int index = 0; index < nodes.getLength(); index++) {
                sectionElement = (Element)nodes.item(index);
                if (sectionElement.getAttribute("id").equals(getSectionId()))
                    result = sectionElement;
            }
        }else{
            String message = "<section> element with id attribute = '" + getSectionId() + "' not found in '"
                    + getTemplateFile() + "'\n"
                    + "Raised in getSectionElementsFromDocument() method";
            throw new TemplateReaderException(message,
                    TemplateReaderException.ExceptionType.ELEMENT_NOT_FOUND_IN_TEMPLATE);
        }
        if (result == null){
            String message = "<section> element with id attribute = '" + getSectionId() + "' not found in '"
                    + getTemplateFile() + "'\n"
                    + "Raised in getSectionElementsFromDocument() method";
            throw new TemplateReaderException(message,
                    TemplateReaderException.ExceptionType.ELEMENT_NOT_FOUND_IN_TEMPLATE);
        }else{
            return result;
        }
    }
    
    /**
     * fetches key-value pairs contained within the specified <project>
     * @param eProject
     * @return HashMap<String,Object> containing specified key-value pairs
     */
    private HashMap<String,Object> getMapFrom(Element eProject){
        HashMap<String,Object> map = new HashMap<>();
        HashMap<String,Object> result = map;
        NodeList keyNodes;
        Node keyNode;
        Element keyElement;
        String keyValue;
        Object objectValue;
        
        keyNodes = eProject.getElementsByTagName("key");
        if (keyNodes.getLength()>0){/*could be an empty <project> element*/
            for (int index = 0; index < keyNodes.getLength(); index++) {
                keyNode = keyNodes.item(index);
                keyElement = (Element)keyNode;
                keyValue = keyElement.getAttribute("id");
                objectValue = getObjectValueFor(keyElement);
                map.put(keyValue,objectValue);
            } 
        }
        return map;
    }
    
    /**
     * fetches <value> for which the specified <key> is the parent, 
     * -- and then extracts the specified data type (Object) from the <value>
     * @param keyElement
     * @return Object the data type of which is specified by the element within <value>
     * -- note a validated XML document ensures via its XSD that the returned Object will be non-null
     */
    private Object getObjectValueFor(Element keyElement){
        NodeList nodes;
        Element blueElement, greenElement, heightElement, 
                redElement, sizeElement, styleElement, titleElement, widthElement, 
                xElement, yElement, valueElement, valueElementChild;
        Object result = null;
        
        valueElement = getFirstElementChildFor(keyElement);
        valueElementChild = getFirstElementChildFor(valueElement);
        switch (valueElementChild.getNodeName()) {
            case "color" -> {
                nodes = valueElementChild.getElementsByTagName("red");
                redElement = (Element) nodes.item(0);
                nodes = valueElementChild.getElementsByTagName("green");
                greenElement = (Element) nodes.item(0);
                nodes = valueElementChild.getElementsByTagName("blue");
                blueElement = (Element) nodes.item(0);
                result = new Color(
                        Integer.parseInt(redElement.getTextContent().trim()),
                        Integer.parseInt(greenElement.getTextContent().trim()),
                        Integer.parseInt(blueElement.getTextContent().trim())
                );
            }
            case "boolean" -> {
                String value;
                if (valueElementChild.getFirstChild() != null) {
                    value = valueElementChild.getFirstChild().getNodeValue();
                    boolean booleanContent = Boolean.parseBoolean(value); // Converts "true"/"false" but not "1"/"0"
                    if (!booleanContent && value.equals("1")) booleanContent = true; // Manually check "1"
                    result = booleanContent; 
                }    
            } 
            case "database_type" -> {
                if (valueElementChild.getFirstChild() != null) {
                    result = valueElementChild.getFirstChild().getNodeValue();
                }
            }
            case "dimension" -> {
                nodes = valueElementChild.getElementsByTagName("width");
                widthElement = (Element) nodes.item(0);
                nodes = valueElementChild.getElementsByTagName("height");
                heightElement = (Element) nodes.item(0);
                result = new Dimension(
                        Integer.parseInt(widthElement.getTextContent().trim()),
                        Integer.parseInt(heightElement.getTextContent().trim())
                );
            }
            case "font" ->{
                nodes = valueElementChild.getElementsByTagName("title");
                titleElement = (Element) nodes.item(0);
                nodes = valueElementChild.getElementsByTagName("style");
                styleElement = (Element) nodes.item(0);
                nodes = valueElementChild.getElementsByTagName("size");
                sizeElement = (Element) nodes.item(0);
                result = new Font(
                        titleElement.getTextContent().trim(),
                        Integer.parseInt(styleElement.getTextContent()),
                        Integer.parseInt(sizeElement.getTextContent())
                );
            } 
            case "integer" -> 
                result = Integer.valueOf(valueElementChild.getTextContent().trim());

            case "point" -> {
                nodes = valueElementChild.getElementsByTagName("x");
                xElement = (Element) nodes.item(0);
                nodes = valueElementChild.getElementsByTagName("y");
                yElement = (Element) nodes.item(0);
                result = new Point(
                        Integer.parseInt(xElement.getTextContent().trim()),
                        Integer.parseInt(yElement.getTextContent().trim())
                );
            }
            case "string" -> {
                result = valueElementChild.getTextContent();
            }
            case "string_array" -> {
                nodes = valueElementChild.getElementsByTagName("string");
                ArrayList<String> strings = new ArrayList<>();
                for (int index = 0; index < nodes.getLength(); index++) {
                    Element stringElement = (Element) nodes.item(index);
                    strings.add(stringElement.getTextContent());
                }
                result = strings;
            } 
        }
        return result;
    }

    /**
     * fetches the first element located within the specified parent element
     * @param parent; containing Element
     * @return 
     */
    private Element getFirstElementChildFor(Element parent) {
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) child; // Found the first element node
            }
            child = child.getNextSibling();
        }
        return null; // No element child found
    }
    
    /**
     * Extracts the HashMap of key-value pairs associated with a specified <project>
     * @param map
     * @return JashMap<String,Object>
     * @throws TemplateReaderException 
     * -- if a specified <section> or <project> cannot be found; or if one or other element with the specified attribute has not been found 
     */
    public HashMap extract(HashMap<String,Object> map)throws TemplateReaderException{
        HashMap<String,Object> result = null;
        Element eSelectedSection = null;
        Element eSelectedProject = null;
        
        eSelectedSection = getSelectedSectionFromDocument();
        if (getProjectId() != null){
            eSelectedProject = getSelectedProjectFrom(eSelectedSection, getProjectId());
            result = getMapFrom(eSelectedProject);
            //eSelectedProject = getSelectedProjectFrom(eSelectedSection,"UNIVERSAL");
            //result.putAll(getMapFrom(eSelectedProject));
        }else{
            /*a project id has not been defined!*/
        String message = "<project> element id attribute has not been defined\n"
                + "Raised in extract() method";
        throw new TemplateReaderException(message,
                TemplateReaderException.ExceptionType.ELEMENT_NOT_FOUND_IN_TEMPLATE);
        }
        return result;
    }
}
