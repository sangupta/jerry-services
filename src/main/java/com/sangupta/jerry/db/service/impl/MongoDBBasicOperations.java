/**
 *
 * jerry - Common Java Functionality
 * Copyright (c) 2012, Sandeep Gupta
 * 
 * http://www.sangupta/projects/jerry
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.sangupta.jerry.db.service.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;
import com.sangupta.jerry.db.service.DatabaseBasicOperationsService;
import com.sangupta.jerry.util.AssertUtils;

/**
 * Abstract MongoDB data provider of a given domain object. Providers type-safe, basic CRUD
 * operations for the object.
 * 
 * Creating a provider is as easy as,
 * <pre>
 * public class Student {
 * 	
 * 	private String id;
 * 
 * 	private String name;
 * 
 * }
 * 
 * public class MongoDBStudentServiceImpl extends MongoTemplateBasicOperations&lt;Student, String&gt; {
 * 
 * 	public boolean allowEmptyOrZeroID() {
 * 		return false;
 * 	}
 * }
 * </pre>
 * 
 * @author sangupta
 *
 */
public abstract class MongoDBBasicOperations<T, X> implements DatabaseBasicOperationsService<T, X> {
	
	protected MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = null;
	
	protected ConversionService conversionService = null;

	@Inject
	protected MongoTemplate mongoTemplate = null;
	
	protected Class<T> entityClass = null;
	
	protected Class<X> primaryIDClass = null;
	
	/**
	 * The identifier key that has been used as the primary key to store in the datastore.
	 * 
	 */
	protected String idKey = null;
	
	/**
	 * Default constructor that goes ahead and infers the entity class via
	 * generics - it will be needed with {@link MongoTemplate} while working.
	 */
	public MongoDBBasicOperations() {
		inferEntityClassViaGenerics();
	}
	
	/**
	 * Return the object as defined by this primary key.
	 * 
	 * @param primaryID
	 *            the primary key for record
	 * 
	 * @return the record if available, <code>null</code> otherwise
	 */
	@Override
	public T get(X primaryID) {
		if(primaryID == null) {
			return null;
		}
		
		T dbObject = this.mongoTemplate.findById(primaryID, this.entityClass);
		return dbObject;
	}

	/**
	 * Get all records for the given {@link Collection} of primary keys.
	 * 
	 * @param ids
	 *            the {@link Collection} of keys
	 * 
	 * @return a {@link List} of available records
	 */
	@Override
	public List<T> getForIdentifiers(Collection<X> ids) {
		if(AssertUtils.isEmpty(ids)) {
			return null;
		}
		
		if(this.idKey == null) {
			this.fetchMappingContextAndConversionService();
		}
		
		Query query = new Query(Criteria.where(this.idKey).in(ids));
		return this.mongoTemplate.find(query, this.entityClass);
	}
	
	@Override
	public List<T> getForIdentifiers(X... ids) {
		return this.getForIdentifiers(Arrays.asList(ids));
	}
	
	@Override
	public List<T> getAllEntities() {
		return this.mongoTemplate.findAll(this.entityClass);
	}
	
	@Override
	public List<T> getEntities(int page, int pageSize) {
		Query query = new Query();
		query.limit(pageSize);
		if(page > 1) {
			query.skip((page - 1) * pageSize);
		}
		return this.mongoTemplate.find(query, this.entityClass);
	}
	
	/**
	 * Insert the object into the data store.
	 * 
	 * @param entity
	 *            the entity to insert
	 * 
	 * @return <code>true</code> if inserted, <code>false</code> otherwise
	 */
	@Override
	public boolean insert(T entity) {
		if(entity == null) {
			return false;
		}
		
		X primaryID = getPrimaryID(entity);
		if(primaryID != null) {
			if(!allowEmptyOrZeroID() && AssertUtils.isEmpty(primaryID)) {
				return false;
			}
		}

		try {
		    this.mongoTemplate.insert(entity);
		} catch(RuntimeException e) {
		    // this ensures that any insert operation that fails returns a false
		    return false;
		}
		
		return true;
	}

