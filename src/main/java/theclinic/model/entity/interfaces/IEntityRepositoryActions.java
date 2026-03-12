/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theclinic.model.entity.interfaces;

import theclinic.model.entity.Entity;
import theclinic.model.repository.StoreException;//01/03/2023
import java.awt.Point;

/**
 *
 * @author colin.hewlett.solutions@gmail.com
 */
public interface IEntityRepositoryActions {
    public Point count()throws StoreException;
    public void create()throws StoreException;
    public void delete()throws StoreException;
    public void drop()throws StoreException;
    public Integer insert()throws StoreException;
    public Entity read()throws StoreException;
    public void update()throws StoreException;
}
