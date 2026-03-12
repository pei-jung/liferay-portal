/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.taglib.servlet.taglib.renderer.LayoutStructureRenderer;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.RenderLayoutContentThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTDisplayRenderer.class)
public class LayoutCTDisplayRenderer extends BaseCTDisplayRenderer<Layout> {

	@Override
	public String[] getAvailableLanguageIds(Layout layout) {
		return layout.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(Layout layout) {
		return layout.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, Layout layout)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_layoutPermission.contains(
				themeDisplay.getPermissionChecker(), layout,
				ActionKeys.UPDATE) ||
			layout.isSystem()) {

			return null;
		}

		String currentURL = _portal.getCurrentURL(httpServletRequest);

		if (layout.isTypeContent()) {
			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(
					layout.fetchDraftLayout(), themeDisplay),
				"p_l_back_url", currentURL, "p_l_mode", Constants.EDIT);
		}

		if (layout.isTypePortlet() &&
			!Objects.equals(
				layout.getType(), LayoutConstants.TYPE_FULL_PAGE_APPLICATION)) {

			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(layout, themeDisplay),
				"p_l_back_url", currentURL);
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout"
		).setParameter(
			"groupId", layout.getGroupId()
		).setParameter(
			"privateLayout", layout.isPrivateLayout()
		).setParameter(
			"selPlid", layout.getPlid()
		).buildString();
	}

	@Override
	public Class<Layout> getModelClass() {
		return Layout.class;
	}

	@Override
	public String getTitle(Locale locale, Layout layout) {
		return layout.getName(locale);
	}

	@Override
	public boolean isHideable(Layout layout) {
		if (layout.isDraftLayout() &&
			(layout.getStatus() == WorkflowConstants.STATUS_DRAFT)) {

			return false;
		}

		return layout.isSystem();
	}

	@Override
	public boolean isShowPreviewDiff() {
		return true;
	}

	@Override
	public String renderPreview(DisplayContext<Layout> displayContext)
		throws Exception {

		Layout layout = displayContext.getModel();

		if (layout.isTypeURL()) {
			return null;
		}

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		HttpServletResponse httpServletResponse =
			displayContext.getHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);

		PageContext pageContext = PageContextFactoryUtil.create(
			httpServletRequest, pipingServletResponse);

		LayoutStructure layoutStructure = null;

		if (layout.isTypeContent()) {
			LayoutPageTemplateStructure layoutPageTemplateStructure =
				_layoutPageTemplateStructureLocalService.
					fetchLayoutPageTemplateStructure(
						layout.getGroupId(), layout.getPlid());

			if (layoutPageTemplateStructure == null) {
				return StringPool.BLANK;
			}

			long segmentsExperienceId = ParamUtil.getLong(
				httpServletRequest, "segmentsExperienceId");

			layoutStructure = LayoutStructure.of(
				layoutPageTemplateStructure.getData(segmentsExperienceId));
		}
		else {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			themeDisplay.setLayout(layout);
			themeDisplay.setLayoutTypePortlet(
				(LayoutTypePortlet)layout.getLayoutType());

			httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);
			httpServletRequest.setAttribute(
				WebKeys.PORTLET_AJAX_RENDER, Boolean.FALSE);
			httpServletRequest.setAttribute(
				"ORIGINAL_HTTP_SERVLET_REQUEST", httpServletRequest);

			layoutStructure = new LayoutStructure();

			LayoutStructureItem rootLayoutStructureItem =
				layoutStructure.addRootLayoutStructureItem();

			layoutStructure.addDropZoneLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);
		}

		if (layoutStructure == null) {
			return StringPool.BLANK;
		}

		boolean originalRenderLayoutContent =
			RenderLayoutContentThreadLocal.isRenderLayoutContent();

		try {
			RenderLayoutContentThreadLocal.setRenderLayoutContent(true);

			LayoutStructureRenderer layoutStructureRenderer =
				new LayoutStructureRenderer(
					httpServletRequest, layoutStructure,
					layoutStructure.getMainItemId(), Constants.PREVIEW,
					pageContext, false, true);

			layoutStructureRenderer.render();
		}
		finally {
			RenderLayoutContentThreadLocal.setRenderLayoutContent(
				originalRenderLayoutContent);
		}

		return unsyncStringWriter.toString();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Layout> displayBuilder) {
		Layout layout = displayBuilder.getModel();

		displayBuilder.display(
			"name", layout.getName(displayBuilder.getLocale())
		).display(
			"title", layout.getTitle()
		).display(
			"description", layout.getDescription(displayBuilder.getLocale())
		).display(
			"friendly-url", layout.getFriendlyURL()
		).display(
			"created-by",
			() -> {
				String userName = layout.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", layout.getCreateDate()
		).display(
			"last-modified", layout.getModifiedDate()
		).display(
			"site",
			() -> {
				Group group = layout.getGroup();

				return group.getName(displayBuilder.getLocale());
			}
		).display(
			"theme",
			() -> {
				Theme theme = layout.getTheme();

				return theme.getName();
			}
		).display(
			"color-scheme",
			() -> {
				ColorScheme colorScheme = layout.getColorScheme();

				return colorScheme.getName();
			}
		).display(
			"style-book",
			() -> {
				if (Validator.isNull(layout.getStyleBookEntryERC())) {
					return null;
				}

				StyleBookEntry styleBookEntry =
					_styleBookEntryLocalService.
						fetchStyleBookEntryByExternalReferenceCode(
							layout.getStyleBookEntryERC(),
							_staging.getLiveGroupId(layout.getGroupId()));

				if (styleBookEntry == null) {
					return null;
				}

				return styleBookEntry.getName();
			}
		).display(
			"type", layout.getType()
		).display(
			"type-settings", layout.getTypeSettings()
		).display(
			"css", layout.getCss()
		).display(
			"keywords", layout.getKeywords()
		).display(
			"robots", layout.getRobots()
		).display(
			"hidden", layout.isHidden()
		).display(
			"system", layout.isSystem()
		).display(
			"publish-date", layout.getPublishDate()
		).display(
			"last-publish-date", layout.getLastPublishDate()
		).display(
			"priority", layout.getPriority()
		);
	}

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Portal _portal;

	@Reference
	private Staging _staging;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}