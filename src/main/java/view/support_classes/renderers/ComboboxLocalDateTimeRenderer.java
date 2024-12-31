/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.support_classes.renderers;

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
public class ComboboxLocalDateTimeRenderer<T>  extends JLabel implements ListCellRenderer<T>{
    private DateTimeFormatter timeOnlyFormat = DateTimeFormatter.ofPattern("HH:mm");
    
    @Override
    public Component getListCellRendererComponent(JList<? extends T> list,
                                                   T value,
                                                   int index,
                                                   boolean isSelected,
                                                   boolean cellHasFocus) {
        if (value!=null){
            LocalDateTime _value = (LocalDateTime)value;
            super.setText(_value.format(timeOnlyFormat));
            super.setHorizontalAlignment(JLabel.CENTER);
        }else super.setText("");

        return this; 
    }
    
}
