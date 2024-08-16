/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.non_entity;

/**
 *
 * @author colin
 */
public class Credential {
    
    public Credential(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public Credential(){
        
    }
    
    private String username = null;
    public void setUsername(String value) {
        username = value;
    }
    public String getUsername(){
        return username;
    }
    
    private String password = null;
    public void setPassword(String value) {
        password = value;
    }
    public String getPassword(){
        return password;
    }
    
    private String passwordReentry = null;
    public void setPasswordReentry(String value) {
        passwordReentry = value;
    }
    public String getPasswordReentry(){
        return passwordReentry;
    }
    
    private boolean isUsernameValid = false;
    public void setIsUsernameValid(boolean value) {
        isUsernameValid = value;
    }
    public boolean getIsUsernameValid(){
        return isUsernameValid;
    }
    
    private boolean isPasswordValid = false;
    public void setIsPasswordValid(boolean value) {
        isPasswordValid = value;
    }
    public boolean getIsPasswordValid(){
        return isPasswordValid;
    }
    
    private boolean isPasswordReentryValid = false;
    public void setIsPasswordReentryValid(boolean value) {
        isPasswordReentryValid = value;
    }
    public boolean getIsPasswordReentryValid(){
        return isPasswordReentryValid;
    }
}
