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

package com.sangupta.jerry.lock.service.impl;

import com.sangupta.jerry.lock.service.LockService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.sangupta.jerry.ApplicationContext;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.StringUtils;

/**
 * Redis based implementation for {@link LockService}.
 * 
 * @author sangupta
 *
 */
public class RedisLockServiceImpl implements LockService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockServiceImpl.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Override
	public boolean obtainLock(String lockName, long timeoutInMillis) {
		Boolean locked = this.redisTemplate.opsForValue().setIfAbsent(getKeyName(lockName), getNewLockValue(timeoutInMillis));
		if(locked == null) {
			return false;
		}
		
		if(locked) {
			// set the expire time
			this.redisTemplate.expire(getKeyName(lockName), timeoutInMillis, TimeUnit.MILLISECONDS);
		}
		
		return locked;
	}

	@Override
	public boolean isLockExpired(String lockName) {
		String lockValue = this.redisTemplate.opsForValue().get(getKeyName(lockName));
		if(AssertUtils.isEmpty(lockValue)) {
			return true;
		}
		
		int index = lockValue.indexOf(':');
		if(index == -1) {
			LOGGER.error("Invalid value for lock with name: {} as {}", lockName, lockValue);
			return true;
		}
		
		String time = lockValue.substring(index + 1);
		long epoch = StringUtils.getLongValue(time, -1);
		if(epoch == -1) {
			LOGGER.error("Invalid value for lock with name: {} as {}", lockName, lockValue);
			return true;
		}
		
		if(System.currentTimeMillis() > epoch) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean obtainExpiredLock(String lockName, long timeoutInMillis) {
		final String currentLockValue = this.redisTemplate.opsForValue().get(getKeyName(lockName));
		boolean expired = this.isLockExpired(lockName);
		if(!expired) {
			LOGGER.debug("Lock {} is not expired, cannot obtain it.", lockName);
			return false;
		}
		
		// try and obtain a lock again with GETSET command
		final String lockValue = getNewLockValue(timeoutInMillis);
		String oldValue = this.redisTemplate.opsForValue().getAndSet(getKeyName(lockName), lockValue);
		if(oldValue.equals(currentLockValue)) {
			// we obtained the lock
			return true;
		}
		
		return false;
	}

	@Override
	public boolean releaseLockIfHeld(String lockName) {
		String lockValue = this.redisTemplate.opsForValue().get(getKeyName(lockName));
		if(AssertUtils.isEmpty(lockValue)) {
			LOGGER.debug("No lock exists with name {}", lockName);
			return false;
		}
		
		if(!lockValue.startsWith(ApplicationContext.NODE_ID)) {
			LOGGER.debug("Lock was not obtained by this node: {} and nodeID: {}", lockName, ApplicationContext.NODE_ID);
			return false;
		}
		
		this.redisTemplate.delete(getKeyName(lockName));
		return true;
	}

	@Override
	public String getLockValue(String lockName) {
		return this.redisTemplate.opsForValue().get(getKeyName(lockName));
	}
	
	/**
	 * Return the key name for the distributed lock.
	 * 
	 * @param lockName
	 * @return
	 */
	protected String getKeyName(String lockName) {
		return "distributedLock::" + lockName;
	}

	/**
	 * Return the new value for the lock for given timeout.
	 * 
	 * @param timeoutInMillis
	 * @return
	 */
	protected String getNewLockValue(long timeoutInMillis) {
		long time = System.currentTimeMillis() + timeoutInMillis + 1l;
		String lockValue = ApplicationContext.NODE_ID + ":" + time;
		return lockValue;
	}
	
}
