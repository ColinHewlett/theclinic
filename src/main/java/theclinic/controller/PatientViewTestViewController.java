/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller;

import theclinic.model.entity.Entity;
import theclinic.model.entity.Patient;
import theclinic.model.repository.StoreException;
import theclinic.view.View;
import theclinic.view.views.non_modal_views.DesktopView;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author colin
 */
public class PatientViewTestViewController extends ViewController{
    
    public PatientViewTestViewController (DesktopViewController myController,
                                 Descriptor myDescriptor,
                                 DesktopView desktopView)throws StoreException{
        setDesktopView(desktopView);
        setMyController(myController);
        Patient patient = new Patient();
        patient.setScope(Entity.Scope.ALL);
        patient.read();
        setDescriptor(myDescriptor);
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENTS, patient.get());
        View.setViewer(View.Viewer.PATIENT_VIEW);
        //setCurrentlySelectedPatient(new Patient());
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
    
    }
}
