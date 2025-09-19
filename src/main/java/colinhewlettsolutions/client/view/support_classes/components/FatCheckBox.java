/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.components;

import java.util.ArrayList;
import javax.swing.JCheckBox;
import org.w3c.dom.Element;
/**
 *
 * @author colin
 */
public class FatCheckBox extends JCheckBox{
    private ArrayList<Element> elements = null;
    
    public FatCheckBox(ArrayList<Element> elementsValue){
        elements = elementsValue;
    }
    
    private ArrayList<Element> getElements(){
        return elements;
    }
}
