package com.sangupta.jerry.db.impl;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;
import com.sangupta.jerry.db.DataStoreService;

/**
 * {@link MongoTemplate} based implementation of the {@link DataStoreService}.
 * 
 * <pre>
 * DataStoreService<MyEntity, String> service = new MongoTemplateDataStoreServiceImpl<MyEntity, String>() {};
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
public abstract class MongoTemplateDataStoreServiceImpl<T, X> extends AbstractDataStoreServiceImpl<T, X> {
    
    @Inject
    public MongoTemplate mongoTemplate;

    @Override
    public long count() {
        return this.mongoTemplate.count(new Query(), this.entityClass);
    }

    @Override
    public void deleteAll() {
        this.mongoTemplate.dropCollection(this.entityClass);
    }

    @Override
    protected T getEntity(X primaryID) {
        return this.mongoTemplate.findById(primaryID, this.entityClass);
    }

    @Override
    protected List<T> getMultipleEntities(Collection<X> ids) {
        Query query = new Query(Criteria.where(this.entityDetails.idFieldName).in(ids));
        return this.mongoTemplate.find(query, this.entityClass);
    }
    
    @Override
    protected List<T> getMultipleEntities(X[] ids) {
        Query query = new Query(Criteria.where(this.entityDetails.idFieldName).in(ids));
        return this.mongoTemplate.find(query, this.entityClass);
    }

    @Override
    protected List<T> getAllEntities() {
        return this.mongoTemplate.findAll(this.entityClass);
    }

    @Override
    protected List<T> getAllEntities(int page, int pageSize, int start, int end) {
        Query query = new Query().skip(start).limit(pageSize);
        return this.mongoTemplate.find(query, this.entityClass);
    }

    @Override
    protected T insertEntity(T entity) {
        return this.mongoTemplate.insert(entity);
    }

    @Override
    protected T updateEntity(T entity) {
        X primaryID = getPrimaryID(entity);
        if (primaryID == null) {
            return null;
        }

        return this.mongoTemplate.save(entity);
    }

    @Override
    protected T upsertEntity(T entity) {
        X primaryID = getPrimaryID(entity);
        if (primaryID == null) {
            this.mongoTemplate.save(entity);
            return null;
        }

        return this.mongoTemplate.save(entity);
    }
    
    @Override
    protected T deleteEntity(T entity) {
        DeleteResult result = this.mongoTemplate.remove(entity);
        if(result != null && result.getDeletedCount() > 0) {
            return entity;
        }
        
        return null;
    }

    @Override
    protected T deleteEntityForID(X primaryID) {
        Query query = new Query(Criteria.where(this.entityDetails.idFieldName).is(primaryID));
        return this.mongoTemplate.findAndRemove(query, this.entityClass);
    }

    @Override
    protected List<T> deleteMultipleEntities(Collection<X> ids) {
        Query query = new Query(Criteria.where(this.entityDetails.idFieldName).in(ids));
        return this.mongoTemplate.findAllAndRemove(query, this.entityClass);
    }

    @Override
    protected List<T> deleteMultipleEntities(X[] ids) {
        Query query = new Query(Criteria.where(this.entityDetails.idFieldName).in(ids));
        return this.mongoTemplate.findAllAndRemove(query, this.entityClass);
    }

}
