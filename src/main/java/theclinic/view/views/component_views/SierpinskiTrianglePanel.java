/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.views.component_views;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SierpinskiTrianglePanel extends JPanel {
    private BufferedImage image;

    public SierpinskiTrianglePanel(BufferedImage image) {
        this.image = image;
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}

