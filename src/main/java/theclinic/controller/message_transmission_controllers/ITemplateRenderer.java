/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package theclinic.controller.message_transmission_controllers;

import java.util.Map;

/**
 *
 * @author colin
 */
public interface ITemplateRenderer {
    
    String renderTemplate(String templateName, Map<String,Object>dataModel);
}