	/**
	 * Update the entity in the data store.
	 * 
	 * @param entity
	 *            the entity to update
	 * 
	 * @return <code>true</code> if updated, <code>false</code> otherwise
	 */
	@Override
	public boolean update(T entity) {
		if(entity == null) {
			return false;
		}
		
		X primaryID = getPrimaryID(entity);
		if(primaryID == null) {
			return false;
		}
		
		if(!allowEmptyOrZeroID() && AssertUtils.isEmpty(primaryID)) {
			return false;
		}
		
		this.mongoTemplate.save(entity);
		return true;
	}

	/**
	 * Add or update an existing object in the data store.
	 * 
	 * @param entity
	 *            the object to save
	 * 
	 * @return <code>true</code> if saved, <code>false</code> otherwise
	 */
	@Override
	public boolean addOrUpdate(T entity) {
		if(entity == null) {
			return false;
		}
		
		X primaryID = getPrimaryID(entity);
		if(primaryID == null) {
			this.mongoTemplate.save(entity);
			return true;
		}
		
		if(!allowEmptyOrZeroID() && AssertUtils.isEmpty(primaryID)) {
			return false;
		}
		
		this.mongoTemplate.save(entity);
		return true;
	}
	
	/**
	 * Delete the object against the given primary key.
	 * 
	 * @param primaryID
	 *            the primary key
	 * 
	 * @return the record that was removed
	 */
	@Override
	public boolean delete(X primaryID) {
		if(primaryID == null) {
			return false;
		}
		
		Query query = new Query(Criteria.where(this.idKey).is(primaryID));
		DeleteResult result = this.mongoTemplate.remove(query, this.entityClass);
		if(result == null) {
			return false;
		}
		
		return result.getDeletedCount() == 1;
	}
	
	/**
	 * Count the total number of objects in the collection
	 * 
	 * @return the total number of objects
	 */
	@Override
	public long count() {
		long items = this.mongoTemplate.count(new Query(), this.entityClass);
		return items;
	}
	
	/**
	 * Drop the given collection.
	 * 
	 */
	@Override
	public void deleteAllEntities() {
		this.mongoTemplate.dropCollection(this.entityClass);
	}
	
	/**
	 * Defines if we need to allow empty or zero value in primary ID of the entity
	 * object.
	 * 
	 * @return returns <code>false</code> for default implementations
	 */
	public boolean allowEmptyOrZeroID() {
		return false;
	}
	
	/**
	 * Extract the value of the primary ID of the entity object
	 * 
	 * @param entity
	 *            the entity to get primary key value of
	 * 
	 * @return the primary key value
	 */
	public X getPrimaryID(T entity) {
		if(mappingContext == null || conversionService == null) {
			fetchMappingContextAndConversionService();
		}

		MongoPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entity.getClass());
		MongoPersistentProperty idProperty = persistentEntity.getIdProperty();
		if(idProperty == null) {
			return null;
		}
		
//		X idValue = BeanWrapper.create(entity, conversionService).getProperty(idProperty, this.primaryIDClass);
		X idValue = (X) this.mappingContext.getPersistentEntity(this.entityClass).getPropertyAccessor(entity).getProperty(idProperty);
		return idValue;
	}
	
	/**
	 * Get the basic services from mongo template
	 */
	protected synchronized void fetchMappingContextAndConversionService() {
		if(mappingContext == null) {
			MongoConverter mongoConverter = this.mongoTemplate.getConverter();
			mappingContext = mongoConverter.getMappingContext();
			conversionService = mongoConverter.getConversionService();

			MongoPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entityClass);
			MongoPersistentProperty idProperty = persistentEntity.getIdProperty();
			this.idKey = idProperty == null ? "_id" : idProperty.getName();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void inferEntityClassViaGenerics() {
		// fetch the entity class over which we will work
		Type t = getClass().getGenericSuperclass();
		if(t instanceof ParameterizedType) {
			Type[] actualTypeArguments = ((ParameterizedType) t).getActualTypeArguments();
			this.entityClass = (Class<T>) actualTypeArguments[0];
			this.primaryIDClass = (Class<X>) actualTypeArguments[1];
		}
	}
	
	// Usual accessors follow

	/**
	 * @return the mongoTemplate
	 */
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

}
