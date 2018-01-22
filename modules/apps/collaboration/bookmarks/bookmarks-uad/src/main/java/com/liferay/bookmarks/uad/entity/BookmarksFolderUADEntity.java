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

package com.liferay.bookmarks.uad.entity;

import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.uad.constants.BookmarksUADConstants;
import com.liferay.user.associated.data.entity.BaseUADEntity;

/**
 * @author Noah Sherrill
 */
public class BookmarksFolderUADEntity extends BaseUADEntity {

	public BookmarksFolderUADEntity(
		long userId, String uadEntityId, BookmarksFolder bookmarksFolder) {

		super(userId, uadEntityId, BookmarksUADConstants.BOOKMARKS_FOLDER);

		_bookmarksFolder = bookmarksFolder;
	}

	public BookmarksFolder getBookmarksFolder() {
		return _bookmarksFolder;
	}

	private final BookmarksFolder _bookmarksFolder;

}