/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.modal_views;

import controller.ViewController;
import view.View;
import view.views.non_modal_views.DesktopView;

/**
 *
 * @author colin
 */
public class ModalPatientGuardianEditorView extends ModalView{
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalPatientGuardianEditorView(
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setTitle("Patient guardian editor");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void initialiseView(){
        
    }
}
