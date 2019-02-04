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

package com.liferay.user.associated.data.display;

import java.io.Serializable;

/**
 * @author Drew Brokke
 */
public interface UADHierarchyDeclaration {

	public boolean contains(
		Class parentContainerClass, Class childContainerClass);

	public Serializable getContainerId(Object object);

	public Class getContainerType(Class clazz);

	public default String[] getExtraColumnNames() {
		return new String[0];
	}

	public Serializable getTopLevelContainerId(
		Class containerType, Serializable containerId, Object object);

	public UADDisplay[] getUADDisplays();

	public boolean isContainerType(Class clazz);

	public boolean isDescendant(
		Class containerType, Serializable containerId, Object object);

}