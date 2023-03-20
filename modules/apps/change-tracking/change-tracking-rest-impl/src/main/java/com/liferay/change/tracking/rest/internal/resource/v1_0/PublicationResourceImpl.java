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
import com.liferay.change.tracking.mapping.CTMappingTableInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTCollectionTable;
import com.liferay.change.tracking.rest.dto.v1_0.Creator;
import com.liferay.change.tracking.rest.dto.v1_0.Publication;
import com.liferay.change.tracking.rest.dto.v1_0.Status;
import com.liferay.change.tracking.rest.internal.util.v1_0.PublishUtil;
import com.liferay.change.tracking.rest.resource.v1_0.PublicationResource;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.service.CTPreferencesService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author David Truong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/publication.properties",
	scope = ServiceScope.PROTOTYPE, service = PublicationResource.class
)
@CTAware
public class PublicationResourceImpl extends BasePublicationResourceImpl {

	@CTAware(onProduction = true)
	@Override
	public void deletePublication(Long ctCollectionId) throws PortalException {
		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if (ctCollection != null) {
			_ctCollectionService.deleteCTCollection(ctCollection);
		}
	}

	@Override
	public Publication getPublication(Long ctCollectionId) throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		return _toPublication(ctCollection);
	}

	@Override
	public Page<Publication> getPublicationsPage(
			Integer[] statuses, String search, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		if (statuses == null) {
			statuses = new Integer[] {
				WorkflowConstants.STATUS_DRAFT, WorkflowConstants.STATUS_EXPIRED
			};
		}

		return Page.of(
			transform(
				_ctCollectionService.getCTCollections(
					contextCompany.getCompanyId(), ArrayUtil.toArray(statuses),
					search, pagination.getStartPosition(),
					pagination.getEndPosition(), _toOrderByComparator(sorts)),
				this::_toPublication),
			pagination,
			_ctCollectionService.getCTCollectionsCount(
				contextCompany.getCompanyId(), ArrayUtil.toArray(statuses),
				search));
	}

	@CTAware(onProduction = true)
	@Override
	public void postPublicationCheckout(Long ctCollectionId)
		throws PortalException {

		_ctPreferencesService.checkoutCTCollection(
			contextCompany.getCompanyId(), contextUser.getUserId(),
			ctCollectionId);
	}

	@CTAware(onProduction = true)
	@Override
	public void postPublicationPublish(Long ctCollectionId, Date publishDate)
		throws PortalException {

		if (publishDate == null) {
			_ctCollectionService.publishCTCollection(
				contextUser.getUserId(), ctCollectionId);

			return;
		}

		Date currentDate = new Date(System.currentTimeMillis());

		if (!publishDate.after(currentDate)) {
			throw new PortalException("the-publish-time-must-be-in-the-future");
		}

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_SCHEDULED) {
			PublishUtil.unschedulePublish(
				ctCollectionId, _ctCollectionLocalService,
				_schedulerEngineHelper);
		}

		PublishUtil.schedulePublish(
			ctCollectionId, _ctCollectionLocalService,
			_ctPreferencesLocalService, _schedulerEngineHelper, publishDate,
			_triggerFactory, contextUser.getUserId());
	}

	private Date _getDateScheduled(CTCollection ctCollection) throws Exception {
		if (ctCollection.getStatus() != WorkflowConstants.STATUS_SCHEDULED) {
			return null;
		}

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				String.valueOf(ctCollection.getCtCollectionId()),
				"liferay/ct_collection_scheduled_publish",
				StorageType.PERSISTED);

		if (schedulerResponse == null) {
			return null;
		}

		return _schedulerEngineHelper.getStartTime(schedulerResponse);
	}

	private boolean _isPublishEnabled(long ctCollectionId) {
		int count = _ctEntryLocalService.getCTCollectionCTEntriesCount(
			ctCollectionId);

		if (count > 0) {
			return true;
		}

		List<CTMappingTableInfo> mappingTableInfos =
			_ctCollectionLocalService.getCTMappingTableInfos(ctCollectionId);

		if (!mappingTableInfos.isEmpty()) {
			return true;
		}

		return false;
	}

	private Creator _toCreator(User user) {
		return new Creator() {
			{
				additionalName = user.getMiddleName();
				contentType = "UserAccount";
				familyName = user.getLastName();
				givenName = user.getFirstName();
				id = user.getUserId();
				name = user.getFullName();

				setImage(
					() -> {
						if (user.getPortraitId() == 0) {
							return null;
						}

						ThemeDisplay themeDisplay = new ThemeDisplay() {
							{
								setPathImage(_portal.getPathImage());
							}
						};

						return user.getPortraitURL(themeDisplay);
					});
				setProfileURL(
					() -> {
						Group group = user.getGroup();

						ThemeDisplay themeDisplay = new ThemeDisplay() {
							{
								setPortalURL(StringPool.BLANK);
								setSiteGroupId(group.getGroupId());
							}
						};

						return group.getDisplayURL(themeDisplay);
					});
			}
		};
	}

	private OrderByComparator<CTCollection> _toOrderByComparator(Sort[] sorts) {
		if (ArrayUtil.isEmpty(sorts)) {
			return null;
		}

		List<Object> objects = new ArrayList<>();

		for (Sort sort : sorts) {
			String fieldName = sort.getFieldName();

			if (fieldName.equals("dateCreated")) {
				objects.add("createDate");
			}
			else if (fieldName.equals("dateModified")) {
				objects.add("modifiedDate");
			}
			else {
				objects.add(fieldName);
			}

			objects.add(!sort.isReverse());
		}

		return OrderByComparatorFactoryUtil.create(
			CTCollectionTable.INSTANCE.getTableName(),
			objects.toArray(new Object[0]));
	}

	private Publication _toPublication(CTCollection ctCollection)
		throws Exception {

		return new Publication() {
			{
				actions = HashMapBuilder.put(
					"checkout",
					() -> {
						if ((ctCollection.getStatus() !=
								WorkflowConstants.STATUS_DRAFT) ||
							(ctCollection.getCtCollectionId() ==
								CTCollectionThreadLocal.getCTCollectionId())) {

							return null;
						}

						return addAction(
							ActionKeys.UPDATE, "postPublicationCheckout",
							CTCollection.class.getName(),
							ctCollection.getCtCollectionId());
					}
				).put(
					"delete",
					() -> addAction(
						ActionKeys.DELETE, "deletePublication",
						CTCollection.class.getName(),
						ctCollection.getCtCollectionId())
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, "getPublication",
						CTCollection.class.getName(),
						ctCollection.getCtCollectionId())
				).put(
					"permissions",
					() -> {
						if (ctCollection.getStatus() !=
								WorkflowConstants.STATUS_DRAFT) {

							return null;
						}

						return addAction(
							ActionKeys.PERMISSIONS, "patchPublication",
							CTCollection.class.getName(),
							ctCollection.getCtCollectionId());
					}
				).put(
					"publish",
					() -> {
						if (!_isPublishEnabled(
								ctCollection.getCtCollectionId())) {

							return null;
						}

						return addAction(
							CTActionKeys.PUBLISH, "postPublicationPublish",
							CTCollection.class.getName(),
							ctCollection.getCtCollectionId());
					}
				).put(
					"schedule",
					() -> {
						if (!_isPublishEnabled(
								ctCollection.getCtCollectionId()) ||
							!PropsValues.SCHEDULER_ENABLED) {

							return null;
						}

						return addAction(
							CTActionKeys.PUBLISH,
							"postPublicationSchedulePublish",
							CTCollection.class.getName(),
							ctCollection.getCtCollectionId());
					}
				).put(
					"update",
					() -> addAction(
						ActionKeys.UPDATE, "putPublication",
						CTCollection.class.getName(),
						ctCollection.getCtCollectionId())
				).build();
				creator = _toCreator(
					_userLocalService.fetchUser(ctCollection.getUserId()));
				dateCreated = ctCollection.getCreateDate();
				dateModified = ctCollection.getModifiedDate();
				dateScheduled = _getDateScheduled(ctCollection);
				description = ctCollection.getDescription();
				id = ctCollection.getCtCollectionId();
				name = ctCollection.getName();
				status = _toStatus(ctCollection.getStatus());
			}
		};
	}

	private Status _toStatus(int status) throws Exception {
		String statusLabel;

		if (status == WorkflowConstants.STATUS_APPROVED) {
			statusLabel = "published";
		}
		else if (status == WorkflowConstants.STATUS_EXPIRED) {
			statusLabel = "out-of-date";
		}
		else if (status == WorkflowConstants.STATUS_DRAFT) {
			statusLabel = "in-progress";
		}
		else if (status == WorkflowConstants.STATUS_DENIED) {
			statusLabel = "failed";
		}
		else if (status == WorkflowConstants.STATUS_SCHEDULED) {
			statusLabel = "scheduled";
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

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTCollectionService _ctCollectionService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTPreferencesService _ctPreferencesService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private TriggerFactory _triggerFactory;

	@Reference
	private UserLocalService _userLocalService;

}