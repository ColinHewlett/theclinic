/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller;

import theclinic.view.views.non_modal_views.DesktopView;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author colin
 */
public class ImageViewerViewController extends ViewController{
    
    public enum Actions{
        VIEW_CLOSE_NOTIFICATION
    }
    
    
    public ImageViewerViewController(DesktopViewController controller, Descriptor descriptor, DesktopView desktopView){
        setMyController(controller);
        setDescriptor(descriptor);
        setDesktopView(desktopView);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Actions action = Actions.valueOf(e.getActionCommand());
        switch(action){
            case VIEW_CLOSE_NOTIFICATION ->{
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.Actions.VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
}
