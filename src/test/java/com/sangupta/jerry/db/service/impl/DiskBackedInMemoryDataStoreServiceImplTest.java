package com.sangupta.jerry.db.service.impl;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.db.impl.DiskBackedInMemoryDataStoreServiceImpl;

public class DiskBackedInMemoryDataStoreServiceImplTest extends AbstarctDataStoreServiceImplTest {

    @Override
    protected DataStoreService<TestObject, String> getService() {
        File file = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString() + ".json");

        return new DiskBackedInMemoryDataStoreServiceImpl<TestObject, String>(file) {
            
            @Override
            public void close() {
                super.close();
                FileUtils.deleteQuietly(file);
            }
            
        };
    }

}
