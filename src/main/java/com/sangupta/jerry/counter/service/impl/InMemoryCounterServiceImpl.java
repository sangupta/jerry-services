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

package com.sangupta.jerry.counter.service.impl;

import com.sangupta.jerry.counter.service.CounterService;
import com.sangupta.jerry.ds.LongCounter;

/**
 * Implementation of {@link CounterService} that uses in-memory based counters
 * for counting.
 * 
 * @author sangupta
 *
 */
public class InMemoryCounterServiceImpl implements CounterService {
	
	private final LongCounter counter = new LongCounter();
	
	@Override
	public boolean create(String name) {
		long value = counter.get(name);
		return value == 0;
	}

	@Override
	public boolean create(String name, long initialValue) {
		long value = counter.get(name, initialValue);
		return value == initialValue;
	}

	@Override
	public long get(String name) {
		return counter.get(name);
	}

	@Override
	public long increment(String name) {
		return counter.increment(name);
	}

	@Override
	public long decrement(String name) {
		return counter.decrement(name);
	}

	@Override
	public boolean set(String name, long value) {
		counter.set(name, value);
		return true;
	}

}
