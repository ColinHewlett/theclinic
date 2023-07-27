/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.factory_methods;

import controller.Descriptor;
import controller.ViewController;
import view.views.DesktopView;
import view.views.modal_internal_frame_views.ModalEmptySlotScanConfigurationView;
import view.View;
import java.awt.event.ActionListener;
/**
 *
 * @author colin
 */
public class ModalEmptySlotScannerFactoryMethod extends ViewFactoryMethod{
    public ModalEmptySlotScannerFactoryMethod(ViewController viewController, 
            Descriptor ed, DesktopView dtView ){
        initialiseView(viewController, ed,dtView);  
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        return new ModalEmptySlotScanConfigurationView(myViewType, getViewController(), 
        getDescriptor(), getDesktopView().getDeskTop());  
    }
    
    private void initialiseView(ViewController controller, Descriptor ed, DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
}
