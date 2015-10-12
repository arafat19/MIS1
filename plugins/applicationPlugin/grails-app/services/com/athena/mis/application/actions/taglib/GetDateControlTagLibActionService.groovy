package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger

/*Renders html of date field*/
class GetDateControlTagLibActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String INPUT_END = ">"
    private static final String NAME = 'name'
    private static final String ON_CHANGE = 'onchange'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String PLACE_HOLDER = 'placeholder'
    private static final String DISABLED = 'disabled'
    private static final String DATE_VALUE = 'dateValue'

    /** Build a map containing properties of html date control
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map dateControlAttributes = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            dateControlAttributes.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

            String name = attrs.get(NAME)
            if (!name) {
                return dateControlAttributes
            }
            dateControlAttributes.put(NAME, name)   // set name

            // Set default values for optional parameters
            dateControlAttributes.put(ON_CHANGE, attrs.onchange ? attrs.onchange : null)
            dateControlAttributes.put(CLASS, attrs.class ? attrs.class : null)
            dateControlAttributes.put(TAB_INDEX, attrs.tabindex ? attrs.tabindex : null)
            dateControlAttributes.put(REQUIRED, Boolean.FALSE)
            dateControlAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
            dateControlAttributes.put(PLACE_HOLDER, DateUtility.dd_MM_yyyy_SLASH)

            if (attrs.required) {
                boolean required = Boolean.parseBoolean(attrs.required)
                dateControlAttributes.put(REQUIRED, new Boolean(required))
            }
            if (attrs.validationMessage) {
                dateControlAttributes.put(VALIDATION_MESSAGE, attrs.validationMessage)
            }
            if (attrs.placeholder) {
                dateControlAttributes.put(PLACE_HOLDER, attrs.placeholder)
            }
            if (attrs.disabled != null) {
                dateControlAttributes.put(DISABLED, DISABLED)
            }
            String strDate = getDateValue(attrs)
            dateControlAttributes.put(Tools.IS_ERROR, Boolean.FALSE)
            dateControlAttributes.put(DATE_VALUE, strDate)
            return dateControlAttributes
        } catch (Exception e) {
            log.error(e.getMessage())
            dateControlAttributes.put(Tools.IS_ERROR, Boolean.TRUE)
            return dateControlAttributes
        }
    }

    /** Build the html for date field
     * @param parameters - map returned from executePreCondition
     * @param obj - N/A
     * @return - html string for date field
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map dateControlAttributes = (Map) parameters
            String html = buildDateControl(dateControlAttributes)
            return html
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get string form of date value to show on UI
     * @param params - a map of given attributes
     * @return - string form of date
     */
    private String getDateValue(Map params) {
        if (params.value != null) {
            return params.value.toString().trim()
        }
        Date ctlDate = new Date()
        if (params.diffWithCurrent) {
            int diff = Integer.parseInt(params.diffWithCurrent.toString())
            ctlDate = ctlDate + diff
        }
        return DateUtility.getDateForUI(ctlDate)
    }

    /** Generate the html for date control
     * @param dateControlAttributes - a map containing the attributes of date control
     * @return - html string for date control
     */
    private String buildDateControl(Map dateControlAttributes) {
        // read map values
        String name = dateControlAttributes.get(NAME)
        String paramOnChange = dateControlAttributes.get(ON_CHANGE)
        String paramClass = dateControlAttributes.get(CLASS)
        String paramTabIndex = dateControlAttributes.get(TAB_INDEX)
        Boolean required = dateControlAttributes.get(REQUIRED)
        String validationMessage = dateControlAttributes.get(VALIDATION_MESSAGE)
        String placeholder = dateControlAttributes.get(PLACE_HOLDER)
        String strDate = dateControlAttributes.get(DATE_VALUE)
        String disabled = dateControlAttributes.get(DISABLED)

        String htmlClass = paramClass ? "class='${paramClass}'" : Tools.EMPTY_SPACE
        String htmlTabIndex = paramTabIndex ? "tabindex='${paramTabIndex}'" : Tools.EMPTY_SPACE
        String htmlRequired = required.booleanValue() ? "required" : Tools.EMPTY_SPACE
        String htmlValidationMessage = required.booleanValue() ? "validationMessage='${validationMessage}'" : Tools.EMPTY_SPACE
        String htmlPlaceholder = placeholder ? "placeholder='${placeholder}'" : Tools.EMPTY_SPACE
        String htmlDisabled = disabled ? "\$('#${name}').data('kendoDatePicker').enable(false);" : Tools.EMPTY_SPACE
        String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : Tools.EMPTY_SPACE

        String html = "<input name = '${name}' id = '${name}' ${htmlClass} ${htmlTabIndex} ${htmlRequired} ${htmlValidationMessage} ${htmlPlaceholder}" + INPUT_END

        String script = """
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDatePicker({
                        format: "dd/MM/yyyy",
                        value: '${strDate}',
                        ${strOnChange}
                    });
                    \$("#${name}").mask('99/99/9999');
                    ${htmlDisabled}
                });
            </script>
        """
        return html + script
    }
}
