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

package com.liferay.users.admin.web.internal.management.toolbar;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.users.admin.constants.UsersAdminManagementToolbarKeys;
import com.liferay.users.admin.management.toolbar.FilterContributor;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = FilterContributor.class)
public class UserTypeFilterContributor implements FilterContributor {

	@Override
	public String getDefaultValue() {
		return "user";
	}

	@Override
	public String getLabel(Locale locale) {
		return _getMessage(locale, "filter-by-type");
	}

	@Override
	public String getManagementToolbarKey() {
		return UsersAdminManagementToolbarKeys.VIEW_FLAT_USERS;
	}

	@Override
	public String getParameter() {
		return "type";
	}

	@Override
	public Map<String, Object> getSearchParameters(String currentValue) {
		Map<String, Object> params = new LinkedHashMap<>();

		if (currentValue.equals("service-account")) {
			params.put(
				"types", new long[] {UserConstants.TYPE_SERVICE_ACCOUNT});
		}
		else if (currentValue.equals("user")) {
			params.put("types", new long[] {UserConstants.TYPE_USER});
		}

		return params;
	}

	@Override
	public String getShortLabel(Locale locale) {
		return _getMessage(locale, "type");
	}

	@Override
	public String getValueLabel(Locale locale, String value) {
		return _getMessage(locale, value);
	}

	@Override
	public String[] getValues() {
		return new String[] {"all", "user", "service-account"};
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker) {
		if (permissionChecker.isCompanyAdmin()) {
			return true;
		}

		return false;
	}

	private String _getMessage(Locale locale, String key) {
		return _language.get(locale, key);
	}

	@Reference
	private Language _language;

}