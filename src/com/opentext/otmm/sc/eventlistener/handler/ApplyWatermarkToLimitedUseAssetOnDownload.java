package com.opentext.otmm.sc.eventlistener.handler;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artesia.asset.AssetIdentifier;
import com.artesia.asset.metadata.services.AssetMetadataServices;
import com.artesia.common.exception.BaseTeamsException;
import com.artesia.entity.TeamsIdentifier;
import com.artesia.event.Event;
import com.artesia.metadata.MetadataCollection;
import com.artesia.metadata.MetadataField;
import com.artesia.security.SecuritySession;
import com.opentext.job.Job;
import com.opentext.otmm.sc.eventlistener.OTMMField;
import com.opentext.otmm.sc.eventlistener.helper.JobHelper;
import com.opentext.otmm.sc.eventlistener.helper.SecurityHelper;

public class ApplyWatermarkToLimitedUseAssetOnDownload implements OTMMEventHandler {

	private static final Log log = LogFactory.getLog(ApplyWatermarkToLimitedUseAssetOnDownload.class);

	@Override
	public boolean handle(Event event) {
		boolean handled = false;

		String assetIdStr = event.getObjectId();		
		log.debug("Asset ID (String version): " + assetIdStr);
		
		if(assetIdStr != null) {
			AssetIdentifier assetId = new AssetIdentifier(assetIdStr);
			MetadataCollection assetMetadataCol = retrieveMetadataForAsset(assetId);

			if(assetMetadataCol != null) {
				log.debug("Asset Metadata (size): " + assetMetadataCol.size());
				
				//Recovering Custom metadata 				
				MetadataField numDownloadsField = (MetadataField) assetMetadataCol.findElementById(new TeamsIdentifier(OTMMField.RVFD_FIELD_NUM_DOWNLOADS));
				MetadataField numMaxDownloadsField = (MetadataField) assetMetadataCol.findElementById(new TeamsIdentifier(OTMMField.RVFD_FIELD_NUM_MAX_DOWNLOADS));
				if(numDownloadsField != null && numMaxDownloadsField != null) {
					log.debug(">>> Number Downloads: " + numDownloadsField.getValue().getIntValue());
					log.debug(">>> Max number Downloads: " + numMaxDownloadsField.getValue().getIntValue());						
				}				
			}
		}
		else {
			log.debug("Assets list was EMPTY!!!");
		}

		return handled;
	}
	
	private MetadataCollection retrieveMetadataForAsset(AssetIdentifier assetId) {
		log.debug("Asset Id: " + assetId.getId());

		// Retrieve tabular metadata fields for the asset
		TeamsIdentifier[] fieldIds = new TeamsIdentifier[] {
				new TeamsIdentifier(OTMMField.RVFD_FIELD_NUM_DOWNLOADS),
				new TeamsIdentifier(OTMMField.RVFD_FIELD_NUM_MAX_DOWNLOADS)};

		SecuritySession session = SecurityHelper.getAdminSession();

		MetadataCollection assetMetadataCol = null;
		try {
			assetMetadataCol = AssetMetadataServices.getInstance().retrieveMetadataForAsset(assetId, fieldIds, session);
		} catch (BaseTeamsException e) {
			log.error("Error retrieving metadata", e);
		}

		return assetMetadataCol;
	}	

}
