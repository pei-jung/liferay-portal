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

<liferay-util:buffer
	var="userAvatar"
>
	<c:if test="<%= themeDisplay.isImpersonated() %>">
		<aui:icon image="asterisk" markupView="lexicon" />
	</c:if>

	<span class="user-avatar-image">
		<liferay-ui:user-portrait
			user="<%= user %>"
		/>
	</span>
	<span class="user-full-name">
		<%= HtmlUtil.escape(user.getFullName()) %>
	</span>
</liferay-util:buffer>

<liferay-product-navigation:user-personal-menu
	componentId='<%= renderResponse.getNamespace() + "userPersonalMenu" %>'
	label="<%= userAvatar %>"
/>

<aui:script>
	Liferay.componentReady('<%= renderResponse.getNamespace() + "userPersonalMenu" %>').then(
		function(userPersonalMenu) {
			userPersonalMenu.expanded = true;
		}
	);
</aui:script>