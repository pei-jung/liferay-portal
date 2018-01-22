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

package com.liferay.bookmarks.uad.aggregator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.uad.constants.BookmarksUADConstants;
import com.liferay.bookmarks.uad.test.BaseBookmarksFolderUADEntityTestCase;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.user.associated.data.aggregator.UADEntityAggregator;
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
public class BookmarksFolderUADEntityAggregatorTest
	extends BaseBookmarksFolderUADEntityTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testCount() throws Exception {
		addBookmarksFolder(_user.getUserId());

		Assert.assertEquals(1, _uadEntityAggregator.count(_user.getUserId()));
	}

	@Test
	public void testGetUADEntities() throws Exception {
		addBookmarksFolder(TestPropsValues.getUserId());
		addBookmarksFolder(_user.getUserId());

		List<UADEntity> uadEntities = _uadEntityAggregator.getUADEntities(
			_user.getUserId());

		Assert.assertEquals(uadEntities.toString(), 1, uadEntities.size());

		UADEntity uadEntity = uadEntities.get(0);

		Assert.assertEquals(_user.getUserId(), uadEntity.getUserId());
	}

	@Test
	public void testGetUADEntitiesByStatusByUserId() throws Exception {
		long defaultUserId = _userLocalService.getDefaultUserId(
			TestPropsValues.getCompanyId());

		bookmarksFolderLocalService.updateStatus(
			_user.getUserId(), addBookmarksFolder(defaultUserId),
			WorkflowConstants.STATUS_APPROVED);

		List<UADEntity> uadEntities = _uadEntityAggregator.getUADEntities(
			_user.getUserId());

		Assert.assertEquals(uadEntities.toString(), 1, uadEntities.size());

		UADEntity uadEntity = uadEntities.get(0);

		Assert.assertEquals(_user.getUserId(), uadEntity.getUserId());
	}

	@Test
	public void testGetUADEntitiesNoBookmarksFolders() throws Exception {
		List<UADEntity> uadEntities = _uadEntityAggregator.getUADEntities(
			_user.getUserId());

		Assert.assertEquals(uadEntities.toString(), 0, uadEntities.size());
	}

	@Test
	public void testGetUADEntity() throws Exception {
		BookmarksFolder bookmarksFolder = addBookmarksFolder(_user.getUserId());

		long folderId = bookmarksFolder.getFolderId();

		UADEntity uadEntity = _uadEntityAggregator.getUADEntity(
			String.valueOf(folderId) + StringPool.POUND +
				String.valueOf(_user.getUserId()));

		Assert.assertEquals(_user.getUserId(), uadEntity.getUserId());
	}

	@Inject(
		filter = "model.class.name=" + BookmarksUADConstants.BOOKMARKS_FOLDER
	)
	private UADEntityAggregator _uadEntityAggregator;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}