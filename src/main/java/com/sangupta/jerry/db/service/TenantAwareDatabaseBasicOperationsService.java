package com.sangupta.jerry.db.service;

import com.sangupta.jerry.entity.TenantAwareEntity;

/**
 * Contract for all data-stores that honor the {@link TenantAwareEntity} to make
 * sure that the entities are only served when the requesting user is the same
 * tenant as that of the entity.
 * 
 * @author sangupta
 *
 * @param <T> The entity object type which is persisted in the data store
 * 
 * @param <X> The primary ID key for this entity object
 */
public interface TenantAwareDatabaseBasicOperationsService<T extends TenantAwareEntity, X> extends DatabaseBasicOperationsService<T, X> {

}
