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

package com.sangupta.jerry.rest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.entity.UserOwnedEntity;
import com.sangupta.jerry.exceptions.HttpException;
import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;

/**
 * A CRUD controller for entities that interact directly with database.
 * 
 * @author sangupta
 *
 * @param <T>
 * @param <X>
 */
@RestController
public abstract class AbstractCrudRestController<T, X> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCrudRestController.class);

    /**
     * The {@link DataStoreService} to use to work with entities.
     * 
     * @return
     */
    protected abstract DataStoreService<T, X> getService();

    /**
     * {@link Class} that represents the entity being persisted by this store.
     */
    protected final Class<T> entityClass;

    /**
     * {@link Class} that represents the primary key in the entity
     */
    protected final Class<X> primaryIDClass;

    /**
     * Check if the entity we are working with is user owned
     */
    protected final boolean isUserOwnedEntity;

    @SuppressWarnings("unchecked")
    protected AbstractCrudRestController() {
        // extract entity class and primary key class
        Type t = getClass().getGenericSuperclass();
        if (!(t instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Instance is not parameterized.");
        }

        Type[] actualTypeArguments = ((ParameterizedType) t).getActualTypeArguments();
        this.entityClass = (Class<T>) actualTypeArguments[0];
        this.primaryIDClass = (Class<X>) actualTypeArguments[1];

        this.isUserOwnedEntity = UserOwnedEntity.class.isAssignableFrom(this.entityClass);
    }

    /**
     * Read all entities from the database.
     * 
     * @return
     */
    @GetMapping("")
    public List<T> getAllEntities() {
        if (!this.allReadAll()) {
            throw new HttpException(HttpStatusCode.METHOD_NOT_ALLOWED);
        }

        return this.getService().getAll();
    }

    @GetMapping("/{entityID}")
    public T getEntity(@PathVariable X entityID) {
        if (AssertUtils.isEmpty(entityID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity ID is required");
        }

        T entity = this.getService().get(entityID);
        if (entity == null) {
            throw new HttpException(HttpStatusCode.NOT_FOUND, "No entity exists with that ID");
        }

        if (!this.isEntityOwnedByUser(entity)) {
            throw new HttpException(HttpStatusCode.FORBIDDEN, "Not the owner of the entity");
        }

        return entity;
    }

    @PostMapping("/{entityID}")
    public T updateEntity(@PathVariable X entityID, @RequestBody T entity) {
        if (entity == null) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity is required");
        }

        if (AssertUtils.isEmpty(entityID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "EntityID is required");
        }

        final X primaryID = this.getService().getPrimaryID(entity);
        if (AssertUtils.isEmpty(primaryID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity needs an ID to be updated");
        }

        if (!entityID.equals(primaryID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "EntityID mismatch in path and payload");
        }

        T existing = this.getService().get(primaryID);
        if (existing == null) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "No entity exists with that ID");
        }

        if (!this.isEntityOwnedByUser(existing)) {
            throw new HttpException(HttpStatusCode.FORBIDDEN, "Not the owner of the entity");
        }

        T updated = this.getService().update(entity);
        if (updated != null) {
            return entity;
        }

        LOGGER.warn("Unable to update entity");
        throw new HttpException(HttpStatusCode.SERVICE_UNAVAILABLE, "Unable to update the entity");
    }
    
    @PutMapping("/{entityID}")
    public T insertEntityWithID(@PathVariable X entityID, @RequestBody T entity) {
        if (entity == null) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity is required");
        }

        if (AssertUtils.isEmpty(entityID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity cannot have an ID, use update instead");
        }
        
        T inserted = this.getService().upsert(entity);
        if (inserted != null) {
            return entity;
        }

        LOGGER.warn("Unable to insert entity");
        throw new HttpException(HttpStatusCode.SERVICE_UNAVAILABLE, "Unable to insert the entity");
    }

    @PutMapping("")
    public T insertEntity(@RequestBody T entity) {
        if (entity == null) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity is required");
        }

        X primaryID = this.getService().getPrimaryID(entity);
        if (AssertUtils.isNotEmpty(primaryID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity cannot have an ID, use update instead");
        }

        T inserted = this.getService().insert(entity);
        if (inserted != null) {
            return entity;
        }

        LOGGER.warn("Unable to insert entity");
        throw new HttpException(HttpStatusCode.SERVICE_UNAVAILABLE, "Unable to insert the entity");
    }

    @DeleteMapping("/{primaryID}")
    public T removeEntity(@PathVariable X primaryID) {
        if (!this.allowDelete()) {
            throw new HttpException(HttpStatusCode.METHOD_NOT_ALLOWED, "DELETE not allowed on this resource");
        }

        T entity = this.getService().deleteForID(primaryID);
        if (entity != null) {
            return entity;
        }

        throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Unable to delete the entity");
    }

    /**
     * Check if the entity is owned by the user or not. Default implementation
     * checks if the entity implements {@link UserOwnedEntity} and matches the value
     * against the {@link SecurityContext#getUserID()}.
     * 
     * Note that for entitites that do not implement {@link UserOwnedEntity} this
     * method always returns <code>true</code>.
     * 
     * Implementations may override this method to suit their needs.
     * 
     * @param entity the entity to check
     * 
     * @return <code>true</code> if entity is owned by user, <code>false</code>
     *         otherwise.
     */
    protected boolean isEntityOwnedByUser(T entity) {
        if (!this.isUserOwnedEntity) {
            return true;
        }

        String userID = SecurityContext.getUserID();
        if (userID == null) {
            return false;
        }

        UserOwnedEntity uoe = (UserOwnedEntity) entity;
        return userID.equals(uoe.getUserID());
    }

    // Convenience methods for customization of the controller

    /**
     * Whether to allow read of entity via GET method.
     * 
     * @return
     */
    protected boolean allowGet() {
        return true;
    }

    /**
     * Whether to allow insertion of entity via PUT method.
     * 
     * @return
     */
    protected boolean allowInsert() {
        return true;
    }

    /**
     * Whether to allow update of entity via POST method.
     * 
     * @return
     */
    protected boolean allowUpdate() {
        return true;
    }

    /**
     * Whether to allow deletion of entity via DELETE method.
     * 
     * @return
     */
    protected boolean allowDelete() {
        return true;
    }

    /**
     * Whether to allow fetching of all entities from the database.
     * 
     * @return
     */
    protected boolean allReadAll() {
        return true;
    }

}
