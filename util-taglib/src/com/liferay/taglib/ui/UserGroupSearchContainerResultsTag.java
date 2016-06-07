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

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.UserGroupConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import java.util.LinkedHashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Drew Brokke
 */
public class UserGroupSearchContainerResultsTag<R> extends IncludeTag {

	public void setSearchTerms(DisplayTerms searchTerms) {
		_searchTerms = searchTerms;
	}

	public void setUseIndexer(boolean useIndexer) {
		_useIndexer = useIndexer;
	}

	public void setUserGroupParams(
		LinkedHashMap<String, Object> userGroupParams) {

		_userGroupParams = userGroupParams;
	}

	@Override
	protected void cleanUp() {
		_searchTerms = null;
		_useIndexer = false;
		_userGroupParams = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		SearchContainerTag<R> searchContainerTag =
			(SearchContainerTag<R>)findAncestorWithClass(
				this, SearchContainerTag.class);

		SearchContainer<R> searchContainer =
			searchContainerTag.getSearchContainer();

		if (Validator.isNotNull(_searchTerms.getKeywords())) {
			setUseIndexer(true);
		}

		if (_hasFinderParam(_userGroupParams)) {
			setUseIndexer(false);
		}

		request.setAttribute(
			"liferay-ui:user-group-search-container-results:searchContainer",
			searchContainer);
		request.setAttribute(
			"liferay-ui:user-group-search-container-results:searchTerms",
			_searchTerms);
		request.setAttribute(
			"liferay-ui:user-group-search-container-results:useIndexer",
			_useIndexer);
		request.setAttribute(
			"liferay-ui:user-group-search-container-results:userGroupParams",
			_userGroupParams);
	}

	private boolean _hasFinderParam(LinkedHashMap<String, Object> params) {
		Set<String> paramKeys = params.keySet();

		for (String paramKey : paramKeys) {
			if (ArrayUtil.contains(
					UserGroupConstants.PARAMS_USER_GROUP_FINDER_PARAMS,
					paramKey)) {

				return true;
			}
		}

		return false;
	}

	private static final String _PAGE =
		"/html/taglib/ui/user_group_search_container_results/page.jsp";

	private DisplayTerms _searchTerms;
	private boolean _useIndexer;
	private LinkedHashMap<String, Object> _userGroupParams;

}