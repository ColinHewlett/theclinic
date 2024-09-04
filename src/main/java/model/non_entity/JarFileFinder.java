/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.non_entity;

import java.net.URL;
import java.security.CodeSource;

/**
 *
 * @author colin
 */
public class JarFileFinder {

    private static CodeSource codeSource = null;
    
    public static String getPath(){
        String result = null;
        codeSource = JarFileFinder.class.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL jarUrl = codeSource.getLocation();
            result = jarUrl.getPath();
        }
        return result;
    }
    
    public static String getName(){
        if (getPath()!=null)return getPath().substring(getPath().lastIndexOf("/") + 1);
        else return null;
    }
    
   
}
