/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theclinic.model.entity;

//<editor-fold defaultstate="collapsed" desc="Imports">
import theclinic.model.entity.Entity;
import theclinic.model.repository.StoreException;//01/03/2023
import java.awt.Point;
import java.time.DayOfWeek;
import java.util.HashMap;
import theclinic.model.entity.interfaces.IEntityRepositoryActions;
//</editor-fold>
/**
 *
 * @author colin
 */
public class SurgeryDaysAssignment extends Entity implements IEntityRepositoryActions {
//<editor-fold defaultstate="collapsed" desc="Private and protected state">
    private HashMap<DayOfWeek,Boolean> assignment = new HashMap<>();
    
    private void set(HashMap<DayOfWeek,Boolean> value){
        assignment = value;
    }
//</editor-fold>   
    
//<editor-fold defaultstate="collapsed" desc="Public interface">
    public SurgeryDaysAssignment(){
        super.setIsSurgeryDaysAssignment(true);
    }
   
    public SurgeryDaysAssignment(HashMap<DayOfWeek,Boolean> value){
        set(value);
        super.setIsSurgeryDaysAssignment(true);
    }
    
    public HashMap<DayOfWeek,Boolean> get(){
        return assignment;
    }

//<editor-fold defaultstate="collapsed" desc="Persistent storage access">
    @Override
    public void delete() throws StoreException{
        getRepository().delete(this);
    }
    
    /**
     * Total number of assignments stored on the system; typically 7, one for each day of the week but could return for example the number of days the surgery is open / or not open 
     * @return Integer, total number of records stored on the system
     * @throws StoreException 
     */
    @Override
    public Point count() throws StoreException{
        return getRepository().count(this);
    }
    
    @Override
    public void create() throws StoreException{
        getRepository().create(this);  
    }
    
    @Override
    public void drop() throws StoreException{
        /*
        IStoreActions store = Store.FACTORY();
        if (!SystemDefinition.getPMSDebug().equals("ENABLED"))
            store.drop(this);
        */
    }
    
    /**
     * implemented for migration purposes only
     * -- 
     * @throws StoreException
     */
    @Override
    public Integer insert() throws StoreException{
        get().put(DayOfWeek.MONDAY, Boolean.TRUE);
        get().put(DayOfWeek.TUESDAY, Boolean.TRUE);
        get().put(DayOfWeek.WEDNESDAY, Boolean.TRUE);
        get().put(DayOfWeek.THURSDAY, Boolean.TRUE);
        get().put(DayOfWeek.FRIDAY, Boolean.TRUE);
        get().put(DayOfWeek.SATURDAY, Boolean.FALSE);
        get().put(DayOfWeek.SUNDAY, Boolean.FALSE);
        return getRepository().insert(this);
    }
    
    /**
     * 
     * @return
     * @throws StoreException 
     */
    @Override
    public SurgeryDaysAssignment read() throws StoreException{
        return getRepository().read(this);
    }
     
    /**
     * 
     * @throws StoreException 
     */
    @Override
    public void update() throws StoreException{
        getRepository().update(this);
    }
 //</editor-fold>
//</editor-fold>
}
