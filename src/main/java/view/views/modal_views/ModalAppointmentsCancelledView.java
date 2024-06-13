/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.modal_views;

import view.views.non_modal_views.DesktopView;
import view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import view.views.view_support_classes.renderers.AppointmentsTableLocalDateRenderer;
import view.views.view_support_classes.renderers.AppointmentsTablePatientRenderer;
import controller.ViewController;
import model.entity.Appointment;
import model.entity.Patient;
import view.views.view_support_classes.renderers.TableHeaderCellBorderRenderer;
import view.views.view_support_classes.models.CancelledAppointmentsTableModel;
import view.View;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.beans.PropertyVetoException;
import java.time.Duration;

/**
 *
 * @author colin
 */
public class ModalAppointmentsCancelledView extends ModalView{
    private final JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private JTable tblCancelledAppointments = new JTable();
    private final JButton btnCloseCancelledAppointmentsView = new JButton("Close cancelled appointments view");
    private final JButton btnUncancelSelectedAppointment = new JButton("Uncancel selected appointment");

    public ModalAppointmentsCancelledView(
            View.Viewer myViewType, 
            ViewController myController, 
            DesktopView desktopView){

            setTitle("Cancelled appointments view");
            setMyController(myController);
            setMyViewType(myViewType);
            setDesktopView(desktopView);
        }
    
    private void populateCancelledAppointmentsTable(){
        if (tblCancelledAppointments.getModel() == null)
            tblCancelledAppointments.setModel(new CancelledAppointmentsTableModel());
        CancelledAppointmentsTableModel model = 
                (CancelledAppointmentsTableModel)tblCancelledAppointments.getModel();
        model.removeAllElements();
        Iterator<Appointment> it = 
                getMyController().getDescriptor().getControllerDescription().getAppointmentCancellations().iterator();
        while (it.hasNext()){
            model.addElement(it.next());
        }
        this.tblCancelledAppointments.setDefaultRenderer(Duration.class, new AppointmentsTableDurationRenderer());
        
       
        this.tblCancelledAppointments.setDefaultRenderer(LocalDateTime.class, new AppointmentsTableLocalDateRenderer());
        this.tblCancelledAppointments.setDefaultRenderer(Patient.class, new AppointmentsTablePatientRenderer());
        this.tblCancelledAppointments.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel columnModel = tblCancelledAppointments.getColumnModel();
 
    }  
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.ScheduleViewControllerPropertyChangeEvent event =
                ViewController.ScheduleViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch (event){
            case APPOINTMENTS_CANCELLED_RECEIVED:
                //setViewDescriptor((Descriptor)e.getNewValue());
                populateCancelledAppointmentsTable();
                ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.VIEW_CHANGED_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
        }        

    }
    
    private void initComponents() {
        btnCloseCancelledAppointmentsView.addActionListener((ActionEvent e) -> 
                btnCloseCancelledAppointmentsViewActionPerformed());
        this.btnUncancelSelectedAppointment.addActionListener((ActionEvent e) ->
                btnUncancelSelectedAppointmentActionPerformed());
        tblCancelledAppointments = new JTable(new CancelledAppointmentsTableModel());
        TableColumnModel columnModel = this.tblCancelledAppointments.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(70);
        columnModel.getColumn(1).setPreferredWidth(190);
        columnModel.getColumn(2).setPreferredWidth(50);
        columnModel.getColumn(3).setPreferredWidth(50);
        columnModel.getColumn(4).setPreferredWidth(105);
        columnModel.getColumn(5).setPreferredWidth(250);
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(2).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(3).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(4).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(5).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        JTableHeader tableHeader = this.tblCancelledAppointments.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true);
        jScrollPane1.setViewportView(tblCancelledAppointments);
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnUncancelSelectedAppointment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 273, Short.MAX_VALUE)
                .addComponent(btnCloseCancelledAppointmentsView))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCloseCancelledAppointmentsView)
                    .addComponent(btnUncancelSelectedAppointment))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        pack();
    }
    
    private void btnCloseCancelledAppointmentsViewActionPerformed(){
        try{
            setClosed(true);
        }catch (PropertyVetoException ex){
            
        }
    }
    
    private void btnUncancelSelectedAppointmentActionPerformed(){
        int row = this.tblCancelledAppointments.getSelectedRow();
        if (row != -1){
            getMyController().getDescriptor().getViewDescription().setAppointment(((CancelledAppointmentsTableModel)tblCancelledAppointments.
                            getModel()).getElementAt(row)); 
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.APPOINTMENT_UNCANCEL_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }else
            JOptionPane.showMessageDialog(this, "An appointment to uncancel has not been selected");
    }
    
    @Override
    public void initialiseView(){
        initComponents(); 
        this.populateCancelledAppointmentsTable();
        this.setVisible(true);
        
    }

}
