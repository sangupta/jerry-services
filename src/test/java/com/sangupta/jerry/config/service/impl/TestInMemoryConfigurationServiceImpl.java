/**
 *
 * jerry - Common Java Functionality
 * Copyright (c) 2012-2014, Sandeep Gupta
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

package com.sangupta.jerry.config.service.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sangupta.jerry.config.service.ConfigurationService;

/**
 * 
 * @author sangupta
 *
 */
public class TestInMemoryConfigurationServiceImpl {
	
	private ConfigurationService service;
	
	@Before
	public void setup() {
		this.service = new InMemoryConfigurationServiceImpl();
	}
	
	@After
	public void tearDown() {
		this.service = null;
	}
	
	@Test
	public void testCreate() {
		Assert.assertNull(service.get("key1"));
		
		Assert.assertTrue(service.create("key1", "value1"));
		Assert.assertNotNull(service.get("key1"));
		Assert.assertEquals("value1", service.getValue("key1"));
		
		Assert.assertFalse(service.create("key1", "value1"));
		Assert.assertTrue(service.update("key1", "v1"));
		Assert.assertEquals("v1", service.getValue("key1"));
		
		Assert.assertTrue(service.delete("key1"));
		Assert.assertFalse(service.delete("key1"));
	}

}
