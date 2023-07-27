/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.factory_methods;

import controller.Descriptor;
import controller.ViewController;
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
            ViewController controller,DesktopView dtView){
        factoryViewInitialisation(controller,dtView);  
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        View view = new ModalUnbookableAppointmentSlotEditorView(
                        myViewType, 
                        this.getViewController(), 
                        getDesktopView().getDeskTop());
        view.initialiseView();
        return view;
    }
    
    private void factoryViewInitialisation(ViewController controller, DesktopView dtView){
        this.setDesktopView(dtView);
        this.setViewController(controller);
    }
    
}
