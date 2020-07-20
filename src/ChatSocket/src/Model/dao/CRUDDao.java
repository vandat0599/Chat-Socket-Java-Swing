/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.Dao;

/**
 *
 * @author user
 */
public interface CRUDDao<T> {
    void create(T ob);
    void update(T ob);
    void delete(T ob);
}
