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

package com.sangupta.jerry.lock.service;

/**
 * Locking service based on http://redis.io/commands/setnx
 * 
 * @author sangupta
 */
public interface LockService {

	/**
	 * Obtain a new lock with the given name in Redis and the given timeout value.
	 * 
	 * @param lockName
	 *            the name of the lock to obtain
	 * 
	 * @param timeout
	 *            the timeout in milliseconds
	 * 
	 * @return <code>true</code> if lock was obtained, <code>false</code> otherwise
	 */
	public boolean obtainLock(String lockName, long timeoutInMillis);
	
	/**
	 * Check if the lock is expired.
	 * 
	 * @param lockName
	 *            the name of the lock to check
	 * 
	 * @return <code>true</code> if lock has expired, <code>false</code> otherwise
	 */
	public boolean isLockExpired(String lockName);
	
	/**
	 * Obtain an expired lock. This implementation should check if the lock is
	 * already expired, and then fire a GETSET command.
	 * 
	 * @param lockName
	 *            the name of the lock to acquire
	 * 
	 * @param timeoutInMillis
	 *            the timeout in milliseconds
	 * 
	 * @return <code>true</code> if lock was obtained, <code>false</code> otherwise
	 */
	public boolean obtainExpiredLock(String lockName, long timeoutInMillis);
	
	/**
	 * Release the lock if we obtained the lock ourself. The lock expiration time
	 * must be checked along with the fact that it was this node that obtained the
	 * lock.
	 * 
	 * @param lockName
	 *            the name of the lock to release
	 * 
	 * @return <code>true</code> if lock was released, <code>false</code> otherwise
	 */
	public boolean releaseLockIfHeld(String lockName);
	
	/**
	 * Return the current value of the lock as the complete {@link String}.
	 * 
	 * @param lockName
	 *            the name of the lock to fetch
	 * 
	 * @return the value of the lock if available, <code>null</code> otherwise
	 */
	public String getLockValue(String lockName);

}
