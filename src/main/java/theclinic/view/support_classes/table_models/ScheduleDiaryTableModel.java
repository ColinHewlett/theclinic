/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import theclinic.model.entity.Appointment;
import theclinic.model.non_entity.Slot;
import theclinic.model.entity.Patient;

/**
 *
 * @author colin
 */
public class ScheduleDiaryTableModel extends DefaultTableModel{
    private ArrayList<Slot> slots = null;
    private enum COLUMN{Slot, Patient,Treatment};
    private final Class[] columnClass = new Class[] {
        LocalDateTime.class,
        Patient.class, 
        String.class,
        };

    /**
     * need to know the current patient (if any)
     
    private Patient patient = null;
    private Patient getCurrentPatient(){
        return patient;
    }
    private void setCurrentPatient(Patient value){
        patient = value;
    }*/
    
    public ScheduleDiaryTableModel(){
        slots = new ArrayList<>();  
    }
    
    public ArrayList<Slot> getSlots(){
        return this.slots;
    }
    
    /*
    public void addElement(Slot slot){
        slots.add(slot);
    }*/
    
    public void removeAllElements(){
        slots.clear();
        this.fireTableDataChanged();
    }
    
    public void setData(ArrayList<Slot> newData){
        slots = newData;
        this.fireTableDataChanged();
    }
    
    public Slot getElementAt(int row){
        return slots.get(row);
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // No cell is editable
    }

    @Override
    public int getRowCount(){
        int result;
        if (slots!=null) result = slots.size();
        else result = 0;
        return result;
    }

    @Override
    public int getColumnCount(){
        return COLUMN.values().length;
    }
    @Override
    public String getColumnName(int columnIndex){
        String result = null;
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                result = column.toString();
                break;
            }
        }
        return result;
    }
    @Override
    public Class<?> getColumnClass(int columnIndex){
        return columnClass[columnIndex];
    }
    
    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        Slot slot = getSlots().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (slot == null){
                    return null;
                }
                else{
                    switch (column){
                        case Slot:
                            result = slot.getStart();
                            break;
                        case Patient:
                            if(slot.getAppointment().getPatient()!=null){ 
                                if(slot.getAppointment().getStart().toLocalTime().equals(slot.getStart().toLocalTime())){
                                    result = slot.getAppointment().getPatient();
                                }
                            }
                            break;
                        case Treatment:
                            if(slot.getAppointment().getPatient()!=null){ 
                                if(slot.getAppointment().getStart().toLocalTime().equals(slot.getStart().toLocalTime())){
                                    result = slot.getAppointment().getNotes();
                                }
                            }
                            break;
                    }

                }
            }
        }
        return result;
    }
}
