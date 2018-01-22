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

package com.liferay.bookmarks.uad.anonymizer;

import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.bookmarks.uad.constants.BookmarksUADConstants;
import com.liferay.bookmarks.uad.entity.BookmarksFolderUADEntity;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.user.associated.data.aggregator.UADEntityAggregator;
import com.liferay.user.associated.data.anonymizer.BaseUADEntityAnonymizer;
import com.liferay.user.associated.data.anonymizer.UADEntityAnonymizer;
import com.liferay.user.associated.data.entity.UADEntity;
import com.liferay.user.associated.data.exception.UADEntityException;
import com.liferay.user.associated.data.util.UADAnonymizerHelper;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noah Sherrill
 */
@Component(
	immediate = true,
	property = {"model.class.name=" + BookmarksUADConstants.BOOKMARKS_FOLDER},
	service = UADEntityAnonymizer.class
)
public class BookmarksFolderUADEntityAnonymizer
	extends BaseUADEntityAnonymizer {

	@Override
	public void autoAnonymize(UADEntity uadEntity) throws PortalException {
		BookmarksFolder bookmarksFolder = _getBookmarksFolder(uadEntity);

		User anonymousUser = _uadAnonymizerHelper.getAnonymousUser();

		if (bookmarksFolder.getStatusByUserId() == uadEntity.getUserId()) {
			bookmarksFolder.setStatusByUserId(anonymousUser.getUserId());
			bookmarksFolder.setStatusByUserName(anonymousUser.getFullName());
		}

		if (bookmarksFolder.getUserId() == uadEntity.getUserId()) {
			bookmarksFolder.setUserId(anonymousUser.getUserId());
			bookmarksFolder.setUserName(anonymousUser.getFullName());
		}

		_bookmarksFolderLocalService.updateBookmarksFolder(bookmarksFolder);
	}

	@Override
	public void delete(UADEntity uadEntity) throws PortalException {
		_bookmarksFolderLocalService.deleteFolder(
			_getBookmarksFolder(uadEntity));
	}

	@Override
	protected List<UADEntity> getUADEntities(long userId) {
		return _uadEntityAggregator.getUADEntities(userId);
	}

	private BookmarksFolder _getBookmarksFolder(UADEntity uadEntity)
		throws PortalException {

		_validate(uadEntity);

		BookmarksFolderUADEntity bookmarksFolderUADEntity =
			(BookmarksFolderUADEntity)uadEntity;

		return bookmarksFolderUADEntity.getBookmarksFolder();
	}

	private void _validate(UADEntity uadEntity) throws PortalException {
		if (!(uadEntity instanceof BookmarksFolderUADEntity)) {
			throw new UADEntityException();
		}
	}

	@Reference
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

	@Reference
	private UADAnonymizerHelper _uadAnonymizerHelper;

	@Reference(
		target = "(model.class.name=" + BookmarksUADConstants.BOOKMARKS_FOLDER + ")"
	)
	private UADEntityAggregator _uadEntityAggregator;

}