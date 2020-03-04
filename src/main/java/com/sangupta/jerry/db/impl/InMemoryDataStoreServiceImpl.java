package com.sangupta.jerry.db.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.util.AssertUtils;

/**
 * An in-memory implementation (using {@link ConcurrentHashMap}) for {@link DataStoreService}.
 * 
 * @author sangupta
 * 
 * @since 2.0.0
 *
 * @param <T>
 * @param <X>
 */
public class InMemoryDataStoreServiceImpl<T, X> extends AbstractDataStoreServiceImpl<T, X> {

    protected final Map<X, T> dataStore = new ConcurrentHashMap<>();

    @Override
    public T get(X primaryID) {
        if (AssertUtils.isEmpty(primaryID)) {
            return null;
        }

        return this.dataStore.get(primaryID);
    }

    @Override
    public boolean delete(X primaryID) {
        if (AssertUtils.isEmpty(primaryID)) {
            return false;
        }

        T entity = this.dataStore.remove(primaryID);
        if (entity != null) {
            return true;
        }

        return false;
    }

    @Override
    public long count() {
        return this.dataStore.size();
    }

    @Override
    public List<T> getForIdentifiers(Collection<X> ids) {
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
    public List<T> getForIdentifiers(X[] ids) {
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
    public List<T> getAll() {
        return new ArrayList<>(this.dataStore.values());
    }

    @Override
    public List<T> getAll(int page, int pageSize) {
        List<T> coll = new ArrayList<>(this.dataStore.values());

        int start = (page - 1) * pageSize;
        int end = start + pageSize;
        int size = coll.size();

        if (start >= size) {
            return null;
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
    protected boolean insertEntity(T entity) {
        X id = this.getPrimaryID(entity);
        T old = this.dataStore.putIfAbsent(id, entity);
        if (old == null) {
            return true;
        }

        return false;
    }

    @Override
    protected boolean updateEntity(T entity) {
        X id = this.getPrimaryID(entity);
        T old = this.dataStore.replace(id, entity);
        if(old == null) {
            return false;
        }
        
        return true;
    }

    @Override
    protected boolean upsertEntity(T entity) {
        return false;
    }

}
