/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.factory_methods;

import controller.Descriptor;
import controller.ViewController;
import view.View;
import view.views.DesktopView;
import view.views.modal_internal_frame_views.ModalCancelledAppointmentsView;
import java.awt.event.ActionListener;

/**
 *
 * @author colin
 */
public class ModalCancelledAppointmentsViewFactoryMethod extends ViewFactoryMethod{
    public ModalCancelledAppointmentsViewFactoryMethod(
            ViewController controller, 
            Descriptor ed, 
            DesktopView dtView){
        initialiseView(controller, ed, dtView);
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        return new ModalCancelledAppointmentsView(
                myViewType, 
                getViewController(), 
                this.getDescriptor(), 
                getDesktopView().getDeskTop());
        
    }
    
    private void initialiseView(
            ViewController controller, 
            Descriptor ed, 
            DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
    
                
    
}

