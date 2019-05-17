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

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.display.UADHierarchyDisplay;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Noah Sherrill
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/delete_uad_entities"
	},
	service = MVCActionCommand.class
)
public class DeleteUADEntitiesMVCActionCommand extends BaseUADMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String applicationKey = ParamUtil.getString(
			actionRequest, "applicationKey");

		UADHierarchyDisplay uadHierarchyDisplay =
			uadRegistry.getUADHierarchyDisplay(applicationKey);

		for (String entityType : getEntityTypes(actionRequest)) {
			String[] primaryKeys = getPrimaryKeys(actionRequest, entityType);

			UADAnonymizer entityUADAnonymizer = getUADAnonymizer(
				actionRequest, entityType);
			UADDisplay<?> entityUADDisplay = getUADDisplay(
				actionRequest, entityType);

			for (String primaryKey : primaryKeys) {
				deleteEntity(
					entityUADAnonymizer, entityUADDisplay, primaryKey,
					getSelectedUserId(actionRequest), uadHierarchyDisplay);
			}
		}

		doReviewableRedirect(actionRequest, actionResponse);
	}

}