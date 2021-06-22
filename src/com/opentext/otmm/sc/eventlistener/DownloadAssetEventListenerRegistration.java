/*
 * (C) Copyright 2021 Joaquín Garzón (http://opentext.com) and others.
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
package com.opentext.otmm.sc.eventlistener;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artesia.common.exception.BaseTeamsException;
import com.artesia.event.services.EventServices;
import com.artesia.security.SecuritySession;

public class DownloadAssetEventListenerRegistration extends AbstractEventListenerRegistration {

	protected static final Log log = LogFactory.getLog(DownloadAssetEventListenerRegistration.class);

	private static final String USER_ALIAS_TSUPER = "tsuper";
		
	// TEAMS.LAUNCH_DOWNLOAD - Download directory:
	private static final String CLIENT_ID_TEAMS_LAUNCH_DOWNLOAD = "TEAMS.LAUNCH_DOWNLOAD";
	// TEAMS.EXPORT_LAUNCH_DOWNLOAD - Download directory:
	//private static final String CLIENT_ID_TEAMS_EXPORT_LAUNCH_DOWNLOAD = "TEAMS.EXPORT_LAUNCH_DOWNLOAD";
	// TEAMS.DOWNLOAD - Request for downloading file out of the repository
	//private static final String CLIENT_ID_TEAMS_DOWNLOAD = "TEAMS.DOWNLOAD";

	public DownloadAssetEventListenerRegistration() {
		super();
		clientId = CLIENT_ID_TEAMS_LAUNCH_DOWNLOAD;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {		
		log.info(">>> " + getClassName() + " >> contextInitialized() Start >>>");
		
		clientId = CLIENT_ID_TEAMS_LAUNCH_DOWNLOAD;
		
		try {
			SecuritySession session = com.opentext.otmm.sc.eventlistener.util.EventListenerUtils
					.getLocalSession(USER_ALIAS_TSUPER);
			DownloadAssetEventListener downloadEventListener = new DownloadAssetEventListener(OTMMEvent.EXPORT_LAUNCH_DOWNLOAD);
			EventServices.getInstance().addEventListener(clientId, downloadEventListener, session);

		} catch (BaseTeamsException e) {
			log.error("", e);
		}

		log.info("<<< " + getClassName() + " >> contextInitialized() End <<<");
	}	
}
