//package com.sangupta.jerry.db.service.impl;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.data.annotation.Id;
//
//import com.sangupta.jerry.db.DataStoreService;
//import com.sangupta.jerry.db.impl.InMemoryDataStoreServiceImpl;
//import com.sangupta.jerry.entity.CreateTimeStampedEntity;
//import com.sangupta.jerry.entity.UpdateTimeStampedEntity;
//import com.sangupta.jerry.entity.UserAwarePrincipal;
//import com.sangupta.jerry.entity.UserOwnedEntity;
//import com.sangupta.jerry.security.SecurityContext;
//
///**
// * Test various entity types like {@link CreateTimeStampedEntity}, {@link UpdateTimeStampedEntity}, 
// * {@link UserOwnedEntity} etc being used in the {@link DataStoreService} implementations.
// * 
// * @author sangupta
// *
// */
//public class DataStoreServiceEntityTypesTest {
//    
//    @Test
//    public void testCreationTimeStampedEntity() {
//        DataStoreService<CreationTimeTestObject, String> service = new InMemoryDataStoreServiceImpl<CreationTimeTestObject, String>() {};
//        
//        // insert
//        CreationTimeTestObject t = new CreationTimeTestObject("hello");
//        Assert.assertEquals(0, t.created);
//        
//        long time = System.currentTimeMillis();
//        Assert.assertTrue(service.insert(t));
//        Assert.assertNotEquals(0, t.created);
//        Assert.assertTrue(time - t.created < 20);
//        
//        // update
//        long old = t.created;
//        Assert.assertTrue(service.update(t));
//        Assert.assertEquals(old, t.created);
//    }
//    
//    @Test
//    public void testUpdateTimeStampedEntity() {
//        DataStoreService<UpdateTimeTestObject, String> service = new InMemoryDataStoreServiceImpl<UpdateTimeTestObject, String>() {};
//        
//        // insert
//        UpdateTimeTestObject t = new UpdateTimeTestObject("hello");
//        Assert.assertEquals(0, t.updated);
//
//        long time = System.currentTimeMillis();
//        Assert.assertTrue(service.insert(t));
//        Assert.assertNotEquals(0, t.updated);
//        Assert.assertTrue(time - t.updated < 20);
//        
//        // update
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            // eat up
//        }
//        
//        long old = t.updated;
//        Assert.assertTrue(service.update(t));
//        Assert.assertNotEquals(old, t.updated);
//    }
//    
//    @Test
//    public void testUserOwnedEntity() {
//        DataStoreService<UserOwnedEntityObject, String> service = new InMemoryDataStoreServiceImpl<UserOwnedEntityObject, String>() {};
//        
//        // insert
//        UserOwnedEntityObject t = new UserOwnedEntityObject("hello");
//        Assert.assertNull(t.userID);
//        Assert.assertNull(t.getUserID());
//        
//        SecurityContext.setPrincipal(new SomeUser("user1"));
//
//        Assert.assertTrue(service.insert(t));
//        Assert.assertEquals("user1", t.userID);
//        Assert.assertEquals("user1", t.getUserID());
//        
//        // update
//        Assert.assertTrue(service.update(t));
//        Assert.assertEquals("user1", t.getUserID());
//        
//        // failed update - from another user
//        UserOwnedEntityObject t2 = new UserOwnedEntityObject("hello");
//        t2.userID = "user2";
//        
//        try {
//            Assert.assertFalse(service.update(t2));
//            Assert.assertTrue(false);
//        } catch(SecurityException e) {
//            Assert.assertTrue(true);
//        }
//        
//        Assert.assertEquals("user1", service.get("hello").getUserID());
//    }
//
//    private static class CreationTimeTestObject implements CreateTimeStampedEntity {
//        @Id
//        String id;
//        long created;
//        
//        CreationTimeTestObject(String id) {
//            this.id = id;
//        }
//        
//        @Override
//        public long getCreateTime() {
//            return this.created;
//        }
//        
//        @Override
//        public void setCreateTime(long creationTime) {
//            this.created = creationTime;
//        }
//    }
//    
//    private static class UpdateTimeTestObject implements UpdateTimeStampedEntity {
//        @Id
//        String id;
//        
//        long updated;
//        
//        UpdateTimeTestObject(String id) {
//            this.id = id;
//        }
//        
//        @Override
//        public long getUpdateTime() {
//            return this.updated;
//        }
//        
//        @Override
//        public void setUpdateTime(long creationTime) {
//            this.updated = creationTime;
//        }
//    }
//    
//    private static class UserOwnedEntityObject implements UserOwnedEntity {
//        @Id
//        String id;
//        
//        String userID;
//        
//        UserOwnedEntityObject(String id) {
//            this.id = id;
//        }
//        
//        @Override
//        public String getUserID() {
//            return this.userID;
//        }
//        
//        @Override
//        public void setUserID(String userID) {
//            this.userID = userID;
//        }
//    }
//    
//    private static class SomeUser implements UserAwarePrincipal {
//        @Id
//        String id;
//        
//        String userID;
//        
//        SomeUser(String userID) {
//            this.userID = userID;
//        }
//        
//        @Override
//        public String getName() {
//            return this.userID;
//        }
//        
//        @Override
//        public String getUserID() {
//            return this.userID;
//        }
//        
//        @Override
//        public void setUserID(String userID) {
//            this.userID = userID;
//        }
//    }
//}
