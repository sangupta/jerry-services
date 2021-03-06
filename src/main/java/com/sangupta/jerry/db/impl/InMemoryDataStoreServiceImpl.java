package com.sangupta.jerry.db.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.util.AssertUtils;

/**
 * An in-memory implementation (using {@link ConcurrentHashMap}) for
 * {@link DataStoreService}. The implementation can be used as:
 * 
 * <pre>
 * DataStoreService<MyEntity, String> service = new InMemoryDataStoreServiceImpl<MyEntity, String>() {};
 * </pre>
 * 
 * Now service can be used for all CRUD operations on <code>MyEntity</code> object
 * using <code>service</code>.
 * 
 * @author sangupta
 * 
 * @since 2.0.0
 *
 * @param <T> The entity object type which is persisted in the datastore
 * 
 * @param <X> The primary ID key for this entity object
 */
public abstract class InMemoryDataStoreServiceImpl<T, X> extends AbstractDataStoreServiceImpl<T, X> {

    protected final Map<X, T> dataStore = new ConcurrentHashMap<>();

    @Override
    protected T getEntity(X primaryID) {
        return this.dataStore.get(primaryID);
    }

    @Override
    protected T deleteEntity(T entity) {
        X id = this.getPrimaryID(entity);
        return this.dataStore.remove(id);
    }

    @Override
    protected T deleteEntityForID(X primaryID) {
        return this.dataStore.remove(primaryID);
    }

    @Override
    protected List<T> deleteMultipleEntities(Collection<X> ids) {
        if (AssertUtils.isEmpty(ids)) {
            return null;
        }

        List<T> list = new ArrayList<>();
        for (X id : ids) {
            list.add(this.deleteForID(id));
        }

        return list;
    }

    @Override
    protected List<T> deleteMultipleEntities(X[] ids) {
        if (AssertUtils.isEmpty(ids)) {
            return null;
        }

        List<T> list = new ArrayList<>();
        for (X id : ids) {
            list.add(this.deleteForID(id));
        }

        return list;
    }

    @Override
    public long count() {
        return this.dataStore.size();
    }

    @Override
    protected List<T> getMultipleEntities(Collection<X> ids) {
        List<T> list = new ArrayList<>();
        for (X id : ids) {
            T entity = this.get(id);
            if (entity != null) {
                list.add(entity);
            }
        }

        return list;
    }

    @Override
    protected List<T> getMultipleEntities(X[] ids) {
        List<T> list = new ArrayList<>();
        for (X id : ids) {
            T entity = this.get(id);
            if (entity != null) {
                list.add(entity);
            }
        }

        return list;
    }

    @Override
    protected List<T> getAllEntities() {
        return new ArrayList<>(this.dataStore.values());
    }

    @Override
    protected List<T> getAllEntities(int page, int pageSize, int start, int end) {
        List<T> coll = new ArrayList<>(this.dataStore.values());

        int size = coll.size();
        if (start >= size) {
            return new ArrayList<>();
        }

        if (end > size) {
            end = size;
        }

        return coll.subList(start, end);
    }

    @Override
    public void deleteAll() {
        this.dataStore.clear();
    }

    @Override
    protected T insertEntity(T entity) {
        X id = this.getPrimaryID(entity);
        T old = this.dataStore.putIfAbsent(id, entity);
        if (old == null) {
            return entity;
        }

        return null;
    }

    @Override
    protected T updateEntity(T entity) {
        X id = this.getPrimaryID(entity);
        boolean exists = this.dataStore.containsKey(id);
        if (!exists) {
            return null;
        }

        this.dataStore.replace(id, entity);
        return entity;
    }

    @Override
    protected T upsertEntity(T entity) {
        X id = this.getPrimaryID(entity);
        this.dataStore.put(id, entity);
        return entity;
    }

}
