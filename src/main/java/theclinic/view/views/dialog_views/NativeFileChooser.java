/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.views.dialog_views;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 * @author colin
 */
public class NativeFileChooser {
    public static File showOpenDialog(Component parent, String title, File initialDirectory, String... allowedExtensions) {
        Frame frame = getFrame(parent);
        FileDialog dialog = new FileDialog(frame, title, FileDialog.LOAD);
        
        if (initialDirectory!=null && initialDirectory.isDirectory()){
            dialog.setDirectory(initialDirectory.getAbsolutePath());
        }

        // Add extension filter if provided
        if (allowedExtensions != null && allowedExtensions.length > 0) {
            Set<String> extSet = Stream.of(allowedExtensions)
                                       .map(e -> e.toLowerCase().replace(".", ""))
                                       .collect(Collectors.toSet());

            dialog.setFilenameFilter(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    int dot = name.lastIndexOf('.');
                    if (dot == -1 || dot == name.length() - 1) return false;
                    String ext = name.substring(dot + 1).toLowerCase();
                    return extSet.contains(ext);
                }
            });
        }

        dialog.setVisible(true);

        if (dialog.getFile() == null) {
            return null; // user cancelled
        }
        return new File(dialog.getDirectory(), dialog.getFile());
    }

    private static Frame getFrame(Component parent) {
        if (parent == null) return null;
        if (parent instanceof Frame) return (Frame) parent;
        return (Frame) javax.swing.SwingUtilities.getWindowAncestor(parent);
    }
}
