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
package com.opentext.otmm.sc.eventlistener.handler;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.artesia.asset.AssetIdentifier;
import com.artesia.asset.metadata.services.AssetMetadataServices;
import com.artesia.common.exception.BaseTeamsException;
import com.artesia.entity.TeamsIdentifier;
import com.artesia.event.Event;
import com.artesia.metadata.MetadataCollection;
import com.artesia.metadata.MetadataField;
import com.artesia.metadata.MetadataValue;
import com.artesia.security.SecuritySession;
import com.opentext.otmm.sc.eventlistener.OTMMField;
import com.opentext.otmm.sc.eventlistener.helper.ManageAssetHelper;
import com.opentext.otmm.sc.eventlistener.helper.MetadataHelper;
import com.opentext.otmm.sc.eventlistener.helper.SecurityHelper;
import com.opentext.otmm.sc.modules.watermark.Watermark;

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

					int numDownloadsInt = numDownloadsField.getValue().getIntValue();
					int numMaxDownloadsInt = numMaxDownloadsField.getValue().getIntValue();

					numDownloadsField.setValue(new MetadataValue(++numDownloadsInt));		

					// Increasing number of downloads counter
					MetadataField[] metadataFields = new MetadataField[] {numDownloadsField};

					MetadataHelper.lockAsset(assetId);
					MetadataHelper.saveMetadataForAsset(assetId, metadataFields);
					MetadataHelper.unlockAsset(assetId);

					// Check if we have achieved  the maximum number of downloads
					if (numDownloadsInt == numMaxDownloadsInt) {
						log.info("Maximum number of download achieved (" + numMaxDownloadsInt + ")");
						log.info("Adding watermark to the asset...");
										
						ManageAssetHelper.checkout(assetId);
						
						File masterFile = ManageAssetHelper.getMasterFile(assetId);

						//Generating watermark
						Watermark wMark = new Watermark();
						File editedFile = wMark.apply(masterFile, "# downloads exceeded");
						log.debug("Watermak added to a new image");
						
						ManageAssetHelper.checkin(assetId, editedFile);
												
						log.debug("Watermak added (as master)!");							
					}
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
			log.error("Error retrieving asset metadata: ", e);
		}

		return assetMetadataCol;
	}	

}
