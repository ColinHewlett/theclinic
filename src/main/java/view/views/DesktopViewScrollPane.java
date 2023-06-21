/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views;

/**
 *
 * @author colin
 */
import view.views.DesktopView;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JViewport;

public class DesktopViewScrollPane extends JScrollPane {

    private JDesktopPane desktopPane;
    private InternalFrameComponentListener componentListener;
    private DesktopView desktopView;

    /**
     * 
     * @param desktopPane
     * @param view 
     */
    public DesktopViewScrollPane(JDesktopPane desktopPane, DesktopView view) {
        desktopView = view;
        componentListener = new InternalFrameComponentListener();

        this.desktopPane = desktopPane;
        //setDesktopSize(view.getSize());
        desktopPane.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                onComponentAdded(e);
                DesktopViewScrollPane.this.desktopView.doSendViewChangedEvent();
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                onComponentRemoved(e);
                DesktopViewScrollPane.this.desktopView.doSendViewChangedEvent();
                
                /**
                 * 7/12/2022 update signals no internal frames 
                 * -- and no desktop icons
                 */
                if (getAllFrames().length == 0){
                        desktopView.doSetClinicLogoViewMode();
                        
                    }
                }
        });
        setViewportView(desktopPane);

        // set some defaults
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //resizeDesktop();
    }

    private void onComponentRemoved(ContainerEvent event) {
        
        Component removedComponent = event.getChild();
        if (removedComponent instanceof JInternalFrame)
            removedComponent.removeComponentListener(componentListener);
    }

    private void onComponentAdded(ContainerEvent event) {
        int test;
        Component addedComponent = event.getChild();
        if (addedComponent instanceof JInternalFrame)
        {
            addedComponent.addComponentListener(componentListener);
            //resizeDesktop();
            shortenResizeDesktop();
        }
        /**
         * 12/02/2022 7:41
         * condition added which checks if only one JInternalFrame attached to scroll pane
         * -- assumption when last internal frame iconified before the internal frame is removed
         * -- this method entered because JDesktopIcon has been added to scrollpane
         * -- HOPE THIS SEQUENCE IS ALWAYS THE CASE
         */
        else if (addedComponent instanceof JDesktopIcon){
            test = getAllFrames().length;
            if (getAllFrames().length == 1){
                desktopView.doSetDesktopViewMode();
            }
        }
       
    }


    /**
     * returns all internal frames placed upon the desktop
     *
     * @return a JInternalFrame array containing references to the internal frames
     */
    public JInternalFrame[] getAllFrames() {
        return desktopPane.getAllFrames();
    }

    /**
     * sets the preferred size of the desktop
     *
     * @param dim a Dimension object representing the desired preferred size
     */
    public void setDesktopSize(Dimension dim) {
        desktopPane.setPreferredSize(dim);
        desktopPane.revalidate();
    }

    private void shortenResizeDesktop(){
        Rectangle viewPort = getViewport().getViewRect();

        int maxX = viewPort.width + viewPort.x, maxY = viewPort.height + viewPort.y;
        int minX = viewPort.x, minY = viewPort.y;

        // determine the min/max extents of all internal frames

        JInternalFrame frame = null;
        JInternalFrame[] frames = getAllFrames();

        for (int i=0; i < frames.length; i++) {

            frame = frames[i];
            int test1 = frame.getY() + 50;
            int test2 = desktopView.getHeight();
            if (frame.getY() < 0){
                frame.setLocation(frame.getX(), 0);
            }
            
            
            else if(frame.getY() + 50 > desktopView.getHeight()){
                frame.setLocation(frame.getX(), desktopView.getHeight()-50);   
            }
            

            else frame.setLocation(frame.getX(), frame.getY());

            /*
            else if(frame.getY() > frame.get Height()-20){
                frame.setLocation(frame.getX(),frame.getHeight()-20);
            }
            */
 /*
            if (frame.getX() < -frame.getWidth()+60){
                frame.setLocation(-frame.getWidth()+60, frame.getY());
            }
            */
            /*
            else if(frame.getY() > getViewport().getHeight()-20){
                frame.setLocation(frame.getX(),getViewport().getHeight()-20);
            }

            else if (frame.getX() > getViewport().getWidth()-60){
                frame.setLocation(getViewport().getWidth()-60, frame.getY());
            }
            */
            
            
/*
            else if (frame.getX() > frame.getWidth()-60){
                frame.setLocation(frame.getWidth()-60, frame.getY());
            }
            */
        }
            
    }
    /**
     * resizes the desktop based upon the locations of its
     * internal frames. This updates the desktop scrollbars in real-time.
     */
    public void resizeDesktop() {

        SwingUtilities.invokeLater(new Runnable() {

            public void run(){

                // has to go through all the internal frames now and make sure none
                // off screen, and if so, add those scroll bars!

                Rectangle viewPort = getViewport().getViewRect();

                int maxX = viewPort.width + viewPort.x, maxY = viewPort.height + viewPort.y;
                int minX = viewPort.x, minY = viewPort.y;

                // determine the min/max extents of all internal frames

                JInternalFrame frame = null;
                JInternalFrame[] frames = getAllFrames();

                for (int i=0; i < frames.length; i++) {

                    frame = frames[i];
                    
                    if (frame.getY() < 0){
                        
                        frame.setLocation(frame.getX(), 0);

                    }
                    
                    else if (frame.getX() < -frame.getWidth()+60){
                        frame.setLocation(-frame.getWidth()+60, frame.getY());
                    }
                    
                    else if(frame.getY() > getViewport().getHeight()-20){
                        frame.setLocation(frame.getX(),getViewport().getHeight()-20);
                    }
                    
                    else if (frame.getX() > getViewport().getWidth()-60){
                        frame.setLocation(getViewport().getWidth()-60, frame.getY());
                    }
                    
                    else {
                        if (frame.getX() < minX) { // get minimum X
                            minX = frame.getX();
                        }
                        if ((frame.getX() + frame.getWidth()) > maxX)
                        {
                            maxX = frame.getX() + frame.getWidth();
                        }

                        if (frame.getY() < minY) { // get minimum Y
                            minY = frame.getY();
                        }
                        if ((frame.getY() + frame.getHeight()) > maxY)
                        {
                            maxY = frame.getY() + frame.getHeight();
                        }
                    }
                }

                // Don't count with frames that get off screen from the left side ant the top
                if (minX < 0) minX = 0;
                if (minY < 0) minY = 0;

                setVisible(false); // don't update the viewport
                // while we move everything (otherwise desktop looks 'bouncy')

                if (minX != 0 || minY != 0) {
                    // have to scroll it to the right or up the amount that it's off screen...
                    // before scroll, move every component to the right / down by that amount

                    for (int i=0; i < frames.length; i++) {
                        frame = frames[i];
                        frame.setLocation(frame.getX()-minX, frame.getY()-minY);
                    }

                    // have to scroll (set the viewport) to the right or up the amount
                    // that it's off screen...
                    JViewport view = getViewport();
                    view.setViewSize(new Dimension((maxX-minX),(maxY-minY)));
                    view.setViewPosition(new Point((viewPort.x-minX),(viewPort.y-minY)));
                    setViewport(view);

                }

                // resize the desktop
                setDesktopSize(new Dimension(maxX-minX, maxY-minY));

                setVisible(true); // update the viewport again


            }
        });
    }

  private class InternalFrameComponentListener implements ComponentListener
  {

      @Override
      public void componentResized(ComponentEvent e) {
          //resizeDesktop();
          shortenResizeDesktop();
      }

      @Override
      public void componentMoved(ComponentEvent e) {
          //resizeDesktop();
          shortenResizeDesktop();
      }

      @Override
      public void componentShown(ComponentEvent e) {
      }

      @Override
      public void componentHidden(ComponentEvent e) {
      }
  }
}

