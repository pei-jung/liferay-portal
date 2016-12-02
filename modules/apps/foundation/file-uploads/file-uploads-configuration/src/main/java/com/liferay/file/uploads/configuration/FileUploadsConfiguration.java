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

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.util.PropsKeys;

/**
 * @author Drew Brokke
 */
@ExtendedObjectClassDefinition(
	category = "foundation", scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.file.uploads.configuration.FileUploadsConfiguration",
	localization = "content/Language", name = "file.uploads.configuration.name"
)
public interface FileUploadsConfiguration {

	@Meta.AD(
		deflt = "104857600",
		description = "dl-maximum-previewable-file-size-help",
		name = "dl-maximum-previewable-file-size", required = false
	)
	public long dlFileEntryPreviewableProcessorMaxSize();

	@Meta.AD(
		deflt = "300", name = "dl-maximum-thumbnail-height", required = false
	)
	public int dlFileEntryThumbnailMaxHeight();

	@Meta.AD(
		deflt = "300", name = "dl-maximum-thumbnail-width", required = false
	)
	public int dlFileEntryThumbnailMaxWidth();

	@Meta.AD(deflt = "*", name = "dl-allowed-file-extensions", required = false)
	public String[] dlFileExtensions();

	@Meta.AD(deflt = "0", name = "dl-maximum-file-size", required = false)
	public long dlFileMaxSize();

	@Meta.AD(
		deflt = ".gif|.jpeg|.jpg|.png",
		name = "journal-allowed-file-extensions", required = false
	)
	public String[] journalImageExtensions();

	@Meta.AD(
		deflt = "51200", name = "journal-maximum-file-size", required = false
	)
	public long journalImageSmallMaxSize();

	@Meta.AD(
		deflt = "", name = "shopping-allowed-file-extensions", required = false
	)
	public String[] shoppingImageExtensions();

	@Meta.AD(deflt = "0", required = false)
	public long shoppingImageLargeMaxSize();

	@Meta.AD(deflt = "0", required = false)
	public long shoppingImageMediumMaxSize();

	@Meta.AD(deflt = "0", required = false)
	public long shoppingImageSmallMaxSize();

	@Meta.AD(
		deflt = "104857600",
		id = PropsKeys.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE,
		name = "overall-maximum-upload-request-size", required = false
	)
	public long uploadServletRequestImplMaxSize();

	@Meta.AD(
		deflt = "", id = PropsKeys.UPLOAD_SERVLET_REQUEST_IMPL_TEMP_DIR,
		name = "temporary-storage-directory", required = false
	)
	public String uploadServletRequestImplTempDir();

	@Meta.AD(
		deflt = "307200", name = "users-maximum-file-size", required = false
	)
	public long usersImageMaxSize();

}