package com.athena.mis.exchangehouse.actions.taglib

import com.athena.mis.exchangehouse.entity.ExhRemittancePurpose
import com.athena.mis.exchangehouse.utility.ExhRemittancePurposeCacheUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownRemittancePurposeTagLibActionService {

	@Autowired
	ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility

	private static final String NAME = 'name'
	private static final String CLASS = 'class'
	private static final String TAB_INDEX = 'tabindex'
	private static final String ON_CHANGE = 'onchange'
	private static final String HINTS_TEXT = 'hintsText'
	private static final String SHOW_HINTS = 'showHints'
	private static final String DEFAULT_VALUE = 'defaultValue'
	private static final String PLEASE_SELECT = 'Please Select...'
	private static final String REQUIRED = 'required'
	private static final String VALIDATION_MESSAGE = 'validationMessage'
	private static final String DEFAULT_MESSAGE = 'Required'
	private static final String DATA_MODEL_NAME = 'dataModelName'

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
			dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
			dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
			dropDownAttributes.put(REQUIRED, Boolean.FALSE)
			dropDownAttributes.put(DEFAULT_VALUE, null)
			dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)

			if (attrs.hintsText) {
				dropDownAttributes.put(HINTS_TEXT, attrs.hintsText)
			}
			if (attrs.showHints) {
				boolean showHints = Boolean.parseBoolean(attrs.showHints)
				dropDownAttributes.put(SHOW_HINTS, new Boolean(showHints))
			}
			if (attrs.required) {
				boolean required = Boolean.parseBoolean(attrs.required)
				dropDownAttributes.put(REQUIRED, new Boolean(required))
			}
			if (attrs.defaultValue) {
				String strDefaultValue = attrs.get(DEFAULT_VALUE)
				long defaultValue = Long.parseLong(strDefaultValue)
				dropDownAttributes.put(DEFAULT_VALUE, new Long(defaultValue))
			}
			if (attrs.validationMessage) {
				dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validationMessage)
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

	/** Get the list of AppGroups
	 *  build the html for 'select'
	 * @param parameters - map returned from preCondition
	 * @param obj - N/A
	 * @return - html string for 'select'
	 */
	public Object execute(Object parameters, Object obj) {
		try {
			Map dropDownAttributes = (Map) parameters
			List<ExhRemittancePurpose> lstAgent = exhRemittancePurposeCacheUtility.list()
			String html = buildDropDown(lstAgent, dropDownAttributes)
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

	private static final String SELECT_END = "</select>"

	/** Generate the html for select
	 * @param lstAppGroup - list for select 'options'
	 * @param dropDownAttributes - a map containing the attributes of drop down
	 * @return - html string for select
	 */
	private String buildDropDown(List<ExhRemittancePurpose> lstAgent, Map dropDownAttributes) {
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
			lstAgent = Tools.listForKendoDropdown(lstAgent,null,null)
		}
		String jsonData = lstAgent as JSON
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
