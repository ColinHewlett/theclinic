/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.factory_methods;

import controller.Descriptor;
import controller.ViewController;
import view.views.DesktopView;
import view.views.ScheduleView;
import view.View;
import java.awt.event.ActionListener;

/**
 *
 * @author colin
 */
public class AppointmentScheduleFactoryMethod extends ViewFactoryMethod{

    public AppointmentScheduleFactoryMethod(ViewController controller, Descriptor ed, DesktopView dtView){
        initialiseView(controller, ed, dtView);
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        return new ScheduleView(myViewType, this.getViewController(), this.getDescriptor());
    }
    
    private void initialiseView(ViewController controller, Descriptor ed, DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
    
    
}
