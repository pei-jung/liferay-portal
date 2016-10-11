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

package com.liferay.portal.configuration.persistence.listener;

import java.io.IOException;

import java.util.Dictionary;

/**
 * @author Drew Brokke
 */
public class ConfigurationModelListenerException extends IOException {

	public ConfigurationModelListenerException(
		Class configurationClass, Class listenerClass, Dictionary properties,
		String message) {

		super(
			String.format(
				"%s encountered an error while saving the configuration %s: %s",
				listenerClass.getSimpleName(),
				configurationClass.getSimpleName(), message));

		_configurationClass = configurationClass;
		_listenerClass = listenerClass;
		_properties = properties;
	}

	private final Class _configurationClass;
	private final Class _listenerClass;
	private final Dictionary _properties;

}