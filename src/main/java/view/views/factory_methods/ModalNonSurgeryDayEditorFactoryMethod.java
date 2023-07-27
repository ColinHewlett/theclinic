/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.factory_methods;

import view.views.modal_internal_frame_views.ModalNonSurgeryDayEditorView;
import view.views.DesktopView;
import controller.Descriptor;
import controller.ViewController;
import view.*;
import java.awt.event.ActionListener;
/**
 *
 * @author colin
 */
public class ModalNonSurgeryDayEditorFactoryMethod extends ViewFactoryMethod{
    
    public ModalNonSurgeryDayEditorFactoryMethod(ViewController viewController, 
            Descriptor ed, DesktopView dtView){
        initialiseView(viewController,ed, dtView);
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        return new ModalNonSurgeryDayEditorView(myViewType, getViewController(),
                getDescriptor(), getDesktopView().getDeskTop());
    }
    
    private void initialiseView(ViewController controller, Descriptor ed, DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
}
