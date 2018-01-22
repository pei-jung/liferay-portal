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

package com.liferay.bookmarks.uad.anonymizer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.uad.constants.BookmarksUADConstants;
import com.liferay.bookmarks.uad.test.BaseBookmarksFolderUADEntityTestCase;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.user.associated.data.aggregator.UADEntityAggregator;
import com.liferay.user.associated.data.anonymizer.UADEntityAnonymizer;
import com.liferay.user.associated.data.entity.UADEntity;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Noah Sherrill
 */
@RunWith(Arquillian.class)
public class BookmarksFolderUADEntityAnonymizerTest
	extends BaseBookmarksFolderUADEntityTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_defaultUser = _userLocalService.getDefaultUser(
			TestPropsValues.getCompanyId());

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testAutoAnonymize() throws Exception {
		BookmarksFolder bookmarksFolder = addBookmarksFolder(_user.getUserId());

		List<UADEntity> uadEntities = _uadEntityAggregator.getUADEntities(
			_user.getUserId());

		_uadEntityAnonymizer.autoAnonymize(uadEntities.get(0));

		bookmarksFolder = bookmarksFolderLocalService.getFolder(
			bookmarksFolder.getFolderId());

		Assert.assertEquals(
			_defaultUser.getUserId(), bookmarksFolder.getUserId());
		Assert.assertEquals(
			_defaultUser.getFullName(), bookmarksFolder.getUserName());
	}

	@Test
	public void testAutoAnonymizeAll() throws Exception {
		BookmarksFolder bookmarksFolder = addBookmarksFolder(
			TestPropsValues.getUserId());
		BookmarksFolder bookmarksFolderAutoAnonymize = addBookmarksFolder(
			_user.getUserId());

		_uadEntityAnonymizer.autoAnonymizeAll(_user.getUserId());

		bookmarksFolderAutoAnonymize = bookmarksFolderLocalService.getFolder(
			bookmarksFolderAutoAnonymize.getFolderId());

		Assert.assertEquals(
			_defaultUser.getUserId(), bookmarksFolderAutoAnonymize.getUserId());
		Assert.assertEquals(
			_defaultUser.getFullName(),
			bookmarksFolderAutoAnonymize.getUserName());

		bookmarksFolder = bookmarksFolderLocalService.getFolder(
			bookmarksFolder.getFolderId());

		Assert.assertEquals(
			TestPropsValues.getUserId(), bookmarksFolder.getUserId());
	}

	@Test
	public void testAutoAnonymizeAllNoBookmarksFolders() throws Exception {
		_uadEntityAnonymizer.autoAnonymizeAll(_user.getUserId());
	}

	@Test
	public void testAutoAnonymizeStatusByUserOnly() throws Exception {
		User user = TestPropsValues.getUser();

		BookmarksFolder bookmarksFolder = addBookmarksFolder(user.getUserId());

		bookmarksFolderLocalService.updateStatus(
			_user.getUserId(), bookmarksFolder,
			WorkflowConstants.STATUS_APPROVED);

		List<UADEntity> uadEntities = _uadEntityAggregator.getUADEntities(
			_user.getUserId());

		_uadEntityAnonymizer.autoAnonymize(uadEntities.get(0));

		bookmarksFolder = bookmarksFolderLocalService.getFolder(
			bookmarksFolder.getFolderId());

		Assert.assertEquals(
			_defaultUser.getUserId(), bookmarksFolder.getStatusByUserId());
		Assert.assertEquals(
			_defaultUser.getFullName(), bookmarksFolder.getStatusByUserName());
		Assert.assertEquals(user.getUserId(), bookmarksFolder.getUserId());
		Assert.assertEquals(user.getFullName(), bookmarksFolder.getUserName());
	}

	@Test
	public void testAutoAnonymizeUserOnly() throws Exception {
		BookmarksFolder bookmarksFolder = addBookmarksFolder(_user.getUserId());

		User user = TestPropsValues.getUser();

		bookmarksFolderLocalService.updateStatus(
			user.getUserId(), bookmarksFolder,
			WorkflowConstants.STATUS_APPROVED);

		List<UADEntity> uadEntities = _uadEntityAggregator.getUADEntities(
			_user.getUserId());

		_uadEntityAnonymizer.autoAnonymize(uadEntities.get(0));

		bookmarksFolder = bookmarksFolderLocalService.getFolder(
			bookmarksFolder.getFolderId());

		Assert.assertEquals(
			user.getUserId(), bookmarksFolder.getStatusByUserId());
		Assert.assertEquals(
			user.getFullName(), bookmarksFolder.getStatusByUserName());
		Assert.assertEquals(
			_defaultUser.getUserId(), bookmarksFolder.getUserId());
		Assert.assertEquals(
			_defaultUser.getFullName(), bookmarksFolder.getUserName());
	}

	@Test(expected = NoSuchFolderException.class)
	public void testDelete() throws Exception {
		BookmarksFolder bookmarksFolder = addBookmarksFolder(_user.getUserId());

		List<UADEntity> uadEntities = _uadEntityAggregator.getUADEntities(
			_user.getUserId());

		_uadEntityAnonymizer.delete(uadEntities.get(0));

		bookmarksFolderLocalService.getFolder(bookmarksFolder.getFolderId());
	}

	@Test
	public void testDeleteAll() throws Exception {
		BookmarksFolder bookmarksFolder = addBookmarksFolder(
			TestPropsValues.getUserId());
		BookmarksFolder bookmarksFolderDelete = addBookmarksFolder(
			_user.getUserId());

		_uadEntityAnonymizer.deleteAll(_user.getUserId());

		bookmarksFolder = bookmarksFolderLocalService.getFolder(
			bookmarksFolder.getFolderId());

		Assert.assertEquals(
			TestPropsValues.getUserId(), bookmarksFolder.getUserId());

		bookmarksFolderDelete =
			bookmarksFolderLocalService.fetchBookmarksFolder(
				bookmarksFolderDelete.getFolderId());

		Assert.assertNull(bookmarksFolderDelete);
	}

	@Test
	public void testDeleteAllNoBookmarksFolders() throws Exception {
		_uadEntityAnonymizer.deleteAll(_user.getUserId());
	}

	private User _defaultUser;

	@Inject(
		filter = "model.class.name=" + BookmarksUADConstants.BOOKMARKS_FOLDER
	)
	private UADEntityAggregator _uadEntityAggregator;

	@Inject(
		filter = "model.class.name=" + BookmarksUADConstants.BOOKMARKS_FOLDER
	)
	private UADEntityAnonymizer _uadEntityAnonymizer;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}