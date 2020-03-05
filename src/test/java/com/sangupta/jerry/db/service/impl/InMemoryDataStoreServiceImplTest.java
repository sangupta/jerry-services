package com.sangupta.jerry.db.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.annotation.Id;

import com.sangupta.jerry.db.DataStoreService;
import com.sangupta.jerry.db.impl.InMemoryDataStoreServiceImpl;

public class InMemoryDataStoreServiceImplTest {

    protected int count = 100;
    
    protected int div = 3;
    
    @Test
    public void testNulls() {
        DataStoreService<TestObject, String> service = new InMemoryDataStoreServiceImpl<TestObject, String>() {};
        
        Assert.assertNull(service.get(null));
        Assert.assertNull(service.getMultiple((Collection<String>) null));
        Assert.assertNull(service.getMultiple((String[]) null));
        Assert.assertNotNull(service.getAll());
        Assert.assertNotNull(service.getAll(1, 10));
        Assert.assertFalse(service.insert(null));
        Assert.assertFalse(service.update(null));
        Assert.assertFalse(service.upsert(null));
        Assert.assertNull(service.delete(null));
        Assert.assertNull(service.deleteMultiple((Collection<String>) null));
        Assert.assertNull(service.deleteMultiple((String[]) null));
    }
    
    @Test
    public void testBasicCrud() {
        DataStoreService<TestObject, String> service = new InMemoryDataStoreServiceImpl<TestObject, String>() {};
        TestObject t1 = new TestObject("hello");
        TestObject t2 = new TestObject("hello2");
        
        Assert.assertEquals(0, service.count());
        
        Assert.assertTrue(service.insert(t1));
        Assert.assertEquals(1, service.count());
        Assert.assertEquals(t1, service.get("hello"));
        
        Assert.assertFalse(service.update(t2));
        
        Assert.assertTrue(service.insert(t2));
        Assert.assertEquals(2, service.count());
        Assert.assertEquals(t1, service.get("hello"));
        Assert.assertEquals(t2, service.get("hello2"));
        
        Assert.assertFalse(service.insert(t2));
        Assert.assertTrue(service.update(t2));
        
        Assert.assertNull(service.delete("hello3"));
        Assert.assertNotNull(service.delete("hello2"));
        Assert.assertEquals(1, service.count());
        
        Assert.assertNull(service.delete("hello2"));
        Assert.assertEquals(1, service.count());
        
        Assert.assertTrue(service.insert(t2));
        Assert.assertEquals(2, service.count());
        
        service.deleteAll();
        Assert.assertEquals(0, service.count());
    }
    
    @Test
    public void testRemoveMultipleCollection() {
        DataStoreService<TestObject, String> service = new InMemoryDataStoreServiceImpl<TestObject, String>() {};
        
        TestObject[] entities = new TestObject[count];
        for(int index = 0; index < count; index++) {
            entities[index] = new TestObject("entity-" + index);
            Assert.assertTrue(service.insert(entities[index]));
        }
        
        // ids to remove
        List<String> toRemove = new ArrayList<>();
        List<String> toRetain = new ArrayList<>();
        for(int index = 0; index < count; index++) {
            if(index % div == 0) {
                toRemove.add("entity-" + index);
            } else {
                toRetain.add("entity-" + index);
            }
        }
        
        // remove
        List<TestObject> removed = service.deleteMultiple(toRemove);
        Assert.assertNotNull(removed);
        Assert.assertEquals(toRemove.size(), removed.size());
        for(int index = 0; index < removed.size(); index++) {
            Assert.assertNull(service.get(removed.get(index).id));
        }
        
        // read multiple
        List<TestObject> retained = service.getMultiple(toRemove);
        Assert.assertNotNull(retained);
        Assert.assertEquals(0, retained.size());
        
        retained = service.getMultiple(toRetain);
        Assert.assertNotNull(retained);
        Assert.assertEquals(toRetain.size(), retained.size());
        for(int index = 0; index < retained.size(); index++) {
            Assert.assertNotNull(service.get(retained.get(index).id));
            Assert.assertEquals(retained.get(index), service.get(retained.get(index).id));
        }
    }
    
    @Test
    public void testRemoveMultipleArray() {
        DataStoreService<TestObject, String> service = new InMemoryDataStoreServiceImpl<TestObject, String>() {};
        
        TestObject[] entities = new TestObject[count];
        for(int index = 0; index < count; index++) {
            entities[index] = new TestObject("entity-" + index);
            Assert.assertTrue(service.insert(entities[index]));
        }
        
        List<String> toRemove = new ArrayList<>();
        List<String> toRetain = new ArrayList<>();
        for(int index = 0; index < count; index++) {
            if(index % div == 0) {
                toRemove.add("entity-" + index);
            } else {
                toRetain.add("entity-" + index);
            }
        }
        
        // remove
        List<TestObject> removed = service.deleteMultiple(toRemove.toArray(new String[] {}));
        Assert.assertNotNull(removed);
        Assert.assertEquals(toRemove.size(), removed.size());
        for(int index = 0; index < removed.size(); index++) {
            Assert.assertNull(service.get(removed.get(index).id));
        }
        
        // read multiple
        List<TestObject> retained = service.getMultiple(toRemove.toArray(new String[] {}));
        Assert.assertNotNull(retained);
        Assert.assertEquals(0, retained.size());
        
        retained = service.getMultiple(toRetain.toArray(new String[] {}));
        Assert.assertNotNull(retained);
        Assert.assertEquals(toRetain.size(), retained.size());
        for(int index = 0; index < retained.size(); index++) {
            Assert.assertNotNull(service.get(retained.get(index).id));
            Assert.assertEquals(retained.get(index), service.get(retained.get(index).id));
        }
    }
    
    @Test
    public void testPaginated() {
        DataStoreService<TestObject, String> service = new InMemoryDataStoreServiceImpl<TestObject, String>() {};
        
        TestObject[] entities = new TestObject[count];
        for(int index = 0; index < 44; index++) {
            entities[index] = new TestObject("entity-" + index);
            Assert.assertTrue(service.insert(entities[index]));
        }
        
        Assert.assertEquals(10, service.getAll(1, 10).size());
        Assert.assertEquals(10, service.getAll(2, 10).size());
        Assert.assertEquals(10, service.getAll(3, 10).size());
        Assert.assertEquals(10, service.getAll(4, 10).size());
        Assert.assertEquals(4, service.getAll(5, 10).size());
        
        Assert.assertEquals(44, service.getAll(1, 100).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllZeroPage() {
        DataStoreService<TestObject, String> service = new InMemoryDataStoreServiceImpl<TestObject, String>() {};
        service.getAll(0, 10);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetAllZeroPageSize() {
        DataStoreService<TestObject, String> service = new InMemoryDataStoreServiceImpl<TestObject, String>() {};
        service.getAll(1, 0);
    }
    
    private static class TestObject {
        @Id
        String id;
        TestObject(String id) {
            this.id = id;
        }
    }
}
