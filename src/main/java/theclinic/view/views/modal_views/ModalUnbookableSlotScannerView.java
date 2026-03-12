/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package theclinic.view.views.modal_views;

import theclinic.controller.PatientViewController;
import theclinic.controller.ScheduleViewController;
import theclinic.controller.SystemDefinition;
import theclinic.controller.ViewController;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import theclinic.model.entity.Appointment;
import theclinic.view.View;
import theclinic.view.views.non_modal_views.DesktopView;
import theclinic.view.support_classes.table_models.EmptySlotAvailability2ColumnTableModel;
import theclinic.view.support_classes.table_models.UnbookableSlotsTableModel;
import theclinic.view.support_classes.table_renderers.TableHeaderCellBorderRenderer;
import java.util.Set;

/**
 *
 * @author colin
 */
public class ModalUnbookableSlotScannerView extends ModalView implements ListSelectionListener{

    /**
     * Creates new form UnbookableSlotScannerView
     */
    public ModalUnbookableSlotScannerView(
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setTitle("Bookable slot scanner");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);
        initComponents();
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        if(e.getSource().equals(this.lsmForSloAvailabilityTable)){
            if (e.getValueIsAdjusting()) return;

            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (!lsm.isSelectionEmpty()) {
                int selectedRow = lsm.getMinSelectionIndex();
                doUnbookableSlotTableRowSelection(selectedRow);
            }
        }
    }
    
    @Override
    public void initialiseView(){
        setTitle("Unbookable slot scan");
        setClosable(true);
        
        this.tblUnbookableSlots = new JTable(new UnbookableSlotsTableModel());
        scrPanelUnbookableSlots.setViewportView(this.tblUnbookableSlots);
        setUnbookableSlotTableListener();
        
        ArrayList<Appointment> unbookableSlots = 
                (ArrayList<Appointment>)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS);
        this.populateUnbookableSlotTable(unbookableSlots);
        ViewController.setJTableColumnProperties(this.tblUnbookableSlots, this.scrPanelUnbookableSlots.getPreferredSize().width, 30,20,50);
        
        addInternalFrameListeners();
        
    }
    
    private InternalFrameAdapter internalFrameAdapter = null;
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosing(InternalFrameEvent e) {
                ModalUnbookableSlotScannerView.this.removeInternalFrameListener(internalFrameAdapter);
                ActionEvent actionEvent = new ActionEvent(
                        ModalUnbookableSlotScannerView.this,ActionEvent.ACTION_PERFORMED,
                        ScheduleViewController.Actions.
                                VIEW_CLOSE_NOTIFICATION.toString());
                ModalUnbookableSlotScannerView.this.getMyController().actionPerformed(actionEvent);
                
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e){
                ActionEvent actionEvent = new ActionEvent(
                        ModalUnbookableSlotScannerView.this,ActionEvent.ACTION_PERFORMED,
                        ScheduleViewController.Actions.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                ModalUnbookableSlotScannerView.this.getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
    }
    
    private void doUnbookableSlotTableRowSelection(int row){
        Appointment appointment = 
                ((UnbookableSlotsTableModel)this.tblUnbookableSlots.getModel()).getElementAt(row);
        LocalDate start = appointment.getStart().toLocalDate();
        
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, start);
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                PatientViewController.Actions.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        this.doCloseViewRequest();
    }
    
    private void doCloseViewRequest(){
        try{
            setClosed(true);
        }catch (PropertyVetoException e){
            
        }
    }
    
    private ListSelectionModel lsmForSloAvailabilityTable = null;
    private void setUnbookableSlotTableListener(){
        this.tblUnbookableSlots.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsmForSloAvailabilityTable = this.tblUnbookableSlots.getSelectionModel();
        lsmForSloAvailabilityTable.addListSelectionListener(this); 
    }

    private void populateUnbookableSlotTable(ArrayList<Appointment> a) {
        if (a == null) {
            a = new ArrayList<>();
        }
        UnbookableSlotsTableModel model;
        /*if (this.tblUnbookableSlots!=null){
            this.scrPanelUnbookableSlots.remove(this.tblUnbookableSlots);   
        }*/
        /*this.tblUnbookableSlots = new JTable(new UnbookableSlotsTableModel());
        scrPanelUnbookableSlots.setViewportView(this.tblUnbookableSlots);
        setUnbookableSlotTableListener();*/
        model = (UnbookableSlotsTableModel)this.tblUnbookableSlots.getModel();
        model.removeAllElements();
        Iterator<Appointment> it = a.iterator();
        while (it.hasNext()){
            ((UnbookableSlotsTableModel)this.tblUnbookableSlots.getModel()).addElement(it.next());
        }

        JTableHeader tableHeader = this.tblUnbookableSlots.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true);
        
        TableColumnModel columnModel = this.tblUnbookableSlots.getColumnModel();
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        scrPanelUnbookableSlots = new javax.swing.JScrollPane();
        tblUnbookableSlots = new javax.swing.JTable();

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblUnbookableSlots.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrPanelUnbookableSlots.setViewportView(tblUnbookableSlots);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPanelUnbookableSlots, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPanelUnbookableSlots, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane scrPanelUnbookableSlots;
    private javax.swing.JTable tblUnbookableSlots;
    // End of variables declaration//GEN-END:variables
}
