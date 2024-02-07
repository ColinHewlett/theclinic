/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import view.views.non_modal_views.DesktopView;
import view.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author colin
 */
public class ImportProgressViewController extends ViewController{
    public static enum Entity{APPOINTMENT, PATIENT, PATIENT_NOTE, NONE}
    private static enum Operation {EXPORT, IMPORT};
    private Entity newEntity = Entity.NONE;
    private Entity oldEntity = Entity.NONE;
    private PropertyChangeSupport pcSupportForView = null;
    private PropertyChangeEvent pcEvent = null;
    private Operation operation = Operation.IMPORT;
    
    @Override
    public void propertyChange(PropertyChangeEvent ex){
        
    }
    
    private Operation getOperation(){ 
        return operation;
    }
    
    private void setOperation(Operation value){
        operation = value;
    }
    
    private Entity getNewEntity(){ 
        return this.newEntity;
    }
    
    private void setNewEntity(Entity entity){
        newEntity = entity;
    }
    
    private Entity getOldEntity(){ 
        return oldEntity;
    }
    
    private void setOldEntity(Entity entity){
        oldEntity = entity;
    }
    
    public ImportProgressViewController(ActionListener controller, DesktopView desktopView, Descriptor entityDescriptor){
        View.setViewer(View.Viewer.EXPORT_PROGRESS_VIEW);
        this.setMyController(controller);
        setDesktopView(desktopView);
    }
    
    private void doDesktopViewControllerAction(ActionEvent e){
        ActionEvent actionEvent = null;
        ViewController.DesktopViewControllerActionEvent actionCommand =
               ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        
        switch (actionCommand){
            case IMPORT_EXPORT_PATIENT_DATA_COMPLETED:
                setOldEntity(getNewEntity());
                setNewEntity(Entity.APPOINTMENT);
                firePropertyChangeEvent(
                        ImportProgressViewControllerPropertyChangeEvent.
                                    PREPARE_FOR_RECEIPT_OF_APPOINTMENT_PROGRESS.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                break;
                
            case IMPORT_EXPORT_APPOINTMENT_DATA_COMPLETED:
                setOldEntity(getNewEntity());
                setNewEntity(Entity.PATIENT_NOTE);
                firePropertyChangeEvent(
                        ImportProgressViewControllerPropertyChangeEvent.
                                    PREPARE_FOR_RECEIPT_OF_PATIENT_NOTE_PROGRESS.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                break;
                /*
                setOldEntity(getNewEntity());
                setNewEntity(Entity.NONE);
                firePropertyChangeEvent(
                        ImportProgressViewControllerPropertyChangeEvent.
                                    OPERATION_COMPLETED.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
*/
            case IMPORT_EXPORT_PATIENT_NOTE_DATA_COMPLETED:
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.IMPORT_EXPORT_MIGRATED_SURGERY_DAYS_ASSIGNMENT.toString());
                getMyController().actionPerformed(actionEvent);
                setOldEntity(getNewEntity());
                setNewEntity(Entity.NONE);
                firePropertyChangeEvent(
                        ImportProgressViewControllerPropertyChangeEvent.
                                    OPERATION_COMPLETED.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ActionEvent actionEvent = null;
        
        if (e.getSource() instanceof DesktopViewController){
            doDesktopViewControllerAction(e);
        }else{
            ViewController.ImportProgressViewControllerActionEvent actionCommand =
               ViewController.ImportProgressViewControllerActionEvent.valueOf(e.getActionCommand());
            
            switch (actionCommand){
                case IMPORT_EXPORT_START_REQUEST:
                    setOldEntity(getNewEntity());
                    setNewEntity(Entity.PATIENT);
                    firePropertyChangeEvent(
                            ImportProgressViewControllerPropertyChangeEvent.
                                        PREPARE_FOR_RECEIPT_OF_PATIENT_PROGRESS.toString(),
                            getView(),
                            this,
                            null,
                            null
                    );
                    break;

                case READY_FOR_RECEIPT_OF_PATIENT_PROGRESS:
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.IMPORT_EXPORT_PATIENT_DATA.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;

                case READY_FOR_RECEIPT_OF_APPOINTMENT_PROGRESS:                 
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.IMPORT_EXPORT_APPOINTMENT_DATA.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
                    
                case READY_FOR_RECEIPT_OF_PATIENT_NOTE_PROGRESS:                 
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.IMPORT_EXPORT_PATIENT_NOTE_DATA.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;

                case IMPORT_EXPORT_PROGRESS_CLOSE_NOTIFICATION:
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.VIEW_CLOSED_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
            }
        }    
    }
}
