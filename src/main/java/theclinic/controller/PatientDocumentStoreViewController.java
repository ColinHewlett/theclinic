/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller;

import theclinic.model.repository.StoreException;
import theclinic.view.views.non_modal_views.DesktopView;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author colin
 */
public class PatientDocumentStoreViewController extends ViewController {
    
    public enum Actions{
        DOCUMENT_DELETE_REQUEST,
        DOCUMENT_OPEN_REQUEST,
        IMAGE_VIEWER_REQUEST,
        VIEW_CLOSE_NOTIFICATION
    }
    
    public PatientDocumentStoreViewController(DesktopViewController controller, Descriptor descriptor, DesktopView desktopView){
        setMyController(controller);
        setDescriptor(descriptor);
        setDesktopView(desktopView);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Actions actionCommand = Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case DOCUMENT_DELETE_REQUEST ->{
                break;
            }
            case DOCUMENT_OPEN_REQUEST ->{
                break;
            }
            case IMAGE_VIEWER_REQUEST ->{
                doImageViewerViewControllerRequest();
                break;
            } 
            case VIEW_CLOSE_NOTIFICATION ->{
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.Actions.VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
    }
    
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
    private void doImageViewerViewControllerRequest(){
        ArrayList<File> patientDocument = 
                (ArrayList<File>)getDescriptor().getViewDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_DOCUMENT);
        getDescriptor().getControllerDescription().
                setProperty(SystemDefinition.Properties.PATIENT_DOCUMENT, patientDocument);
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                DesktopViewController.Actions.IMAGE_VIEWER_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
}
