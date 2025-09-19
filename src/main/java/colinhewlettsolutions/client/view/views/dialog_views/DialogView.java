/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.views.dialog_views;

import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.modal_views.ModalView;

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
