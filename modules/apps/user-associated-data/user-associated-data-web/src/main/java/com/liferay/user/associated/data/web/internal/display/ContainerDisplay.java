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

/**
 * @author Drew Brokke
 */
public class ContainerDisplay {

	public ContainerDisplay(Object container) {
		_container = container;
		_count = 0;
	}

	public ContainerDisplay(Object container, long count) {
		_container = container;
		_count = count;
	}

	public Object getContainer() {
		return _container;
	}

	public long getCount() {
		return _count;
	}

	private final Object _container;
	private final long _count;

}