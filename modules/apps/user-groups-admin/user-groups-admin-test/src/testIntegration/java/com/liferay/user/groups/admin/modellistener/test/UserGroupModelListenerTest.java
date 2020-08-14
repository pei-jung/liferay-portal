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

package com.liferay.user.groups.admin.modellistener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcell Gyöpös
 */
@RunWith(Arquillian.class)
public class UserGroupModelListenerTest {

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		addLayouts(false, false);
	}

	@After
	public void tearDown() throws Exception {
		UserLocalServiceUtil.deleteUser(_userId);
		UserGroupLocalServiceUtil.deleteUserGroup(_userGroupIds[0]);
		UserGroupLocalServiceUtil.deleteUserGroup(_userGroupIds[1]);
		_userGroupIds = new long[2];
	}

	@Test
	public void testDeleteUserGroupFromSiteWhenUserDoesntHaveAnotherGroupAndDirectMembership()
		throws Exception {

		addUsers();
		addUserGroups();
		UserGroupLocalServiceUtil.addUserUserGroup(_userId, _userGroupIds[0]);
		GroupLocalServiceUtil.addUserGroupGroup(_userGroupIds[0], _group);
		addLayouts(true, false);
		BlogsEntryLocalServiceUtil.subscribe(_userId, _group.getGroupId());
		GroupLocalServiceUtil.deleteUserGroupGroup(
			_userGroupIds[0], _group.getGroupId());
		Assert.assertFalse(
			SubscriptionLocalServiceUtil.isSubscribed(
				_group.getCompanyId(), _userId, BlogsEntry.class.getName(),
				_group.getGroupId()));
	}

	@Test
	public void testDeleteUserGroupFromSiteWhenUserHasAnotherGroup()
		throws Exception {

		addUsers();
		addUserGroups();

		UserGroupLocalServiceUtil.addUserUserGroup(_userId, _userGroupIds[0]);

		UserGroupLocalServiceUtil.addUserUserGroup(_userId, _userGroupIds[1]);

		GroupLocalServiceUtil.addUserGroupGroup(_userGroupIds[0], _group);

		GroupLocalServiceUtil.addUserGroupGroup(_userGroupIds[1], _group);

		addLayouts(true, false);

		BlogsEntryLocalServiceUtil.subscribe(_userId, _group.getGroupId());

		GroupLocalServiceUtil.deleteUserGroupGroup(
			_userGroupIds[0], _group.getGroupId());

		Assert.assertTrue(
			SubscriptionLocalServiceUtil.isSubscribed(
				_group.getCompanyId(), _userId, BlogsEntry.class.getName(),
				_group.getGroupId()));
	}

	@Test
	public void testDeleteUserGroupFromSiteWhenUserHasDirectMembership()
		throws Exception {

		addUsers();
		addUserGroups();

		UserGroupLocalServiceUtil.addUserUserGroup(_userId, _userGroupIds[0]);

		GroupLocalServiceUtil.addUserGroup(_userId, _group);

		GroupLocalServiceUtil.addUserGroupGroup(_userGroupIds[0], _group);

		addLayouts(true, false);

		BlogsEntryLocalServiceUtil.subscribe(_userId, _group.getGroupId());

		GroupLocalServiceUtil.deleteUserGroupGroup(
			_userGroupIds[0], _group.getGroupId());

		Assert.assertTrue(
			SubscriptionLocalServiceUtil.isSubscribed(
				_group.getCompanyId(), _userId, BlogsEntry.class.getName(),
				_group.getGroupId()));
	}

	@Test
	public void testDeleteUsersFromGroupWhenUserDoesntHaveAnotherGroupAndDirectMembership()
		throws Exception {

		addUsers();
		addUserGroups();

		UserGroupLocalServiceUtil.addUserUserGroup(_userId, _userGroupIds[0]);

		GroupLocalServiceUtil.addUserGroupGroup(_userGroupIds[0], _group);

		addLayouts(true, false);

		BlogsEntryLocalServiceUtil.subscribe(_userId, _group.getGroupId());

		UserGroupLocalServiceUtil.deleteUserUserGroup(
			_userId, _userGroupIds[0]);

		Assert.assertFalse(
			SubscriptionLocalServiceUtil.isSubscribed(
				_group.getCompanyId(), _userId, BlogsEntry.class.getName(),
				_group.getGroupId()));
	}

	@Test
	public void testDeleteUsersFromGroupWhenUserHasAnotherGroup()
		throws Exception {

		addUsers();
		addUserGroups();

		UserGroupLocalServiceUtil.addUserUserGroup(_userId, _userGroupIds[0]);

		UserGroupLocalServiceUtil.addUserUserGroup(_userId, _userGroupIds[1]);

		GroupLocalServiceUtil.addUserGroupGroup(_userGroupIds[0], _group);

		GroupLocalServiceUtil.addUserGroupGroup(_userGroupIds[1], _group);

		addLayouts(true, false);

		BlogsEntryLocalServiceUtil.subscribe(_userId, _group.getGroupId());

		UserGroupLocalServiceUtil.deleteUserUserGroup(
			_userId, _userGroupIds[0]);

		Assert.assertTrue(
			SubscriptionLocalServiceUtil.isSubscribed(
				_group.getCompanyId(), _userId, BlogsEntry.class.getName(),
				_group.getGroupId()));
	}

	@Test
	public void testDeleteUsersFromGroupWhenUserHasDirectMembership()
		throws Exception {

		addUsers();
		addUserGroups();

		UserGroupLocalServiceUtil.addUserUserGroup(_userId, _userGroupIds[0]);

		GroupLocalServiceUtil.addUserGroup(_userId, _group);

		GroupLocalServiceUtil.addUserGroupGroup(_userGroupIds[0], _group);

		addLayouts(true, false);

		BlogsEntryLocalServiceUtil.subscribe(_userId, _group.getGroupId());

		UserGroupLocalServiceUtil.deleteUserUserGroup(
			_userId, _userGroupIds[0]);

		Assert.assertTrue(
			SubscriptionLocalServiceUtil.isSubscribed(
				_group.getCompanyId(), _userId, BlogsEntry.class.getName(),
				_group.getGroupId()));
	}

	protected void addLayouts(
			boolean portletExists, boolean blogEntryWithDifferentGroup)
		throws Exception {

		_blogLayout = LayoutTestUtil.addLayout(_group);
		_assetLayout = LayoutTestUtil.addLayout(_group);

		if (portletExists) {
			String portletId = PortletProviderUtil.getPortletId(
				BlogsEntry.class.getName(), PortletProvider.Action.VIEW);

			LayoutTestUtil.addPortletToLayout(_blogLayout, portletId);
		}

		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"assetLinkBehavior", new String[] {"viewInPortlet"}
		).build();

		_testPortletId = PortletIdCodec.encode(
			"com_liferay_hello_world_web_portlet_HelloWorldPortlet");

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		LayoutTestUtil.addPortletToLayout(
			user.getUserId(), _assetLayout, _testPortletId, "column-1",
			preferenceMap);
	}

	protected void addUserGroups() throws Exception {
		UserGroup userGroup1 = UserGroupTestUtil.addUserGroup();
		UserGroup userGroup2 = UserGroupTestUtil.addUserGroup();
		_userGroupIds[0] = userGroup1.getUserGroupId();
		_userGroupIds[1] = userGroup2.getUserGroupId();
	}

	protected void addUsers() throws Exception {
		User user = UserTestUtil.addUser();

		_userId = user.getUserId();
	}

	private static long[] _userGroupIds = new long[2];
	private static long _userId;

	@DeleteAfterTestRun
	private Layout _assetLayout;

	@DeleteAfterTestRun
	private Layout _blogLayout;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private String _testPortletId;

}