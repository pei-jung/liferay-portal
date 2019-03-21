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

<%@ include file="/user_personal_menu/init.jsp" %>

<%
boolean expanded = (boolean)request.getAttribute("liferay-product-navigation:user-personal-menu:expanded");
String label = (String)request.getAttribute("liferay-product-navigation:user-personal-menu:label");

JSONArray dropdownItems =
	UserPersonalMenuDropdownItemsProviderUtil.getDropdownItemsJSONArray(request);
%>

<div id="user_personal_menu_dropdown">
	<div id="user_personal_menu_dropdown_toggle">
		<%= label %>
	</div>

	<div id="clay_dropdown_portal"></div>
</div>

<aui:script require="clay-dropdown/src/ClayDropdown as ClayDropdown,metal-dom/src/dom as dom">
	var toggle = document.getElementById('user_personal_menu_dropdown_toggle');

	if (toggle) {
		dom.once(
			toggle,
			'click',
			function(event) {
				window.dropdown = new ClayDropdown.default(
					{
						element: '#user_personal_menu_dropdown_toggle',
						events: {
							'willAttach': function(event) {
								if (<%= expanded %>) {
									this.expanded = true;
								}
							}
						},
						items: <%= dropdownItems.toJSONString() %>,
						label: toggle.innerHTML,
						showToggleIcon: false,
						spritemap: '<%= themeDisplay.getPathThemeImages().concat("/clay/icons.svg") %>'
					},
					'#user_personal_menu_dropdown'
				);
			}
		);
	}
</aui:script>