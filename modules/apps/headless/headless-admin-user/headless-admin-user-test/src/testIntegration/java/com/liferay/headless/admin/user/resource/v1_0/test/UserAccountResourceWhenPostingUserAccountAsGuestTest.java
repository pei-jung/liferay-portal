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

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.problem.Problem;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class UserAccountResourceWhenPostingUserAccountAsGuestTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_sapEntry = _sapEntryLocalService.addSAPEntry(
			TestPropsValues.getUserId(),
			"com.liferay.headless.admin.user.internal.resource.v1_0." +
				"UserAccountResourceImpl#postUserAccount",
			true, true, "Guest",
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "Guest"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_captcha = CaptchaUtil.getCaptcha();

		UserAccountResource.Builder builder = UserAccountResource.builder();

		_userAccountResource = builder.locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		_sapEntryLocalService.deleteSAPEntry(_sapEntry);

		CaptchaUtil.setCaptcha(_captcha);

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testShouldAddUserWithValidCaptcha() throws Exception {
		Captcha mockCaptcha = new MockCaptcha(
			() -> {
			});

		_registerCaptcha(mockCaptcha);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"captchaEngine", MockCaptcha.class.getName()
					).put(
						"createAccountCaptchaEnabled", true
					).build())) {

			UserAccount userAccount = _randomUserAccount();

			Assert.assertNull(
				_userLocalService.fetchUserByEmailAddress(
					TestPropsValues.getCompanyId(),
					userAccount.getEmailAddress()));

			_userAccountResource.postUserAccount(userAccount);

			Assert.assertNotNull(
				_userLocalService.fetchUserByEmailAddress(
					TestPropsValues.getCompanyId(),
					userAccount.getEmailAddress()));
		}
	}

	@Test
	public void testShouldNotUseCaptcha() throws Exception {
		AtomicBoolean called = new AtomicBoolean();

		Captcha mockCaptcha = new MockCaptcha(() -> called.set(true));

		_registerCaptcha(mockCaptcha);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"captchaEngine", MockCaptcha.class.getName()
					).put(
						"createAccountCaptchaEnabled", false
					).build())) {

			UserAccount userAccount = _randomUserAccount();

			_userAccountResource.postUserAccount(userAccount);

			Assert.assertFalse(called.get());
			Assert.assertNotNull(
				_userLocalService.fetchUserByEmailAddress(
					TestPropsValues.getCompanyId(),
					userAccount.getEmailAddress()));
		}
	}

	@Test
	public void testShouldThrowExceptionWithInvalidCaptcha() throws Exception {
		Captcha mockCaptcha = new MockCaptcha(
			() -> {
				throw new CaptchaException();
			});

		_registerCaptcha(mockCaptcha);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"captchaEngine", MockCaptcha.class.getName()
					).put(
						"createAccountCaptchaEnabled", true
					).build())) {

			UserAccount userAccount = _randomUserAccount();

			Assert.assertNull(
				_userLocalService.fetchUserByEmailAddress(
					TestPropsValues.getCompanyId(),
					userAccount.getEmailAddress()));

			_userAccountResource.postUserAccount(userAccount);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(
				CaptchaException.class.getName(), problem.getType());
		}
	}

	public class MockCaptcha implements Captcha {

		public MockCaptcha(UnsafeRunnable<CaptchaException> check) {
			_unsafeRunnableCheck = check;
		}

		@Override
		public void check(HttpServletRequest httpServletRequest)
			throws CaptchaException {

			_unsafeRunnableCheck.run();
		}

		@Override
		public void check(PortletRequest portletRequest)
			throws CaptchaException {

			_unsafeRunnableCheck.run();
		}

		@Override
		public String getTaglibPath() {
			return null;
		}

		@Override
		public boolean isEnabled(HttpServletRequest httpServletRequest) {
			return false;
		}

		@Override
		public boolean isEnabled(PortletRequest portletRequest) {
			return false;
		}

		@Override
		public void serveImage(
				HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse)
			throws IOException {
		}

		@Override
		public void serveImage(
				ResourceRequest resourceRequest,
				ResourceResponse resourceResponse)
			throws IOException {
		}

		private final UnsafeRunnable<CaptchaException> _unsafeRunnableCheck;

	}

	private UserAccount _randomUserAccount() throws Exception {
		return new UserAccount() {
			{
				additionalName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				alternateName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				birthDate = RandomTestUtil.nextDate();
				dashboardURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				emailAddress =
					StringUtil.toLowerCase(RandomTestUtil.randomString()) +
						"@liferay.com";
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				familyName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				givenName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				honorificPrefix = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				honorificSuffix = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				image = StringUtil.toLowerCase(RandomTestUtil.randomString());
				jobTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				lastLoginDate = RandomTestUtil.nextDate();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				password = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				profileURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	private void _registerCaptcha(Captcha captcha) {
		Bundle bundle = FrameworkUtil.getBundle(
			UserAccountResourceWhenPostingUserAccountAsGuestTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Captcha.class, captcha,
			HashMapDictionaryBuilder.put(
				"captcha.engine.impl", MockCaptcha.class.getName()
			).build());
	}

	private static final String _PID =
		"com.liferay.captcha.configuration.CaptchaConfiguration";

	private Captcha _captcha;
	private SAPEntry _sapEntry;

	@Inject
	private SAPEntryLocalService _sapEntryLocalService;

	private ServiceRegistration<Captcha> _serviceRegistration;
	private UserAccountResource _userAccountResource;

	@Inject
	private UserLocalService _userLocalService;

}