/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import view.views.factory_methods.PatientFactoryMethod;
import view.views.factory_methods.ModalPatientSelectionViewFactoryMethod;
import view.views.factory_methods.ModalAppointmentCreatorEditorFactoryMethod;
import view.views.factory_methods.ModalNonSurgeryDayEditorFactoryMethod;
import view.views.factory_methods.PatientNotificationFactoryMethod;
import view.views.factory_methods.AppointmentScheduleFactoryMethod;
import view.views.factory_methods.ModalSurgeryDaysEditorFactoryMethod;
import view.views.factory_methods.ModalPatientNotificationEditorFactoryMethod;
import view.views.factory_methods.ImportProgressFactoryMethod;
import view.views.factory_methods.ModalEmptySlotScannerFactoryMethod;
import view.views.factory_methods.ModalCancelledAppointmentsViewFactoryMethod;
import view.views.factory_methods.ModalUnbookableAppointmentSlotEditorFactoryMethod;
import view.views.DesktopView;
import controller.Descriptor;
import controller.ViewController;
import view.views.interfaces.IView;
import view.views.interfaces.IViewInternalFrameListener;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author colin
 */
public abstract class View extends JInternalFrame
                           implements PropertyChangeListener,IView, IViewInternalFrameListener{
    private JDesktopPane desktop = null;
    private ViewController.ViewMode viewMode;
    private static Viewer viewer = null;
    private Boolean viewChangedSinceLastSaved = false;
    private Boolean isViewInitialised = false;
    
    private Viewer myViewType = null;
    private ViewController myController = null;
    private Descriptor viewDescriptor = null;
    
    public Boolean getIsViewInitialised(){
        return isViewInitialised;
    }
    
    protected void setIsViewInitialised(Boolean value){
        isViewInitialised = value;
    }

    public View(String title){
        super(title,true,true,true,true); 
    }
    
    public static enum Viewer { 
        SCHEDULE_VIEW,
        APPOINTMENT_CREATOR_VIEW,
        APPOINTMENT_CREATOR_EDITOR_VIEW,
        APPOINTMENT_EDITOR_VIEW,
        APPOINTMENT_REMINDERS_VIEW,
        CANCELLED_APPOINTMENTS_VIEW,
        EMPTY_SLOT_SCAN_CONFIGURATION_VIEW,
        EXPORT_PROGRESS_VIEW,
        MIGRATION_MANAGER_VIEW,
        NON_SURGERY_DAY_EDITOR_VIEW,
        PATIENT_RECOVERY_SELECTION_VIEW,
        PATIENT_SELECTION_VIEW,
        PATIENT_VIEW,
        PATIENT_NOTIFICATION_VIEW,
        PATIENT_NOTIFICATION_EDITOR_VIEW,
        UNACTIONED_PATIENT_NOTIFICATION_VIEW,
        SURGERY_DAY_EDITOR_VIEW,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW
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

    public static View factory(ViewController controller, Descriptor ed, DesktopView dtView){
        View result = null;
        switch(viewer){
            case SCHEDULE_VIEW:
                result = new AppointmentScheduleFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case CANCELLED_APPOINTMENTS_VIEW:
                result = new ModalCancelledAppointmentsViewFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case APPOINTMENT_CREATOR_VIEW:
                result = null;
                break;
            case APPOINTMENT_CREATOR_EDITOR_VIEW:
                result = new ModalAppointmentCreatorEditorFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case APPOINTMENT_EDITOR_VIEW:
                result = null;
                break;
            case EMPTY_SLOT_SCAN_CONFIGURATION_VIEW:
                result = new ModalEmptySlotScannerFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case EXPORT_PROGRESS_VIEW:
                result = new ImportProgressFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case MIGRATION_MANAGER_VIEW:
                result = new ImportProgressFactoryMethod(controller, ed, dtView).makeView(viewer);
            case PATIENT_VIEW:
                result = new PatientFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case PATIENT_NOTIFICATION_VIEW:
                result = new PatientNotificationFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case PATIENT_NOTIFICATION_EDITOR_VIEW:
                result = new ModalPatientNotificationEditorFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
                /*
            case PATIENT_SELECTION_VIEW:
                result = new ModalPatientSelectionViewFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
*/
            case PATIENT_RECOVERY_SELECTION_VIEW:
                result = new ModalPatientSelectionViewFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case NON_SURGERY_DAY_EDITOR_VIEW:
                result = new ModalNonSurgeryDayEditorFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case SURGERY_DAY_EDITOR_VIEW:
                result = new ModalSurgeryDaysEditorFactoryMethod(controller, ed, dtView).makeView(viewer);
                break;
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW:
                result = new ModalUnbookableAppointmentSlotEditorFactoryMethod(controller, dtView).makeView(viewer);
                break;
            default:
                JOptionPane.showMessageDialog(dtView, 
                        "Could not find the requested view (" + viewer.toString());
                break;
                
        }
        return result;
    }
    
    public Viewer getMyViewType(){
        return myViewType;
    }
    
    public JDesktopPane getDesktop(){
        return desktop;
    }
    
    public void setDesktop(JDesktopPane value){
        desktop = value;
    }

    /*
    public Descriptor getViewDescriptor(){
        return viewDescriptor;
    }
    */
    public void setMyViewType(Viewer value){
        myViewType = value;
    }

    protected ViewController getMyController(){
        return myController;
    }
    
    protected void setMyController(ViewController value){
        myController = value;
    }
    
    public void initialiseView(){
        
    }
}
