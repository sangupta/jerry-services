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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.sangupta.jerry.counter.service.CounterService;
import com.sangupta.jerry.util.AssertUtils;

/**
 * A Redis based implementation of {@link CounterService}.
 * 
 * @author sangupta
 *
 */
public class RedisCounterServiceImpl implements CounterService {
	
	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	@Override
	public boolean create(String name) {
		return this.create(name, 0l);
	}

	@Override
	public boolean create(String name, long initialValue) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be null");
		}
		
		Boolean created = this.redisTemplate.opsForValue().setIfAbsent(name, initialValue);
		if(created == null) {
			return false;
		}
		
		return created;
	}

	@Override
	public long get(String name) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be null");
		}
		
		Long value = this.redisTemplate.opsForValue().get(name);
		if(value == null) {
			return 0l;
		}
		
		return value;
	}

	@Override
	public long increment(String name) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be null");
		}
		
		Long value = this.redisTemplate.opsForValue().increment(name, 1l);
		if(value == null) {
			return 0l;
		}
		
		return value;
	}

	@Override
	public long decrement(String name) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be null");
		}
		
		Long value = this.redisTemplate.opsForValue().increment(name, -1l);
		if(value == null) {
			return 0l;
		}
		
		return value;
	}

	@Override
	public boolean set(String name, long value) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be null");
		}
		
		this.redisTemplate.opsForValue().set(name, value);
		return true;
	}

}
