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

package com.liferay.blogs.uad.anonymizer;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(immediate = true, service = UADAnonymizer.class)
public class BlogsEntryUADAnonymizer extends BaseBlogsEntryUADAnonymizer {
	@Override
	public void autoAnonymize(
		BlogsEntry blogsEntry, long userId, User anonymousUser)
		throws PortalException {

		if (blogsEntry.getUserId() == userId) {
			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

			assetEntry.setUserId(anonymousUser.getUserId());
			assetEntry.setUserName(anonymousUser.getFullName());

			_assetEntryLocalService.updateAssetEntry(assetEntry);
		}

		super.autoAnonymize(blogsEntry, userId, anonymousUser);
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

}