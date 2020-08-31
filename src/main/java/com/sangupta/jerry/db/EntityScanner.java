package com.sangupta.jerry.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import com.sangupta.jerry.entity.ReadOnlyEntity;
import com.sangupta.jerry.entity.SoftDeleteEntity;
import com.sangupta.jerry.util.AssertUtils;

/**
 * 
 * @author sangupta
 *
 */
public class EntityScanner {
   
    /**
     * logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityScanner.class);
    
    public static final Map<Class<?>, EntityDetails> MAP = new HashMap<>();
    
    public static EntityDetails getDetails(Class<?> clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        
        EntityDetails details = MAP.get(clazz);
        if(details != null) {
            return details;
        }
        
        details = scanEntity(clazz);
        MAP.put(clazz, details);
        
        return details;
    }
    
    protected static EntityDetails scanEntity(Class<?> clazz) {
        EntityDetails details = populatePrimaryField(clazz);
        
        details.softDelete = clazz.isAssignableFrom(SoftDeleteEntity.class);
        details.readOnly = clazz.isAssignableFrom(ReadOnlyEntity.class);
        
        return details;
    }
    
    protected static EntityDetails populatePrimaryField(Class<?> clazz) {
        // figure out the primary field
        Field[] fields = clazz.getDeclaredFields();
        if (AssertUtils.isEmpty(fields)) {
            LOGGER.warn("Entity class has no field defined: {}", clazz);
            throw new IllegalArgumentException("Entity class has no fields defined");
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

        return new EntityDetails(primaryField);
    }
    
    public static class EntityDetails {
        
        public final Field idField;
        
        public final String idFieldName;
        
        private boolean softDelete;
        
        private boolean readOnly;
        
        public boolean isSoftDelete() {
            return this.softDelete;
        }
        
        public boolean isReadOnly() {
            return this.readOnly;
        }
        
        public EntityDetails(Field primaryField) {
            this.idField = primaryField;
            if(primaryField != null) {
                this.idFieldName = primaryField.getName();
                this.idField.setAccessible(true);
            } else {
                this.idFieldName = null;
            }
        }
        
    }
    
}
