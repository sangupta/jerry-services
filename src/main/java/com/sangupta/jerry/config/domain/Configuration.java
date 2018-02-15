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

package com.sangupta.jerry.config.domain;

import org.springframework.data.annotation.Id;

/**
 * 
 * @author sangupta
 *
 */
public class Configuration {
	
	/**
	 * Primary key for the database
	 */
	@Id
	private String configKey;
	
	/**
	 * The value of the configuration pair
	 */
	private String value;
	
	/**
	 * Whether the value can be edited or not
	 */
	private boolean readOnly;
	
	/**
	 * Default constructor
	 */
	public Configuration() {
		
	}
	
	/**
	 * Convenience constructor
	 * 
	 * @param key
	 *            the configuration key to use
	 * 
	 * @param value
	 *            the configuration value to use
	 */
	public Configuration(String key, String value) {
		this.configKey = key;
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Configuration)) {
			return false;
		}
		
		Configuration c = (Configuration) obj;
		return this.configKey.equals(c.configKey);
	}
	
	@Override
	public int hashCode() {
		return this.configKey.hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + this.configKey + "::" + this.value + "]";
	}
	
	// Usual accessors follow

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return the configKey
	 */
	public String getConfigKey() {
		return configKey;
	}

	/**
	 * @param configKey the configKey to set
	 */
	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

}
