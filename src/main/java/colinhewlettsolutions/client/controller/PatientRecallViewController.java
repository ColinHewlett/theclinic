/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author colin
 */
public class PatientRecallViewController extends ViewController{
    
    public enum Actions{

    }

    public enum Properties{
        
    }
    
    public PatientRecallViewController(
            DesktopViewController controller,
            DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
}
