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

import com.liferay.ibm.icu.text.TimeZoneFormat;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneComparator;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.taglib.util.IncludeTag;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class InputTimeZoneTag extends IncludeTag {

	public InputTimeZoneTag() {
		TimeZone timeZone = TimeZoneUtil.getDefault();

		_value = timeZone.getID();
	}

	public void setAutoFocus(boolean autoFocus) {
		_autoFocus = autoFocus;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setDaylight(boolean daylight) {
		_daylight = daylight;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setDisplayStyle(int displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setNullable(boolean nullable) {
		_nullable = nullable;
	}

	public void setValue(String value) {
		_value = value;
	}

	public class TimeZoneDisplay {

		public TimeZoneDisplay(String displayName, String timeZoneId) {
			_displayName = displayName;
			_timeZoneId = timeZoneId;
		}

		public String getDisplayName() {
			return _displayName;
		}

		public String getTimeZoneId() {
			return _timeZoneId;
		}

		private final String _displayName;
		private final String _timeZoneId;

	}

	@Override
	protected void cleanUp() {
		_autoFocus = false;
		_cssClass = null;
		_daylight = false;
		_disabled = false;
		_displayStyle = TimeZone.LONG;
		_name = null;
		_nullable = false;

		TimeZone timeZone = TimeZoneUtil.getDefault();

		_value = timeZone.getID();
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		request.setAttribute(
			"liferay-ui:input-time-zone:autoFocus", String.valueOf(_autoFocus));
		request.setAttribute("liferay-ui:input-time-zone:cssClass", _cssClass);
		request.setAttribute(
			"liferay-ui:input-time-zone:daylight", String.valueOf(_daylight));
		request.setAttribute(
			"liferay-ui:input-time-zone:disabled", String.valueOf(_disabled));
		request.setAttribute("liferay-ui:input-time-zone:name", _name);
		request.setAttribute(
			"liferay-ui:input-time-zone:nullable", String.valueOf(_nullable));
		request.setAttribute("liferay-ui:input-time-zone:value", _value);

		request.setAttribute(
			"liferay-ui:input-time-zone:timeZoneDisplays",
			_getTimeZoneDisplays(request));
	}

	private TimeZoneDisplay _getTimeZoneDisplay(
		Date date, Locale locale, long currentTime, NumberFormat numberFormat,
		TimeZone timeZone) {

		StringBundler displayNameSb = new StringBundler();

		int totalOffset = timeZone.getOffset(currentTime);

		if (totalOffset != 0) {
			displayNameSb.append(StringPool.OPEN_PARENTHESIS);
			displayNameSb.append("UTC");

			String offsetHour = numberFormat.format(totalOffset / Time.HOUR);
			String offsetMinute = numberFormat.format(
				Math.abs(totalOffset % Time.HOUR) / Time.MINUTE);

			displayNameSb.append(StringPool.SPACE);

			if (totalOffset > 0) {
				displayNameSb.append(StringPool.PLUS);
			}

			displayNameSb.append(offsetHour);
			displayNameSb.append(StringPool.COLON);
			displayNameSb.append(offsetMinute);
			displayNameSb.append(StringPool.CLOSE_PARENTHESIS);
			displayNameSb.append(StringPool.SPACE);
		}

		displayNameSb.append(
			timeZone.getDisplayName(
				timeZone.inDaylightTime(date), _displayStyle, locale));

		String timeZoneId = timeZone.getID();

		if (timeZoneId.contains("Phoenix")) {
			StringBundler sb = new StringBundler(4);

			displayNameSb.append(StringPool.SPACE);
			displayNameSb.append(StringPool.OPEN_PARENTHESIS);

			com.liferay.ibm.icu.util.TimeZone icuTimeZone =
				com.liferay.ibm.icu.util.TimeZone.getTimeZone(timeZoneId);

			com.liferay.ibm.icu.text.SimpleDateFormat icuSimpleDateFormat =
				new com.liferay.ibm.icu.text.SimpleDateFormat();

			TimeZoneFormat icuTimeZoneFormat =
				icuSimpleDateFormat.getTimeZoneFormat();

			displayNameSb.append(
				icuTimeZoneFormat.format(
					TimeZoneFormat.Style.ZONE_ID, icuTimeZone, date.getTime()));

			displayNameSb.append(StringPool.CLOSE_PARENTHESIS);
		}

		return new TimeZoneDisplay(displayNameSb.toString(), timeZoneId);
	}

	private List<TimeZoneDisplay> _getTimeZoneDisplays(
		HttpServletRequest request) {

		List<TimeZoneDisplay> timeZoneDisplays = new ArrayList();

		Set<TimeZone> timeZones = new TreeSet(new TimeZoneComparator());

		for (String timeZoneId : PropsUtil.getArray(PropsKeys.TIME_ZONES)) {
			TimeZone curTimeZone = TimeZoneUtil.getTimeZone(timeZoneId);

			timeZones.add(curTimeZone);
		}

		long currentTime = System.currentTimeMillis();
		Date date = new Date();
		Locale locale = PortalUtil.getLocale(request);

		NumberFormat numberFormat = NumberFormat.getInstance(locale);

		numberFormat.setMinimumIntegerDigits(2);

		for (TimeZone timeZone : timeZones) {
			timeZoneDisplays.add(
				_getTimeZoneDisplay(
					date, locale, currentTime, numberFormat, timeZone));
		}

		return timeZoneDisplays;
	}

	private static final String _PAGE =
		"/html/taglib/ui/input_time_zone/page.jsp";

	private boolean _autoFocus;
	private String _cssClass;
	private boolean _daylight;
	private boolean _disabled;
	private int _displayStyle = TimeZone.LONG;
	private String _name;
	private boolean _nullable;
	private String _value;

}