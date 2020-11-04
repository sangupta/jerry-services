package com.sangupta.jerry.db.service.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.annotation.Id;

import com.sangupta.jerry.db.DataStoreService;

/**
 * Abstracted tests that should pass on any {@link DataStoreService} implementation.
 * 
 * @author sangupta
 *
 */
public abstract class AbstarctDataStoreServiceImplTest {
    
    protected static final SecureRandom RANDOM = new SecureRandom();

    protected int count = 100;
    
    protected int div = 3;
    
    protected abstract DataStoreService<TestObject, String> getService();
    
    protected DataStoreService<TestObject, String> service;
    
    @Before
    public void beforeEachTest() {
        this.service = this.getService();
    }
    
    @After
    public void afterEachTest() {
        this.service.close();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetPrimaryIDForNull() {
        this.service.getPrimaryID(null);
    }
    
    @Test
    public void testGetPrimaryID() {
        TestObject obj = new TestObject("hello");
        Assert.assertEquals("hello", this.service.getPrimaryID(obj));
    }
    
    @Test
    public void testGetForNull() {
        Assert.assertNull(this.service.get(null));
        Assert.assertNull(this.service.get("hello"));
        
        TestObject obj = new TestObject("hello");
        this.service.insert(obj);
        Assert.assertEquals(obj, this.service.get("hello"));
    }
    
    @Test
    public void testGetMultipleCollection() {
        Collection<String> ids = null;
        Assert.assertNull(this.service.getMultiple(ids));
        
        ids = List.of("hello", "world");
        Assert.assertEquals(0, this.service.getMultiple(ids).size());
        
        TestObject obj = new TestObject("hello");
        this.service.insert(obj);
        
        Assert.assertEquals(1, this.service.getMultiple(ids).size());
        
        TestObject obj2 = new TestObject("world");
        this.service.insert(obj2);
        
        Assert.assertEquals(2, this.service.getMultiple(ids).size());
    }
    
    @Test
    public void testGetMultipleArray() {
        String[] ids = null;
        Assert.assertNull(this.service.getMultiple(ids));
        
        ids = new String[] { "hello", "world" };
        Assert.assertEquals(0, this.service.getMultiple(ids).size());
        
        TestObject obj = new TestObject("hello");
        this.service.insert(obj);
        
        Assert.assertEquals(1, this.service.getMultiple(ids).size());
        
        TestObject obj2 = new TestObject("world");
        this.service.insert(obj2);
        
        Assert.assertEquals(2, this.service.getMultiple(ids).size());
    }
    
    @Test
    public void testGetAll() {
        Assert.assertEquals(0, this.service.getAll().size());
        
        int max = 20 + RANDOM.nextInt(100);
        for(int index = 0; index < max; index++) {
            this.service.insert(new TestObject("random-" + index));
        }
        
        Assert.assertEquals(max, this.service.getAll().size());
    }
    
    @Test
    public void testGetAllPaginated() {
        Assert.assertNull(this.service.getAll(-1, 10));
        Assert.assertNull(this.service.getAll(0, -10));
        Assert.assertNull(this.service.getAll(0, 0));
        
        int start = 0, end = 8;
        for(int index = start; index < end; index++) {
            this.service.insert(new TestObject("random-" + index));
        }
        
        Assert.assertEquals(8, this.service.count());
        Assert.assertEquals(8, this.service.getAll(0, 10).size());
        
        Assert.assertEquals(5, this.service.getAll(0, 5).size());
        Assert.assertEquals(3, this.service.getAll(1, 5).size());
        
        Assert.assertEquals(3, this.service.getAll(0, 3).size());
        Assert.assertEquals(3, this.service.getAll(1, 3).size());
        Assert.assertEquals(2, this.service.getAll(2, 3).size());
        
        Assert.assertEquals(0, this.service.getAll(10, 3).size());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInsertNull() {
        Assert.assertNull(this.service.insert(null));
    }
    
    @Test
    public void testInsert() {
        int max = 20 + RANDOM.nextInt(100);
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            Assert.assertEquals(obj, this.service.insert(obj));
        }
        
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            Assert.assertNull(this.service.insert(obj));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNull() {
        Assert.assertNull(this.service.update(null));
    }
    
    @Test
    public void testUpdate() {
        int max = 20 + RANDOM.nextInt(100);
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            Assert.assertNull(this.service.update(obj));
        }
        
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            this.service.insert(obj);
        }
        
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            Assert.assertEquals(obj, this.service.update(obj));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpsertNull() {
        Assert.assertNull(this.service.upsert(null));
    }
    
    @Test
    public void testUpsert() {
        int max = 20 + RANDOM.nextInt(100);
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            Assert.assertEquals(obj, this.service.upsert(obj));
        }
        
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            Assert.assertEquals(obj, this.service.upsert(obj));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteNull() {
        Assert.assertNull(this.service.delete(null));
    }
    
    @Test
    public void testDelete() {
        int max = 20 + RANDOM.nextInt(100);
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            Assert.assertNull(this.service.delete(obj));
        }
        
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            this.service.insert(obj);
        }
        
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            Assert.assertEquals(obj, this.service.delete(obj));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteForIDNull() {
        Assert.assertNull(this.service.deleteForID(null));
    }
    
    @Test
    public void testDeleteForID() {
        Assert.assertNull(this.service.deleteForID("hello"));
        this.service.insert(new TestObject("hello"));
        Assert.assertNotNull(this.service.deleteForID("hello"));
        Assert.assertNull(this.service.deleteForID("hello"));
    }
    
    @Test
    public void testUpdateForCollection() {
        List<String> ids = null;
        Assert.assertNull(this.service.deleteMultiple(ids));
        
        ids = new ArrayList<>();
        final int max = 20 + RANDOM.nextInt(100);
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            if(index < 20) {
                ids.add("random-" + index);
            }
            
            Assert.assertNotNull(this.service.insert(obj));
        }
        
        List<TestObject> list = this.service.deleteMultiple(ids);
        Assert.assertNotNull(list);
        Assert.assertEquals(20, list.size());
    }
    
    @Test
    public void testUpdateForArray() {
        String[] ids = null;
        Assert.assertNull(this.service.deleteMultiple(ids));
        
        ids = new String[20];
        final int max = 20 + RANDOM.nextInt(100);
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            if(index < 20) {
                ids[index] = "random-" + index;
            }
            
            Assert.assertNotNull(this.service.insert(obj));
        }
        
        List<TestObject> list = this.service.deleteMultiple(ids);
        Assert.assertNotNull(list);
        Assert.assertEquals(20, list.size());
    }
    
    @Test
    public void testCount() {
        Assert.assertEquals(0, this.service.count());
        
        int max = 20 + RANDOM.nextInt(100);
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            this.service.insert(obj);
            Assert.assertEquals(index + 1, this.service.count());
        }
    }
    
    @Test
    public void testDeleteAll() {
        Assert.assertEquals(0, this.service.count());
        
        int max = 20 + RANDOM.nextInt(100);
        for(int index = 0; index < max; index++) {
            TestObject obj = new TestObject("random-" + index);
            this.service.insert(obj);
            Assert.assertEquals(index + 1, this.service.count());
        }
        
        this.service.deleteAll();
        Assert.assertEquals(0, this.service.count());
    }
    
    protected static class TestObject {
        
        @Id
        String id;
        
        TestObject(String id) {
            this.id = id;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            
            if(!(obj instanceof TestObject)) {
                return false;
            }
            
            return this.id.equals(((TestObject) obj).id);
        }
    }
}
