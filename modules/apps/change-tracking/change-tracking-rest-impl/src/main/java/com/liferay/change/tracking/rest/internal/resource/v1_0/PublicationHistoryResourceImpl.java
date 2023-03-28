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

package com.liferay.change.tracking.rest.internal.resource.v1_0;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.rest.dto.v1_0.PublicationHistory;
import com.liferay.change.tracking.rest.dto.v1_0.Status;
import com.liferay.change.tracking.rest.internal.odata.entity.v1_0.PublicationHistoryEntityModel;
import com.liferay.change.tracking.rest.resource.v1_0.PublicationHistoryResource;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.change.tracking.service.CTProcessService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.util.Collections;

import javax.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author David Truong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/publication-history.properties",
	scope = ServiceScope.PROTOTYPE, service = PublicationHistoryResource.class
)
public class PublicationHistoryResourceImpl
	extends BasePublicationHistoryResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public PublicationHistory getPublicationHistory(Long ctProcessId)
		throws Exception {

		CTProcess ctProcess = _ctProcessLocalService.fetchCTProcess(
			ctProcessId);

		return _toPublicationHistory(ctProcess);
	}

	@Override
	public Page<PublicationHistory> getPublicationHistoryPage(
			Integer statuses, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CTProcess.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute("statuses", statuses);
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}
			},
			sorts,
			document -> _toPublicationHistory(
				_ctProcessLocalService.getCTProcess(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private PublicationHistory _toPublicationHistory(CTProcess ctProcess)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchBackgroundTask(
				ctProcess.getBackgroundTaskId());

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctProcess.getCtCollectionId());

		return new PublicationHistory() {
			{
				actions = HashMapBuilder.put(
					"get",
					() -> {
						if (backgroundTask.getStatus() !=
								BackgroundTaskConstants.STATUS_SUCCESSFUL) {

							return null;
						}

						return addAction(
							ActionKeys.VIEW, "getPublicationHistory",
							CTCollection.class.getName(),
							ctProcess.getCtCollectionId());
					}
				).put(
					"revert",
					() -> {
						if ((backgroundTask.getStatus() !=
								BackgroundTaskConstants.STATUS_SUCCESSFUL) ||
							(ctCollection == null) ||
							!_ctSchemaVersionLocalService.
								isLatestCTSchemaVersion(
									ctCollection.getSchemaVersionId()) ||
							!_portletResourcePermission.contains(
								PermissionThreadLocal.getPermissionChecker(),
								null, CTActionKeys.ADD_PUBLICATION)) {

							return null;
						}

						return addAction(
							ActionKeys.VIEW, "postPublicationHistoryRevert",
							CTCollection.class.getName(),
							ctCollection.getCtCollectionId());
					}
				).build();
				dateCreated = ctProcess.getCreateDate();
				description = ctCollection.getDescription();
				id = ctProcess.getCtProcessId();
				name = ctCollection.getName();
				status = _toPublishedStatus(backgroundTask.getStatus());

				User user = _userLocalService.fetchUser(ctProcess.getUserId());

				if (user != null) {
					publisherName = user.getFullName();
				}
			}
		};
	}

	private Status _toPublishedStatus(int status) throws Exception {
		String statusLabel;

		if (status == BackgroundTaskConstants.STATUS_SUCCESSFUL) {
			statusLabel = "published";
		}
		else if (status == BackgroundTaskConstants.STATUS_IN_PROGRESS) {
			statusLabel = "in-progress";
		}
		else if (status == BackgroundTaskConstants.STATUS_FAILED) {
			statusLabel = "failed";
		}
		else {
			statusLabel = StringPool.BLANK;
		}

		return new Status() {
			{
				code = status;
				label = statusLabel;
				label_i18n = _language.get(
					contextCompany.getLocale(), statusLabel);
			}
		};
	}

	private static final EntityModel _entityModel =
		new PublicationHistoryEntityModel();

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTProcessLocalService _ctProcessLocalService;

	@Reference
	private CTProcessService _ctProcessService;

	@Reference
	private CTSchemaVersionLocalService _ctSchemaVersionLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(target = "(resource.name=" + CTConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private UserLocalService _userLocalService;

}