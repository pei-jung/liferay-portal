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
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Noah Sherrill
 */
public class BookmarksFolderUADEntityTest {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		_bookmarksFolderUADEntity = new BookmarksFolderUADEntity(
			_USER_ID, _UAD_ENTITY_ID, _bookmarksFolder);
	}

	@Test
	public void testGetBookmarksFolder() throws Exception {
		Assert.assertEquals(
			_bookmarksFolder, _bookmarksFolderUADEntity.getBookmarksFolder());
	}

	@Test
	public void testGetUADEntityId() throws Exception {
		Assert.assertEquals(
			_UAD_ENTITY_ID, _bookmarksFolderUADEntity.getUADEntityId());
	}

	@Test
	public void testGetUADRegistryKey() throws Exception {
		Assert.assertEquals(
			BookmarksFolder.class.getName(),
			_bookmarksFolderUADEntity.getUADRegistryKey());
	}

	@Test
	public void testGetUserId() throws Exception {
		Assert.assertEquals(_USER_ID, _bookmarksFolderUADEntity.getUserId());
	}

	private static final String _UAD_ENTITY_ID = RandomTestUtil.randomString();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	@Mock
	private BookmarksFolder _bookmarksFolder;

	private BookmarksFolderUADEntity _bookmarksFolderUADEntity;

}