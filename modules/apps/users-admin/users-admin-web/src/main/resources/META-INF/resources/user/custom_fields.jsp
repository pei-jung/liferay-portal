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
User selUser = (User)request.getAttribute(UsersAdminWebKeys.SELECTED_USER);

PortletURL portletURL = PortletProviderUtil.getPortletURL(request, ExpandoColumn.class.getName(), PortletProvider.Action.MANAGE);

portletURL.setParameter("modelResource", User.class.getName());
portletURL.setParameter("redirect", themeDisplay.getURLCurrent());
%>

<div class="autofit-padded-no-gutters autofit-row">
	<span class="autofit-col autofit-col-expand">
		<h4 class="sheet-tertiary-title">
			<liferay-ui:message key="custom-fields" />
		</h4>
	</span>
	<span class="autofit-col">
		<liferay-ui:icon
			cssClass="modify-link"
			label="<%= true %>"
			linkCssClass="btn btn-secondary btn-sm"
			message="manage"
			method="get"
			url="<%= portletURL.toString() %>"
		/>
	</span>
</div>

<liferay-expando:custom-attribute-list
	className="com.liferay.portal.kernel.model.User"
	classPK="<%= (selUser != null) ? selUser.getUserId() : 0 %>"
	editable="<%= true %>"
	label="<%= true %>"
/>