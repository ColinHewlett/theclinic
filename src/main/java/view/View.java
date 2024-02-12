/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;


import view.views.non_modal_views.*;
import view.views.modal_views.*;
import controller.ViewController;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.MenuComponent;
import java.awt.Toolkit;
import view.views.interfaces.IView;
import view.views.interfaces.IViewInternalFrameListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author colin
 */
public class View extends JInternalFrame
                           implements PropertyChangeListener,IView, IViewInternalFrameListener{
    private DesktopView desktopView = null;
    private static Viewer viewer = null;
    private Boolean viewChangedSinceLastSaved = false;
    private Boolean isViewInitialised = false;
    
    private Viewer myViewType = null;
    private ViewController myController = null;
    private View view = null;
    private ModalView modalView = null;
    
    static Font borderTitleFont = new Font("Segoe UI", 1, 12);
    static Color borderTitleColor = new Color(0,0,153);
    
    protected Color getBorderTitleColor(){
        return borderTitleColor;
    }
    
    protected Font getBorderTitleFont(){
        return borderTitleFont;
    }

    
    public Boolean getIsViewInitialised(){
        return isViewInitialised;
    }
    
    protected void setIsViewInitialised(Boolean value){
        isViewInitialised = value;
    }
    
    /**
     * 
     * @param viewer
     * @param controller
     * @param desktopView
     * @return; possible value returned
     * -- View if the view created is non modal
     * -- ModalView if the view created is modal
     * -- null if a view was not created
     */
    public View make(
            Viewer viewer,
            ViewController controller,
            DesktopView desktopView){
        setView(null);
        setModalView(null);
        switch(viewer){
            case EXPORT_PROGRESS_VIEW:
                setView(makeView(new ImportProgressView(viewer, controller,desktopView)));
                break;
            case MIGRATION_MANAGER_VIEW:
                setView(makeView(new ImportProgressView(viewer, controller, desktopView)));
                break;
            case PATIENT_VIEW:
                setView(makeView(new PatientView(viewer, controller, desktopView)));
                break;
            case NOTIFICATION_VIEW:
                setView(makeView(new NotificationView(viewer, controller, desktopView)));
                break;
            case NOTES_VIEW:
                setView(makeView(new NotesView(viewer, controller, desktopView)));
                break;
            case SCHEDULE_VIEW:
                setView(makeView(new ScheduleView(viewer, controller, desktopView)));
                break;
            case APPOINTMENTS_CANCELLED_VIEW:
                //result = new ModalAppointmentsCancelledFactoryMethod(controller,dtView).makeView(viewer);
                //setModalView(makeView(new ModalAppointmentsCancelledView(viewer, controller, desktopView)));
                setModalView(makeView(new ModalCancelledAppointmentsView(viewer, controller, desktopView)));
                break;
            case APPOINTMENT_EDITOR_VIEW:
                setModalView(makeView(new ModalAppointmentEditorView(viewer, controller, desktopView)));
                break;
            case APPOINTMENT_EMPTY_SLOT_SCAN_CONFIGURATION_VIEW:
                setModalView(makeView(new ModalEmptySlotScanConfigurationView(viewer, controller, desktopView)));
                break;
            case NOTIFICATION_EDITOR_VIEW:
                setModalView(makeView(new ModalNotificationEditorView(viewer, controller, desktopView)));
                break;
            case PATIENT_RECOVERY_SELECTION_VIEW:
                setModalView(makeView(new ModalPatientSelectionView(viewer, controller, desktopView)));
                break;
            /*
            case PATIENT_NOTES_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientNotesEditorView(viewer, controller,desktopView)));
                break;
            */
            case PATIENT_RECALL_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientRecallEditorView(viewer, controller,desktopView)));
                break;
            case PATIENT_PHONE_EMAIL_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientPhoneEmailEditorView(viewer, controller,desktopView)));
                break;
            case PATIENT_GUARDIAN_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientGuardianEditorView(viewer, controller,desktopView)));
                break;
            case PATIENT_SELECTION_VIEW:
                setModalView(makeView(new ModalPatientSelectionView(viewer, controller,desktopView)));
                break;
            case NON_SURGERY_DAY_EDITOR_VIEW:
                setModalView(makeView(new ModalNonSurgeryDayEditorView(viewer, controller, desktopView)));
                break;
            case SURGERY_DAY_EDITOR_VIEW:
                setModalView(makeView(new ModalSurgeryDaysEditorView(viewer,controller,desktopView)));
                break;
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW:
                setModalView(makeView(new ModalUnbookableAppointmentSlotEditorView(viewer, controller, desktopView)));
                break;
            case TEST_PATIENT_VIEW:
                setView(makeView(new TestPatientViewOld(viewer, controller, desktopView)));
                break;
            default:
                JOptionPane.showMessageDialog(desktopView, 
                        "Could not find the requested view (" + viewer.toString());
                setView(null);
                setModalView(null);
                break;
        }
        if ((getView()==null)&&(getModalView()==null)) return null;
        else if (getView()==null) return getModalView();
        else return getView();
    }

    public static enum Viewer { 
        
        APPOINTMENT_CREATOR_VIEW,
        APPOINTMENT_EDITOR_VIEW,
        APPOINTMENT_EMPTY_SLOT_SCAN_CONFIGURATION_VIEW,
        APPOINTMENTS_CANCELLED_VIEW,
        EXPORT_PROGRESS_VIEW,
        MIGRATION_MANAGER_VIEW,
        NON_SURGERY_DAY_EDITOR_VIEW,
        NOTES_VIEW,
        PATIENT_RECALL_EDITOR_VIEW,
        PATIENT_PHONE_EMAIL_EDITOR_VIEW,
        PATIENT_GUARDIAN_EDITOR_VIEW,
        //PATIENT_NOTES_EDITOR_VIEW,
        PATIENT_RECOVERY_SELECTION_VIEW,
        PATIENT_SELECTION_VIEW,
        PATIENT_VIEW,        
        NOTIFICATION_VIEW,
        NOTIFICATION_EDITOR_VIEW,
        SCHEDULE_VIEW,
        UNACTIONED_PATIENT_NOTIFICATION_VIEW,
        SURGERY_DAY_EDITOR_VIEW,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW,
        TEST_PATIENT_VIEW
    }
    
    protected Boolean getViewStatus(){
        return viewChangedSinceLastSaved;
    }
    
    protected void setViewStatus(Boolean value){
        viewChangedSinceLastSaved = value;
    }
    
    public static void setViewer(Viewer value){
        viewer = value;
    }
    
    public static Viewer getViewer(){
        return viewer;
    }

    
    
    public Viewer getMyViewType(){
        return myViewType;
    }
    
    public DesktopView getDesktopView(){
        return desktopView;
    }
    
    public void setDesktopView(DesktopView value){
        desktopView = value;
    }

    public void setMyViewType(Viewer value){
        myViewType = value;
    }

    public ViewController getMyController(){
        return myController;
    }
    
    public void setMyController(ViewController value){
        myController = value;
    }
    
    
    
    public void initialiseView(){
        
    }
    
    //public abstract void startModal();
    
    protected final View makeView(View view){
        view.getMyController().setView(view);
        view.getMyController().getView().initialiseView();
        view.getMyController().centreViewOnDesktop(view.getDesktopView(), view.getMyController().getView());
        view.getDesktopView().getDeskTop().add(view.getMyController().getView());
        view.toFront();
        
        try{
            view.setSelected(true);
        }catch(PropertyVetoException ex){
            
        }

        return view;
    }
    
    protected final ModalView makeView(ModalView view){
        view.getMyController().setModalView(view);
        view.setLayer(JLayeredPane.MODAL_LAYER);
        view.getMyController().getModalView().initialiseView();
        view.getMyController().centreViewOnDesktop(view.getDesktopView(), view.getMyController().getModalView());
        view.getDesktopView().getDeskTop().add(view.getMyController().getModalView());
        view.toFront();
        startModal(view);
        return view;
    }
    
    protected void startModal(View view){
        // We need to add an additional glasspane-like component directly
        // below the frame, which intercepts all mouse events that are not
        // directed at the frame itself.
        JPanel modalInterceptor = new JPanel();
        modalInterceptor.setOpaque(false);
        JLayeredPane lp = JLayeredPane.getLayeredPaneAbove(view);
        lp.setLayer(modalInterceptor, JLayeredPane.MODAL_LAYER.intValue());
        modalInterceptor.setBounds(0, 0, lp.getWidth(), lp.getHeight());
        modalInterceptor.addMouseListener(new MouseAdapter(){});
        modalInterceptor.addMouseMotionListener(new MouseMotionAdapter(){});
        lp.add(modalInterceptor);
        view.toFront();
        view.setVisible(true);

        try{
            view.setSelected(true);
        }catch(PropertyVetoException ex){
            
        }
        // We need to explicitly dispatch events when we are blocking the event
        // dispatch thread.
        EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        try {
            while (! view.isClosed())       {
                if (EventQueue.isDispatchThread())    {
                    // The getNextEventMethod() issues wait() when no
                    // event is available, so we don't need do explicitly wait().
                    AWTEvent ev = queue.getNextEvent();
                    // This mimics EventQueue.dispatchEvent(). We can't use
                    // EventQueue.dispatchEvent() directly, because it is
                    // protected, unfortunately.
                    if (ev!=null){
                        if (ev instanceof ActiveEvent )  ((ActiveEvent) ev).dispatch();
                        //if (ev instanceof ActiveEvent event)  event.dispatch();
                        //else if (ev.getSource() instanceof Component component)  component.dispatchEvent(ev);
                        //else if (ev.getSource() instanceof MenuComponent menuComponent)  menuComponent.dispatchEvent(ev);
                        else if (ev.getSource() instanceof Component)  ((Component) ev.getSource()).dispatchEvent(ev);
                        else if (ev.getSource() instanceof MenuComponent)  ((MenuComponent) ev.getSource()).dispatchEvent(ev);
                        // Other events are ignored as per spec in
                        // EventQueue.dispatchEvent
                    }
                    
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
            Container parent = view.getParent();
            if (parent != null) parent.remove(view);
        }
    }
    
    public final View getView(){
        return view;
    }
    
    public final void setView(View value){
        view = value;
    }
    

    public  ModalView getModalView(){
        return modalView;
    }
    
    private final void setModalView(ModalView value){
        modalView = value;
    }

    @Override
    public void propertyChange(PropertyChangeEvent ex){
        
    }
}
