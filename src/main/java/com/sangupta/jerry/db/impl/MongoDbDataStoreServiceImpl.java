package com.sangupta.jerry.db.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.util.ReflectionMapper;

/**
 * {@link Mongo} based implementation of the {@link DataStoreService}. This
 * implementation depends directly on the MongoDB driver and does not need any
 * other framework.
 * 
 * If {@link #MongoDbDataStoreServiceImpl(MongoCollection)} based instantiation
 * is used, the collection name from the {@link MongoCollection} is used.
 * 
 * However, if {@link #MongoDbDataStoreServiceImpl(MongoDatabase)} constructor
 * is used, the collection name is decided based on the class name of the entity
 * using {@link Class#getName()} method. If the name needs to be customized,
 * overridethe {@link #getCollectionName()} method.
 * 
 * @author sangupta
 *
 * @param <T>
 * 
 * @param <X>
 */
public class MongoDbDataStoreServiceImpl<T, X> extends AbstractDataStoreServiceImpl<T, X> {

    protected MongoCollection<Document> collection;

    protected String collectionName;

    public MongoDbDataStoreServiceImpl(MongoDatabase database) {
        this.collection = database.getCollection(this.getCollectionName());
    }

    public MongoDbDataStoreServiceImpl(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public long count() {
        return this.collection.countDocuments();
    }

    @Override
    public void deleteAll() {
        this.collection.drop();
    }

    @Override
    protected T getEntity(X primaryID) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(primaryID.toString()));

        Bson filter = Filters.eq("_id", new ObjectId(primaryID.toString()));
        Document document = this.collection.find(filter).first();
        if (document == null) {
            return null;
        }

        return ReflectionMapper.from(document, this.entityClass);
    }

    @Override
    protected T insertEntity(T entity) {
        return null;
    }

    @Override
    protected T updateEntity(T entity) {
        return null;
    }

    @Override
    protected T upsertEntity(T entity) {
        return null;
    }

    @Override
    protected T deleteEntity(T entity) {
        X primaryID = this.getPrimaryID(entity);
        Bson filter = Filters.eq("_id", new ObjectId(primaryID.toString()));
        DeleteResult result = this.collection.deleteOne(filter);
        if (result.getDeletedCount() > 0) {
            return entity;
        }

        return null;
    }

    @Override
    protected T deleteEntityForID(X primaryID) {
        Bson filter = Filters.eq("_id", new ObjectId(primaryID.toString()));
        Document document = this.collection.findOneAndDelete(filter);
        if (document == null) {
            return null;
        }

        return ReflectionMapper.from(document, this.entityClass);
    }

    @Override
    protected List<T> getAllEntities() {
        FindIterable<Document> iterable = this.collection.find();
        return this.getFromIterable(iterable);
    }

    @Override
    protected List<T> getAllEntities(int page, int pageSize, int start, int end) {
        return null;
    }

    @Override
    protected List<T> getMultipleEntities(Collection<X> ids) {
        Bson filter = Filters.in("_id", ids);
        FindIterable<Document> iterable = this.collection.find(filter);
        return this.getFromIterable(iterable);
    }

    @Override
    protected List<T> getMultipleEntities(X[] ids) {
        Bson filter = Filters.in("_id", ids);
        FindIterable<Document> iterable = this.collection.find(filter);
        return this.getFromIterable(iterable);
    }

    @Override
    protected List<T> deleteMultipleEntities(Collection<X> ids) {
        Bson filter = Filters.in("_id", ids);
        FindIterable<Document> iterable = this.collection.find(filter);
        return null;
    }

    @Override
    protected List<T> deleteMultipleEntities(X[] ids) {
        return null;
    }
    
    protected List<T> getFromIterable(FindIterable<Document> iterable) {
        List<T> list = new ArrayList<>();
        
        MongoCursor<Document> iterator = iterable.iterator();
        while(iterator.hasNext()) {
            Document document = iterator.next();
            list.add(ReflectionMapper.from(document, this.entityClass));
        }
        
        return list;        
    }

    protected String getCollectionName() {
        if (this.collectionName != null) {
            return this.collectionName;
        }

        String name = this.entityClass.getName();
        char[] array = name.toCharArray();
        array[0] = Character.toLowerCase(array[0]);

        this.collectionName = new String(array);
        return this.collectionName;
    }

}
