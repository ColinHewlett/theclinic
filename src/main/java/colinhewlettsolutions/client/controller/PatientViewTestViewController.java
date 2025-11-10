/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
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
