package com.sangupta.jerry.db.impl;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;
import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.util.AssertUtils;

/**
 * {@link MongoTemplate} based implementation of the {@link DataStoreService}.
 * 
 * @author sangupta
 * 
 * @since 2.0.0
 *
 * @param <T>
 * 
 * @param <X>
 */
public abstract class MongoTemplateDataStoreServiceImpl<T, X> extends AbstractDataStoreServiceImpl<T, X> {

    @Inject
    protected MongoTemplate mongoTemplate;

    @Override
    public T get(X primaryID) {
        if (primaryID == null) {
            return null;
        }

        return this.mongoTemplate.findById(primaryID, this.entityClass);
    }

    @Override
    public boolean insertEntity(T entity) {
        X primaryID = this.getPrimaryID(entity);
        if (primaryID != null) {
            if (!this.allowEmptyOrZeroID() && AssertUtils.isEmpty(primaryID)) {
                return false;
            }
        }

        try {
            this.mongoTemplate.insert(entity);
        } catch (RuntimeException e) {
            // this ensures that any insert operation that fails returns a false
            return false;
        }

        return true;
    }

    @Override
    public boolean updateEntity(T entity) {
        X primaryID = getPrimaryID(entity);
        if (primaryID == null) {
            return false;
        }

        if (!allowEmptyOrZeroID() && AssertUtils.isEmpty(primaryID)) {
            return false;
        }

        this.mongoTemplate.save(entity);
        return true;
    }

    @Override
    public boolean upsertEntity(T entity) {
        X primaryID = getPrimaryID(entity);
        if (primaryID == null) {
            this.mongoTemplate.save(entity);
            return true;
        }

        if (!allowEmptyOrZeroID() && AssertUtils.isEmpty(primaryID)) {
            return false;
        }

        this.mongoTemplate.save(entity);
        return true;
    }

    @Override
    public boolean delete(X primaryID) {
        if (primaryID == null) {
            return false;
        }

        Query query = new Query(Criteria.where(this.idKeyFieldName).is(primaryID));
        DeleteResult result = this.mongoTemplate.remove(query, this.entityClass);
        if (result == null) {
            return false;
        }

        return result.getDeletedCount() == 1;
    }

    @Override
    public long count() {
        return this.mongoTemplate.count(new Query(), this.entityClass);
    }

    @Override
    public List<T> getForIdentifiers(Collection<X> ids) {
        if (AssertUtils.isEmpty(ids)) {
            return null;
        }

        Query query = new Query(Criteria.where(this.idKeyFieldName).in(ids));
        return this.mongoTemplate.find(query, this.entityClass);
    }
    
    @Override
    public List<T> getForIdentifiers(X[] ids) {
        if (AssertUtils.isEmpty(ids)) {
            return null;
        }

        Query query = new Query(Criteria.where(this.idKeyFieldName).in(ids));
        return this.mongoTemplate.find(query, this.entityClass);
    }

    @Override
    public List<T> getAll() {
        return this.mongoTemplate.findAll(this.entityClass);
    }

    @Override
    public List<T> getAll(int page, int pageSize) {
        Query query = new Query();
        query.limit(pageSize);
        if (page > 1) {
            query.skip((page - 1) * pageSize);
        }
        return this.mongoTemplate.find(query, this.entityClass);
    }

    @Override
    public void deleteAll() {
        this.mongoTemplate.dropCollection(this.entityClass);
    }

    /**
     * Whether to allow <code>empty</code> primary key values
     * in the entity.
     * 
     * @return
     */
    protected boolean allowEmptyOrZeroID() {
        return false;
    }

}
