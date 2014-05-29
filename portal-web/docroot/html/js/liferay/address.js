AUI.add(
	'liferay-address',
	function(A) {
		var Address = {
			getCountries: function(callback) {
				Liferay.Service(
					'/country/get-countries',
					{
						active: true
					}
				).then(callback);
			},

			getRegions: function(callback, selectKey) {
				Liferay.Service(
					'/region/get-regions',
					{
						countryId: Number(selectKey),
						active: true
					}
				).then(callback);
			}
		};

		Liferay.Address = Address;
	},
	'',
	{
		requires: ['liferay-service']
	}
);
