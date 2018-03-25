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

package com.liferay.bookmarks.uad.exportimport.data.handler;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.user.associated.data.entity.UADEntity;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(immediate = true, service = StagedModelDataHandler.class)
public class BookmarksEntryUADEntityStagedModelDataHandler
	extends BaseStagedModelDataHandler<UADEntity<BookmarksEntry>> {

	public static final String[] CLASS_NAMES = {UADEntity.class.getName()};

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(UADEntity<BookmarksEntry> uadEntity) {
		BookmarksEntry bookmarksEntry = uadEntity.getEntity();

		return bookmarksEntry.getName();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			UADEntity<BookmarksEntry> uadEntity)
		throws Exception {

		BookmarksEntry bookmarksEntry = uadEntity.getEntity();

		Element element = portletDataContext.getExportDataElement(uadEntity);

		portletDataContext.addClassedModel(
			element, ExportImportPathUtil.getModelPath(uadEntity),
			bookmarksEntry);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			UADEntity<BookmarksEntry> uadEntity)
		throws Exception {

		throw new UnsupportedOperationException();
	}

}