/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colinhewlettsolutions.client.controller;

import java.util.HashMap;


/**
 *
 * @author colin
 */
public class Descriptor {
    
    private ControllerDescription controllerDescription = null;
    public ControllerDescription getControllerDescription(){
        return controllerDescription;
    }
    
    private ViewDescription viewDescription = null;
    public ViewDescription getViewDescription(){
        return viewDescription;
    }
    
    public Descriptor(){
        controllerDescription = new ControllerDescription(); 
        viewDescription = new ViewDescription();
    }
    
    public class ControllerDescription {
        private HashMap<SystemDefinition.Properties,Object> properties = new HashMap<>();
        protected ControllerDescription (){
            for (SystemDefinition.Properties property : SystemDefinition.Properties.values()){
                properties.put(property, null);
            }
        }

        public void setProperty(SystemDefinition.Properties key, Object value){
           properties.put(key, value);
        }
        public Object getProperty(SystemDefinition.Properties key){
           return properties.get(key);
        }
         
        protected void setProperties(HashMap<SystemDefinition.Properties,Object> value){
            properties = new HashMap<>(value);
         }
         public HashMap<SystemDefinition.Properties,Object> getProperties(){
            return properties;
        }


         private  ViewController.ViewMode viewMode = null;
         public  ViewController.ViewMode getViewMode(){
             return viewMode; 
         }
         public  void setViewMode(ViewController.ViewMode value){
             viewMode = value;
         }
    }

    public class ViewDescription { 
        private HashMap<SystemDefinition.Properties,Object> properties = new HashMap<>();
        protected ViewDescription(){
            for (SystemDefinition.Properties property : SystemDefinition.Properties.values()){
                properties.put(property, null);
            }
        }

        public void setProperty(SystemDefinition.Properties key, Object value){
            properties.put(key, value); 
        }
        public Object getProperty(SystemDefinition.Properties key){
            return properties.get(key);
        }
        
        protected void setProperties(HashMap<SystemDefinition.Properties,Object> value){
            properties = new HashMap<>(value);
         }
         public HashMap<SystemDefinition.Properties,Object> getProperties(){
            return properties;
        }

        private ViewController.ViewMode viewMode = null;
         public ViewController.ViewMode getViewMode(){
             return viewMode; 
         }
         public void setViewMode(ViewController.ViewMode value){
             viewMode = value;
         } 
   }
    
}