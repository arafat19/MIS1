package com.athena.mis.exchangehouse.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownExhTaskStatusTagLibActionService extends BaseService implements ActionIntf{

    @Autowired
    ExhSessionUtil exhSessionUtil
	@Autowired
	ExhTaskStatusCacheUtility exhTaskStatusCacheUtility

	SystemEntityService systemEntityService

	private static final String NAME = 'name'
	private static final String CLASS = 'class'
	private static final String TAB_INDEX = 'tabindex'
	private static final String ON_CHANGE = 'onchange'
	private static final String FILTER_MAPPING = 'filterMapping'
	private static final String ADD_ALL_ATTRIBUTES = 'addAllAttributes'
	private static final String HINTS_TEXT = 'hintsText'
	private static final String SHOW_HINTS = 'showHints'
	private static final String DEFAULT_VALUE = 'defaultValue'
	private static final String PLEASE_SELECT = 'Please Select...'
	private static final String REQUIRED = 'required'
	private static final String VALIDATION_MESSAGE = 'validationMessage'
	private static final String DEFAULT_MESSAGE = 'Required'
	private static final String DATA_MODEL_NAME = 'dataModelName'
	private static final String LIST_TASK_STATUS = 'listTaskStatus'

	private Logger log = Logger.getLogger(getClass())

	/** Build a map containing properties of html select
	 *  1. Set default values of properties
	 *  2. Overwrite default properties if defined in parameters
	 * @param parameters - a map of given attributes
	 * @param obj - N/A
	 * @return - a map containing all necessary properties with value
	 */
	public Object executePreCondition(Object parameters, Object obj) {
		Map dropDownAttributes = new LinkedHashMap()
		try {
			Map attrs = (Map) parameters
			dropDownAttributes.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

			String name = attrs.get(NAME)
			String dataModelName = attrs.get(DATA_MODEL_NAME)
			if ((!name) || (!dataModelName)) {
				return dropDownAttributes
			}
			dropDownAttributes.put(NAME, name)   // set name
			dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)   // set dataModelName

			// Set default values for optional parameters
			dropDownAttributes.put(CLASS, attrs.class ? attrs.class : null)
			dropDownAttributes.put(TAB_INDEX, attrs.tabindex ? attrs.tabindex : null)
			dropDownAttributes.put(ON_CHANGE, attrs.onchange ? attrs.onchange : null)
			dropDownAttributes.put(ADD_ALL_ATTRIBUTES, Boolean.FALSE)
			dropDownAttributes.put(ADD_ALL_ATTRIBUTES, Boolean.FALSE)
			dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
			dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
			dropDownAttributes.put(DEFAULT_VALUE, null)
			dropDownAttributes.put(LIST_TASK_STATUS, null)
			dropDownAttributes.put(REQUIRED, Boolean.FALSE)
			dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)

			if (attrs.hintsText) {
				dropDownAttributes.put(HINTS_TEXT, attrs.hintsText)
			}
			if (attrs.showHints) {
				boolean showHints = Boolean.parseBoolean(attrs.showHints)
				dropDownAttributes.put(SHOW_HINTS, new Boolean(showHints))
			}
			if (attrs.defaultValue) {
				String strDefaultValue = attrs.get(DEFAULT_VALUE)
				long defaultValue = Long.parseLong(strDefaultValue)
				dropDownAttributes.put(DEFAULT_VALUE, new Long(defaultValue))
			}
			if (attrs.filterMapping) {
				String filterMapping = (attrs.filterMapping.toString())
				dropDownAttributes.put(FILTER_MAPPING, filterMapping)
			}
			if (attrs.addAllAttributes) {
				boolean filterMapping = Boolean.parseBoolean(attrs.filterMapping)
				dropDownAttributes.put(FILTER_MAPPING, new Boolean(filterMapping))
			}
			if (attrs.required) {
				boolean required = Boolean.parseBoolean(attrs.required)
				dropDownAttributes.put(REQUIRED, new Boolean(required))
			}
			if (attrs.validationMessage) {
				dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validationMessage)
			}
			if (attrs.lstTaskStatus) {
				dropDownAttributes.put(LIST_TASK_STATUS, attrs.lstTaskStatus)
			}
			dropDownAttributes.put(Tools.IS_ERROR, Boolean.FALSE)
			return dropDownAttributes
		} catch (Exception e) {
			log.error(e.getMessage())
			dropDownAttributes.put(Tools.IS_ERROR, Boolean.TRUE)
			return dropDownAttributes
		}
	}

	public Object executePostCondition(Object parameters, Object obj) {
		return null
	}

	/** Get the list of Projects
	 *  build the html for 'select'
	 * @param parameters - map returned from preCondition
	 * @param obj - N/A
	 * @return - html string for 'select'
	 */
	public Object execute(Object parameters, Object obj) {
		try {
			Map dropDownAttributes = (Map) parameters
			List lstTaskStatus = (List) dropDownAttributes.get(LIST_TASK_STATUS)
			List<SystemEntity> lstForDropDown =(List<SystemEntity>) buildList(lstTaskStatus)
			String html = buildDropDown(lstForDropDown, dropDownAttributes)
			return html
		} catch (Exception e) {
			log.error(e.message)
			return Boolean.FALSE
		}
	}

	public Object buildSuccessResultForUI(Object obj) {
		return null
	}

	public Object buildFailureResultForUI(Object obj) {
		return null
	}

	/** Get the list of backlog
	 * @param filterMapping - true/false
	 * @return - list of backlogs
	 */
	private List<SystemEntity> buildList(List lstTaskStatus) {
		List<SystemEntity> systemEntityList = []
		for(int i =0; i< lstTaskStatus.size(); i++)
		{
			long id = (long) lstTaskStatus[i]
			long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
			SystemEntity entity = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(id,companyId)
			systemEntityList << [id: entity.id, name: entity.key]
		}
		return systemEntityList
	}



	private static final String SELECT_END = "</select>"

	/** Generate the html for select
	 * @param lstProject - list for select 'options'
	 * @param dropDownAttributes - a map containing the attributes of drop down
	 * @return - html string for select
	 */
	private String buildDropDown(List lstTaskStatus, Map dropDownAttributes) {
		// read map values
		String name = dropDownAttributes.get(NAME)
		String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
		String paramClass = dropDownAttributes.get(CLASS)
		String paramTabIndex = dropDownAttributes.get(TAB_INDEX)
		String paramOnChange = dropDownAttributes.get(ON_CHANGE)
		String hintsText = dropDownAttributes.get(HINTS_TEXT)
		Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
		Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
		Boolean required = dropDownAttributes.get(REQUIRED)
		String validationMessage = dropDownAttributes.get(VALIDATION_MESSAGE)

		String htmlClass = paramClass ? "class='${paramClass}'" : Tools.EMPTY_SPACE
		String htmlTabIndex = paramTabIndex ? "tabindex='${paramTabIndex}'" : Tools.EMPTY_SPACE
		String htmlRequired = required.booleanValue() ? "required" : Tools.EMPTY_SPACE
		String htmlValidationMessage = required.booleanValue() ? "validationMessage='${validationMessage}'" : Tools.EMPTY_SPACE

		String html = "<select name = '${name}' id = '${name}' ${htmlClass} ${htmlTabIndex} ${htmlRequired} ${htmlValidationMessage}>\n" + SELECT_END
		String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : Tools.EMPTY_SPACE
		String strDefaultValue = defaultValue ? defaultValue : ''

		if(showHints.booleanValue())
		{
			lstTaskStatus = Tools.listForKendoDropdown(lstTaskStatus, null, null)
		}
		String jsonData = lstTaskStatus as JSON

		String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'name',
                        dataValueField:'id',
                        dataSource:${jsonData},
                        value:'${strDefaultValue}',
                        ${strOnChange}
                    });
                });
                ${dataModelName} = \$("#${name}").data("kendoDropDownList");
            </script>
        """
		return html + script
	}
}
