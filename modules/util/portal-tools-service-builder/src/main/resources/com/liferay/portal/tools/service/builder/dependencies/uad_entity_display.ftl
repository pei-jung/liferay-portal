package ${packagePath}.uad.display;

import ${apiPackagePath}.model.${entity.name};
import ${packagePath}.uad.constants.${portletShortName}UADConstants;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.user.associated.data.display.UADEntityDisplay;
import com.liferay.user.associated.data.entity.UADEntity;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;

/**
 * @author ${author}
 * @generated
 */
@Component(
	immediate = true,
	property = {"model.class.name=" + ${portletShortName}UADConstants.CLASS_NAME_${entity.constantName}},
	service = UADEntityDisplay.class
)
public class ${entity.name}UADEntityDisplay implements UADEntityDisplay<${entity.name}> {

	public String getApplicationName() {
		return ${portletShortName}UADConstants.UAD_ENTITY_SET_NAME;
	}

	public String[] getDisplayFieldNames() {
		return _${entity.varName}UADEntityDisplayHelper.getDisplayFieldNames();
	}

	@Override
	public String getEditURL(UADEntity<${entity.name}> uadEntity, LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse) throws Exception {
		return _${entity.varName}UADEntityDisplayHelper.get${entity.name}EditURL(uadEntity.getEntity(), liferayPortletRequest, liferayPortletResponse);
	}

	public String getKey() {
		return ${portletShortName}UADConstants.CLASS_NAME_${entity.constantName};
	}

	@Override
	public Map<String, Object> getUADEntityNonanonymizableFieldValues(UADEntity<${entity.name}> uadEntity) {
		return _${entity.varName}UADEntityDisplayHelper.getUADEntityNonanonymizableFieldValues(uadEntity.getEntity());
	}

	@Override
	public String getUADEntityTypeDescription() {
		return "${entity.UADEntityTypeDescription}";
	}

	@Override
	public String getUADEntityTypeName() {
		return "${entity.name}";
	}

	@Reference
	private ${entity.name}UADEntityDisplayHelper _${entity.varName}UADEntityDisplayHelper;

}