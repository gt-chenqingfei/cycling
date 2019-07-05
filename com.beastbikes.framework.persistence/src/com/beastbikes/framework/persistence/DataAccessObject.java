package com.beastbikes.framework.persistence;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * {@link DataAccessObject} interface is used for persistent data accessing
 *
 * @author johnson
 */

@SuppressWarnings("unchecked")
public interface DataAccessObject<T extends PersistentObject> {

    public PersistenceManager getPersistenceManager();

    /**
     * Returns the number of rows
     *
     * @return the number of rows
     * @throws PersistenceException
     */
    public long count() throws PersistenceException;

    /**
     * Returns the number of rows with the specified condition
     *
     * @param clauses The condition clauses
     * @param args    The arguments of clauses
     * @return the number of rows
     * @throws PersistenceException
     */
    public long count(String clauses, String... args)
            throws PersistenceException;

    /**
     * Returns the persistent object with the specified id
     *
     * @param id
     * @return
     * @throws PersistenceException
     */
    public T get(String id) throws PersistenceException;

    /**
     * Returns all persistent objects
     *
     * @return all persistent objects
     * @throws PersistenceException
     */
    public List<T> getAll() throws PersistenceException;

    /**
     * Save the specified objects into storage
     *
     * @param pos The objects to be inserted into storage
     * @throws PersistenceException
     */
    public void create(T... pos) throws PersistenceException;

    /**
     * Save the specified objects into storage
     *
     * @param pos The objects to be inserted into storage
     * @throws PersistenceException
     */
    public void create(List<T> pos) throws PersistenceException;

    /**
     * Update the specified objects which has already exist in storage
     *
     * @param pos The objects to be updated
     * @throws PersistenceException
     */
    public void update(T... pos) throws PersistenceException;

    /**
     * Update the specified objects which has already exist in storage
     *
     * @param pos The objects to be updated
     * @throws PersistenceException
     */
    public void update(List<T> pos) throws PersistenceException;

    /**
     * Save or update the specified objects
     *
     * @param pos The object to be saved or updated
     * @throws PersistenceException
     */
    public void createOrUpdate(T... pos) throws PersistenceException;

    /**
     * Save or update the specified objects
     *
     * @param pos The object to be saved or updated
     * @throws PersistenceException
     */
    public void createOrUpdate(List<T> pos) throws PersistenceException;

    /**
     * Delete the specified objects which has already exist in storage
     *
     * @param pos The object to be deleted
     * @throws PersistenceException
     */
    public void delete(T... pos) throws PersistenceException;

    /**
     * Delete the specified objects which has already exist in storage
     *
     * @param pos The object to be deleted
     * @throws PersistenceException
     */
    public void delete(List<T> pos) throws PersistenceException;

    /**
     * Delete the persistent object with specified ids
     *
     * @param ids
     * @throws PersistenceException
     */
    public void delete(String... ids) throws PersistenceException;

    /**
     * Test whether the specified persistent object exists or not
     *
     * @param po The object to be tested
     * @return True if only the specified persistent object already exists, or
     * false is returned
     * @throws PersistenceException
     */
    public boolean exists(T po) throws PersistenceException;

    /**
     * Test whether a persistent object with the specified id exists or not
     *
     * @param po The object to be tested
     * @return True if only a persistent object with the specified id already
     * exists, or false is returned
     * @throws PersistenceException
     */
    public boolean exists(String id) throws PersistenceException;

    /**
     * Execute raw SQL with the specified arguments
     *
     * @param sql  The SQL to be executed
     * @param args The arguments of the specified SQL
     * @return
     * @throws PersistenceException
     */
    public void execute(String sql, String... args) throws PersistenceException;

    /**
     * Execute the specified transaction
     *
     * @param transaction A transaction
     * @return
     * @throws PersistenceException
     */
    public <V> V execute(Callable<V> transaction) throws PersistenceException;

}
