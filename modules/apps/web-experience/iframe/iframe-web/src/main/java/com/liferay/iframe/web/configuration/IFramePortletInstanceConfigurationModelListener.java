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

package com.liferay.iframe.web.configuration;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(
	immediate = true,
	property = {
		"model.class.name=com.liferay.iframe.web.configuration.IFramePortletInstanceConfiguration"
	},
	service = ConfigurationModelListener.class
)
public class IFramePortletInstanceConfigurationModelListener
	implements ConfigurationModelListener {

	public void doAfter(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		System.out.println("Configuration successfully saved!");
	}

	public void doBefore(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		String authType = (String)properties.get("authType");

		if (authType.equals("none")) {
			throw new ConfigurationModelListenerException(
				IFramePortletInstanceConfiguration.class,
				IFramePortletInstanceConfigurationModelListener.class,
				properties, "authType should never be \"none\"");
		}
	}

}