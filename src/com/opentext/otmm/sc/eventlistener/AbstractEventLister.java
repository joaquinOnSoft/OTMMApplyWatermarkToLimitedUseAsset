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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artesia.event.Event;
import com.artesia.event.EventListener;

public abstract class AbstractEventLister implements EventListener{

	protected static final Log log = LogFactory.getLog(AbstractEventLister.class);

	protected String eventsToRegister = "";
	
	public AbstractEventLister() {
		super();
	}
	
	public AbstractEventLister(String events) {
		this.eventsToRegister = events;
		log.debug("Registering listener to events: " + this.eventsToRegister);		
	}	

	protected void displayEventObject(Event theEvent) {
		log.debug("== Event Init == ");
		log.debug("Event ID : " + theEvent.getEventId().getId());
		log.debug("user ID : " + theEvent.getUserId().getId());
		log.debug("Object ID : " + theEvent.getObjectId());
		log.debug("Object Name : " + theEvent.getObjectName());		
		log.debug("Object Type : " + theEvent.getObjectType());
		log.debug("Event Description : " + theEvent.getDescription());
		log.debug("Event XMLContent : " + theEvent.getXmlContent());
		log.debug("== Event End ==");
	}

}