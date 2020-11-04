package com.sangupta.jerry.db.service.impl;

import org.junit.Ignore;

import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.db.impl.MongoTemplateDataStoreServiceImpl;

@Ignore
public class MongoTemplateDataStoreServiceImplTest extends AbstarctDataStoreServiceImplTest {

    @Override
    protected DataStoreService<TestObject, String> getService() {
        MongoTemplateDataStoreServiceImpl<TestObject, String> service = new MongoTemplateDataStoreServiceImpl<TestObject, String>() {};
        
        return service;
    }

}
