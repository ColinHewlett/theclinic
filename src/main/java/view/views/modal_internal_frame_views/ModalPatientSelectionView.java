/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.modal_internal_frame_views;

import controller.Descriptor;
import controller.ViewController;
import model.Patient;
import view.View;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
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
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author colin
 */
public class ModalPatientSelectionView extends View{
    private ActionListener myController = null;
    private Descriptor entityDescriptor = null;
    
    public ModalPatientSelectionView(
            View.Viewer myViewType,
            ViewController myController,
            Descriptor entityDescriptor, 
            JDesktopPane desktop){ 
        super("Patient selector view");
        //setViewDescriptor(entityDescriptor);
        setMyController(myController);
        setMyViewType(myViewType);
        initComponents(); 
        TitledBorder titledBorder = (TitledBorder)pnlPatientSelection.getBorder();
        switch (getMyViewType()){
            case PATIENT_SELECTION_VIEW:
                titledBorder.setTitle("Select patient");
                this.setTitle("Patient selection view");
                break;
            case PATIENT_RECOVERY_SELECTION_VIEW:
                titledBorder.setTitle("Select patient to recover");
                this.setTitle("Patient recovery selection view");
                break;
        }
        
        populatePatientSelector();
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
    
    private void populatePatientSelector(){
        DefaultComboBoxModel<Patient> model = 
                new DefaultComboBoxModel<>();
        ArrayList<Patient> patients = 
                getMyController().getDescriptor().
                        getControllerDescription().getPatients();
        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()){
            Patient patient = it.next();
            model.addElement(patient);
        }
        this.cmbPatientSelector.setModel(model);
        Patient patient = getMyController().getDescriptor().
                getControllerDescription().getPatient();
        if (patient!=null){
            if (patient.getIsKeyDefined())
                this.cmbPatientSelector.setSelectedItem(patient);
            else this.cmbPatientSelector.setSelectedIndex(-1);
        }
        else this.cmbPatientSelector.setSelectedIndex(-1);
    }

    @Override
    public void addInternalFrameListeners(){
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        if (e.getPropertyName().equals(
            ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.APPOINTMENT_SCHEDULE_ERROR_RECEIVED.toString())){
            Descriptor ed = (Descriptor)e.getNewValue();
            ViewController.displayErrorMessage(ed.getControllerDescription().getError(),
                                               "Appointment editor dialog error",
                                               JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void initialiseView(){
        
    }
    
    private void initComponents() {

        pnlPatientSelection = new javax.swing.JPanel();
        btnClearSelection = new javax.swing.JButton("Clear selection");
        cmbPatientSelector = new javax.swing.JComboBox<Patient>();
        pnlPatientSelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Select patient"));
        DefaultComboBoxModel<Patient> model = 
                new DefaultComboBoxModel<>();
        cmbPatientSelector.setModel(model);

 
        cmbPatientSelector.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPatientSelectorActionPerformed(evt);
            }
        });
        
        btnClearSelection.addActionListener((ActionEvent e) -> btnClearSelectionActionPerformed());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(pnlPatientSelection);
        pnlPatientSelection.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(btnClearSelection)
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPatientSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPatientSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }                        

    private void cmbPatientSelectorActionPerformed(java.awt.event.ActionEvent evt) {
        ViewController.PatientViewControllerActionEvent action = null;
        if (this.cmbPatientSelector.getSelectedIndex()!=-1){
            getMyController().getDescriptor().getViewDescription().setPatient(
                    (Patient)this.cmbPatientSelector.getSelectedItem());
            switch(getMyViewType()){
                /*
                case PATIENT_SELECTION_VIEW:
                    action = ViewController.PatientViewControllerActionEvent.PATIENT_REQUEST;
                    break;
                    */
                case PATIENT_RECOVERY_SELECTION_VIEW:
                    action = ViewController.PatientViewControllerActionEvent.PATIENT_RECOVER_REQUEST;
                    break;
                default:
                    action = ViewController.PatientViewControllerActionEvent.NULL_PATIENT_REQUEST;
                    break;
            }
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED, 
                    action.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }  
    
    private void btnClearSelectionActionPerformed(){
        getMyController().getDescriptor().getViewDescription().setPatient(new Patient());
        this.cmbPatientSelector.setSelectedIndex(-1);
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientViewControllerActionEvent.NULL_PATIENT_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }


    // Variables declaration - do not modify                     
    private javax.swing.JComboBox<Patient> cmbPatientSelector;
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JPanel pnlPatientSelection;
    // End of variables declaration 
    
    
}

