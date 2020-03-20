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

import ClayModal, {useModal} from '@clayui/modal';
import {RuleEditor} from 'dynamic-data-mapping-form-builder';
import React, {useContext, useEffect, useRef, useState} from 'react';

import AppContext from '../../AppContext.es';
import {getItem} from '../../utils/client.es';
import Button from '../button/Button.es';

const RuleEditorModalBody = () => {
	const ruleEditorRef = useRef();
	const [
		{
			config: {ruleSettings},
		},
	] = useContext(AppContext);

	const [state, setState] = useState({
		isLoading: true,
		roles: [],
	});

	useEffect(() => {
		const {isLoading, roles} = state;

		if (isLoading) {
			return;
		}

		RuleEditorWrapper.newInstance(ruleEditorRef, roles, ruleSettings);
	}, [ruleEditorRef, ruleSettings, state]);

	useEffect(() => {
		getItem('/o/headless-admin-user/v1.0/roles').then(
			({items: roles = []}) => {
				roles = roles.map(({id, name}) => ({
					id: `${id}`,
					label: name,
					name,
					value: `${id}`,
				}));

				setState(prevState => ({
					...prevState,
					isLoading: false,
					roles,
				}));
			}
		);
	}, []);

	return (
		<ClayModal.Body>
			<div ref={ruleEditorRef}></div>
		</ClayModal.Body>
	);
};

const RuleEditorModal = ({isVisible, onClose}) => {
	const {observer} = useModal({
		onClose,
	});

	if (!isVisible) {
		return <></>;
	}

	return (
		<ClayModal observer={observer} size="lg" status="info">
			<ClayModal.Header>{'Title'}</ClayModal.Header>
			<RuleEditorModalBody />
			<ClayModal.Footer
				last={<Button onClick={onClose}>{'Primary'}</Button>}
			/>
		</ClayModal>
	);
};

class RuleEditorWrapper extends RuleEditor {
	getChildContext() {
		return {
			store: {
				editingLanguageId: 'en_US',
			},
		};
	}

	static newInstance(ruleEditorRef, roles, {functionsMetadata = {}}) {
		return new RuleEditorWrapper(
			{
				actions: [],
				conditions: [],
				dataProviderInstanceParameterSettingsURL:
					'/o/dynamic-data-mapping-form-builder-provider-instance-parameter-settings/',
				dataProviderInstancesURL:
					'/o/dynamic-data-mapping-form-builder-data-provider-instances/',
				events: {
					ruleAdded: () => {},
					ruleCancelled: () => {},
					ruleDeleted: () => {},
					ruleEdited: () => {},
				},
				functionsMetadata,
				functionsURL: '/o/dynamic-data-mapping-form-builder-functions/',
				key: 'create',
				pages: [
					{
						description: '',
						localizedDescription: {
							en_US: '',
						},
						localizedTitle: {
							en_US: '',
						},
						rows: [
							{
								columns: [
									{
										fields: [],
										size: 12,
									},
								],
							},
						],
						title: '',
					},
				],
				ref: 'RuleEditor',
				roles,
				spritemap: `${Liferay.ThemeDisplay.getPathThemeImages()}/lexicon/icons.svg`,
			},
			ruleEditorRef.current
		);
	}
}

export default RuleEditorModal;
