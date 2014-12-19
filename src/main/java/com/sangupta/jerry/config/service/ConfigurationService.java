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
import com.sangupta.jerry.config.service.impl.InMemoryConfigurationServiceImpl;
import com.sangupta.jerry.config.service.impl.JVMCachedConfigurationServiceImpl;
import com.sangupta.jerry.config.service.impl.MongoDBConfigurationServiceImpl;

/**
 * A service that handles {@link Configuration} instances.
 * 
 * Current implementations include {@link InMemoryConfigurationServiceImpl}, 
 * {@link MongoDBConfigurationServiceImpl}.
 * 
 * Some decorators are also available like {@link JVMCachedConfigurationServiceImpl}
 * that can be used with core implementations.
 * 
 * @author sangupta
 *
 */
public interface ConfigurationService {

	/**
	 * Create a new {@link Configuration} instance in the store.
	 * 
	 * @param configuration
	 *            the instance to be created
	 * 
	 * @return <code>true</code> if creation was successful, <code>false</code>
	 *         otherwise
	 */
	public boolean create(Configuration configuration);
	
	/**
	 * Create a new {@link Configuration} instance with the given key and value.
	 * 
	 * @param key
	 *            the configuration key to use
	 * 
	 * @param value
	 *            the configuration value to use
	 * 
	 * @return <code>true</code> if creation was successful, <code>false</code>
	 *         otherwise
	 */
	public boolean create(String key, String value);
	
	public Configuration get(String key);
	
	public String getValue(String key);
	
	public boolean update(Configuration configuration);
	
	public boolean update(String key, String value);
	
	public boolean update(String key, String value, boolean readOnly);
	
	public boolean delete(Configuration configuration);
	
	public boolean delete(String key);
	
	public List<Configuration> getAllConfigurations();
	
	// some generic methods for getting typed value
	
	/**
	 * Return the <code>short</code> version of the configuration value if it is
	 * available and can be converted to a <code>short</code> value, else
	 * returns the provided default value
	 * 
	 * @param key
	 *            the configuration key to fetch
	 * 
	 * @param defaultValue
	 *            the value to return if configuration not available, or value
	 *            cannot be converted successfully.
	 * 
	 * @return the <code>short</code> value
	 */
	public short getShortValue(String key, short defaultValue);
	
	/**
	 * Return the <code>int</code> version of the configuration value if it is
	 * available and can be converted to a <code>int</code> value, else
	 * returns the provided default value
	 * 
	 * @param key
	 *            the configuration key to fetch
	 * 
	 * @param defaultValue
	 *            the value to return if configuration not available, or value
	 *            cannot be converted successfully.
	 * 
	 * @return the <code>short</code> value
	 */
	public int getIntValue(String key, int defaultValue);
	
	/**
	 * Return the <code>long</code> version of the configuration value if it is
	 * available and can be converted to a <code>long</code> value, else
	 * returns the provided default value
	 * 
	 * @param key
	 *            the configuration key to fetch
	 * 
	 * @param defaultValue
	 *            the value to return if configuration not available, or value
	 *            cannot be converted successfully.
	 * 
	 * @return the <code>short</code> value
	 */
	public long getLongValue(String key, long defaultValue);
	
	/**
	 * Return the <code>float</code> version of the configuration value if it is
	 * available and can be converted to a <code>float</code> value, else
	 * returns the provided default value
	 * 
	 * @param key
	 *            the configuration key to fetch
	 * 
	 * @param defaultValue
	 *            the value to return if configuration not available, or value
	 *            cannot be converted successfully.
	 * 
	 * @return the <code>short</code> value
	 */
	public float getFloatValue(String key, float defaultValue);
	
	/**
	 * Return the <code>double</code> version of the configuration value if it is
	 * available and can be converted to a <code>double</code> value, else
	 * returns the provided default value
	 * 
	 * @param key
	 *            the configuration key to fetch
	 * 
	 * @param defaultValue
	 *            the value to return if configuration not available, or value
	 *            cannot be converted successfully.
	 * 
	 * @return the <code>short</code> value
	 */
	public double getDoubleValue(String key, double defaultValue);
	
}
