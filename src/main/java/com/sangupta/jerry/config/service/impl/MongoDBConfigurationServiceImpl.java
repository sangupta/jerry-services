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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.sangupta.jerry.config.domain.Configuration;
import com.sangupta.jerry.util.AssertUtils;

/**
 * 
 * @author sangupta
 *
 */
public class MongoDBConfigurationServiceImpl extends BaseConfigurationServiceImpl {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public boolean create(Configuration configuration) {
		if(configuration == null) {
			throw new IllegalArgumentException("Configuration object cannot be null");
		}
		
		this.mongoTemplate.insert(configuration);
		return true;
	}

	@Override
	public Configuration get(String key) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		return this.mongoTemplate.findById(key, Configuration.class);
	}

	@Override
	public boolean update(String key, String value, boolean readOnly) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration current = get(key);
		if(current == null) {
			return false;
		}
		
		if(current.isReadOnly()) {
			return false;
		}
		
		current.setValue(value);
		current.setReadOnly(readOnly);
		this.mongoTemplate.save(current);
		
		return true;
	}

	@Override
	public boolean delete(String key) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration config = this.get(key);
		if(config == null) {
			return false;
		}
		
		this.mongoTemplate.remove(config);
		return true;
	}

	@Override
	public List<Configuration> getAllConfigurations() {
		return this.mongoTemplate.findAll(Configuration.class);
	}

}
