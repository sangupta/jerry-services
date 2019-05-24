package com.sangupta.jerry.db.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;
import com.sangupta.jerry.db.service.TenantAwareDatabaseBasicOperationsService;
import com.sangupta.jerry.entity.TenantAwareEntity;
import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;

/**
 * Implementation of the {@link TenantAwareDatabaseBasicOperationsService} that
 * uses MongoDB as its data store.
 * 
 * @author sangupta
 * 
 * @since 2.0.0
 *
 * @param <T>
 * @param <X>
 */
public class TenantAwareMongoDBBasicOperations<T extends TenantAwareEntity, X> extends MongoDBBasicOperations<T, X> implements TenantAwareDatabaseBasicOperationsService<T, X> {

	/**
	 * The key under which the tenant name is stored in the data store.
	 */
	protected final String tenantKey;

	public TenantAwareMongoDBBasicOperations() {
		this("tenant");
	}

	public TenantAwareMongoDBBasicOperations(String tenantKey) {
		this.tenantKey = tenantKey;
	}

	/**
	 * 
	 */
	@Override
	public T get(X primaryID) {
		T entity = super.get(primaryID);
		if (entity == null) {
			return null;
		}

		if (SecurityContext.isSameTenant(entity.getTenant())) {
			return entity;
		}

		return null;
	}

	@Override
	public List<T> getForIdentifiers(Collection<X> ids) {
		if (AssertUtils.isEmpty(ids)) {
			return null;
		}

		if (this.idKey == null) {
			this.fetchMappingContextAndConversionService();
		}

		Query query = new Query(Criteria.where(this.idKey).in(ids).and(this.tenantKey).is(this.tenantKey));
		return this.mongoTemplate.find(query, this.entityClass);
	}

	@Override
	public List<T> getAllEntities() {
		Query query = new Query(Criteria.where(this.tenantKey).is(this.tenantKey));
		return this.mongoTemplate.find(query, this.entityClass);
	}

	@Override
	public List<T> getEntities(int page, int pageSize) {
		Query query = new Query(Criteria.where(this.tenantKey).is(this.tenantKey));

		query.limit(pageSize);
		if (page > 1) {
			query.skip((page - 1) * pageSize);
		}
		return this.mongoTemplate.find(query, this.entityClass);
	}

	@Override
	public boolean insert(T entity) {
		if (entity == null) {
			return false;
		}

		if (!SecurityContext.isSameTenant(entity.getTenant())) {
			return false;
		}

		return super.insert(entity);
	}

	@Override
	public boolean update(T entity) {
		if (entity == null) {
			return false;
		}

		if (!SecurityContext.isSameTenant(entity.getTenant())) {
			return false;
		}

		return super.update(entity);
	}

	@Override
	public boolean addOrUpdate(T entity) {
		if (entity == null) {
			return false;
		}

		if (!SecurityContext.isSameTenant(entity.getTenant())) {
			return false;
		}

		return super.addOrUpdate(entity);
	}

	@Override
	public boolean delete(X primaryID) {
		if (primaryID == null) {
			return false;
		}

		Query query = new Query(Criteria.where(this.idKey).is(primaryID).and(this.tenantKey).is(this.tenantKey));
		DeleteResult result = this.mongoTemplate.remove(query, this.entityClass);
		if (result == null) {
			return false;
		}

		return result.getDeletedCount() == 1;
	}

	@Override
	public long count() {
		Query query = new Query(Criteria.where(this.tenantKey).is(this.tenantKey));
		long items = this.mongoTemplate.count(query, this.entityClass);
		return items;
	}
}
