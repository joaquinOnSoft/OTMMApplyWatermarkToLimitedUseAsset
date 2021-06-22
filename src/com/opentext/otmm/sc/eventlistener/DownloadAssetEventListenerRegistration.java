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

import com.artesia.common.exception.BaseTeamsException;
import com.artesia.event.services.EventServices;
import com.artesia.security.SecuritySession;

public class DownloadAssetEventListenerRegistration extends AbstractEventListenerRegistration {


	//60006	- Asset Exported - TEAMS.EXPORT -An Asset was exported
	private static final String CLIENT_ID_TEAMS_EXPORT = "TEAMS.EXPORT";
		
	public DownloadAssetEventListenerRegistration() {
		super();
		clientId = CLIENT_ID_TEAMS_EXPORT;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {		
		log.info(">>> >> contextInitialized() Start >>>");
				
		try {
			SecuritySession session = com.opentext.otmm.sc.eventlistener.util.EventListenerUtils
					.getLocalSession(USER_ALIAS_TSUPER);
					
			log.info("Registering event " + CLIENT_ID_TEAMS_EXPORT + ": " + OTMMEvent.ASSET_EXPORTED);
			
			DownloadAssetEventListener downloadEventListener = new DownloadAssetEventListener(OTMMEvent.ASSET_EXPORTED);
			EventServices.getInstance().addEventListener(CLIENT_ID_TEAMS_EXPORT, downloadEventListener, session);
					
		} catch (BaseTeamsException e) {
			log.error("Download event listener registration: ", e);
		}

		log.info("<<< >> contextInitialized() End <<<");
	}
	
}
