/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinicpms.view.views.factory_methods;

import clinicpms.controller.Descriptor;
import clinicpms.view.View;
import clinicpms.view.views.modal_internal_frame_views.ModalPatientSelectionView;
import clinicpms.view.views.DesktopView;
import java.awt.event.ActionListener;

/**
 *
 * @author colin
 */
public class ModalPatientSelectionViewFactoryMethod extends ViewFactoryMethod{
    public ModalPatientSelectionViewFactoryMethod(ActionListener controller, Descriptor ed, DesktopView dtView){
        initialiseView(controller, ed, dtView);
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        return new ModalPatientSelectionView(myViewType, this.getViewController(), 
                this.getDescriptor(), getDesktopView().getDeskTop());
        
    }
    
    private void initialiseView(ActionListener controller, Descriptor ed, DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
    
                
    
}
