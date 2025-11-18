/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.view.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author colin
 */
public class ImageViewer extends View implements ActionListener{
    enum Actions{
        REQUEST_NEXT_IMAGE,
        REQUEST_PREVIOUS_IMAGE
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
        }
        updateDisplayStatus();
    }
    
    @Override
    public void initialiseView(){
        setResizable(true);
        setClosable(true);
        setVisible(true);
        setTitle("Scanned medical history for " + getPatient().toString());
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setImageIndex(0);
        populateViewWithImage(getImagesForView().get(getImageIndex()));
        this.btnNext.setActionCommand(Actions.REQUEST_NEXT_IMAGE.toString());
        this.btnPrevious.setActionCommand(Actions.REQUEST_PREVIOUS_IMAGE.toString());
        this.btnNext.addActionListener(this);
        this.btnPrevious.addActionListener(this);
        updateDisplayStatus();
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
        lblPageNumber.setText("Page " + (index+1));
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
    
    private ArrayList<File> getImagesForView(){
        return (ArrayList<File>)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.PATIENT_DOCUMENT);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                .addComponent(lblPageNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
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
                .addComponent(scrImageView, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JLabel lblPageNumber;
    private javax.swing.JPanel pnlImageView;
    private javax.swing.JScrollPane scrImageView;
    // End of variables declaration//GEN-END:variables
}
