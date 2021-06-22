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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artesia.common.exception.BaseTeamsException;
import com.artesia.event.services.EventServices;
import com.artesia.security.SecuritySession;
import com.opentext.otmm.sc.eventlistener.util.EventListenerUtils;

public class DownloadAssetEventListenerRegistration implements ServletContextListener {

	protected static final Log log = LogFactory.getLog(DownloadAssetEventListenerRegistration.class);

	private static final String USER_ALIAS_TSUPER = "tsuper";
		
	// TEAMS.LAUNCH_DOWNLOAD - Download directory:
	private static final String CLIENT_ID_TEAMS_LAUNCH_DOWNLOAD = "TEAMS.LAUNCH_DOWNLOAD";
	// TEAMS.EXPORT_LAUNCH_DOWNLOAD - Download directory:
	private static final String CLIENT_ID_TEAMS_EXPORT_LAUNCH_DOWNLOAD = "TEAMS.EXPORT_LAUNCH_DOWNLOAD";
	// TEAMS.DOWNLOAD - Request for downloading file out of the repository
	private static final String CLIENT_ID_TEAMS_DOWNLOAD = "TEAMS.DOWNLOAD";	
	//1114361 -  TEAMS.EXPORT - Ending export job
	private static final String CLIENT_ID_ENDING_EXPORT_JOB = "TEAMS.ENDING_EXPORT";
	//60006	- Asset Exported - TEAMS.EXPORT -An Asset was exported
	private static final String CLIENT_ID_TEAMS_EXPORT = "TEAMS.EXPORT";
	
	private Map<String, String> events;
	
	public DownloadAssetEventListenerRegistration() {
		super();
		
		events = new HashMap<String, String>();
		events.put(CLIENT_ID_TEAMS_LAUNCH_DOWNLOAD, OTMMEvent.LAUNCH_DOWNLOAD);
		events.put(CLIENT_ID_TEAMS_EXPORT_LAUNCH_DOWNLOAD, OTMMEvent.EXPORT_LAUNCH_DOWNLOAD);
		events.put(CLIENT_ID_TEAMS_DOWNLOAD, OTMMEvent.FTP_DOWNLOAD);
		events.put(CLIENT_ID_TEAMS_EXPORT, OTMMEvent.ASSET_EXPORTED);
		events.put(CLIENT_ID_ENDING_EXPORT_JOB, OTMMEvent.ENDING_JOB_EXPORT);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {		
		log.info(">>> >> contextInitialized() Start >>>");
				
		try {
			SecuritySession session = com.opentext.otmm.sc.eventlistener.util.EventListenerUtils
					.getLocalSession(USER_ALIAS_TSUPER);
		
			for (Map.Entry<String, String> entry : events.entrySet()) {
				log.info("Registering event " + entry.getKey() + ": " + entry.getValue());
				
				DownloadAssetEventListener downloadEventListener = new DownloadAssetEventListener(entry.getValue());
				EventServices.getInstance().addEventListener(entry.getKey(), downloadEventListener, session);
			}			
		} catch (BaseTeamsException e) {
			log.error("", e);
		}

		log.info("<<< >> contextInitialized() End <<<");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log.info(">>> >> contextDestroyed() Start >>>");
		
		try {
			for (Map.Entry<String, String> entry : events.entrySet()) {	
				log.info("Unregistering event " + entry.getKey() + ": " + entry.getValue());

				SecuritySession session = EventListenerUtils.getLocalSession(USER_ALIAS_TSUPER);
				EventServices.getInstance().removeEventListener(entry.getKey(), session);
			}
		} catch (BaseTeamsException e) {
			log.error("An exception occured while destroying the servlet context", e);
		}
		
		log.info("<<< >> contextDestroyed() End <<<");		
	}		
}
