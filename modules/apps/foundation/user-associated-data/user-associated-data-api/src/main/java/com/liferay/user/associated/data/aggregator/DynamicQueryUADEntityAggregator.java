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

package com.liferay.user.associated.data.aggregator;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.user.associated.data.entity.BaseUADEntity;
import com.liferay.user.associated.data.entity.UADEntity;
import com.liferay.user.associated.data.util.UADDynamicQueryHelper;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
public abstract class DynamicQueryUADEntityAggregator<T>
	implements UADEntityAggregator<T> {

	@Override
	public long count(long userId) {
		return doCount(_getDynamicQuery(userId));
	}

	@Override
	public List<UADEntity<T>> getUADEntities(long userId) {
		return getUADEntities(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<UADEntity<T>> getUADEntities(long userId, int start, int end) {
		List<T> entities = doGetEntities(_getDynamicQuery(userId), start, end);

		List<UADEntity<T>> uadEntities = new ArrayList<>();

		for (T entity : entities) {
			uadEntities.add(_constructUADEntity(userId, entity));
		}

		return uadEntities;
	}

	@Override
	public UADEntity<T> getUADEntity(String uadEntityId) throws Exception {
		return _constructUADEntity(
			_getUserId(uadEntityId), doGetEntity(_getEntryId(uadEntityId)));
	}

	protected abstract long doCount(DynamicQuery dynamicQuery);

	protected abstract DynamicQuery doGetDynamicQuery();

	protected abstract List<T> doGetEntities(
		DynamicQuery dynamicQuery, int start, int end);

	protected abstract T doGetEntity(long entityId) throws PortalException;

	protected abstract long doGetEntityId(T t);

	protected abstract String doGetRegistryKey();

	protected abstract long doGetUserId(T t);

	@Reference
	protected UADDynamicQueryHelper uadDynamicQueryHelper;

	private UADEntity<T> _constructUADEntity(long userId, T entity) {
		return new BaseUADEntity<>(
			entity, _getUADEntityId(userId, entity), doGetRegistryKey(),
			userId);
	}

	private DynamicQuery _getDynamicQuery(long userId) {
		return uadDynamicQueryHelper.addDynamicQueryCriteria(
			doGetDynamicQuery(), getUserIdFieldNames(), userId);
	}

	private long _getEntryId(String uadEntityId) {
		return _getUADEntityIdPart(uadEntityId, 0);
	}

	private String _getUADEntityId(long userId, T t) {
		return StringBundler.concat(
			String.valueOf(doGetEntityId(t)), StringPool.POUND,
			String.valueOf(userId));
	}

	private long _getUADEntityIdPart(String uadEntityId, int part) {
		String[] uadEntityIdParts = uadEntityId.split(StringPool.POUND);

		return Long.parseLong(uadEntityIdParts[Math.min(Math.max(part, 0), 1)]);
	}

	private long _getUserId(String uadEntityId) {
		return _getUADEntityIdPart(uadEntityId, 1);
	}

}