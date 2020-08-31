package com.sangupta.jerry.db;

import java.util.Collection;
import java.util.List;

/**
 * This class borrows heavily from the earlier available
 * <code>DatabaseBasicOperationsService</code> available in the
 * https://github.com/sangupta/jerry-services project.
 * 
 * @author sangupta
 * 
 * @since 2.0.0
 *
 * @param <T> The entity object type which is persisted in the datastore
 * 
 * @param <X> The primary ID key for this entity object
 */
public interface DataStoreService<T, X> {

    /**
     * Return the primary ID for the given entity.
     * 
     * @param entity the entity for which primary ID is required
     * 
     * @return the value of primary ID for the entity.
     * 
     * @throws IllegalArgumentException if the entity is <code>null</code>
     */
    public X getPrimaryID(T entity);

    /**
     * Retrieve the entity object with the given primary key.
     * 
     * @param primaryID the primary key for which to look for the object
     * 
     * @return the object that is stored for the given primary key
     */
    public T get(X primaryID);

    /**
     * Retrieves a list of all entities in the datastore that match the list of
     * given primary identifiers.
     * 
     * <b>Note:</b> If there are too many entity identifiers supplied, the code may
     * go out of memory or may take too long to complete. This method should be used
     * only by developer at discretion.
     * 
     * @param ids the primary key identifiers for which we need to fetch the
     *            objects.
     * 
     * @return list of objects as fetched for the given identifiers.
     * 
     */
    public List<T> getMultiple(Collection<X> ids);

    /**
     * 
     * @param ids
     * @return
     */
    public List<T> getMultiple(X[] ids);

    /**
     * Retrieves a list of all entities in the datastore.
     * 
     * <b>Note:</b> If there are too many entities in the data store, the code may
     * go out of memory or may take too long to complete. This method should be used
     * only by developers at discretion.
     * 
     * It is recommended not to use this method in production instances. Rather, use
     * the method {@link #getAll(int, int)}.
     * 
     * @return all the objects in the data store. Never returns a <code>null</code>.
     *         If there are no objects in the data store, it should return an empty
     *         array list.
     * 
     */
    public List<T> getAll();

    /**
     * Retrieves a list of entities for the given page number with the give page
     * size. The page numbering starts from 1.
     * 
     * @param page     the page for which the results are needed,
     *                 <code>0</code>-based. If the value is less than zero, returns
     *                 <code>null</code>
     * 
     * @param pageSize the page size to use. If the value is less than or equal to
     *                 zero, returns <code>null</code>
     * 
     * @return the list of all objects falling in that page
     * 
     */
    public List<T> getAll(int page, int pageSize);

    /**
     * Insert a new entity object into the data store
     * 
     * @param entity the entity that needs to be saved
     * 
     * @return the entity if it was inserted successfully, <code>null</code>
     *         otherwise
     * 
     * @throws IllegalArgumentException if the entity is <code>null</code>
     */
    public T insert(T entity);

    /**
     * Update the entity object into the data store
     * 
     * @param entity the entity to be updated in the data store
     * 
     * @return the entity if it was updated successfully, <code>null</code>
     *         otherwise
     * 
     * @throws IllegalArgumentException if the entity is <code>null</code>
     */
    public T update(T entity);

    /**
     * Add or update the entity object into the data store
     * 
     * @param entity the object that needs to be persisted or updated in the data
     *               store.
     * 
     * @return the entity if it was upserted successfully, <code>null</code>
     *         otherwise
     */
    public T upsert(T entity);

    /**
     * Delete the given entity from the data store.
     * 
     * @param entity the object to be removed
     * 
     * @return the entity if it was deleted successfully, <code>null</code>
     *         otherwise
     * 
     * @throws IllegalArgumentException if the entity is <code>null</code>
     */
    public T delete(T entity);

    /**
     * Delete the data store entity with the given primary key
     * 
     * @param primaryID the primary key of the object that needs to be deleted
     * 
     * @return <code>true</code> if the entity was deleted, <code>false</code>
     *         otherwise
     * 
     * @throws IllegalArgumentException if the entity is <code>null</code>
     */
    public T deleteForID(X primaryID);

    /**
     * Delete entities from data store where entities are represented by given
     * primary IDs.
     * 
     * @param ids primary IDs for which entities are to be removed
     * 
     * @return a list of all entities that were removed, <code>null</code> if no
     *         entity was removed.
     */
    public List<T> deleteMultiple(Collection<X> ids);

    /**
     * Delete entities from data store where entities are represented by given
     * primary IDs.
     * 
     * @param ids primary IDs for which entities are to be removed
     * 
     * @return a list of all entities that were removed, <code>null</code> if no
     *         entity was removed.
     */
    public List<T> deleteMultiple(X[] ids);

    /**
     * Return the count of total objects in the data store
     * 
     * @return the number of objects in the database
     * 
     */
    public long count();

    /**
     * Clean the database of all entities in this collection.
     * 
     */
    public void deleteAll();

}
