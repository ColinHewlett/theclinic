/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clinicpms.view.views.factory_methods;

import clinicpms.controller.Descriptor;
import clinicpms.view.views.DesktopView;
import clinicpms.view.View;
import clinicpms.view.views.PatientNotificationView;
import java.awt.event.ActionListener;

/**
 *
 * @author colin
 */
public class PatientNotificationFactoryMethod extends ViewFactoryMethod{
    public PatientNotificationFactoryMethod(ActionListener controller, 
            Descriptor ed, 
            DesktopView dtView){   
        initialiseView(controller, ed, dtView);   
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        return new PatientNotificationView(myViewType, 
                getViewController(), 
                getDescriptor(), getDesktopView().getDeskTop());
        
    }
    
    private void initialiseView(ActionListener controller, 
            Descriptor ed, 
            DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
    
}
