/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import java.awt.Point;
import java.util.ArrayList;
import model.IEntityStoreActions;
import repository.StoreException;
import repository.Repository;

/**
 *
 * @author colin
 */
public class Doctor extends Entity implements IEntityStoreActions{
    private String title = null;
    private String line1 = null;
    private String line2 = null;
    private String town = null;
    private String county = null;
    private String postcode = null;
    private String phone = null;
    private String email = null;
    private Integer patientKey = null;
    private Integer key = null;
    private Patient patient =  null;
    private ArrayList<Doctor> collection = null;
    
    public Doctor(){
        setIsDoctor(true);
    }
    
    public Doctor(Integer key){
        setIsDoctor(true);
        this.key = key;
    }
    
    public Doctor(Patient patient){
        setIsDoctor(true);
        this.patient = patient;
    }
    
    public ArrayList<Doctor> get() {
        return collection;
    }

    public void set(ArrayList<Doctor> value) {
        this.collection = value;
    }
    
    public Integer getKey() {
        return key;
    }

    public void setKey(Integer value) {
        this.key = value;
    }
    
    public Integer getPatientKey() {
        return patientKey;
    }

    public void setPatientKey(Integer value) {
        this.patientKey = value;
    }
    
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient value) {
        this.patient = value;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String doctor) {
        this.title = doctor;
    }
    
    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }  
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public Point count()throws StoreException{
        return new Repository().count(this);
    }

    @Override
    public void create()throws StoreException{
        new Repository().create(this);
    }
    
    @Override
    public void delete()throws StoreException{
        new Repository().delete(this);
    }
    
    @Override
    public void drop(){
        
    }

    @Override
    public Integer insert() throws StoreException{
        Integer value = null;
        value = new Repository().insert(this);
        setKey(value);
        return getKey();
    }
    
    @Override
    public Doctor read()throws StoreException{
        return new Repository().read(this);
    }
    
    @Override
    public void update() throws StoreException{
        new Repository().update(this);
    }
}
