/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.views.dialog_views;

import theclinic.view.View;
import theclinic.view.views.modal_views.ModalView;

/**
 * 
 * @author colin
 * @param <T> 
 */
public abstract class DialogView<T> extends ModalView{
    private View myParentView = null;
    public View getMyParentView(){
        return myParentView;
    }
    public void setMyParentView(View value){
        myParentView = value;
    }
    
    private T selectedItem = null;
    public T getSelectedItem(){
        return  selectedItem;
    }
    public void setSelectedItem(T value){
        selectedItem = value;
    }
    
}
