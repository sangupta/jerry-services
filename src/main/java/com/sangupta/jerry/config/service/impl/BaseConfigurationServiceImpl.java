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

import com.sangupta.jerry.config.domain.Configuration;
import com.sangupta.jerry.config.service.ConfigurationService;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.StringUtils;

/**
 * Helper methods that will be required across all implementations.
 * 
 * @author sangupta
 *
 */
public abstract class BaseConfigurationServiceImpl implements ConfigurationService {

	public boolean create(String key, String value) {
		if(AssertUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Configuration key cannot be null/empty");
		}
		
		return create(new Configuration(key, value));
	}

	public boolean delete(Configuration configuration) {
		if(configuration == null) {
			throw new IllegalArgumentException("Configuration object cannot be null");
		}
		
		return this.delete(configuration.getConfigKey());
	}

	public String getValue(String key) {
		Configuration configuration = this.get(key);
		if(configuration == null) {
			return null;
		}
		
		return configuration.getValue();
	}

	@Override
	public boolean update(String key, String value) {
		return this.update(key, value, false);
	}

	@Override
	public boolean update(Configuration configuration) {
		if(configuration == null) {
			throw new IllegalArgumentException("Configuration object cannot be null");
		}
		
		return this.update(configuration.getConfigKey(), configuration.getValue(), configuration.isReadOnly());
	}
	
	public short getShortValue(String key, short defaultValue) {
		return StringUtils.getShortValue(getValue(key), defaultValue);
	}
	
	public int getIntValue(String key, int defaultValue) {
		return StringUtils.getIntValue(getValue(key), defaultValue);
	}
	
	public long getLongValue(String key, long defaultValue) {
		return StringUtils.getLongValue(getValue(key), defaultValue);
	}
	
	public float getFloatValue(String key, float defaultValue) {
		return StringUtils.getFloatValue(getValue(key), defaultValue);
	}
	
	public double getDoubleValue(String key, double defaultValue) {
		return StringUtils.getDoubleValue(getValue(key), defaultValue);
	}

}
