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

<%@ include file="/publications/init.jsp" %>

<clay:navigation-bar
	navigationItems="<%= publicationsDisplayContext.getViewNavigationItems() %>"
/>

<clay:container-fluid>
	<frontend-data-set:headless-display
		apiURL="<%= publicationsDisplayContext.getAPIURL() %>"
		creationMenu="<%= publicationsDisplayContext.getCreationMenu() %>"
		fdsActionDropdownItems="<%= publicationsDisplayContext.getFDSActionDropdownItems() %>"
		id="<%= PublicationsFDSNames.PUBLICATIONS_ONGOING %>"
		style="stacked"
	/>
</clay:container-fluid>

<%
CTLocalizedException ctLocalizedException = null;

if (MultiSessionErrors.contains(liferayPortletRequest, CTLocalizedException.class.getName())) {
	ctLocalizedException = (CTLocalizedException)MultiSessionErrors.get(liferayPortletRequest, CTLocalizedException.class.getName());
}
%>

<c:if test="<%= ctLocalizedException != null %>">
	<aui:script>
		Liferay.Util.openToast({
			autoClose: 10000,
			message:
				'<%= HtmlUtil.escapeJS(ctLocalizedException.formatMessage(resourceBundle)) %>',
			title: '<liferay-ui:message key="error" />:',
			type: 'danger',
		});
	</aui:script>
</c:if>