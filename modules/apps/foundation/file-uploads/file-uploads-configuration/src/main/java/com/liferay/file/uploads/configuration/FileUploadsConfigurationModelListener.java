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

package com.liferay.file.uploads.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.upload.UploadServletRequestImpl;

import java.io.File;
import java.io.IOException;

import java.util.Dictionary;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(
	immediate = true,
	property = {
		"model.class.name=com.liferay.file.uploads.configuration.FileUploadsConfiguration"
	},
	service = ConfigurationModelListener.class
)
public class FileUploadsConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterDelete(String pid)
		throws ConfigurationModelListenerException {

		_updateSettings(new HashMapDictionary());
	}

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		_updateSettings(properties);
	}

	private void _updateSettings(Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		FileUploadsConfiguration fileUploadsConfiguration =
			ConfigurableUtil.createConfigurable(
				FileUploadsConfiguration.class, properties);

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences();

		long dlFileEntryPreviewableProcessorMaxSize =
			fileUploadsConfiguration.dlFileEntryPreviewableProcessorMaxSize();
		long dlFileEntryThumbnailMaxHeight =
			fileUploadsConfiguration.dlFileEntryThumbnailMaxHeight();
		long dlFileEntryThumbnailMaxWidth =
			fileUploadsConfiguration.dlFileEntryThumbnailMaxWidth();
		String dlFileExtensions = StringUtil.merge(
			fileUploadsConfiguration.dlFileExtensions());
		long dlFileMaxSize = fileUploadsConfiguration.dlFileMaxSize();
		String journalImageExtensions = StringUtil.merge(
			fileUploadsConfiguration.journalImageExtensions());
		long journalImageSmallMaxSize =
			fileUploadsConfiguration.journalImageSmallMaxSize();
		String shoppingImageExtensions = StringUtil.merge(
			fileUploadsConfiguration.shoppingImageExtensions());
		long shoppingImageLargeMaxSize =
			fileUploadsConfiguration.shoppingImageLargeMaxSize();
		long shoppingImageMediumMaxSize =
			fileUploadsConfiguration.shoppingImageMediumMaxSize();
		long shoppingImageSmallMaxSize =
			fileUploadsConfiguration.shoppingImageSmallMaxSize();
		long uploadServletRequestImplMaxSize =
			fileUploadsConfiguration.uploadServletRequestImplMaxSize();
		String uploadServletRequestImplTempDir =
			fileUploadsConfiguration.uploadServletRequestImplTempDir();
		long usersImageMaxSize = fileUploadsConfiguration.usersImageMaxSize();

		try {
			portletPreferences.setValue(
				PropsKeys.DL_FILE_ENTRY_PREVIEWABLE_PROCESSOR_MAX_SIZE,
				String.valueOf(dlFileEntryPreviewableProcessorMaxSize));
			portletPreferences.setValue(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_HEIGHT,
				String.valueOf(dlFileEntryThumbnailMaxHeight));
			portletPreferences.setValue(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_WIDTH,
				String.valueOf(dlFileEntryThumbnailMaxWidth));
			portletPreferences.setValue(
				PropsKeys.DL_FILE_EXTENSIONS, dlFileExtensions);
			portletPreferences.setValue(
				PropsKeys.DL_FILE_MAX_SIZE, String.valueOf(dlFileMaxSize));
			portletPreferences.setValue(
				PropsKeys.JOURNAL_IMAGE_EXTENSIONS, journalImageExtensions);
			portletPreferences.setValue(
				PropsKeys.JOURNAL_IMAGE_SMALL_MAX_SIZE,
				String.valueOf(journalImageSmallMaxSize));
			portletPreferences.setValue(
				PropsKeys.SHOPPING_IMAGE_EXTENSIONS, shoppingImageExtensions);
			portletPreferences.setValue(
				PropsKeys.SHOPPING_IMAGE_LARGE_MAX_SIZE,
				String.valueOf(shoppingImageLargeMaxSize));
			portletPreferences.setValue(
				PropsKeys.SHOPPING_IMAGE_MEDIUM_MAX_SIZE,
				String.valueOf(shoppingImageMediumMaxSize));
			portletPreferences.setValue(
				PropsKeys.SHOPPING_IMAGE_SMALL_MAX_SIZE,
				String.valueOf(shoppingImageSmallMaxSize));
			portletPreferences.setValue(
				PropsKeys.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE,
				String.valueOf(uploadServletRequestImplMaxSize));

			if (Validator.isNotNull(uploadServletRequestImplTempDir)) {
				portletPreferences.setValue(
					PropsKeys.UPLOAD_SERVLET_REQUEST_IMPL_TEMP_DIR,
					uploadServletRequestImplTempDir);

				UploadServletRequestImpl.setTempDir(
					new File(uploadServletRequestImplTempDir));
			}

			portletPreferences.setValue(
				PropsKeys.USERS_IMAGE_MAX_SIZE,
				String.valueOf(usersImageMaxSize));

			portletPreferences.store();
		}
		catch (IOException | ReadOnlyException | ValidatorException e) {
			Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());

			throw new ConfigurationModelListenerException(
				LanguageUtil.get(
					resourceBundle,
					"there-was-an-issue-storing-the-preferences"),
				FileUploadsConfiguration.class, this.getClass(), properties);
		}
	}

}