/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.renderers;

import colinhewlettsolutions.client.view.views.non_modal_views.ScheduleDiaryView.ScheduleDiaryAction;
import java.awt.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author colin
 */
public class ComboboxScheduleDiaryActionRenderer<T>  extends JLabel implements ListCellRenderer<T>{
    String action = null;
    @Override
    public Component getListCellRendererComponent(JList<? extends T> list,
                                                   T value,
                                                   int index,
                                                   boolean isSelected,
                                                   boolean cellHasFocus) {
        if (value!=null){
            ScheduleDiaryAction _value = (ScheduleDiaryAction)value;
            switch (_value){
                case EXTEND_APPOINTMENT_EARLIER:
                    action = "Extend booking earlier";
                    break;
                case EXTEND_APPOINTMENT_LATER:
                    action = "Extend booking later";
                    break;
                case SHIFT_APPOINTMENT_EARLIER:
                    action = "Shift booking earlier";
                    break;
                case SHIFT_APPOINTMENT_LATER:
                    action = "Shift booking later";
                    break;        
            }
            super.setText(action);
            //super.setHorizontalAlignment(JLabel.CENTER);
        }else super.setText("");

        return this; 
    }
    
}
