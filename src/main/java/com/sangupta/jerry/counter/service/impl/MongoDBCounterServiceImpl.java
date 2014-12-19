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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.sangupta.jerry.counter.service.CounterService;
import com.sangupta.jerry.util.AssertUtils;

/**
 * {@link MongoTemplate} based implementation of {@link CounterService}.
 * 
 * @author sangupta
 *
 */
public class MongoDBCounterServiceImpl implements CounterService {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public boolean create(String name) {
		return create(name, 0);
	}

	@Override
	public boolean create(String name, long initialValue) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be empty/null");
		}
		
		MongoCounter counter = this.mongoTemplate.findOne(new Query(Criteria.where("counterName").is(name)), MongoCounter.class);
		if(counter != null) {
			return false;
		}

		counter = new MongoCounter(name);
		counter.setValue(initialValue);
		this.mongoTemplate.insert(counter);
		return true;
	}

	@Override
	public long get(String name) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be empty/null");
		}
		
		MongoCounter counter = this.mongoTemplate.findOne(new Query(Criteria.where("counterName").is(name)), MongoCounter.class);
		if(counter == null) {
			return 0;
		}
		
		return counter.getValue();
	}

	@Override
	public long increment(String name) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be empty/null");
		}
		
		Update update = new Update();
		update.inc("value", 1);
		MongoCounter counter = this.mongoTemplate.findAndModify(new Query(Criteria.where("counterName").is(name)), update, MongoCounter.class);
		if(counter != null) {
			return counter.getValue(); 
		}
		
		return 0l;
	}

	@Override
	public long decrement(String name) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be empty/null");
		}
		
		Update update = new Update();
		update.inc("value", -1);
		MongoCounter counter = this.mongoTemplate.findAndModify(new Query(Criteria.where("counterName").is(name)), update, MongoCounter.class);
		if(counter != null) {
			return counter.getValue();
		}
		
		return 0l;
	}

	@Override
	public boolean set(String name, long value) {
		if(AssertUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Counter name cannot be empty/null");
		}
		
		Update update = new Update();
		update.set("value", value);
		MongoCounter counter = this.mongoTemplate.findAndModify(new Query(Criteria.where("counterName").is(name)), update, MongoCounter.class);
		if(counter != null) {
			return true;
		}
		
		return false;
	}
	
	// static class follows

	private static class MongoCounter {

		@Id
		private final String counterName;
		
		private long value;
		
		public MongoCounter(String counterName) {
			if(AssertUtils.isEmpty(counterName)) {
				throw new IllegalArgumentException("Counter name cannot be empty/null");
			}
			
			this.counterName = counterName;
		}
		
		// Usual accessors follow

		/**
		 * @return the value
		 */
		public long getValue() {
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(long value) {
			this.value = value;
		}
		
	}

	
	// Usual accessors follow

	/**
	 * @return the mongoTemplate
	 */
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	/**
	 * @param mongoTemplate the mongoTemplate to set
	 */
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	} 
	
}
