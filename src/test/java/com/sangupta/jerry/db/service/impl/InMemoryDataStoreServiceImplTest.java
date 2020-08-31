package com.sangupta.jerry.db.service.impl;

import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.db.impl.InMemoryDataStoreServiceImpl;

public class InMemoryDataStoreServiceImplTest extends AbstarctDataStoreServiceImplTest {

    @Override
    protected DataStoreService<TestObject, String> getService() {
        return new InMemoryDataStoreServiceImpl<TestObject, String>() {
            
        };
    }

}
