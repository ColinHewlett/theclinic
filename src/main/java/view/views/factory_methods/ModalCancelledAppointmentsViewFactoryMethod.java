/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinicpms.view.views.factory_methods;

import clinicpms.controller.Descriptor;
import clinicpms.view.View;
import clinicpms.view.views.DesktopView;
import clinicpms.view.views.modal_internal_frame_views.ModalCancelledAppointmentsView;
import java.awt.event.ActionListener;

/**
 *
 * @author colin
 */
public class ModalCancelledAppointmentsViewFactoryMethod extends ViewFactoryMethod{
    public ModalCancelledAppointmentsViewFactoryMethod(
            ActionListener controller, 
            Descriptor ed, 
            DesktopView dtView){
        initialiseView(controller, ed, dtView);
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        return new ModalCancelledAppointmentsView(
                myViewType, 
                this.getViewController(), 
                this.getDescriptor(), 
                getDesktopView().getDeskTop());
        
    }
    
    private void initialiseView(
            ActionListener controller, 
            Descriptor ed, 
            DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
    
                
    
}

