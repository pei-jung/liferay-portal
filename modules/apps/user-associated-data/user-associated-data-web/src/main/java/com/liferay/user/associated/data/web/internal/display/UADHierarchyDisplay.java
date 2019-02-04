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

package com.liferay.user.associated.data.web.internal.display;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.display.UADHierarchyDeclaration;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Drew Brokke
 */
public class UADHierarchyDisplay {

	public UADHierarchyDisplay(
		UADHierarchyDeclaration uadHierarchyDeclaration) {

		_uadHierarchyDeclaration = uadHierarchyDeclaration;
	}

	public long countAll(long userId) {
		long count = 0;

		for (UADDisplay uadDisplay :
				_uadHierarchyDeclaration.getUADDisplays()) {

			count += uadDisplay.count(userId);
		}

		return count;
	}

	public Map<String, Object> getFieldValues(
		Object object, String[] fieldNames) {

		Map<String, Object> fieldValues = new HashMap<>();

		Class<?> clazz = object.getClass();

		if (object instanceof ContainerDisplay) {
			ContainerDisplay containerDisplay = (ContainerDisplay)object;

			Object containerObject = containerDisplay.getContainer();

			clazz = containerObject.getClass();
		}

		UADDisplay uadDisplay = getUADDisplay(clazz);

		if (uadDisplay != null) {
			String[] allFieldNames = ArrayUtil.append(
				fieldNames, _uadHierarchyDeclaration.getExtraColumnNames());

			if (object instanceof ContainerDisplay) {
				ContainerDisplay containerDisplay = (ContainerDisplay)object;

				Object containerObject = containerDisplay.getContainer();

				fieldValues = uadDisplay.getFieldValues(
					containerObject, allFieldNames);

				fieldValues.put("count", containerDisplay.getCount());
			}
			else {
				fieldValues = uadDisplay.getFieldValues(object, allFieldNames);

				fieldValues.put("count", "--");
			}
		}

		return fieldValues;
	}

	public Class[] getTypeClasses() {
		UADDisplay[] uadDisplays = _uadHierarchyDeclaration.getUADDisplays();

		Class<?>[] typeClasses = new Class<?>[uadDisplays.length];

		for (int i = 0; i < uadDisplays.length; i++) {
			typeClasses[i] = uadDisplays[i].getTypeClass();
		}

		return typeClasses;
	}

	public UADDisplay getUADDisplay(Class clazz) {
		for (UADDisplay uadDisplay :
				_uadHierarchyDeclaration.getUADDisplays()) {

			Class<?> typeClass = uadDisplay.getTypeClass();

			if (typeClass.isAssignableFrom(clazz)) {
				return uadDisplay;
			}
		}

		return null;
	}

	public List search(
		long userId, long[] groupIds, Serializable parentContainerId,
		String keywords, String orderByField, String orderByType, int start,
		int end, Class containerType) {

		List results = new ArrayList<>();

		List descendantItems = searchDescendants(
			userId, groupIds, keywords, orderByField, orderByType,
			containerType, parentContainerId);

		for (Class clazz : getTypeClasses()) {
			if (_uadHierarchyDeclaration.isContainerType(clazz)) {
				results.addAll(
					getContainerResults(
						clazz, parentContainerId, descendantItems));
			}
			else {
				results.addAll(
					ListUtil.filter(
						descendantItems,
						item -> {
							if ((item != null) &&
								clazz.isAssignableFrom(item.getClass()) &&
								parentContainerId.equals(
									_uadHierarchyDeclaration.getContainerId(
										item))) {

								return true;
							}

							return false;
						}));
			}
		}

		return ListUtil.subList(results, start, end);
	}

	protected List getContainerResults(
		Class containerClass, Serializable parentContainerId,
		List<Object> items) {

		Map<Serializable, Integer> topLevelCategories = new HashMap<>();

		for (Object item : items) {
			Serializable topLevelContainerId =
				_uadHierarchyDeclaration.getTopLevelContainerId(
					containerClass, parentContainerId, item);

			if (topLevelContainerId == null) {
				continue;
			}

			if (containerClass.isAssignableFrom(item.getClass()) &&
				(_uadHierarchyDeclaration.getContainerId(item) ==
					parentContainerId)) {

				topLevelCategories.putIfAbsent(topLevelContainerId, 0);
			}
			else {
				incrementCount(topLevelCategories, topLevelContainerId);
			}
		}

		Set<Map.Entry<Serializable, Integer>> entrySet =
			topLevelCategories.entrySet();

		Stream<Map.Entry<Serializable, Integer>> topLevelContainersEntryStream =
			entrySet.stream();

		return topLevelContainersEntryStream.map(
			entry -> {
				ContainerDisplay containerDisplay = null;

				try {
					UADDisplay uadDisplay = getUADDisplay(containerClass);

					if (uadDisplay != null) {
						containerDisplay = new ContainerDisplay(
							uadDisplay.get(entry.getKey()), entry.getValue());
					}
				}
				catch (Exception e) {
					_log.error(
						"The primary key returned from " +
							"UADHierarchyDeclaration::getTopLevelContainerId " +
								"should always return a valid primary key",
						e);
				}

				return containerDisplay;
			}
		).filter(
			Validator::isNotNull
		).collect(
			Collectors.toList()
		);
	}

	protected void incrementCount(
		Map<Serializable, Integer> map, Serializable key) {

		int currentCount = 0;

		if (map.containsKey(key)) {
			currentCount = map.get(key);
		}

		map.put(key, currentCount + 1);
	}

	protected List searchDescendants(
		long userId, long[] groupIds, String keywords, String orderByField,
		String orderByType, Class containerType,
		Serializable parentContainerId) {

		List results = new ArrayList<>();

		for (UADDisplay uadDisplay :
				_uadHierarchyDeclaration.getUADDisplays()) {

			if (!_uadHierarchyDeclaration.contains(
					containerType, uadDisplay.getTypeClass())) {

				continue;
			}

			results.addAll(
				uadDisplay.search(
					userId, groupIds, keywords, orderByField, orderByType,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}

		return ListUtil.filter(
			results,
			item -> _uadHierarchyDeclaration.isDescendant(
				containerType, parentContainerId, item));
	}

	private static Log _log = LogFactoryUtil.getLog(UADHierarchyDisplay.class);

	private final UADHierarchyDeclaration _uadHierarchyDeclaration;

}