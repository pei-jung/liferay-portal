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

package com.liferay.document.library.uad.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.user.associated.data.display.UADDisplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class DLStructureTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_fileUadDisplay = getFileUADDisplay();
		_folderUadDisplay = getFolderUADDisplay();
	}

	@Test
	public void testStructure() throws Exception {
		Map<Long, Integer> topLevelFoldersMap = _getTopLevelFoldersMap(
			0, 37401);

		return;
	}

	protected UADDisplay getFileUADDisplay() {
		return _fileUadDisplay;
	}

	protected UADDisplay getFolderUADDisplay() {
		return _folderUadDisplay;
	}

	private void _addTopLevelFolderToMap(
			DLFileEntry dlFileEntry, long parentFolderId,
			Map<Long, Integer> topLevelFoldersMap)
		throws PortalException {

		long topLevelFolderId = dlFileEntry.getFolderId();

		if (topLevelFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		DLFolder dlFolder = dlFileEntry.getFolder();

		int itemCount = 0;

		if (dlFolder.getParentFolderId() != parentFolderId) {
			topLevelFolderId = _getTopLevelFolderId(dlFolder, parentFolderId);

			itemCount = 1;
		}

		if (topLevelFoldersMap.containsKey(topLevelFolderId)) {
			itemCount += topLevelFoldersMap.get(topLevelFolderId);
		}

		topLevelFoldersMap.put(topLevelFolderId, itemCount);
	}

	private void _addTopLevelFolderToMap(
			DLFolder dlFolder, long parentFolderId,
			Map<Long, Integer> topLevelFoldersMap)
		throws PortalException {

		long topLevelFolderId = dlFolder.getFolderId();

		int itemCount = 0;

		if (dlFolder.getParentFolderId() != parentFolderId) {
			topLevelFolderId = _getTopLevelFolderId(dlFolder, parentFolderId);

			itemCount = 1;
		}

		if (topLevelFoldersMap.containsKey(topLevelFolderId)) {
			itemCount += topLevelFoldersMap.get(topLevelFolderId);
		}

		topLevelFoldersMap.put(topLevelFolderId, itemCount);
	}

	private long _getTopLevelFolderId(DLFolder dlFolder, long parentFolderId)
		throws PortalException {

		List<Long> ancestorFolderIds = dlFolder.getAncestorFolderIds();

		if (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return ancestorFolderIds.get(ancestorFolderIds.size() - 1);
		}

		return ancestorFolderIds.get(
			ancestorFolderIds.indexOf(parentFolderId) - 1);
	}

	private Map<Long, Integer> _getTopLevelFoldersMap(
			long parentFolderId, long userId)
		throws PortalException {

		Map<Long, Integer> topLevelFoldersMap = new HashMap<>();

		List<DLFileEntry> dlFileEntries = _fileUadDisplay.search(
			userId, null, "", "modifiedDate", "asc", QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			_addTopLevelFolderToMap(
				dlFileEntry, parentFolderId, topLevelFoldersMap);
		}

		List<DLFolder> dlFolders = _folderUadDisplay.search(
			userId, null, "", "modifiedDate", "asc", QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		for (DLFolder dlFolder : dlFolders) {
			_addTopLevelFolderToMap(
				dlFolder, parentFolderId, topLevelFoldersMap);
		}

		return topLevelFoldersMap;
	}

	@Inject(filter = "component.name=*.DLFileEntryUADDisplay")
	private UADDisplay _fileUadDisplay;

	@Inject(filter = "component.name=*.DLFolderUADDisplay")
	private UADDisplay _folderUadDisplay;

}