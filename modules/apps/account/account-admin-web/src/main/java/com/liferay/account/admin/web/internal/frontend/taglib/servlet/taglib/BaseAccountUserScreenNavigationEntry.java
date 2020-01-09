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

package com.liferay.account.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.account.admin.web.internal.constants.AccountScreenNavigationEntryConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
public abstract class BaseAccountUserScreenNavigationEntry
	implements ScreenNavigationEntry<User> {

	public abstract String getActionCommandName();

	public abstract String getJspPath();

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(getResourceBundle(locale), getEntryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return AccountScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_ACCOUNT_USER;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			"ACTION_COMMAND_NAME", getActionCommandName());
		httpServletRequest.setAttribute("EDITABLE", Boolean.TRUE);
		httpServletRequest.setAttribute(
			"FORM_LABEL", getLabel(httpServletRequest.getLocale()));
		httpServletRequest.setAttribute("JSP_PATH", getJspPath());
		httpServletRequest.setAttribute("SHOW_CONTROLS", isShowControls());
		httpServletRequest.setAttribute("SHOW_TITLE", isShowTitle());

		PortletURL redirect = portal.getControlPanelPortletURL(
			httpServletRequest, AccountPortletKeys.ACCOUNT_USERS_ADMIN,
			PortletRequest.RENDER_PHASE);

		redirect.setParameter(
			"mvcPath", "/account_users_admin/edit_account_user.jsp");
		redirect.setParameter(
			"p_u_i_d", ParamUtil.getString(httpServletRequest, "p_u_i_d"));

		DynamicServletRequest dynamicServletRequest = new DynamicServletRequest(
			httpServletRequest);

		dynamicServletRequest.appendParameter("redirect", redirect.toString());

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/edit_user_navigation.jsp");

		try {
			requestDispatcher.include(
				dynamicServletRequest, httpServletResponse);
		}
		catch (ServletException se) {
			throw new IOException(
				"Unable to render /edit_user_navigation.jsp", se);
		}
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			resourceBundle, portal.getResourceBundle(locale));
	}

	protected boolean isShowControls() {
		return true;
	}

	protected boolean isShowTitle() {
		return true;
	}

	@Reference
	protected Portal portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.users.admin.web)")
	protected ServletContext servletContext;

}