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

import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.display.UADHierarchyDeclaration;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class DummyUADHierarchyDeclaration implements UADHierarchyDeclaration {

	public DummyUADHierarchyDeclaration(
		DummyUADDisplay<DummyEntry> dummyEntryUADDisplay,
		DummyUADDisplay<DummyContainer> dummyContainerUADDisplay) {

		_dummyEntryUADDisplay = dummyEntryUADDisplay;
		_dummyContainerUADDisplay = dummyContainerUADDisplay;
	}

	@Override
	public boolean contains(
		Class parentContainerClass, Class childContainerClass) {

		if (parentContainerClass.equals(DummyContainer.class)) {
			return true;
		}

		return false;
	}

	@Override
	public Serializable getContainerId(Object object) {
		UserAssociatedEntity userAssociatedEntity =
			(UserAssociatedEntity)object;

		return userAssociatedEntity.getContainerId();
	}

	@Override
	public Class getContainerType(Class clazz) {
		return DummyContainer.class;
	}

	@Override
	public String[] getExtraColumnNames() {
		return new String[] {"uuid"};
	}

	@Override
	public Serializable getTopLevelContainerId(
		Class containerType, Serializable containerId, Object object) {

		long containerIdLong = (long)containerId;
		long objectParentId = (long)getContainerId(object);

		if (containerIdLong == objectParentId) {
			if (object instanceof DummyContainer) {
				return ((UserAssociatedEntity)object).getId();
			}

			return null;
		}

		List<Long> tree = _buildTree((UserAssociatedEntity)object);

		if (containerIdLong == 0) {
			return tree.get(tree.size() - 1);
		}

		if (tree.contains(containerIdLong)) {
			return tree.get(tree.indexOf(containerIdLong) - 1);
		}

		return null;
	}

	@Override
	public UADDisplay[] getUADDisplays() {
		return new UADDisplay[] {
			_dummyContainerUADDisplay, _dummyEntryUADDisplay
		};
	}

	@Override
	public boolean isContainerType(Class clazz) {
		if (clazz.equals(DummyEntry.class)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isDescendant(
		Class containerType, Serializable containerId, Object object) {

		long containerIdLong = (long)containerId;
		long objectParentId = (long)getContainerId(object);

		if ((containerIdLong == 0) || (containerIdLong == objectParentId)) {
			return true;
		}

		List<Long> tree = _buildTree((UserAssociatedEntity)object);

		if (tree.contains(containerIdLong)) {
			return true;
		}

		return false;
	}

	private List<Long> _buildTree(UserAssociatedEntity userAssociatedEntity) {
		List<Long> tree = new ArrayList<>();

		long containerId = userAssociatedEntity.getContainerId();

		if (containerId == 0) {
			return tree;
		}

		while (containerId != 0) {
			tree.add(containerId);

			DummyContainer dummyContainer = _dummyContainerUADDisplay.get(
				containerId);

			containerId = dummyContainer.getContainerId();
		}

		return tree;
	}

	private final DummyUADDisplay<DummyContainer> _dummyContainerUADDisplay;
	private final DummyUADDisplay<DummyEntry> _dummyEntryUADDisplay;

}