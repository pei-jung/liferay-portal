/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.wiki.uad.anonymizer;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.wiki.model.WikiPage;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(immediate = true, service = UADAnonymizer.class)
public class WikiPageUADAnonymizer extends BaseWikiPageUADAnonymizer {

	@Override
	public void autoAnonymize(
			WikiPage wikiPage, long userId, User anonymousUser)
		throws PortalException {

		if (wikiPage.getUserId() == userId) {
			wikiPage.setUserId(anonymousUser.getUserId());
			wikiPage.setUserName(anonymousUser.getFullName());

			AssetEntry assetEntry = assetEntryLocalService.getEntry(
				WikiPage.class.getName(), wikiPage.getResourcePrimKey());

			assetEntry.setUserId(anonymousUser.getUserId());
			assetEntry.setUserName(anonymousUser.getFullName());

			assetEntryLocalService.updateAssetEntry(assetEntry);
		}

		if (wikiPage.getStatusByUserId() == userId) {
			wikiPage.setStatusByUserId(anonymousUser.getUserId());
			wikiPage.setStatusByUserName(anonymousUser.getFullName());
		}

		wikiPageLocalService.updateWikiPage(wikiPage);
	}

}