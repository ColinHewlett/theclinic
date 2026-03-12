/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package theclinic.view.views.non_modal_views;

import theclinic.controller.SystemDefinition;
import theclinic.controller.ViewController;
import theclinic.controller.ImageViewerViewController;
import theclinic.model.entity.Patient;
import theclinic.view.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author colin
 */
public class ImageViewer extends View implements ActionListener{
    enum Actions{
        REQUEST_CLOSE_VIEW,
        REQUEST_NEXT_IMAGE,
        REQUEST_PREVIOUS_IMAGE,
        REQUEST_SWITCH_PAGES
    }
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ImageViewer(View.Viewer myViewType, 
            ViewController myController, DesktopView desktopView) {
        setMyViewType(myViewType);
        setMyController(myController); 
        setDesktopView(desktopView);
        initComponents();
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Actions action = Actions.valueOf(e.getActionCommand());
        switch (action){
            case REQUEST_CLOSE_VIEW ->{
                try{
                    setClosed(true);
                }catch(PropertyVetoException ex){
                    
                }
                break;
            }
            case REQUEST_NEXT_IMAGE ->{
                setImageIndex(getImageIndex() + 1);
                populateViewWithImage(getImagesForView().get(getImageIndex()));
                break;
            }
            case REQUEST_PREVIOUS_IMAGE ->{
                setImageIndex(getImageIndex() - 1);
                populateViewWithImage(getImagesForView().get(getImageIndex()));
                break;
            }
            case REQUEST_SWITCH_PAGES ->{
                doSwitchPages();
                break;
            }
        }
        updateDisplayStatus();
    }
    
    @Override
    public void initialiseView(){
        setResizable(true);
        setClosable(true);
        setVisible(true);
        
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        ArrayList<File> documents = 
                (ArrayList<File>)getMyController().getDescriptor().
                        getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_DOCUMENT);
        setImagesForView(documents);
        setImageIndex(0);
        populateViewWithImage(getImagesForView().get(getImageIndex()));
        
        this.btnNext.setActionCommand(Actions.REQUEST_NEXT_IMAGE.toString());
        this.btnPrevious.setActionCommand(Actions.REQUEST_PREVIOUS_IMAGE.toString());
        this.btnNext.addActionListener(this);
        this.btnPrevious.addActionListener(this);
        this.mniCloseView.setActionCommand(Actions.REQUEST_CLOSE_VIEW.toString());
        this.mniCloseView.addActionListener(this);
        this.mniSwitchPages.setActionCommand(Actions.REQUEST_SWITCH_PAGES.toString());
        this.mniSwitchPages.addActionListener(this);
        addInternalFrameListener(); 
        setTitle(getDateFromFilename().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                " medical history and questionnaire for " + getPatient().toString());
    }
    
    private LocalDate getDateFromFilename(){
        LocalDate result = null;
        File file = getImagesForView().get(0);
        String filename = file.getName();
        filename = filename.substring(0,10);
        result = LocalDate.parse(filename, DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        return result;
    }
    
    private void doSwitchPages(){
        ArrayList<File> switchedDocuments = new ArrayList<>();
        switchedDocuments.add(getImagesForView().get(1));
        switchedDocuments.add(getImagesForView().get(0));
        setImagesForView(switchedDocuments);
        setImageIndex(0);
        populateViewWithImage(getImagesForView().get(getImageIndex()));
        updateDisplayStatus();
    }
    
    private InternalFrameAdapter internalFrameAdapter = null;
    /**
     * If user exits via the window 'X' click view controller is informed of the event
     */
    public void addInternalFrameListener(){
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosing(InternalFrameEvent e) {
                ImageViewer.this.removeInternalFrameListener(internalFrameAdapter);
                ActionEvent actionEvent = new ActionEvent(
                        ImageViewer.this,ActionEvent.ACTION_PERFORMED,
                        ImageViewerViewController.Actions.
                                VIEW_CLOSE_NOTIFICATION.toString());
                ImageViewer.this.getMyController().actionPerformed(actionEvent);   
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
    }
    
    private void updateDisplayStatus(){
        if (isFirstImageDisplayed())
            btnPrevious.setEnabled(false);
        else
            btnPrevious.setEnabled(true);
        if (isLastImageDisplayed())
            btnNext.setEnabled(false);
        else 
            btnNext.setEnabled(true);
    }
    
    private int index = 0;
    private void setImageIndex(int value){
        index = value;
        switch(index){
            case 0 ->{
                lblPageNumber.setText("Page 1 (medical questionnaire)");
                break;
            }
            case 1 ->{
                lblPageNumber.setText("Page 2 (medical history)");
                break;
            }   
        }
    }
    private int getImageIndex(){
        return index;
    }
    
    private boolean isFirstImageDisplayed(){
        return getImageIndex()==0;
    }
    
    private boolean isLastImageDisplayed(){
        return getImageIndex() == getImagesForView().size()-1;
    }
    
    private ArrayList<File> imagesForView;
    private ArrayList<File> getImagesForView(){
        return imagesForView;
    }
    private void setImagesForView(ArrayList<File> value){
        imagesForView = value;
    }
          
    
    private Patient getPatient(){
        return (Patient)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.PATIENT);
    }
    
    private void populateViewWithImage(File imageFile) {
        try {
            // Load the image
            BufferedImage img = ImageIO.read(imageFile);
            if (img == null) {
                JOptionPane.showMessageDialog(this, "File is not a valid image.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            // Wrap the image in a label
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imageLabel.setOpaque(true);
            
            this.scrImageView.setViewportView(imageLabel);


            // Size frame to fit image (up to a limit)
            int width = Math.min(img.getWidth() + 20, 800);
            int height = Math.min(img.getHeight() + 40, 800);
            this.setSize(width+30, height+60);
            
            updateDisplayStatus();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading image:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrImageView = new javax.swing.JScrollPane();
        pnlImageView = new javax.swing.JPanel();
        btnNext = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        lblPageNumber = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuActions = new javax.swing.JMenu();
        mniSwitchPages = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        btnNext.setText(">");

        btnPrevious.setText("<");

        lblPageNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPageNumber.setText("jLabel1");

        javax.swing.GroupLayout pnlImageViewLayout = new javax.swing.GroupLayout(pnlImageView);
        pnlImageView.setLayout(pnlImageViewLayout);
        pnlImageViewLayout.setHorizontalGroup(
            pnlImageViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlImageViewLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnPrevious)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(lblPageNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                .addComponent(btnNext)
                .addGap(20, 20, 20))
        );
        pnlImageViewLayout.setVerticalGroup(
            pnlImageViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlImageViewLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlImageViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNext)
                    .addComponent(btnPrevious)
                    .addComponent(lblPageNumber))
                .addGap(6, 6, 6))
        );

        mnuActions.setText("Actions");

        mniSwitchPages.setText("Switch pages");
        mnuActions.add(mniSwitchPages);
        mnuActions.add(jSeparator1);

        mniCloseView.setText("Close view");
        mnuActions.add(mniCloseView);

        jMenuBar1.add(mnuActions);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrImageView)
                    .addComponent(pnlImageView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlImageView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(scrImageView, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel lblPageNumber;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniSwitchPages;
    private javax.swing.JMenu mnuActions;
    private javax.swing.JPanel pnlImageView;
    private javax.swing.JScrollPane scrImageView;
    // End of variables declaration//GEN-END:variables
}
