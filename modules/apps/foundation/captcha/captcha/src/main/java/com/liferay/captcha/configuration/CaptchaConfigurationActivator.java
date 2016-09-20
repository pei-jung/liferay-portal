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

package com.liferay.captcha.configuration;

import com.liferay.portal.captcha.recaptcha.ReCaptchaImpl;
import com.liferay.portal.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.captcha.Captcha;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Pei-Jung Lan
 */
@Component(
	configurationPid = "com.liferay.captcha.configuration.CaptchaConfiguration",
	immediate = true, service = CaptchaConfigurationActivator.class
)
public class CaptchaConfigurationActivator {

	public CaptchaConfiguration getCaptchaConfiguration() {
		return _captchaConfiguration;
	}

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_captchaConfiguration = ConfigurableUtil.createConfigurable(
			CaptchaConfiguration.class, properties);

		String captchaEngine = _captchaConfiguration.captchaEngine();

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		Captcha captcha = null;

		if (captchaEngine.equals(SimpleCaptchaImpl.class.getName())) {
			captcha = new SimpleCaptchaImpl();
		}
		else {
			captcha = new ReCaptchaImpl();
		}

		_serviceRegistration = bundleContext.registerService(
			Captcha.class, captcha, null);
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		_captchaConfiguration = null;
	}

	private volatile CaptchaConfiguration _captchaConfiguration;
	private ServiceRegistration<Captcha> _serviceRegistration;

}