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
package com.opentext.otmm.sc.eventlistener.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artesia.common.exception.BaseTeamsException;
import com.opentext.job.Job;
import com.opentext.job.JobCriteria;
import com.opentext.job.services.JobServices;

public class JobHelper {
	private static Log log = LogFactory.getLog(JobHelper.class);
	

	public static Job retrieveJob(String jobId) {
		Long longJobId = null;
		try {
			longJobId = Long.parseLong(jobId);
		}
		catch (NumberFormatException e) {
			log.error("Error converting Job Id (" + jobId + ") to 'Long'",e);
		}
		
		return longJobId == null? null : retrieveJob(longJobId);
	}
	
	public static Job retrieveJob(Long jobId) {
		Job job = null;
		
		JobCriteria jobCriteria = new JobCriteria();
		jobCriteria.setLoadAssetDetails(true);
		jobCriteria.setLoadJobContext(true);
		jobCriteria.setLoadJobDetails(true);
		jobCriteria.setLoadSteps(true);
		jobCriteria.setLoadTaskDetails(true);
		
		try {
			job = JobServices.getInstance().retrieveJob(jobId, jobCriteria, SecurityHelper.getAdminSession());
			
			log.debug("Job " + jobId + " retrieved.");
		} catch (BaseTeamsException e) {
			log.error("Error retrieving Job (Id: " + jobId + ")", e);
		}		
		
		return job;
	}
}