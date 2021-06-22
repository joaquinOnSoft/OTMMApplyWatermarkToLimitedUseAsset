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

import com.artesia.entity.TeamsIdentifier;
import com.artesia.event.Event;
import com.opentext.otmm.sc.eventlistener.handler.ApplyWatermarkToLimitedUseAssetOnDownload;
import com.opentext.otmm.sc.eventlistener.handler.OTMMEventHandler;

public class DownloadAssetEventListener extends AbstractEventLister {
	
	public DownloadAssetEventListener() {
		super();
	}

	public DownloadAssetEventListener(String events) {
		super(events);		
	}
	
	@Override
	public void onEvent(Event event) {
		displayEventObject(event);
		
		if (event.getEventId().equals(new TeamsIdentifier(OTMMEvent.ASSET_EXPORTED))) {
			log.info("Ids match for 'download asset' event. Event Id: " + event.getEventId());
			
			OTMMEventHandler handler = new ApplyWatermarkToLimitedUseAssetOnDownload();
			handler.handle(event);
		}
	}
}
