/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theclinic.view.exceptions;

/**
 *
 * @author colin
 */
public class CrossCheckErrorException extends Exception {
    private String s = null;
    public CrossCheckErrorException(String s) 
    { 
        // Call constructor of parent Exception 
        super(s); 
    }
}
