package com.sangupta.jerry.db.impl;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.util.GsonUtils;

/**
 * An in-memory {@link DataStoreService} implementation based on
 * {@link InMemoryDataStoreServiceImpl} that uses a JSON file on disk as a
 * backup strategy. The entire data set is loaded in-memory at load time, and
 * then written to disk, when a call is made explicitly to {@link #close()}
 * method. If {@link #close()} is not called, any changes in memory are lost. If
 * intermediate persistence is required, the callee may invoke {@link #flush()}
 * method.
 * 
 * @author sangupta
 *
 * @param <T>
 * @param <X>
 */
public class DiskBackedInMemoryDataStoreServiceImpl<T, X> extends InMemoryDataStoreServiceImpl<T, X> implements Closeable {

    protected final File dbPath;

    public DiskBackedInMemoryDataStoreServiceImpl(File dbPath) {
        super();

        this.dbPath = dbPath;

        final Gson gson = GsonUtils.getGson();

        try {
            Type type = new TypeToken<HashMap<X, JsonObject>>() {
            }.getType();
            String json = FileUtils.readFileToString(this.dbPath, StandardCharsets.UTF_8);
            HashMap<X, JsonObject> map = gson.fromJson(json, type);
            if (map != null) {
                for (Entry<X, JsonObject> entry : map.entrySet()) {
                    X key = entry.getKey();
                    JsonObject value = entry.getValue();

                    T entity = gson.fromJson(value, this.entityClass);
                    this.dataStore.put(key, entity);
                }
            }
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Flush all data back to disk.
     */
    public void flush() {
        try {
            String json = GsonUtils.getGson().toJson(this.dataStore);
            FileUtils.writeStringToFile(this.dbPath, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.flush();
    }

}
