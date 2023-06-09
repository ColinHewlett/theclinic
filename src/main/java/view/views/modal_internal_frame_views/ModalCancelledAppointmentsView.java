/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinicpms.view.views.modal_internal_frame_views;

import clinicpms.view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import clinicpms.view.views.view_support_classes.renderers.AppointmentsTableLocalDateRenderer;
import clinicpms.view.views.view_support_classes.renderers.AppointmentsTablePatientRenderer;
import clinicpms.controller.Descriptor;
import clinicpms.controller.ViewController;
import clinicpms.model.Appointment;
import clinicpms.model.Patient;
import clinicpms.view.views.view_support_classes.renderers.TableHeaderCellBorderRenderer;
import clinicpms.view.views.view_support_classes.models.Appointments6ColumnTableModel;
import clinicpms.view.View;
import clinicpms.view.views.view_support_classes.models.Appointments5ColumnTableModel;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.beans.PropertyVetoException;
import java.time.Duration;

/**
 *
 * @author colin
 */
public class ModalCancelledAppointmentsView extends View{
    private final JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private JTable tblCancelledAppointments = new JTable();
    private JButton btnCloseCancelledAppointmentsView = new JButton("Close cancelled appointments view");
    private JButton btnUncancelSelectedAppointment = new JButton("Uncancel selected appointment");
    
    public ModalCancelledAppointmentsView(
            View.Viewer myViewType, 
            ActionListener myController, 
            Descriptor entityDescriptor,
            JDesktopPane desktop){

            super("Cancelled appointment view");
            setViewDescriptor(entityDescriptor);
            this.setMyController(myController);
            this.setMyViewType(myViewType);
            initComponents(); 
            this.populateCancelledAppointmentsTable();
            desktop.add(this);
            this.setLayer(JLayeredPane.MODAL_LAYER);
            centreViewOnDesktop(desktop.getParent(),this);
            this.setVisible(true);

            startModal(this);
        }
    
    private void startModal(JInternalFrame f) {
        // We need to add an additional glasspane-like component directly
        // below the frame, which intercepts all mouse events that are not
        // directed at the frame itself.
        JPanel modalInterceptor = new JPanel();
        modalInterceptor.setOpaque(false);
        JLayeredPane lp = JLayeredPane.getLayeredPaneAbove(f);
        lp.setLayer(modalInterceptor, JLayeredPane.MODAL_LAYER.intValue());
        modalInterceptor.setBounds(0, 0, lp.getWidth(), lp.getHeight());
        modalInterceptor.addMouseListener(new MouseAdapter(){});
        modalInterceptor.addMouseMotionListener(new MouseMotionAdapter(){});
        lp.add(modalInterceptor);
        f.toFront();

        // We need to explicitly dispatch events when we are blocking the event
        // dispatch thread.
        EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        try {
            while (! f.isClosed())       {
                if (EventQueue.isDispatchThread())    {
                    // The getNextEventMethod() issues wait() when no
                    // event is available, so we don't need do explicitly wait().
                    AWTEvent ev = queue.getNextEvent();
                    // This mimics EventQueue.dispatchEvent(). We can't use
                    // EventQueue.dispatchEvent() directly, because it is
                    // protected, unfortunately.
                    if (ev instanceof ActiveEvent)  ((ActiveEvent) ev).dispatch();
                    else if (ev.getSource() instanceof Component)  ((Component) ev.getSource()).dispatchEvent(ev);
                    else if (ev.getSource() instanceof MenuComponent)  ((MenuComponent) ev.getSource()).dispatchEvent(ev);
                    // Other events are ignored as per spec in
                    // EventQueue.dispatchEvent
                } else  {
                    // Give other threads a chance to become active.
                    Thread.yield();
                }
            }
        }
        catch (InterruptedException ex) {
            // If we get interrupted, then leave the modal state.
        }
        finally {
            // Clean up the modal interceptor.
            lp.remove(modalInterceptor);

            // Remove the internal frame from its parent, so it is no longer
            // lurking around and clogging memory.
            Container parent = f.getParent();
            if (parent != null) parent.remove(f);
        }
    }
    
    private void centreViewOnDesktop(Container desktopView, JInternalFrame view){
        Insets insets = desktopView.getInsets();
        Dimension deskTopViewDimension = desktopView.getSize();
        Dimension myViewDimension = view.getSize();
        Point point = new Point(
                (int)((deskTopViewDimension.getWidth()) - (myViewDimension.getWidth()))/2,
                (int)((deskTopViewDimension.getHeight()-insets.top) - myViewDimension.getHeight())/2);
        
        view.setLocation(point);
    }
    
    private void populateCancelledAppointmentsTable(){
        if (tblCancelledAppointments.getModel() == null)
            tblCancelledAppointments.setModel(new Appointments6ColumnTableModel());
        Appointments6ColumnTableModel model = 
                (Appointments6ColumnTableModel)tblCancelledAppointments.getModel();
        model.removeAllElements();
        Iterator<Appointment> it = 
                getViewDescriptor().getControllerDescription().getAppointmentCancellations().iterator();
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
        ViewController.AppointmentScheduleViewControllerPropertyChangeEvent event =
                ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch (event){
            case APPOINTMENTS_CANCELLED_RECEIVED:
                setViewDescriptor((Descriptor)e.getNewValue());
                populateCancelledAppointmentsTable();
                ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                        ViewController.AppointmentScheduleViewControllerActionEvent.VIEW_CHANGED_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
        }        

    }
    
    private void initComponents() {
        btnCloseCancelledAppointmentsView.addActionListener((ActionEvent e) -> 
                btnCloseCancelledAppointmentsViewActionPerformed());
        this.btnUncancelSelectedAppointment.addActionListener((ActionEvent e) ->
                btnUncancelSelectedAppointmentActionPerformed());
        tblCancelledAppointments = new JTable(new Appointments6ColumnTableModel());
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
            getViewDescriptor().getViewDescription().setAppointment(
                    ((Appointments6ColumnTableModel)tblCancelledAppointments.
                            getModel()).getElementAt(row)); 
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.AppointmentScheduleViewControllerActionEvent.APPOINTMENT_UNCANCEL_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }else
            JOptionPane.showMessageDialog(this, "An appointment to uncancel has not been selected");
    }
    
    public void initialiseView(){
        
    }
    
    @Override
    public void addInternalFrameListeners(){
        
    }
}
