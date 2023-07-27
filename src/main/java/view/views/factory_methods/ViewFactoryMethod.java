/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.factory_methods;

import controller.Descriptor;
import controller.ViewController;
import view.views.DesktopView;
import view.View;
import java.awt.event.ActionListener;

/**
 *
 * @author colin
 */
public abstract class ViewFactoryMethod {
    private ViewController viewController = null;
    private Descriptor entityDescriptor = null;
    private DesktopView desktopView = null;
    protected abstract View makeView(View.Viewer viewType); 

    public ViewController getViewController(){
        return viewController;
    }
    
    public void setViewController(ViewController value){
        this.viewController = value;
    }
    
    public Descriptor getDescriptor(){
        return this.entityDescriptor;
    }
    
    public void setDescriptor(Descriptor value){
        this.entityDescriptor = value;
    }
    
    public DesktopView getDesktopView(){
        return this.desktopView;
    }
    
    public void setDesktopView(DesktopView value){
        this.desktopView = value;
    }
}


