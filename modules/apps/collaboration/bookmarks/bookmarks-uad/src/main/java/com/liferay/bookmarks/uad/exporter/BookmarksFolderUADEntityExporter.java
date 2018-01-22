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

package com.liferay.bookmarks.uad.exporter;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.uad.constants.BookmarksUADConstants;
import com.liferay.bookmarks.uad.entity.BookmarksFolderUADEntity;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.user.associated.data.aggregator.UADEntityAggregator;
import com.liferay.user.associated.data.entity.UADEntity;
import com.liferay.user.associated.data.exception.UADEntityException;
import com.liferay.user.associated.data.exception.UADEntityExporterException;
import com.liferay.user.associated.data.exporter.BaseUADEntityExporter;
import com.liferay.user.associated.data.exporter.UADEntityExporter;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noah Sherrill
 */
@Component(
	immediate = true,
	property = {"model.class.name=" + BookmarksUADConstants.BOOKMARKS_FOLDER},
	service = UADEntityExporter.class
)
public class BookmarksFolderUADEntityExporter extends BaseUADEntityExporter {

	@Override
	public void export(UADEntity uadEntity) throws PortalException {
		BookmarksFolder bookmarksFolder = _getBookmarksFolder(uadEntity);

		String json = getJSON(bookmarksFolder);

		Folder folder = getFolder(
			bookmarksFolder.getCompanyId(), BookmarksPortletKeys.BOOKMARKS,
			_FOLDER_NAME);

		try {
			InputStream is = new UnsyncByteArrayInputStream(
				json.getBytes(StringPool.UTF8));

			PortletFileRepositoryUtil.addPortletFileEntry(
				folder.getGroupId(), bookmarksFolder.getUserId(),
				Group.class.getName(), folder.getGroupId(),
				BookmarksPortletKeys.BOOKMARKS, folder.getFolderId(), is,
				uadEntity.getUADEntityId() + ".json",
				ContentTypes.APPLICATION_JSON, false);
		}
		catch (UnsupportedEncodingException uee) {
			throw new UADEntityExporterException(uee);
		}
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

	private static final String _FOLDER_NAME = "UADExport";

	@Reference(
		target = "(model.class.name=" + BookmarksUADConstants.BOOKMARKS_FOLDER + ")"
	)
	private UADEntityAggregator _uadEntityAggregator;

}