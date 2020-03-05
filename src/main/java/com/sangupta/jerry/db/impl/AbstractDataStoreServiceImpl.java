package com.sangupta.jerry.db.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.entity.CreationTimeStampedEntity;
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
     * My personal logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataStoreServiceImpl.class);

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
    protected final Field primaryKeyField;

    /**
     * Name of the field as represented by {@link #primaryKeyField}
     */
    protected final String idKeyFieldName;
    
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

        // figure out the primary field
        Field[] fields = this.entityClass.getDeclaredFields();
        if (AssertUtils.isEmpty(fields)) {
            LOGGER.warn("Entity class has no field defined: {}", this.entityClass);
            throw new IllegalArgumentException("Entity class has no field defined");
        }

        // find the primary key field
        Field primaryField = null;
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                primaryField = field;
                break;
            }
        }

        if (primaryField == null) {
            this.primaryKeyField = null;
            this.idKeyFieldName = null;
            return;
        }

        this.primaryKeyField = primaryField;
        this.idKeyFieldName = primaryField.getName();
    }

    /**
     * Return the value of the primary key for this entity object. Concrete
     * implementation may override this method to provide a better performance
     * to this method, than using Java Reflection by default.
     * 
     * @param entity the entity being used in the data store
     * 
     * @return the primary ID 
     */
    protected X getPrimaryID(T entity) {
        if(this.primaryKeyField == null) {
            throw new IllegalStateException("This DataStore must either implement getPrimaryID() method, or add an @Id annotation to a field in the entity");
        }
        try {
            this.primaryKeyField.setAccessible(true);
            return this.primaryIDClass.cast(this.primaryKeyField.get(entity));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to access primary key field", e);
        }
    }

    @Override
    public final boolean insert(T entity) {
        if (entity == null) {
            return false;
        }

        this.massage(entity, false);

        return this.insertEntity(entity);
    }

    @Override
    public final boolean update(T entity) {
        if (entity == null) {
            return false;
        }

        this.massage(entity, true);
        return this.updateEntity(entity);
    }

    @Override
    public final boolean upsert(T entity) {
        if (entity == null) {
            return false;
        }

        this.massage(entity, true);
        return this.upsertEntity(entity);
    }

    /**
     * Massage the entity to set fields for {@link CreationTimeStampedEntity},
     * {@link UpdateTimeStampedEntity} and {@link UserOwnedEntity}.
     * 
     * @param entity
     * @param isUpdateRequest
     */
    protected final void massage(T entity, boolean isUpdateRequest) {
        long currentTime = System.currentTimeMillis();

        if (entity instanceof CreationTimeStampedEntity) {
            CreationTimeStampedEntity cte = (CreationTimeStampedEntity) entity;
            cte.setCreated(currentTime);
        }
        if (entity instanceof UpdateTimeStampedEntity) {
            UpdateTimeStampedEntity ute = (UpdateTimeStampedEntity) entity;
            ute.setUpdated(currentTime);
        }
        if (entity instanceof UserOwnedEntity) {
            UserOwnedEntity uoe = (UserOwnedEntity) entity;
            uoe.setUserID(SecurityContext.getUserID());
        }

        this.massageEntity(entity, isUpdateRequest);
    }

    /**
     * Extension point for child classes that want to extend massaging the entity
     * other than what the implementation supports.
     * 
     * @param entity
     * @param isUpdateRequest
     */
    protected void massageEntity(T entity, boolean isUpdateRequest) {
        // this method is left intentional for child classes
        // to override as needed
    }

    protected abstract boolean insertEntity(T entity);

    protected abstract boolean updateEntity(T entity);

    protected abstract boolean upsertEntity(T entity);

}
