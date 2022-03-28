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

package com.liferay.account.admin.web.internal.dao.search;

import com.liferay.account.admin.web.internal.display.AccountUserDisplay;
import com.liferay.account.configuration.AccountEntryEmailDomainsConfiguration;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.util.TransformUtil;
import com.liferay.users.admin.kernel.util.UsersAdminUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = {})
public class AssignableAccountUserDisplaySearchContainerFactory {

	public static SearchContainer<AccountUserDisplay> create(
			long accountEntryId, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			RowChecker rowChecker)
		throws PortalException {

		SearchContainer<AccountUserDisplay> searchContainer =
			new SearchContainer(
				liferayPortletRequest,
				PortletURLUtil.getCurrent(
					liferayPortletRequest, liferayPortletResponse),
				null, "no-users-were-found");

		searchContainer.setId("accountUsers");
		searchContainer.setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				liferayPortletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				"assignable-account-user-order-by-col", "last-name"));
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				liferayPortletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				"assignable-account-user-order-by-type", "asc"));

		String navigation = ParamUtil.getString(
			liferayPortletRequest, "navigation");

		if (Validator.isNull(navigation)) {
			navigation = _getDefaultNavigation(liferayPortletRequest);
		}

		String keywords = ParamUtil.getString(
			liferayPortletRequest, "keywords", null);

		LinkedHashMap<String, Object> params =
			LinkedHashMapBuilder.<String, Object>put(
				"emailAddressDomains",
				_getEmailAddressDomains(accountEntryId, navigation)
			).build();

		long accountRoleId = ParamUtil.getLong(
			liferayPortletRequest, "accountRoleId");

		if ((accountEntryId > 0) && (accountRoleId > 0)) {
			params.put("accountEntryIds", new long[] {accountEntryId});
		}
		else if (navigation.equals("account-users")) {
			params.put(
				"accountEntryIds",
				new long[] {AccountConstants.ACCOUNT_ENTRY_ID_ANY});
		}
		else if (navigation.equals("no-assigned-account")) {
			params.put("accountEntryIds", new long[0]);
		}

		List<User> users = _userLocalService.search(
			PortalUtil.getCompanyId(liferayPortletRequest), keywords,
			WorkflowConstants.STATUS_APPROVED, params,
			searchContainer.getStart(), searchContainer.getEnd(),
			UsersAdminUtil.getUserOrderByComparator(
				searchContainer.getOrderByCol(),
				searchContainer.getOrderByType()));

		searchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(users, AccountUserDisplay::of),
			_userLocalService.searchCount(
				PortalUtil.getCompanyId(liferayPortletRequest), keywords,
				WorkflowConstants.STATUS_APPROVED, params));

		searchContainer.setRowChecker(rowChecker);

		return searchContainer;
	}

	@Reference(unbind = "-")
	protected void setAccountEntryLocalService(
		AccountEntryLocalService accountEntryLocalService) {

		_accountEntryLocalService = accountEntryLocalService;
	}

	@Reference(unbind = "-")
	protected void setAccountEntryUserRelLocalService(
		AccountEntryUserRelLocalService accountEntryUserRelLocalService) {

		_accountEntryUserRelLocalService = accountEntryUserRelLocalService;
	}

	@Reference(unbind = "-")
	protected void setAccountRoleLocalService(
		AccountRoleLocalService accountRoleLocalService) {

		_accountRoleLocalService = accountRoleLocalService;
	}

	@Reference(unbind = "-")
	protected void setUserGroupRoleLocalService(
		UserGroupRoleLocalService userGroupRoleLocalService) {

		_userGroupRoleLocalService = userGroupRoleLocalService;
	}

	@Reference(unbind = "-")
	protected void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	private static String _getDefaultNavigation(
		LiferayPortletRequest liferayPortletRequest) {

		try {
			AccountEntryEmailDomainsConfiguration
				accountEntryEmailDomainsConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						AccountEntryEmailDomainsConfiguration.class,
						PortalUtil.getCompanyId(liferayPortletRequest));

			if (accountEntryEmailDomainsConfiguration.
					enableEmailDomainValidation()) {

				return "valid-domain-users";
			}
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return "all-users";
	}

	private static String[] _getEmailAddressDomains(
		long accountEntryId, String navigation) {

		if (Objects.equals(navigation, "valid-domain-users")) {
			AccountEntry accountEntry =
				_accountEntryLocalService.fetchAccountEntry(accountEntryId);

			return StringUtil.split(accountEntry.getDomains());
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssignableAccountUserDisplaySearchContainerFactory.class);

	private static AccountEntryLocalService _accountEntryLocalService;
	private static AccountEntryUserRelLocalService
		_accountEntryUserRelLocalService;
	private static AccountRoleLocalService _accountRoleLocalService;
	private static UserGroupRoleLocalService _userGroupRoleLocalService;
	private static UserLocalService _userLocalService;

}