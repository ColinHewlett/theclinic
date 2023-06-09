/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clinicpms.view.views.factory_methods;

import clinicpms.view.views.AppointmentRemindersView;
import clinicpms.view.views.DesktopView;
import clinicpms.controller.Descriptor;
import clinicpms.view.*;
import java.awt.event.ActionListener;
/**
 *
 * @author colin
 */
public class AppointmentRemindersFactoryMethod extends ViewFactoryMethod{
    
    public AppointmentRemindersFactoryMethod(ActionListener viewController,
            Descriptor ed, DesktopView dtView){
        initialiseView(viewController, ed, dtView);
    }

    @Override
    public View makeView(View.Viewer myViewType){
        return new AppointmentRemindersView(myViewType, getViewController(), getDescriptor()); 
    }
    
    private void initialiseView(ActionListener controller, Descriptor ed, DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
    
}
