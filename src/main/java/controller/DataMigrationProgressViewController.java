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
public class DataMigrationProgressViewController extends ViewController{
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
    
    public DataMigrationProgressViewController(ActionListener controller, DesktopView desktopView, Descriptor entityDescriptor){
        View.setViewer(View.Viewer.EXPORT_PROGRESS_VIEW);
        this.setMyController(controller);
        setDesktopView(desktopView);
    }
    
    private void doDesktopViewControllerAction(ActionEvent e){
        ActionEvent actionEvent = null;
        ViewController.DesktopViewControllerActionEvent actionCommand =
               ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        
        switch (actionCommand){
            case MIGRATE_PATIENT_DATA_COMPLETED:
                firePropertyChangeEvent(
                        
                        DataMigrationViewControllerPropertyChangeEvent.
                                    PREPARE_FOR_RECEIPT_OF_APPOINTMENT_MIGRATION_PROGRESS.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                break;
                
            case MIGRATE_APPOINTMENT_DATA_COMPLETED:
                firePropertyChangeEvent(
                        DataMigrationViewControllerPropertyChangeEvent.
                                    PREPARE_FOR_RECEIPT_OF_PRIMARY_CONDITION_MIGRATION_PROGRESS.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                break;
  
            case MIGRATE_PATIENT_NOTE_DATA_COMPLETED:
                /*
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.MIGRATE_SURGERY_DAYS_ASSIGNMENT_DATA.toString());
                getMyController().actionPerformed(actionEvent);
                */
                //setOldEntity(getNewEntity());
                //setNewEntity(Entity.NONE);
                firePropertyChangeEvent(
                        DataMigrationViewControllerPropertyChangeEvent
                                .PREPARE_FOR_RECEIPT_OF_PRIMARY_CONDITION_MIGRATION_PROGRESS
                                .toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                break;
                
            case MIGRATE_PRIMARY_CONDITION_DATA_COMPLETED:
                firePropertyChangeEvent(
                        DataMigrationViewControllerPropertyChangeEvent.
                                    PREPARE_FOR_RECEIPT_OF_SECONDARY_CONDITION_MIGRATION_PROGRESS.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                break;
                
            case MIGRATE_SECONDARY_CONDITION_DATA_COMPLETED:
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.MIGRATE_SURGERY_DAYS_ASSIGNMENT_DATA.toString());
                getMyController().actionPerformed(actionEvent);
                firePropertyChangeEvent(
                        DataMigrationViewControllerPropertyChangeEvent
                                .DATA_MIGRATION_COMPLETED.toString(),
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
            ViewController.DataMigrationViewControllerActionEvent actionCommand =
               ViewController.DataMigrationViewControllerActionEvent.valueOf(e.getActionCommand());
            
            switch (actionCommand){
                case DATA_MIGRATION_START_REQUEST:
                    setOldEntity(getNewEntity());
                    setNewEntity(Entity.PATIENT);
                    firePropertyChangeEvent(
                            DataMigrationViewControllerPropertyChangeEvent.
                                        PREPARE_FOR_RECEIPT_OF_PATIENT_MIGRATION_PROGRESS.toString(),
                            getView(),
                            this,
                            null,
                            null
                    );
                    break;

                case READY_FOR_RECEIPT_OF_PATIENT_MIGRATION_PROGRESS:
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.MIGRATE_PATIENT_DATA.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;

                case READY_FOR_RECEIPT_OF_APPOINTMENT_MIGRATION_PROGRESS:                 
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.MIGRATE_APPOINTMENT_DATA.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
                    
                case READY_FOR_RECEIPT_OF_PATIENT_NOTE_MIGRATION_PROGRESS:                 
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.MIGRATE_PATIENT_NOTE_DATA.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
                    
                case READY_FOR_RECEIPT_OF_PRIMARY_CONDITION_MIGRATION_PROGRESS:
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.MIGRATE_PRIMARY_CONDITION_DATA.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
                    
                case READY_FOR_RECEIPT_OF_SECONDARY_CONDITION_MIGRATION_PROGRESS:
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.MIGRATE_SECONDARY_CONDITION_DATA.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;

                case DATA_MIGRATION_PROGRESS_VIEW_CLOSE_NOTIFICATION:
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.VIEW_CLOSED_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
            }
        }    
    }
}
