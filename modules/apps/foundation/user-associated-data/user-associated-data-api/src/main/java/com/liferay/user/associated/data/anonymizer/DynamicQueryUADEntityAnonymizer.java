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

package com.liferay.user.associated.data.anonymizer;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.user.associated.data.entity.UADEntity;
import com.liferay.user.associated.data.util.UADDynamicQueryHelper;

import org.osgi.service.component.annotations.Reference;

/**
 * @author William Newbury
 */
public abstract class DynamicQueryUADEntityAnonymizer<T>
	implements UADEntityAnonymizer<T> {

	@Override
	public void autoAnonymize(UADEntity<T> uadEntity) throws PortalException {
		doAutoAnonymize(uadEntity.getEntity(), uadEntity.getUserId());
	}

	@Override
	public void autoAnonymizeAll(long userId) throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			_getActionableDynamicQuery(userId);

		actionableDynamicQuery.setPerformActionMethod(
			(T entity) -> doAutoAnonymize(entity, userId));

		actionableDynamicQuery.performActions();
	}

	@Override
	public void delete(UADEntity<T> uadEntity) throws PortalException {
		doDelete(uadEntity.getEntity());
	}

	@Override
	public void deleteAll(long userId) throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			_getActionableDynamicQuery(userId);

		actionableDynamicQuery.setPerformActionMethod(
			(T entity) -> doDelete(entity));

		actionableDynamicQuery.performActions();
	}

	protected abstract void doAutoAnonymize(T entity, long userId)
		throws PortalException;

	protected abstract void doDelete(T entity) throws PortalException;

	protected abstract ActionableDynamicQuery doGetActionableDynamicQuery();

	@Reference
	protected UADDynamicQueryHelper uadDynamicQueryHelper;

	private ActionableDynamicQuery _getActionableDynamicQuery(long userId) {
		return uadDynamicQueryHelper.addActionableDynamicQueryCriteria(
			doGetActionableDynamicQuery(), getUserIdFieldNames(), userId);
	}

}