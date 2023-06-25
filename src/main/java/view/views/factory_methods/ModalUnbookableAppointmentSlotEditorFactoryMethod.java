/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.factory_methods;

import controller.Descriptor;
import java.awt.event.ActionListener;
import view.View;
import view.views.DesktopView;
import view.views.modal_internal_frame_views.ModalUnbookableAppointmentSlotEditorView;

/**
 *
 * @author colin
 */
public class ModalUnbookableAppointmentSlotEditorFactoryMethod extends ViewFactoryMethod{

    public ModalUnbookableAppointmentSlotEditorFactoryMethod(
            ActionListener controller, Descriptor ed, DesktopView dtView){
        initialiseView(controller, ed, dtView);  
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        return new ModalUnbookableAppointmentSlotEditorView(
                myViewType, 
                this.getViewController(), 
                this.getDescriptor(), 
                getDesktopView().getDeskTop());
        
    }
    
    private void initialiseView(ActionListener controller, Descriptor ed, DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
    
}
