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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sangupta.jerry.config.domain.Configuration;
import com.sangupta.jerry.util.AssertUtils;

/**
 * 
 * @author sangupta
 *
 */
public class InMemoryConfigurationServiceImpl extends BaseConfigurationServiceImpl {
	
	private static final ConcurrentMap<String, Configuration> CONFIG_MAP = new ConcurrentHashMap<String, Configuration>();

	@Override
	public boolean create(Configuration configuration) {
		if(configuration == null) {
			throw new IllegalArgumentException("Configuration object cannot be null");
		}
		
		if(AssertUtils.isEmpty(configuration.getConfigKey())) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration older = CONFIG_MAP.putIfAbsent(configuration.getConfigKey(), configuration);
		if(older == null) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean create(String key, String value) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		return create(new Configuration(key, value));
	}

	@Override
	public Configuration get(String key) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		return CONFIG_MAP.get(key);
	}

	@Override
	public String getValue(String key) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration configuration = CONFIG_MAP.get(key);
		if(configuration == null) {
			return null;
		}
		
		return configuration.getValue();
	}

	@Override
	public boolean update(Configuration configuration) {
		if(configuration == null) {
			throw new IllegalArgumentException("Configuration object cannot be null");
		}
		
		if(AssertUtils.isEmpty(configuration.getConfigKey())) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration current = get(configuration.getConfigKey());
		if(current == null) {
			return false;
		}
		
		if(current.isReadOnly()) {
			return false;
		}
		
		current.setValue(configuration.getValue());
		current.setReadOnly(configuration.isReadOnly());
		return true;
	}

	@Override
	public boolean update(String key, String value) {
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
		return true;
	}

	@Override
	public boolean delete(Configuration configuration) {
		if(configuration == null) {
			throw new IllegalArgumentException("Configuration object cannot be null");
		}
		
		if(AssertUtils.isEmpty(configuration.getConfigKey())) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration config = CONFIG_MAP.remove(configuration.getConfigKey());
		if(config != null) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean delete(String key) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		Configuration config = CONFIG_MAP.remove(key);
		if(config != null) {
			return true;
		}
		
		return false;
	}

	@Override
	public List<Configuration> getAllConfigurations() {
		return new ArrayList<Configuration>(CONFIG_MAP.values());
	}

}
