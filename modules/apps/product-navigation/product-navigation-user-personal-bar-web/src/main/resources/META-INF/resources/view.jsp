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
PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("mvcPath", "/user_personal_menu.jsp");
portletURL.setWindowState(LiferayWindowState.EXCLUSIVE);
%>

<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() %>">
		<span class="user-avatar-link">
			<div data-url="<%= portletURL.toString() %>" id="<portlet:namespace />userAvatar">
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
			</div>

			<%
			int notificationsCount = GetterUtil.getInteger(request.getAttribute(ProductNavigationUserPersonalBarWebKeys.NOTIFICATIONS_COUNT));
			%>

			<c:if test="<%= notificationsCount > 0 %>">

				<%
				PortletURL notificationsURL = PortletProviderUtil.getPortletURL(request, UserNotificationEvent.class.getName(), PortletProvider.Action.VIEW);
				%>

				<aui:a href="<%= (notificationsURL != null) ? notificationsURL.toString() : null %>">
					<span class="badge badge-danger panel-notifications-count">
						<span class="badge-item badge-item-expand"><%= notificationsCount %></span>
					</span>
				</aui:a>
			</c:if>
		</span>
	</c:when>
	<c:otherwise>

		<%
		Map<String, Object> anchorData = new HashMap<String, Object>();

		anchorData.put("redirect", String.valueOf(PortalUtil.isLoginRedirectRequired(request)));
		%>

		<span class="sign-in text-default" role="presentation">
			<aui:icon cssClass="sign-in text-default" data="<%= anchorData %>" image="user" label="sign-in" markupView="lexicon" url="<%= themeDisplay.getURLSignIn() %>" />
		</span>
	</c:otherwise>
</c:choose>

<aui:script>
	var userAvatar = $('#<portlet:namespace />userAvatar');

	userAvatar.one(
		'click',
		function(event) {
			$.ajax({
				url: userAvatar.data('url'),
				dataType: 'html',
				type: 'get',
				success: function(data) {
					userAvatar.html(data);
				}
			})
		}
	);
</aui:script>