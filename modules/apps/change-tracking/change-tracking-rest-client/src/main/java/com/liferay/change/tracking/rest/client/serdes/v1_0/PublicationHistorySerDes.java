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

package com.liferay.change.tracking.rest.client.serdes.v1_0;

import com.liferay.change.tracking.rest.client.dto.v1_0.PublicationHistory;
import com.liferay.change.tracking.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author David Truong
 * @generated
 */
@Generated("")
public class PublicationHistorySerDes {

	public static PublicationHistory toDTO(String json) {
		PublicationHistoryJSONParser publicationHistoryJSONParser =
			new PublicationHistoryJSONParser();

		return publicationHistoryJSONParser.parseToDTO(json);
	}

	public static PublicationHistory[] toDTOs(String json) {
		PublicationHistoryJSONParser publicationHistoryJSONParser =
			new PublicationHistoryJSONParser();

		return publicationHistoryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PublicationHistory publicationHistory) {
		if (publicationHistory == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (publicationHistory.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(publicationHistory.getActions()));
		}

		if (publicationHistory.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					publicationHistory.getDateCreated()));

			sb.append("\"");
		}

		if (publicationHistory.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(publicationHistory.getDescription()));

			sb.append("\"");
		}

		if (publicationHistory.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(publicationHistory.getId());
		}

		if (publicationHistory.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(publicationHistory.getName()));

			sb.append("\"");
		}

		if (publicationHistory.getPublisherName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"publisherName\": ");

			sb.append("\"");

			sb.append(_escape(publicationHistory.getPublisherName()));

			sb.append("\"");
		}

		if (publicationHistory.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(publicationHistory.getStatus()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PublicationHistoryJSONParser publicationHistoryJSONParser =
			new PublicationHistoryJSONParser();

		return publicationHistoryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PublicationHistory publicationHistory) {

		if (publicationHistory == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (publicationHistory.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(publicationHistory.getActions()));
		}

		if (publicationHistory.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					publicationHistory.getDateCreated()));
		}

		if (publicationHistory.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(publicationHistory.getDescription()));
		}

		if (publicationHistory.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(publicationHistory.getId()));
		}

		if (publicationHistory.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(publicationHistory.getName()));
		}

		if (publicationHistory.getPublisherName() == null) {
			map.put("publisherName", null);
		}
		else {
			map.put(
				"publisherName",
				String.valueOf(publicationHistory.getPublisherName()));
		}

		if (publicationHistory.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(publicationHistory.getStatus()));
		}

		return map;
	}

	public static class PublicationHistoryJSONParser
		extends BaseJSONParser<PublicationHistory> {

		@Override
		protected PublicationHistory createDTO() {
			return new PublicationHistory();
		}

		@Override
		protected PublicationHistory[] createDTOArray(int size) {
			return new PublicationHistory[size];
		}

		@Override
		protected void setField(
			PublicationHistory publicationHistory, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					publicationHistory.setActions(
						(Map)PublicationHistorySerDes.toMap(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					publicationHistory.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					publicationHistory.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					publicationHistory.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					publicationHistory.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "publisherName")) {
				if (jsonParserFieldValue != null) {
					publicationHistory.setPublisherName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					publicationHistory.setStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			Class<?> valueClass = value.getClass();

			if (value instanceof Map) {
				sb.append(_toJSON((Map)value));
			}
			else if (valueClass.isArray()) {
				Object[] values = (Object[])value;

				sb.append("[");

				for (int i = 0; i < values.length; i++) {
					sb.append("\"");
					sb.append(_escape(values[i]));
					sb.append("\"");

					if ((i + 1) < values.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(entry.getValue()));
				sb.append("\"");
			}
			else {
				sb.append(String.valueOf(entry.getValue()));
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

}