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

package com.liferay.bookmarks.uad.aggregator;

import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.bookmarks.uad.constants.BookmarksUADConstants;
import com.liferay.bookmarks.uad.entity.BookmarksFolderUADEntity;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.user.associated.data.aggregator.BaseUADEntityAggregator;
import com.liferay.user.associated.data.aggregator.UADEntityAggregator;
import com.liferay.user.associated.data.entity.UADEntity;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noah Sherrill
 */
@Component(
	immediate = true,
	property = {"model.class.name=" + BookmarksUADConstants.BOOKMARKS_FOLDER},
	service = UADEntityAggregator.class
)
public class BookmarksFolderUADEntityAggregator
	extends BaseUADEntityAggregator {

	@Override
	public List<UADEntity> getUADEntities(long userId) {
		DynamicQuery dynamicQuery = _bookmarksFolderLocalService.dynamicQuery();

		Criterion statusByUserIdCriterion = RestrictionsFactoryUtil.eq(
			"statusByUserId", userId);
		Criterion userIdCriterion = RestrictionsFactoryUtil.eq(
			"userId", userId);

		dynamicQuery.add(
			RestrictionsFactoryUtil.or(
				statusByUserIdCriterion, userIdCriterion));

		List<BookmarksFolder> bookmarksFolders =
			_bookmarksFolderLocalService.dynamicQuery(dynamicQuery);

		List<UADEntity> uadEntities = new ArrayList<>(bookmarksFolders.size());

		for (BookmarksFolder bookmarksFolder : bookmarksFolders) {
			uadEntities.add(
				new BookmarksFolderUADEntity(
					userId, _getUADEntityId(userId, bookmarksFolder),
					bookmarksFolder));
		}

		return uadEntities;
	}

	@Override
	public UADEntity getUADEntity(String uadEntityId) throws PortalException {
		BookmarksFolder bookmarksFolder =
			_bookmarksFolderLocalService.getBookmarksFolder(
				_getFolderId(uadEntityId));

		return new BookmarksFolderUADEntity(
			_getUserId(uadEntityId), uadEntityId, bookmarksFolder);
	}

	private long _getFolderId(String uadEntityId) {
		String[] uadEntityIdParts = uadEntityId.split(StringPool.POUND);

		return Long.parseLong(uadEntityIdParts[0]);
	}

	private String _getUADEntityId(
		long userId, BookmarksFolder bookmarksFolder) {

		return String.valueOf(bookmarksFolder.getFolderId()) +
			StringPool.POUND + String.valueOf(userId);
	}

	private long _getUserId(String uadEntityId) {
		String[] uadEntityIdParts = uadEntityId.split(StringPool.POUND);

		return Long.parseLong(uadEntityIdParts[1]);
	}

	@Reference
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

}