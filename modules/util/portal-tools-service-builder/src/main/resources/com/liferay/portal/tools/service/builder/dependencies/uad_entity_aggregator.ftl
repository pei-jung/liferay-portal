package ${packagePath}.uad.aggregator;

import ${apiPackagePath}.model.${entity.name};
import ${apiPackagePath}.service.${entity.name}LocalService;
import ${packagePath}.uad.constants.${portletShortName}UADConstants;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.user.associated.data.aggregator.DynamicQueryUADEntityAggregator;
import com.liferay.user.associated.data.aggregator.UADEntityAggregator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author ${author}
 * @generated
 */
@Component(
	immediate = true,
	property = {"model.class.name=" + ${portletShortName}UADConstants.CLASS_NAME_${entity.constantName}},
	service = UADEntityAggregator.class
)
public class ${entity.name}UADEntityAggregator extends DynamicQueryUADEntityAggregator<${entity.name}> {

	@Override
	public String getUADEntitySetName() {
		return ${portletShortName}UADConstants.UAD_ENTITY_SET_NAME;
	}

	@Override
	public String[] getUserIdFieldNames() {
		return ${portletShortName}UADConstants.USER_ID_FIELD_NAMES_${entity.constantName};
	}

	@Override
	protected long doCount(DynamicQuery dynamicQuery) {
		return _${entity.varName}LocalService.dynamicQueryCount(dynamicQuery);
	}

	@Override
	protected DynamicQuery doGetDynamicQuery() {
		return _${entity.varName}LocalService.dynamicQuery();
	}

	@Override
	protected List<${entity.name}> doGetEntities(DynamicQuery dynamicQuery, int start, int end) {
		return _${entity.varName}LocalService.dynamicQuery(dynamicQuery, start, end);
	}

	@Override
	protected ${entity.name} doGetEntity(long entityId) throws PortalException {
		return _${entity.varName}LocalService.get${entity.name}(entityId);
	}

	@Override
	protected long doGetEntityId(${entity.name} ${entity.varName}) {
		return ${entity.varName}.get${textFormatter.format(entity.PKVarName, 6)}();
	}

	@Override
	protected String doGetRegistryKey() {
		return ${portletShortName}UADConstants.CLASS_NAME_${entity.constantName};
	}

	@Override
	protected long doGetUserId(${entity.name} ${entity.varName}) {
		return ${entity.varName}.getUserId();
	}

	@Reference
	private ${entity.name}LocalService _${entity.varName}LocalService;

}