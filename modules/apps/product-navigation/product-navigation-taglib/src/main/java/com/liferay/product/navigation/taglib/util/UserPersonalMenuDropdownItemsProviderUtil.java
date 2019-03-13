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

package com.liferay.product.navigation.taglib.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.user.personal.menu.UserPersonalMenuEntry;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pei-Jung Lan
 */
public class UserPersonalMenuDropdownItemsProviderUtil {

	public static JSONArray getDropdownItemsJSONArray(
		HttpServletRequest request) {

		List<List<UserPersonalMenuEntry>> groupedPersonalMenuEntries =
			UserPersonalMenuEntryRegistryUtil.
				getGroupedUserPersonalMenuEntries();

		int size = groupedPersonalMenuEntries.size();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < size; i++) {
			int index = i;

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			jsonObject.put(
				"items",
				_getDropdownItemsJSONArray(
					request, groupedPersonalMenuEntries.get(i)));

			if (index < (size - 1)) {
				jsonObject.put("separator", true);
			}

			jsonObject.put("type", "group");

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private static JSONArray _getDropdownItemsJSONArray(
		HttpServletRequest request,
		List<UserPersonalMenuEntry> userPersonalMenuEntries) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (UserPersonalMenuEntry userPersonalMenuEntry :
				userPersonalMenuEntries) {

			if (!userPersonalMenuEntry.isShow(
					themeDisplay.getPermissionChecker())) {

				continue;
			}

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			try {
				jsonObject.put(
					"href", userPersonalMenuEntry.getPortletURL(request));
			}
			catch (PortalException pe) {
				_log.error(pe, pe);
			}

			jsonObject.put(
				"label",
				userPersonalMenuEntry.getLabel(themeDisplay.getLocale()));

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserPersonalMenuDropdownItemsProviderUtil.class);

}