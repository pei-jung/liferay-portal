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

package com.liferay.wiki.web.internal.change.tracking.spi.display;

import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.model.WikiPage;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import com.liferay.wiki.model.WikiPageDisplay;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.web.internal.util.WikiUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.wiki.service.WikiPageLocalService;

/**
 * @author Noor Najjar
 */
@Component(service = CTDisplayRenderer.class)
public class WikiPageCTDisplayRenderer extends BaseCTDisplayRenderer<WikiPage> {


	public WikiPageCTDisplayRenderer() {
		_wikiEngineRenderer = null;
	}

	@Override
	public String getEditURL(
		HttpServletRequest httpServletRequest, WikiPage wikiPage) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, WikiPortletKeys.WIKI_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/wiki/edit_page"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"nodeId", wikiPage.getNodeId()
		).setParameter(
			"title", wikiPage.getTitle()
		).buildString();
	}

	@Override
	public Class<WikiPage> getModelClass() {
		return WikiPage.class;
	}

	@Override
	public String getTitle(Locale locale, WikiPage wikiPage) {
		return StringBundler.concat(
			wikiPage.getTitle(), " (", wikiPage.getVersion(), ")");
	}

	@Override
	public String renderPreview(DisplayContext<WikiPage> displayContext)
		throws Exception {

//		WikiPage wikiPage = displayContext.getModel();

//
//		PortletRequest portletRequest =
//			(PortletRequest)httpServletRequest.getAttribute(
//				JavaConstants.JAVAX_PORTLET_REQUEST);
//		PortletResponse portletResponse =
//			(PortletResponse)httpServletRequest.getAttribute(
//				JavaConstants.JAVAX_PORTLET_RESPONSE);
//
//		PortletRequestModel portletRequestModel = new PortletRequestModel(
//			portletRequest, portletResponse);
//
//		String attachmentURLPrefix = StringBundler.concat(
//			themeDisplay.getPathMain(), "/wiki/get_page_attachment?p_l_id=",
//			themeDisplay.getPlid(), "&nodeId=", wikiPage.getNodeId(), "&title=",
//			URLCodec.encodeURL(wikiPage.getTitle()), "&fileName=");
//
//		String url = null;
//
//		String urlw = _wikiEngineRenderer.convert(
//			displayContext.getModel(), null, null, attachmentURLPrefix);
//
//
//		WikiPageDisplay wikiPageDisplay =
//			_wikiPageLocalService.getPageDisplay(
//				wikiPage,  null,
//				null, attachmentURLPrefix);

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
		RenderResponse renderResponse =
			(RenderResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		WikiPage wikiPage = (WikiPage)httpServletRequest.getAttribute(WikiWebKeys.WIKI_PAGE);
		WikiEngineRenderer wikiEngineRenderer = (WikiEngineRenderer)httpServletRequest.getAttribute(
			WikiWebKeys.WIKI_ENGINE_RENDERER);

		PortletURL viewPageURL = renderResponse.createRenderURL();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		viewPageURL.setParameter("mvcRenderCommandName", "/wiki/view_page");

		PortletURL editPageURL = PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCRenderCommandName(
			"/wiki/edit_page"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"nodeId", wikiPage.getNodeId()
		).setParameter(
			"title", wikiPage.getTitle()
		).buildPortletURL();

		String formattedContent = null;

		try {
			formattedContent = WikiUtil.getFormattedContent(wikiEngineRenderer, renderRequest, renderResponse, wikiPage, viewPageURL, editPageURL, wikiPage.getTitle(), true);
		}
		catch (Exception e) {
			formattedContent = wikiPage.getContent();
		}

		return 	formattedContent;
		//StringBundler.concat(wikiPageDisplay.getTitle(), wikiPageDisplay.getFormattedContent(), wikiPageDisplay.getAttachmentsFileEntries());
}


	@Override
	public boolean showPreviewDiff() {
		return true;
	}

	@Override
	protected void buildDisplay(DisplayBuilder<WikiPage> displayBuilder) {
		WikiPage wikiPage = displayBuilder.getModel();

		displayBuilder.display(
			"name", wikiPage.getTitle()
		).display(
			"created-by",
			() -> {
				String userName = wikiPage.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", wikiPage.getCreateDate()
		).display(
			"last-modified", wikiPage.getModifiedDate()
		);
	}

	@Reference
	private Portal _portal;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

	@Reference
	private  WikiEngineRenderer _wikiEngineRenderer;

	@Reference
	private Language _language;

	@Reference
	private  RenderRequest _renderRequest;

	@Reference
	private  RenderResponse _renderResponse;


}