package com.sangupta.jerry.db.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.sangupta.jerry.db.DataStoreOperation;
import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.db.EntityScanner;
import com.sangupta.jerry.db.EntityScanner.EntityDetails;
import com.sangupta.jerry.entity.CreateTimeStampedEntity;
import com.sangupta.jerry.entity.DeleteTimeStampedEntity;
import com.sangupta.jerry.entity.SoftDeleteEntity;
import com.sangupta.jerry.entity.UpdateTimeStampedEntity;
import com.sangupta.jerry.entity.UserOwnedEntity;
import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;

/**
 * An abstract implementation for {@link DataStoreService} that handles the
 * incoming entity object for creation time, update time and the user who owns
 * the entity.
 * 
 * @author sangupta
 * 
 * @since 2.0.0
 *
 * @param <T>
 * @param <X>
 */
public abstract class AbstractDataStoreServiceImpl<T, X> implements DataStoreService<T, X> {

    /**
     * {@link Class} that represents the entity being persisted by this store.
     */
    protected final Class<T> entityClass;

    /**
     * {@link Class} that represents the primary key in the entity
     */
    protected final Class<X> primaryIDClass;

    /**
     * {@link Field} that represents that primary key attribute in the entity
     */
    protected final EntityDetails entityDetails;

    @SuppressWarnings("unchecked")
    protected AbstractDataStoreServiceImpl() {
        // extract entity class and primary key class
        Type t = getClass().getGenericSuperclass();
        if (!(t instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Instance is not parameterized.");
        }

        Type[] actualTypeArguments = ((ParameterizedType) t).getActualTypeArguments();
        this.entityClass = (Class<T>) actualTypeArguments[0];
        this.primaryIDClass = (Class<X>) actualTypeArguments[1];

        // set soft delete params
        this.entityDetails = EntityScanner.getDetails(this.entityClass);
    }

    /**
     * Return the value of the primary key for this entity object. Concrete
     * implementation may override this method to provide a better performance to
     * this method, than using Java Reflection by default.
     * 
     * @param entity the entity being used in the data store
     * 
     * @return the primary ID
     */
    public X getPrimaryID(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        if (this.entityDetails.idField == null) {
            throw new IllegalStateException("This DataStore must either implement getPrimaryID() method, or add an @Id annotation to a field in the entity");
        }
        try {
            return this.primaryIDClass.cast(this.entityDetails.idField.get(entity));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to access primary key field", e);
        }
    }
    
    @Override
    public final T get(X primaryID) {
        if (AssertUtils.isEmpty(primaryID)) {
            return null;
        }
        
        return this.getEntity(primaryID);
    }
    
    @Override
    public final List<T> getAll() {
        List<T> entities = this.getAllEntities();
        
        if(entities == null) {
            entities = new ArrayList<>();
        }
        
        return entities;
    }
    
    @Override
    public final List<T> getAll(int page, int pageSize) {
        if(page < 0) {
            return null;
        }
        
        if(pageSize <= 0) {
            return null;
        }
        
        List<T> entities = this.getAllEntities(page, pageSize);
        
        if(entities == null) {
            entities = new ArrayList<>();
        }
        
        return entities;
    }

    @Override
    public final T insert(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        this.massage(entity, DataStoreOperation.CREATE);
        return this.insertEntity(entity);
    }

    @Override
    public final T update(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        this.massage(entity, DataStoreOperation.UPDATE);
        return this.updateEntity(entity);
    }

    @Override
    public final T upsert(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        this.massage(entity, DataStoreOperation.UPSERT);
        return this.upsertEntity(entity);
    }

    @Override
    public final T delete(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        this.massage(entity, DataStoreOperation.DELETE);
        if (entity instanceof SoftDeleteEntity) {
            return this.update(entity);
        }

        return this.deleteEntity(entity);
    }

    @Override
    public final T deleteForID(X primaryID) {
        if (primaryID == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (this.entityDetails.isSoftDelete()) {
            T entity = this.get(primaryID);
            return this.delete(entity);
        }

        return this.deleteEntityForID(primaryID);
    }

    /**
     * Massage the entity to set fields for {@link CreateTimeStampedEntity},
     * {@link UpdateTimeStampedEntity} and {@link UserOwnedEntity}.
     * 
     * @param entity
     * @param operation
     */
    protected final void massage(T entity, DataStoreOperation operation) {
        long currentTime = System.currentTimeMillis();

        if (entity instanceof CreateTimeStampedEntity) {
            CreateTimeStampedEntity cte = (CreateTimeStampedEntity) entity;
            cte.setCreateTime(currentTime);
        }

        if (entity instanceof UpdateTimeStampedEntity) {
            UpdateTimeStampedEntity ute = (UpdateTimeStampedEntity) entity;
            ute.setUpdateTime(currentTime);
        }

        if (operation == DataStoreOperation.DELETE) {
            if (entity instanceof DeleteTimeStampedEntity) {
                DeleteTimeStampedEntity dte = (DeleteTimeStampedEntity) entity;
                dte.setDeleteTime(currentTime);
            }

            if (entity instanceof SoftDeleteEntity) {
                SoftDeleteEntity sde = (SoftDeleteEntity) entity;
                sde.setDeleted(true);
            }
        }

        if (entity instanceof UserOwnedEntity) {
            UserOwnedEntity uoe = (UserOwnedEntity) entity;
            String entityUserID = uoe.getUserID();
            String currentUserID = SecurityContext.getUserID();

            if ((entityUserID == null) || (entityUserID != null && entityUserID.equals(currentUserID))) {
                uoe.setUserID(currentUserID);
            } else {
                throw new SecurityException("Entity is owned by a different user");
            }
        }

        this.massageEntity(entity, operation);
    }

    /**
     * Extension point for child classes that want to extend massaging the entity
     * other than what the implementation supports.
     * 
     * @param entity
     * @param operation
     */
    protected void massageEntity(T entity, DataStoreOperation operation) {
        // this method is left intentional for child classes
        // to override as needed
    }
    
    /**
     * 
     * @param primaryID the primary ID for which entity is required. Will never be <code>null</code>
     * 
     * @return
     */
    protected abstract T getEntity(X primaryID);

    /**
     * 
     * @param entity the entity to be inserted. Will never be <code>null</code>
     * 
     * @return
     */
    protected abstract T insertEntity(T entity);

    /**
     * 
     * @param entity the entity to be updated. Will never be <code>null</code>
     * 
     * @return
     */
    protected abstract T updateEntity(T entity);

    /**
     * 
     * @param entity the entity to be upserted. Will never be <code>null</code>
     * 
     * @return
     */
    protected abstract T upsertEntity(T entity);

    /**
     * Remove the entity from the data store. For {@link SoftDeleteEntity} the
     * {@link #updateEntity(Object)} method is called.
     * 
     * @param entity the entity to be removed. Will never be <code>null</code>
     * 
     * @return
     */
    protected abstract T deleteEntity(T entity);

    /**
     * 
     * @param primaryID the primary ID for which to remove entity. Will never be
     *                  <code>null</code>
     * @return
     */
    protected abstract T deleteEntityForID(X primaryID);

    protected abstract List<T> getAllEntities();
    
    protected abstract List<T> getAllEntities(int page, int pageSize);
}
