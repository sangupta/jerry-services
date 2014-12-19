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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sangupta.jerry.config.domain.Configuration;
import com.sangupta.jerry.config.service.ConfigurationService;
import com.sangupta.jerry.util.AssertUtils;

/**
 * A decorator that caches all values in JVM memory along with the service
 * that is being used.
 * 
 * @author sangupta
 *
 */
public class JVMCachedConfigurationServiceImpl extends BaseConfigurationServiceImpl {
	
	private final Map<String, Configuration> CACHE = new HashMap<String, Configuration>();
	
	private ConfigurationService service;
	
	public JVMCachedConfigurationServiceImpl(ConfigurationService service) {
		this.service = service;
	}

	@Override
	public boolean create(Configuration configuration) {
		boolean created = this.service.create(configuration);
		if(created) {
			CACHE.put(configuration.getConfigKey(), configuration);
		}
		
		return created;
	}

	@Override
	public Configuration get(String key) {
		if(CACHE.containsKey(key)) {
			return CACHE.get(key);
		}
		
		Configuration config = this.service.get(key);
		if(config == null) {
			return null;
		}
		
		CACHE.put(config.getConfigKey(), config);
		return config;
	}

	@Override
	public boolean update(String key, String value, boolean readOnly) {
		boolean updated = this.service.update(key, value, readOnly);
		if(!updated) {
			return false;
		}
		
		Configuration config = CACHE.get(key);
		if(config == null) {
			config = this.get(key);
			CACHE.put(key, config);
			return true;
		}
		
		config.setValue(value);
		config.setReadOnly(readOnly);
		return true;
	}

	@Override
	public boolean delete(String key) {
		boolean deleted = this.service.delete(key);
		if(deleted) {
			CACHE.remove(key);
		}
		
		return deleted;
	}

	@Override
	public List<Configuration> getAllConfigurations() {
		List<Configuration> list = this.service.getAllConfigurations();
		if(AssertUtils.isEmpty(list)) {
			return list;
		}
		
		for(Configuration config : list) {
			CACHE.put(config.getConfigKey(), config);
		}
		
		return list;
	}

}
