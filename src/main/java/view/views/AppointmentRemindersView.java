/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clinicpms.view.views;

import clinicpms.view.views.view_support_classes.models.AppointmentRemindersView6ColumnTableModel;
import clinicpms.view.views.view_support_classes.renderers.TableHeaderCellBorderRenderer;
import clinicpms.view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import clinicpms.controller.Descriptor;
import clinicpms.controller.ViewController;
import clinicpms.model.Appointment;
import clinicpms.view.View;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.ArrayList;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.io.PrintStream;

/**
 *
 * @author colin
 */
public class AppointmentRemindersView extends View {

    private InternalFrameAdapter internalFrameAdapter = null;
    private JTable tblPatientAppointmentContacts = null;
    
    
    @Override
    public void addInternalFrameListeners(){
        
    }
    
        
    /**
     * 
     * @param myViewType
     * @param myController
     * @param value 
     */
    public AppointmentRemindersView(View.Viewer myViewType, ActionListener myController, Descriptor value) {
        super("Patient appointment reminders");
        setMyController(myController);
        setViewDescriptor(value);
        initComponents();
        //addInternalFrameClosingListener();
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        AppointmentRemindersView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.AppointmentRemindersViewControllerActionEvent.
                                VIEW_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
 
    /**
     * Method processes the PropertyChangeEvent its received from the view controller
     * -- directing view to refresh the data in its table view
     * @param e PropertyChangeEvent, embedded in which is refresh relevant data 
     */
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.AppointeeContactDetailsForScheduleViewControllerPropertyChangeEvent propertyName = 
                ViewController.AppointeeContactDetailsForScheduleViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        setViewDescriptor((Descriptor)e.getNewValue());
        switch (propertyName){ 
            case APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_REFRESH_RECEIVED:
                populateAppointeeContactsTable(getViewDescriptor().getControllerDescription().getAppointments());
                break;
        }
    }
    
    @Override
    public void initialiseView(){
        try{
            LocalDate scheduleDay = getViewDescriptor().getViewDescription().getDay();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            String title = scheduleDay.format(formatter);
            title = title + " appointment schedule -> patient reminders";
            setTitle(title);
            setVisible(true);
            setClosable(true);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(false);
            setSelected(true);
            setSize(850,375); 
        }
        catch (PropertyVetoException ex){
            
        }
    }
    
    private void populateAppointeeContactsTable(ArrayList<Appointment> a){
        AppointmentRemindersView6ColumnTableModel model;
        if (this.tblPatientAppointmentContacts!=null){
            this.scrPatientAppointmentContactView.remove(this.tblPatientAppointmentContacts);   
        }
        this.tblPatientAppointmentContacts = new JTable(new AppointmentRemindersView6ColumnTableModel());
        this.tblPatientAppointmentContacts.setRowSelectionAllowed(false);
        scrPatientAppointmentContactView.setViewportView(this.tblPatientAppointmentContacts);
        this.tblPatientAppointmentContacts.setDefaultRenderer(Duration.class, new AppointmentsTableDurationRenderer());
        JTableHeader tableHeader = this.tblPatientAppointmentContacts.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true);

        TableColumnModel columnModel = this.tblPatientAppointmentContacts.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(190);
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setPreferredWidth(60);
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(2).setPreferredWidth(60);
        columnModel.getColumn(2).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(3).setPreferredWidth(105);
        columnModel.getColumn(3).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(4).setPreferredWidth(400);
        columnModel.getColumn(4).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(5).setPreferredWidth(75);
        columnModel.getColumn(5).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        
        model = (AppointmentRemindersView6ColumnTableModel)this.tblPatientAppointmentContacts.getModel();
        model.removeAllElements();
        Iterator<Appointment> it = a.iterator();
        while (it.hasNext()){
            ((AppointmentRemindersView6ColumnTableModel)this.tblPatientAppointmentContacts.getModel()).addElement(it.next());
        }
        String title = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        //LocalDate scheduleDay = a.get(0).getStart().toLocalDate();
        LocalDate scheduleDay = getViewDescriptor().getControllerDescription().getAppointmentScheduleDay();
        title = scheduleDay.format(formatter);
        title = title + " schedule -> patient reminders";
        setTitle(title);
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.AppointmentRemindersViewControllerActionEvent.
                        VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
        model.addTableModelListener(new TableModelListener(){
            Appointment appointment = null;
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                AppointmentRemindersView6ColumnTableModel model =  
                        (AppointmentRemindersView6ColumnTableModel)e.getSource();
                Boolean value = (Boolean)model.getValueAt(row, column);
                appointment = model.getElementAt(row);
                appointment.setHasPatientBeenContacted(value);
                getViewDescriptor().getViewDescription().setAppointment(appointment);
                ActionEvent actionEvent = new ActionEvent(
                        AppointmentRemindersView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.AppointmentRemindersViewControllerActionEvent.
                                APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_CHANGE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        });  
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        scrPatientAppointmentContactView = new javax.swing.JScrollPane();
        btnCloseView = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(762, 557));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientAppointmentContactView, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientAppointmentContactView, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnCloseView.setText("Close view");
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(36, 632, Short.MAX_VALUE)
                .addComponent(btnCloseView)
                .addGap(33, 33, 33))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCloseView)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseViewActionPerformed
        // TODO add your handling code here:
        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException ex){
            
        }
    }//GEN-LAST:event_btnCloseViewActionPerformed

    public static String centreString (int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }
    
    public static String leftAlignString(int width, String s){
        return String.format("%-" + width + "s", s);
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane scrPatientAppointmentContactView;
    // End of variables declaration//GEN-END:variables


}
