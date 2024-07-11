/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.entity.Appointment;
import model.non_entity.Slot;
import model.entity.Patient;

/**
 *
 * @author colin
 */
public class ScheduleDiaryTableModel extends DefaultTableModel{
    private ArrayList<Slot> appointments = null;
    private enum COLUMN{Slot, Patient,Treatment};
    private final Class[] columnClass = new Class[] {
        LocalDateTime.class,
        Patient.class, 
        String.class,
        };

    public ScheduleDiaryTableModel(){
        appointments = new ArrayList<>();  
    }
    
}
