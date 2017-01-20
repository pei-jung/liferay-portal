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

package com.liferay.portal.kernel.util;

import com.liferay.ibm.icu.text.TimeZoneFormat;

import java.text.NumberFormat;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Drew Brokke
 */
public class TimeZoneDisplay {

	public TimeZoneDisplay(
		Date date, int displayStyle, Locale locale, long currentTime,
		NumberFormat numberFormat, TimeZone timeZone) {

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
				timeZone.inDaylightTime(date), displayStyle, locale));

		String timeZoneId = timeZone.getID();

		if (timeZoneId.contains("Phoenix")) {
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

		_displayName = displayNameSb.toString();
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