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

package com.sangupta.jerry.counter.service;

/**
 * Contract for services that wish to maintain value counters
 * 
 * @author sangupta
 *
 */
public interface CounterService {
	
	/**
	 * Create a new counter with the given name and a default value of
	 * <code>0</code>
	 * 
	 * @param name
	 *            the name of the counter
	 * 
	 * @return <code>true</code> if counter creation was successful,
	 *         <code>false</code> otherwise or if counter already existed
	 */
	public boolean create(String name);
	
	/**
	 * Create a new counter with the given name and given default value.
	 * 
	 * @param name
	 *            the name of the counter
	 *            
	 * @param initialValue the initial value of the counter to set
	 * 
	 * @return <code>true</code> if counter creation was successful,
	 *         <code>false</code> otherwise or if counter already existed
	 */
	public boolean create(String name, long initialValue);
	
	/**
	 * Return the current value of the counter
	 * 
	 * @param name the name of the counter
	 * 
	 * @return the current value of the counter if found, or <code>0</code>
	 */
	public long get(String name);

	/**
	 * Increment the value of the counter with the given name by <code>1</code>
	 * 
	 * @param name
	 *            the name of the counter
	 * 
	 * @return <code>true</code> if counter was found and successfully
	 *         incremented, <code>false</code> otherwise
	 */
	public long increment(String name);
	
	/**
	 * Decrement the value of the counter with the given name by <code>-1</code>
	 * 
	 * @param name
	 *            the name of the counter
	 * 
	 * @return <code>true</code> if counter was found and successfully
	 *         decremented, <code>false</code> otherwise
	 */
	public long decrement(String name);

	/**
	 * Set the current value of the counter to given value
	 * 
	 * @param name the name of the counter
	 * 
	 * @param value the value of the counter
	 * 
	 * @return <code>true</code> if counter was found and successfully
	 *         updated to given value, <code>false</code> otherwise
	 */
	public boolean set(String name, long value);


}
