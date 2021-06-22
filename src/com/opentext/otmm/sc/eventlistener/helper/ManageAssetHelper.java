package com.opentext.otmm.sc.eventlistener.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artesia.asset.Asset;
import com.artesia.asset.AssetContentInfo;
import com.artesia.asset.AssetIdentifier;
import com.artesia.asset.content.services.AssetContentLoadRequest;
import com.artesia.asset.content.services.AssetContentServices;
import com.artesia.asset.imprt.ImportAsset;
import com.artesia.asset.imprt.ImportJob;
import com.artesia.asset.imprt.services.AssetImportServices;
import com.artesia.asset.services.AssetServices;
import com.artesia.common.exception.BaseTeamsException;
import com.artesia.common.prefs.PrefData;
import com.artesia.common.prefs.PrefDataId;
import com.artesia.content.ContentData.ContentDataSource;
import com.artesia.content.ContentInfo;
import com.artesia.manageddirectory.ManagedDirectory;
import com.artesia.manageddirectory.services.ManagedDirectoryServices;
import com.artesia.security.SecuritySession;
import com.artesia.system.SystemSettingConstants;
import com.artesia.system.services.SystemServices;
import com.opentext.job.DataType;
import com.opentext.job.JobClass;
import com.opentext.job.JobConstants;
import com.opentext.job.JobContext;
import com.opentext.job.JobParameter;
import com.opentext.job.JobRequest;
import com.opentext.job.services.JobServices;

/**
 * <strong>Checking assets in and out</strong>
 * 
 * The process of creating new versions of assets referred to as "check-out" and
 * "check-in" of assets. When you check-in a new version of an asset, you will
 * provide new content. The checked-in asset will inherit metadata, security
 * profiles, and potentially links from the previous version of the asset.
 * 
 * To check-out an asset means that you are exclusively locking the content of
 * the asset for editing. NOTE: The asset itself may be unlocked while the asset
 * is checked-out in order to enable updates of non-content attributes. Once
 * checked-out, you can access the asset's content by performing an export or by
 * using <strong>AssetContentServices</strong>. The act of checking-in a new
 * version of an asset will implicitly cancel the check-out of the checked-out
 * asset; therefore, the programmer does not need to perform any additional
 * actions on the checked-out asset after a successful check-in.
 *
 * @see Media_Management_Programmer_Guide_20.2/programmers-guide/section_CheckInAndOut.html
 */
public class ManageAssetHelper {

	private static final Log log = LogFactory.getLog(ManageAssetHelper.class);

	/**
	 * <strong>Check-Out</strong> The first step in creating a new asset version is
	 * to check-out the content. In order to check-out an asset, you need to lock it
	 * first.
	 */
	public static boolean checkout(AssetIdentifier assetId) {
		boolean checkedout = true;
		SecuritySession securitySession = SecurityHelper.getAdminSession();

		try {
			// We need to lock the asset first
			AssetServices.getInstance().lockAsset(assetId, securitySession);
			// Now check it out
			AssetServices.getInstance().checkoutAsset(assetId, securitySession);
		} catch (BaseTeamsException e) {
			log.error("Checkout: ", e);
			checkedout = false;
		}

		return checkedout;
	}

	/**
	 * <strong>Get the Content</strong>
	 * The next step is typically to get the content for editing. In the following example, we use AssetContentServices to get the asset's content.
	 * @param assetId
	 * @return
	 */
	public static File getMasterFile(AssetIdentifier assetId) {
		File masterFile = null;
		
		SecuritySession securitySession = SecurityHelper.getAdminSession();

		// Set up a request object that will tell the service where we want the content delivered.
		AssetContentLoadRequest contentRequest = new AssetContentLoadRequest();
		// deliver the content file to this directory - 
		// NOTE: this path must be accessible to the content service
		contentRequest.setDestinationDirectory(new File("c:\\temp")); //TODO avoid hardcoded path
		// load the master content file
		contentRequest.setLoadMasterContent(true);
		// request the content as a file
		contentRequest.setRequestedFormat(ContentDataSource.FILE);

		AssetContentInfo info = null;
		try {
			info = AssetContentServices.getInstance().retrieveAssetContent(
					assetId, contentRequest, null, securitySession);
		} catch (BaseTeamsException e) {
			log.error("Get master file: ", e);
		}

		if(info != null) {
			masterFile = info.getMasterContent().getFile();
		}
		
		return masterFile;		
	}


