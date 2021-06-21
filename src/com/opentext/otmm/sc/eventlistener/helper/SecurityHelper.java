/*
 * (C) Copyright 2019 Joaquín Garzón (http://opentext.com) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   Joaquín Garzón - initial implementation
 */
package com.opentext.otmm.sc.eventlistener.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artesia.common.exception.BaseTeamsException;
import com.artesia.security.SecuritySession;
import com.artesia.security.session.services.LocalAuthenticationServices;

public class SecurityHelper {

	private static Log log = LogFactory.getLog(SecurityHelper.class);

	public static SecuritySession getAdminSession() {
		SecuritySession session = null;
		try {
			session = LocalAuthenticationServices.getInstance().createLocalSession("tsuper");
		} catch (BaseTeamsException e) {
			log.error(e.getMessage(), e);
		}
		return session;
	}

	public static SecuritySession getUserSession(String userName) {
		SecuritySession session = null;
		try {
			System.out.println("Trying to create a session for " + userName);
			session = LocalAuthenticationServices.getInstance().createLocalSession(userName);
		} catch (BaseTeamsException e) {
			log.error(e.getMessage(), e);
		}
		return session;
	}
}