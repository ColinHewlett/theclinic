/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colinhewlettsolutions.client.view;


/*import view.views.modal_views.ModalPatientMedicalHistory1EditorView;*/
import colinhewlettsolutions.client.view.views.modal_views.UserSystemWideSettingsView;
import colinhewlettsolutions.client.view.views.modal_views.ModalUserScheduleListSettingsView;
import colinhewlettsolutions.client.view.views.modal_views.ModalUserScheduleDiarySettingsView;
import colinhewlettsolutions.client.view.views.non_modal_views.LoginNewPasswordEditorView;
import colinhewlettsolutions.client.view.views.non_modal_views.LoginOldPasswordCheckView;
import colinhewlettsolutions.client.view.views.non_modal_views.UserLoginView;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.view.views.non_modal_views.*;
import colinhewlettsolutions.client.view.views.modal_views.*;
import colinhewlettsolutions.client.view.views.dialog_views.*;
import colinhewlettsolutions.client.controller.ViewController;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.MenuComponent;
import java.awt.Toolkit;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import colinhewlettsolutions.client.view.interfaces.IView;
import colinhewlettsolutions.client.view.interfaces.IViewInternalFrameListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
    private ArrayList<JCheckBox> options = null;
    
    
    
    static Font borderTitleFont = new Font("Segoe UI", 1, 12);
    static Color borderTitleColor = new Color(0,0,153);
    
    public ArrayList<JCheckBox> getOptions(){
        return options;
    }
    
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
     * @param <T>
     * @param viewer
     * @param myParentView
     * @param items
     * @param dialogCaption
     * @param selectorCaption
     * @return 
     */
    public <T> View make(
            Viewer viewer,
            View myParentView,
            List<T> items, 
            String dialogCaption, 
            String selectorCaption){
        DialogView result = null;
        switch(viewer){
            case COMPOSITE_SCAN_COUNT_DIALOG:
            case EARLY_BOOKING_START_EDITOR_DIALOG:
            case EXTEND_SHIFT_BOOKING_DIALOG:
            case LATE_BOOKING_END_EDITOR_DIALOG:
            case PATIENT_SELECTION_DIALOG:
                //setDialogView(makeView(new DialogUsingGenericSelector(viewer, myParentView, items, dialogCaption, selectorCaption)));
                result = makeView(new DialogUsingGenericSelector(viewer, myParentView, items, dialogCaption, selectorCaption));
                myParentView.setDialogView(result);
                break;
        }
        return result;
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
            case APPOINTMENTS_CANCELLED_VIEW:
                setModalView(makeView(new ModalCancelledAppointmentsView(viewer, controller, desktopView)));
                break;
            case APPOINTMENT_EMPTY_SLOT_SCAN_CONFIGURATION_VIEW:
                setModalView(makeView(new ModalEmptySlotScanConfigurationView(viewer, controller, desktopView)));
                break;
            case APPOINTMENT_TREATMENT_VIEW:
                setModalView(makeView(new ModalAppointmentTreatmentView(viewer, controller, desktopView)));
                break;
            case ARCHIVED_PATIENTS_VIEW:
                setView(makeView(new ArchivedPatientsView(viewer, controller, desktopView)));
                break;
            case BOOKABLE_SLOT_SCANNER_VIEW://ModalBookableSlotScannerView
                setModalView(makeView(new ModalBookableSlotScannerView(viewer, controller,desktopView)));
                break;
            case CLINICAL_NOTE_VIEW:
                setModalView(makeView(new ModalClinicalNoteView(viewer, controller, desktopView)));
                break;
            case EXPORT_PROGRESS_VIEW:
                setView(makeView(new DataMigrationProgressView(viewer, controller,desktopView)));
                break;
            case IMAGE_VIEWER:
                setView(makeView(new ImageViewer(viewer, controller, desktopView)));
                break;
            case LOGIN_NEW_PASSWORD_EDITOR_VIEW:
                setView(makeView(new LoginNewPasswordEditorView(viewer, controller, desktopView)));
                break;
            case LOGIN_OLD_PASSWORD_CHECK_VIEW:
                setView(makeView(new LoginOldPasswordCheckView(viewer, controller, desktopView)));
                break;
            case LOGIN_VIEW:
                setView(makeView(new UserLoginView(viewer, controller, desktopView)));
                break;
            case MEDICAL_CONDITION_VIEW:
                setView(makeView(new MedicalConditionView(viewer, controller, desktopView)));
                break;
            case MIGRATION_MANAGER_VIEW:
                setView(makeView(new DataMigrationProgressView(viewer, controller, desktopView)));
                break;
            case NON_SURGERY_DAY_EDITOR_VIEW:
                setModalView(makeView(new ModalNonSurgeryDayEditorView(viewer, controller, desktopView)));
                break;
            case NOTIFICATION_EDITOR_VIEW:
                setModalView(makeView(new ModalNotificationEditorView(viewer, controller, desktopView)));
                break;
            case NOTIFICATION_VIEW:
                setView(makeView(new PatientNotificationView(viewer, controller, desktopView)));
                break;
            case RECALL_PATIENTS_VIEW:
                setView(makeView(new RecallPatientsView(viewer, controller, desktopView)));
                break; 
                
            case PATIENT_APPOINTMENT_DATA_VIEW:
                setView(makeView(new PatientAppointmentDataView(viewer, controller, desktopView)));
                break;
            case PATIENT_DOCTOR_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientDoctorEditorView(viewer, controller,desktopView)));
                break;
            case PATIENT_GBT_RECALL_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientGBTRecallEditorView(viewer, controller,desktopView)));
                break; 
            case PATIENT_GUARDIAN_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientGuardianEditorView(viewer, controller,desktopView)));
                break;
            case PATIENT_INVOICE_VIEW:
                setView(makeView(new PatientInvoiceView(viewer, controller,desktopView)));
                break;
            case PATIENT_MEDICAL_HISTORY_VIEW:
                setView(makeView(new PatientMedicalHistoryView(viewer, controller, desktopView)));
                break;
            case PATIENT_MEDICATION_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientMedicationEditorView(viewer, controller,desktopView)));
                break;
            case PATIENT_PHONE_EMAIL_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientPhoneEmailEditorView(viewer, controller,desktopView)));
                break;
            case PATIENT_QUESTIONNAIRE_VIEW:;
                setView(makeView(new PatientQuestionnaireView2(viewer, controller, desktopView)));
                break;
            case PATIENT_RECALL_EDITOR_VIEW:
                setModalView(makeView(new ModalPatientRecallEditorView(viewer, controller,desktopView)));
                break;
            case PATIENT_RECOVERY_SELECTION_VIEW:
                setModalView(makeView(new ModalPatientSelectionView(viewer, controller, desktopView)));
                break;
            case PATIENT_SELECTION_VIEW:
                setModalView(makeView(new ModalPatientSelectionView(viewer, controller,desktopView)));
                break; 
            case PATIENT_VIEW:
                setView(makeView(new PatientView(viewer, controller,desktopView)));
                break;
            case PATIENT_VIEW_TEST:
                setView(makeView(new PatientViewTest(viewer, controller,desktopView)));
                break;    
            case MODAL_DATE_DIALOG:
                setModalView(makeView(new ModalDateDialog(viewer, controller,desktopView)));
                break;
            case MODAL_DOCUMENT_STORE_VIEW:
                setModalView(makeView(new ModalDocumentStoreView(viewer, controller,desktopView)));
                break;
            case MODAL_PROGRESS_VIEW:
                setModalView(makeView(new ModalProgressView(viewer, controller,desktopView)));
                break;
            case SCHEDULE_DIARY_VIEW:
                setView(makeView(new ScheduleDiaryView(viewer, controller, desktopView)));
                break;
            case SCHEDULE_EDITOR_VIEW:
                setModalView(makeView(new ModalScheduleEditorView(viewer, controller, desktopView)));
                break;
            case SCHEDULE_LIST_VIEW:
                setView(makeView(new ScheduleListView(viewer, controller, desktopView)));
                break;
            case SURGERY_DAY_EDITOR_VIEW:
                setModalView(makeView(new ModalSurgeryDaysEditorView(viewer,controller,desktopView)));
                break;
            case TO_DO_EDITOR_VIEW:
                setModalView(makeView(new ModalToDoEditorView(viewer, controller, desktopView)));
                break;
            case TO_DO_VIEW:
                setView(makeView(new ToDoView(viewer, controller, desktopView)));
                break;
            case TREATMENT_VIEW:
                setView(makeView(new TreatmentView(viewer, controller, desktopView)));
                break;
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW:
                setModalView(makeView(new ModalUnbookableAppointmentSlotEditorView(viewer, controller, desktopView)));
                break;
            case UNBOOKABLE_SLOT_SCANNER_VIEW:
                setModalView(makeView(new ModalUnbookableSlotScannerView(viewer, controller,desktopView)));
                break;
            case USER_SCHEDULE_DIARY_SETTINGS_VIEW:
                setModalView(makeView(new ModalUserScheduleDiarySettingsView(viewer, controller, desktopView)));
                break;
            case USER_SCHEDULE_LIST_SETTINGS_VIEW:
                setModalView(makeView(new ModalUserScheduleListSettingsView(viewer, controller, desktopView)));
                break;
            case USER_SYSTEM_WIDE_SETTINGS_VIEW: 
                setView(makeView(new UserSystemWideSettingsView(viewer, controller, desktopView)));
                break;
            
            
            
            
            case EARLY_BOOKING_START_EDITOR_DIALOG:
            case LATE_BOOKING_END_EDITOR_DIALOG:
                //setModalView(makeView(new DialogUsingGenericSelectorx(viewer, controller, desktopView)));
                break;
            case NOTE_TAKER:
                //setModalView(makeView(new ModalNoteTaker(viewer, controller, desktopView)));
                break;    
            case CHECKBOX_LIST_VIEW:
                setModalView(makeView(new ModalCheckBoxListView(
                        viewer, 
                        controller, 
                        desktopView)));
                break;
            case TEST_PATIENT_VIEW:
                //setView(makeView(new TestPatientViewOld(viewer, controller, desktopView)));
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
        APPOINTMENTS_CANCELLED_VIEW,
        APPOINTMENT_CREATOR_VIEW,
        APPOINTMENT_EMPTY_SLOT_SCAN_CONFIGURATION_VIEW,
        APPOINTMENT_TREATMENT_VIEW,
        ARCHIVED_PATIENTS_VIEW,
        BOOKABLE_SLOT_SCANNER_VIEW,
        CLINICAL_NOTE_VIEW,
        CHECKBOX_LIST_VIEW,
        COMPOSITE_SCAN_COUNT_DIALOG,
        EARLY_BOOKING_START_EDITOR_DIALOG,
        EXPORT_PROGRESS_VIEW,
        EXTEND_SHIFT_BOOKING_DIALOG,
        IMAGE_VIEWER,
        LATE_BOOKING_END_EDITOR_DIALOG,
        LATE_BOOKING_END_EDITOR_VIEW,
        LOGIN_OLD_PASSWORD_CHECK_VIEW,
        LOGIN_NEW_PASSWORD_EDITOR_VIEW,
        LOGIN_VIEW,
        MEDICAL_CONDITION_VIEW,
        MIGRATION_MANAGER_VIEW,
        NON_SURGERY_DAY_EDITOR_VIEW,
        NOTE_TAKER,
        NOTES_VIEW,
        NOTIFICATION_EDITOR_VIEW,
        NOTIFICATION_VIEW,
        PATIENT_APPOINTMENT_DATA_VIEW,
        PATIENT_INVOICE_VIEW,
        PATIENT_DOCTOR_EDITOR_VIEW,
        PATIENT_GBT_RECALL_EDITOR_VIEW,
        PATIENT_GUARDIAN_EDITOR_VIEW,
        PATIENT_MEDICAL_HISTORY_VIEW,
        PATIENT_MEDICATION_EDITOR_VIEW,
        PATIENT_PHONE_EMAIL_EDITOR_VIEW,
        PATIENT_QUESTIONNAIRE_VIEW,
        PATIENT_RECALL_EDITOR_VIEW,
        PATIENT_RECOVERY_SELECTION_VIEW,
        PATIENT_SELECTION_DIALOG,
        PATIENT_SELECTION_VIEW,
        PATIENT_VIEW,
        PATIENT_VIEW_TEST,
        RECALL_PATIENTS_VIEW,
        MODAL_DATE_DIALOG,
        MODAL_PROGRESS_VIEW,
        MODAL_DOCUMENT_STORE_VIEW,
        SCHEDULE_DIARY_VIEW,
        SCHEDULE_EDITOR_VIEW,
        SCHEDULE_LIST_VIEW,
        SURGERY_DAY_EDITOR_VIEW,
        TREATMENT_VIEW,
        UNACTIONED_PATIENT_NOTIFICATION_VIEW,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW,
        UNBOOKABLE_SLOT_SCANNER_VIEW,
        TEST_PATIENT_VIEW,
        TO_DO_EDITOR_VIEW,
        TO_DO_VIEW,
        USER_SCHEDULE_DIARY_SETTINGS_VIEW,
        USER_SCHEDULE_LIST_SETTINGS_VIEW,
        USER_SYSTEM_WIDE_SETTINGS_VIEW
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
    
    private JDesktopPane parentViewDesktopPane = null;
    public JDesktopPane getParentDesktopPane(){
        return parentViewDesktopPane;
    }
    public void setParentDesktopPane(JDesktopPane value){
        parentViewDesktopPane = value;
    }

    public ViewController getMyController(){
        return myController;
    }
    
    public void setMyController(ViewController value){
        myController = value;
    }
    
    
    
    public void initialiseView(){}
    
    //public abstract void startModal();
    
    protected final View makeView(View view){
        /*view.getMyController().setView(view);
        view.getDesktopView().getDeskTop().add(view.getMyController().getView());
        view.getMyController().getView().initialiseView();
        //System.out.println("PatientView size: " + getSize());
        //System.out.println("PatientView preferred size: " + getPreferredSize());
        ViewController.centerInternalFrame(view.getDesktopView().getDeskTop(), view);
        setVisible(true);
        //view.toFront;
        debugFrameState(view,view.getDesktopView().getDeskTop());

        //view.getDesktopView().getDeskTop().revalidate();  // <-- ensure layout happens
        //view.getDesktopView().getDeskTop().repaint();     // <-- ensure it’s drawn
        //SwingUtilities.invokeLater(() -> view.moveToFront());
        //try {
        //    view.setSelected(true);  // forces activation, triggers paint
        //} catch (PropertyVetoException e) {
        //    e.printStackTrace();
        //}

        //return view;*/
        /*
        view.getMyController().setView(view);
        view.getMyController().getView().initialiseView();
        ViewController.centerInternalFrame(view.getDesktopView().getDeskTop(), view);
        view.getDesktopView().getDeskTop().add(view.getMyController().getView());
        view.toFront();
        debugFrameState(view,view.getDesktopView().getDeskTop());
        */
        
        view.getMyController().setView(view);
    JDesktopPane desktop = view.getDesktopView().getDeskTop();

    // Add before setting visibility
    desktop.add(view, JDesktopPane.DEFAULT_LAYER);

    // Initialise view *after* adding
    view.initialiseView();

    // Ensure a sensible size
    //if (view.getWidth() <= 0 || view.getHeight() <= 0)
    //    view.setSize(857, 600);

    // Center after sizing
    ViewController.centerInternalFrame(desktop, view);
/*
    // Defer visibility and selection until Swing has added the frame
    SwingUtilities.invokeLater(() -> {
        view.setVisible(true);       // trigger addNotify()
        desktop.revalidate();
        desktop.repaint();

        try {
            view.setSelected(true);  // activates frame
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        System.out.println("After invokeLater: isShowing=" + view.isShowing());
    });
*/
        // Wait until desktop is actually displayable before making frame visible
        /*
        Runnable showFrame = () -> {
            view.setVisible(true);
            try {
                view.setSelected(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
            desktop.revalidate();
            desktop.repaint();
            System.out.println(view.getTitle() + " now showing: " + view.isShowing());
        };

        if (desktop.isDisplayable()) {
            SwingUtilities.invokeLater(showFrame);
        } else {
            // If desktop isn't ready yet, wait for it to become displayable
            desktop.addHierarchyListener(e -> {
                if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.DISPLAYABILITY_CHANGED) != 0
                        && desktop.isDisplayable()) {
                    SwingUtilities.invokeLater(showFrame);
                }
            });
        }*/
        return view;
    }
    
    protected final View makeViewx(View view) {
        view.getMyController().setView(view);
        var desktop = view.getDesktopView().getDeskTop();

        desktop.add(view); // add first

        view.initialiseView(); // set up contents

        // ensure proper sizing
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            view.setSize(857, 600);
        }

        ViewController.centerInternalFrame(desktop, view);

        SwingUtilities.invokeLater(() -> {
            desktop.revalidate();
            desktop.repaint();
            view.setVisible(true);  // <- now safe, layout is valid
            try { view.setSelected(true); } catch (Exception ignore) {}
            System.out.println(view.getTitle() + " isShowing()=" + view.isShowing());
        });

        return view;
    }
    
    protected final View makeViewxx(View view) {
        view.getMyController().setView(view);
        final JDesktopPane desktop = view.getDesktopView().getDeskTop();

        Runnable addAndShow = () -> {
            // add once (avoid duplicate add if already parented)
            if (view.getParent() == null) {
                desktop.add(view, JDesktopPane.DEFAULT_LAYER);
            }

            // populate the view before showing
            view.initialiseView();

            // ensure sensible size
            if (view.getWidth() <= 0 || view.getHeight() <= 0) {
                view.setSize(857, 600);
            }

            ViewController.centerInternalFrame(desktop, view);

            // Final visibility/activation on next EDT turn
            SwingUtilities.invokeLater(() -> {
                view.setVisible(true);
                try { if (!view.isSelected()) view.setSelected(true); }
                catch (PropertyVetoException ex) { ex.printStackTrace(); }
                desktop.revalidate();
                desktop.repaint();
                view.moveToFront();
                System.out.println(">>> " + view.getTitle() + " isShowing(): " + view.isShowing());
            });
        };

        if (desktop.isDisplayable()) {
            // Desktop already ready — add now (on EDT)
            SwingUtilities.invokeLater(addAndShow);
        } else {
            // Desktop not ready — wait until it becomes displayable, then add
            java.awt.event.HierarchyListener l = new java.awt.event.HierarchyListener() {
                @Override
                public void hierarchyChanged(java.awt.event.HierarchyEvent e) {
                    // listen for displayability change
                    if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 && desktop.isDisplayable()) {
                        desktop.removeHierarchyListener(this); // fire once
                        SwingUtilities.invokeLater(addAndShow);
                    }
                }
            };
            desktop.addHierarchyListener(l);
        }

        return view;
    }
    
    private void debugFrameState(JInternalFrame f, JDesktopPane desktop) {
        System.out.println("---- debugFrameState ----");
        System.out.println("title: " + f.getTitle());
        System.out.println("isVisible: " + f.isVisible());
        System.out.println("isShowing: " + f.isShowing());
        System.out.println("isDisplayable: " + f.isDisplayable());
        System.out.println("isSelected: " + f.isSelected());
        System.out.println("isIcon: " + f.isIcon());
        System.out.println("isMaximum: " + f.isMaximum());
        System.out.println("bounds: " + f.getBounds());
        System.out.println("rootPane: " + f.getRootPane());
        System.out.println("content count: " + f.getContentPane().getComponentCount());
        System.out.println("ui: " + f.getUI());
        System.out.println("desktop layer: " + desktop.getLayer(f));
        System.out.println("desktop children:");
        for (Component c : desktop.getComponents()) {
            System.out.println("  " + c.getClass().getName() + " bounds=" + c.getBounds() + " visible=" + c.isVisible());
        }
        System.out.println("-------------------------");
    }

    protected final DialogView makeView(DialogView view){
        view.setLayer(JLayeredPane.MODAL_LAYER);
        view.initialiseView();
        ViewController.centerInternalFrame(view.getMyParentView().getDesktopPane(), view);
        view.getMyParentView().getDesktopPane().add(view);
        view.toFront();
        startModal(view);
        return view;
    }
    
    protected final ModalView makeView(ModalView view){
        view.getMyController().setModalView(view);
        view.setLayer(JLayeredPane.MODAL_LAYER);
        view.getMyController().getModalView().initialiseView();
        ViewController.centerInternalFrame(view.getDesktopView().getDeskTop(), view.getMyController().getModalView());
        view.getDesktopView().getDeskTop().add(view.getMyController().getModalView());
        view.toFront();
        startModal(view);
        return view;
    }
    
    protected void startModal(View view){
        // We need to add an additional glasspane-like component directly
        // below the frame, which intercepts all mouse events that are not
        // directed at the frame itself.
        //view.setLayer(JLayeredPane.MODAL_LAYER);
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
    
    private void setModalView(ModalView value){
        modalView = value;
    }
    
    private DialogView dialogView = null;
    public  DialogView getDialogView(){
        return dialogView;
    }
    
    public void setDialogView(DialogView value){
        dialogView = value;
    }

    
    public void propertyChange(PropertyChangeEvent ex){};
    
    
    public void doOpenDocumentForPrinting(String filepath){
        File file = new File(filepath);
        
        if (!Desktop.isDesktopSupported()) {
            System.out.println("Desktop API is not supported on this system.");
            return;
        }

        // Get the Desktop instance
        Desktop desktop = Desktop.getDesktop();
        try {
            // Open the document with the default application
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