	/**
	 * <strong>Check-in the New Content</strong> The final step is to check-in the
	 * new content. This is done by importing a new version of the checked out
	 * asset.
	 * 
	 * @param asset
	 * @param editedFile
	 */
	public static boolean checkin(AssetIdentifier checkedOutAssetId, File editedFile) {
		boolean checkedin = true;

		log.info("chechin (START): " + editedFile.getAbsolutePath());

		try {
			SecuritySession session = SecurityHelper.getAdminSession();

			ImportAsset newAssetVersion = new ImportAsset("checkin", new Asset());
			// Set the id of the asset we have checked out
			newAssetVersion.setCheckinAssetId(checkedOutAssetId);
			// Set the new content
			newAssetVersion.getAsset().setMasterContentInfo(new ContentInfo(editedFile));
			log.debug("\t Import asset created");

			ImportJob checkinJob = AssetImportServices.getInstance().createImportJob(session);
			checkinJob.addImportAsset(newAssetVersion);
			log.debug("\t Import job created");

			// submit the import
			log.debug("\t Submit the import");	
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(JobConstants.IMPORT_JOB, checkinJob);

			JobContext jobContext = new JobContext();
			jobContext.setData(data);

			// Constructing job request
			log.debug("\t Constructing job request");			
			JobRequest jobRequest = new JobRequest();
			jobRequest.setJobId(checkinJob.getImportJobId().asLong());
			jobRequest.setInitiatorLoginId(session.getLoginName());
			jobRequest.setJobContext(jobContext);
			jobRequest.setJobClass(JobClass.IMPORT);
			jobRequest.setJobName("Asset Import (checkin)");

			PrefData prefData = SystemServices.getInstance()
					.retrieveSystemSettingsByPrefDataId(new PrefDataId(SystemSettingConstants.JOB,
							SystemSettingConstants.CONFIG, SystemSettingConstants.DEFAULT_IMPORT), session);

			String jobType = null;

			if (prefData != null) {
				jobType = prefData.getValue();
			}

			if (jobType == null) {
				jobType = "Import";
			}

			jobRequest.setJobType(jobType);

			// Creating job parameters
			log.debug("\t Creating job parameters");			
			List<JobParameter> jobParameters = new ArrayList<JobParameter>();
			JobParameter jobParameter = new JobParameter();
			jobParameter.setKey(JobConstants.IMPORT_JOB_ID);
			jobParameter.setType(DataType.LONG);
			jobParameter.setValue(checkinJob.getImportJobId().asLong());

			jobParameters.add(jobParameter);

			// Loading job parameters into job request
			jobRequest.setJobParameters(jobParameters);

			// Initiating a job with job request
			log.debug("\tInitiating a job with job request");			

			JobServices jobServices = JobServices.getInstance();
			Long jobId = jobServices.initiateJob(jobRequest, session);

			log.debug("\t Job initialized: " + jobId);			

			ManagedDirectoryServices mds = ManagedDirectoryServices.getInstance();
			ManagedDirectory managedDirectory = 
					mds.retrieveManagedDirectoryByJobId(checkinJob.getImportJobId().asLong(), session);
			managedDirectory.setContentStaged(true);
			mds.updateManagedDirectory(managedDirectory, session);
		} catch (BaseTeamsException e) {
			log.error("Checkin: ", e);
			checkedin = false;
		}

		log.info("chechin (END)");

		return checkedin;
	}
}
