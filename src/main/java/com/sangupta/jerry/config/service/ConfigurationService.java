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

package com.sangupta.jerry.config.service;

import java.util.List;

import com.sangupta.jerry.config.domain.Configuration;

/**
 * 
 * @author sangupta
 *
 */
public interface ConfigurationService {

	public boolean create(Configuration configuration);
	
	public boolean create(String key, String value);
	
	public Configuration get(String key);
	
	public String getValue(String key);
	
	public boolean update(Configuration configuration);
	
	public boolean update(String key, String value);
	
	public boolean delete(Configuration configuration);
	
	public boolean delete(String key);
	
	public List<Configuration> getAllConfigurations();
	
	// some generic methods for getting typed value
	
	public short getShortValue(String key, short defaultValue);
	
	public int getIntValue(String key, int defaultValue);
	
	public long getLongValue(String key, long defaultValue);
	
	public float getFloatValue(String key, float defaultValue);
	
	public double getDoubleValue(String key, double defaultValue);
	
}
