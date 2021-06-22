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
package com.opentext.otmm.sc.eventlistener;

public interface OTMMEvent {
	/**
	 * <strong>EVENT_ID</strong>: 1114362	
	 * <strong>DESCR</strong>: LAUNCH_DOWNLOAD	
	 * <strong>PUBLICATION_KEY</strong>: TEAMS.LAUNCH_DOWNLOAD
	 * <strong>MESSAGE</strong>: Download directory:
	 */
	public static final String LAUNCH_DOWNLOAD = "1114362";

	/**
	 * <strong>EVENT_ID</strong>: 2031680	
	 * <strong>DESCR</strong>: EXPORT_LAUNCH_DOWNLOAD	
	 * <strong>PUBLICATION_KEY</strong>: TEAMS.EXPORT_LAUNCH_DOWNLOAD
	 * <strong>MESSAGE</strong>: Download directory:
	 */
	public static final String EXPORT_LAUNCH_DOWNLOAD = "2031680";
	
	/**
	 * <strong>EVENT_ID</strong>: 2752513	
	 * <strong>DESCR</strong>: FTP DOWNLOAD	
	 * <strong>PUBLICATION_KEY</strong>: TEAMS.DOWNLOAD
	 * <strong>MESSAGE</strong>: Request for downloading file out of the repository
	 */
	public static final String FTP_DOWNLOAD = "2752513";
		
	/**
	 * <strong>EVENT_ID</strong>: 60006	
	 * <strong>DESCR</strong>: Asset Exported	
	 * <strong>PUBLICATION_KEY</strong>: TEAMS.EXPORT
	 * <strong>MESSAGE</strong>: An Asset was exported
	 */
	public static final String ASSET_EXPORTED = "60006";
	
	/**
	 * <strong>EVENT_ID</strong>: 1114361	
	 * <strong>DESCR</strong>: Asset Exported	
	 * <strong>PUBLICATION_KEY</strong>: TEAMS.EXPORT
	 * <strong>MESSAGE</strong>: Ending export job
	 */
	public static final String ENDING_JOB_EXPORT = "1114361";	
}