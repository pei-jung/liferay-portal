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

package com.liferay.portal.configuration.settings.internal.scoped.configuration;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;

/**
 * @author Drew Brokke
 */
public class DrewConfigurationBeanManagedService
	implements ManagedServiceFactory {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void updated(
			String pid, Dictionary<String, ?> properties)
		throws ConfigurationException {

	}

	@Override
	public void deleted(String pid) {

	}
}
