package com.sangupta.jerry.db.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import com.sangupta.jerry.db.DataStoreService;

/**
 * A Redis implementation to {@link DataStoreService} that uses
 * {@link RedisTemplate}. The data is stored as a
 * <a href="https://redis.io/commands#hash">Redis HASH</a>. By default the hash
 * name used is the FQCN for the entity being managed by the instance of this
 * store. It can be modified by overriding the {@link #getRedisHashKey()}
 * method.
 * 
 * 
 * @author sangupta
 *
 * @param <T>
 * @param <X>
 */
public class RedisDataStoreServiceImpl<T, X> extends AbstractDataStoreServiceImpl<T, X> {

    @Inject
    protected RedisTemplate<String, Object> redisTemplate;

    @Override
    public long count() {
        return this.redisTemplate.opsForHash().size(this.getRedisHashKey());
    }

    @Override
    public void deleteAll() {
        this.redisTemplate.delete(this.getRedisHashKey());
    }

    @Override
    protected T getEntity(X primaryID) {
        Object entity = this.redisTemplate.opsForHash().get(this.getRedisHashKey(), primaryID);
        if(entity == null) {
            return null;
        }
        
        return this.entityClass.cast(entity);
    }

    @Override
    protected T insertEntity(T entity) {
        X primaryID = this.getPrimaryID(entity);
        Boolean inserted = this.redisTemplate.opsForHash().putIfAbsent(this.getRedisHashKey(), primaryID, entity);
        if(inserted != null && inserted) {
            return entity;
        }
        
        return null;
    }

    @Override
    protected T updateEntity(T entity) {
        X primaryID = this.getPrimaryID(entity);
        boolean exists = this.redisTemplate.opsForHash().hasKey(this.getRedisHashKey(), primaryID);
        if(!exists) {
            return null;
        }
        
        this.redisTemplate.opsForHash().put(this.getRedisHashKey(), primaryID, entity);
        return entity;
    }

    @Override
    protected T upsertEntity(T entity) {
        X primaryID = this.getPrimaryID(entity);
        this.redisTemplate.opsForHash().put(this.getRedisHashKey(), primaryID, entity);
        return entity;
    }

    @Override
    protected T deleteEntity(T entity) {
        X primaryID = this.getPrimaryID(entity);
        Long removed = this.redisTemplate.opsForHash().delete(this.getRedisHashKey(), primaryID);
        if(removed != null && removed == 1) {
            return entity;
        }
        
        return null;
    }

    @Override
    protected T deleteEntityForID(X primaryID) {
        Object value = this.redisTemplate.opsForHash().get(this.getRedisHashKey(), primaryID);
        Long removed = this.redisTemplate.opsForHash().delete(this.getRedisHashKey(), primaryID);
        if(removed != null && removed == 1) {
            return this.entityClass.cast(value);
        }
        
        return null;
    }

    @Override
    protected List<T> getAllEntities() {
        List<Object> objects = this.redisTemplate.opsForHash().values(this.getRedisHashKey());
        if(objects == null) {
            return null;
        }
        
        List<T> list = new ArrayList<>();
        for(Object obj : objects) {
            list.add(this.entityClass.cast(obj));
        }
        
        return list;
    }

    @Override
    protected List<T> getAllEntities(int page, int pageSize, int start, int end) {
        ScanOptions scanOptions = ScanOptions.scanOptions().count(end).build();
        Cursor<Entry<Object, Object>> cursor = this.redisTemplate.opsForHash().scan(this.getRedisHashKey(), scanOptions);
        if(cursor == null) {
            return null;
        }
        
        List<T> list = new ArrayList<>();
        int skipped = 0;
        while(cursor.hasNext()) {
            Entry<Object, Object> entry = cursor.next();
            if(skipped < start) {
                skipped++;
                continue;
            }

            list.add(this.entityClass.cast(entry.getValue()));
        }
        
        return list;
    }

    @Override
    protected List<T> getMultipleEntities(Collection<X> ids) {
        List<T> list = new ArrayList<>();
        for(X id  : ids) {
            list.add(this.entityClass.cast(this.getEntity(id)));
        }
        
        return list;
    }

    @Override
    protected List<T> getMultipleEntities(X[] ids) {
        List<T> list = new ArrayList<>();
        for(X id  : ids) {
            list.add(this.entityClass.cast(this.getEntity(id)));
        }
        
        return list;
    }

    @Override
    protected List<T> deleteMultipleEntities(Collection<X> ids) {
        List<T> list = new ArrayList<>();
        for(X id : ids) {
            list.add(this.deleteEntityForID(id));
        }
        
        return list;
    }

    @Override
    protected List<T> deleteMultipleEntities(X[] ids) {
        List<T> list = new ArrayList<>();
        for(X id : ids) {
            list.add(this.deleteEntityForID(id));
        }
        
        return list;
    }

    /**
     * Return the key to be used in Redis HASH for storing all elements of this
     * type.
     * 
     * @return
     */
    protected String getRedisHashKey() {
        return this.getClass().getName();
    }
}
