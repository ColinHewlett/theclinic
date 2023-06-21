/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.factory_methods;

import controller.Descriptor;
import view.views.ImportProgressView;
import view.View;
import view.views.DesktopView;
import java.awt.event.ActionListener;

/**
 *
 * @author colin
 */
public class ImportProgressFactoryMethod extends ViewFactoryMethod{
    
    public ImportProgressFactoryMethod(ActionListener controller, Descriptor ed, DesktopView dtView){
        initialiseView(controller, ed, dtView);
    }
    
    @Override
    public View makeView(View.Viewer myViewType){
        return new ImportProgressView(myViewType, this.getViewController(), 
                this.getDescriptor(), getDesktopView().getDeskTop());
        
    }
    
    private void initialiseView(ActionListener controller, Descriptor ed, DesktopView dtView){
        this.setDesktopView(dtView);
        this.setDescriptor(ed);
        this.setViewController(controller);
    }
    
}
