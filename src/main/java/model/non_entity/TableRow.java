/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.non_entity;

import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
/**
 *
 * @author colin
 */
public class TableRow extends XWPFTableRow{
    
    public TableRow(CTRow row, XWPFTable table, State value){
        super(row,table);
        state = value;
    }
    
    public enum State{
        USED,
        UNUSED
    }
    
    private State state = null;
    public State getState(){
        return state;
    }
    public void setState(State value){
        state = value;
    }
}
