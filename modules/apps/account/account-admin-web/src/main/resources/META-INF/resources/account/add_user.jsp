<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
AccountDisplay accountDisplay = (AccountDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_DISPLAY);

PortletURL backURL = renderResponse.createRenderURL();

backURL.setParameter("mvcRenderCommandName", "/account_admin/edit_account");
backURL.setParameter("screenNavigationCategoryKey", AccountScreenNavigationEntryConstants.CATEGORY_KEY_USERS);
backURL.setParameter("backURL", String.valueOf(backURL));
backURL.setParameter("accountEntryId", String.valueOf(accountDisplay.getAccountId()));

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(String.valueOf(backURL));

renderResponse.setTitle((accountDisplay == null) ? "" : LanguageUtil.format(request, "add-new-user-to-x", accountDisplay.getName(), false));
%>

<portlet:actionURL name="/account_admin/account/add_user" var="addAccountUsersURL" />

<liferay-frontend:edit-form
	action="<%= addAccountUsersURL %>"
>
	<liferay-frontend:edit-form-body>
		<h2 class="sheet-title">
			<%= LanguageUtil.get(request, "information") %>
		</h2>

		<div class="sheet-section">
			<h3 class="sheet-subtitle">
				<%= LanguageUtil.get(request, "user-display-data") %>
			</h3>

			<aui:row>
				<aui:col width="<%= 50 %>">
					<aui:field-wrapper cssClass="form-group lfr-input-text-container">
						<aui:input label="screen-name" name="name" required="<%= true %>" type="text" value="<%= null %>" />
					</aui:field-wrapper>

					<aui:field-wrapper cssClass="form-group lfr-input-text-container">
						<aui:input label="email" name="email" required="<%= true %>" type="text" value="<%= null %>" />
					</aui:field-wrapper>

					<aui:field-wrapper cssClass="form-group lfr-input-text-container">
						<aui:input label="first-name" name="firstName" required="<%= true %>" type="text" value="<%= null %>" />
					</aui:field-wrapper>
				</aui:col>

				<aui:col width="<%= 40 %>">
					<div class="text-center">
						<liferay-ui:logo-selector
							currentLogoURL='<%= themeDisplay.getPathImage() + "//user_portrait?img_id=0" %>'
							defaultLogo="<%= true %>"
							defaultLogoURL='<%= themeDisplay.getPathImage() + "//user_portrait?img_id=0" %>'
							tempImageFileName="0"
						/>
					</div>

					<aui:field-wrapper cssClass="form-group lfr-input-text-container">
						<aui:input label="last-name" name="lastName" required="<%= true %>" type="text" value="<%= null %>" />
					</aui:field-wrapper>
				</aui:col>
			</aui:row>
		</div>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />

		<aui:button href="<%= String.valueOf(backURL) %>" type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<