/**
 *
 * jerry - Common Java Functionality
 * Copyright (c) 2012, Sandeep Gupta
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

package com.sangupta.jerry.email.service;

import com.sangupta.jerry.email.domain.Email;
import com.sangupta.jerry.email.domain.EmailAddress;

/**
 * Service that abstracts out sending emails.
 * 
 * @author sangupta
 * 
 * @since 1.0.0
 */
public interface EmailService {
	
	public boolean sendEmail(String fromAddress, String toAddress, String subject, String text);
	
	public boolean sendEmail(EmailAddress fromAddress, EmailAddress toAddress, String subject, String text);

	/**
	 * Send a fully generated email.
	 * 
	 * @param email
	 * @return
	 */
	public boolean sendEmail(Email email);

}
