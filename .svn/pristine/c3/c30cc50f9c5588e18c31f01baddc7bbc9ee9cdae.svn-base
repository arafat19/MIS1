package com.athena.mis.projecttrack.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownPtBacklogTagLibActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    @Autowired
    PtSessionUtil ptSessionUtil

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
    private static final String IDEA = 'idea'

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
            dropDownAttributes.put(FILTER_MAPPING, Boolean.FALSE)
            dropDownAttributes.put(ADD_ALL_ATTRIBUTES, Boolean.FALSE)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
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
                boolean filterMapping = Boolean.parseBoolean(attrs.filterMapping)
                dropDownAttributes.put(FILTER_MAPPING, new Boolean(filterMapping))
            }
            if (attrs.addAllAttributes) {
                boolean addAllAttributes = Boolean.parseBoolean(attrs.addAllAttributes)
                dropDownAttributes.put(ADD_ALL_ATTRIBUTES, new Boolean(addAllAttributes))
            }
            if (attrs.required) {
                boolean required = Boolean.parseBoolean(attrs.required)
                dropDownAttributes.put(REQUIRED, new Boolean(required))
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

    /** Get the list of Projects
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map dropDownAttributes = (Map) parameters
            Boolean filterMapping = (Boolean) dropDownAttributes.get(FILTER_MAPPING)
            Boolean addAllAttributes = (Boolean) dropDownAttributes.get(ADD_ALL_ATTRIBUTES)
            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            List<PtBacklog> lstServiceResult = (List<PtBacklog>) ptBacklogService.list(companyId)
            List<PtBacklog> lstBacklog = (List<PtBacklog>) listBacklog(filterMapping, lstServiceResult)
            List lstForDropDown = buildListForDropDown(lstBacklog, addAllAttributes)
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
    private List<PtBacklog> listBacklog(boolean filterMapping, List<PtBacklog> lstBacklog) {
        List<PtBacklog> lstPtBacklog = []
        if (filterMapping) {
            for (int i = 0; i < lstBacklog.size(); i++) {
                if (lstBacklog[i].sprintId == 0L) {
                    lstPtBacklog << lstBacklog[i]
                }
            }
        } else {
            lstPtBacklog = lstBacklog
        }
        return lstPtBacklog
    }

    /**
     * Build list for drop down
     * @param lstBacklog - list of backlog
     * @param addAllAttributes - boolean value (true/false)
     * @return - list for drop down
     */
    private List buildListForDropDown(List<PtBacklog> lstBacklog, boolean addAllAttributes) {
        List lstDropDown = []
        if ((lstBacklog == null) || (lstBacklog.size() <= 0))
            return lstDropDown
        if (addAllAttributes) {
            for (int i = 0; i < lstBacklog.size(); i++) {
                PtBacklog ptBacklog = lstBacklog[i]
                lstDropDown << [id: ptBacklog.id, idea: Tools.makeDetailsShort(ptBacklog.idea, 30), actor: ptBacklog.actor, purpose: ptBacklog.purpose, benefit: ptBacklog.benefit, priority: ptBacklog.priorityId, status: ptBacklog.statusId, createdOn: DateUtility.getDateForUI(ptBacklog.createdOn)]
            }
        } else {
            for (int i = 0; i < lstBacklog.size(); i++) {
                PtBacklog ptBacklog = lstBacklog[i]
                lstDropDown << [id: ptBacklog.id, idea: Tools.makeDetailsShort(ptBacklog.idea, 30)]
            }
        }
        return lstDropDown
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstProject - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstBacklog, Map dropDownAttributes) {
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
        String strDefaultValue = defaultValue ? defaultValue : Tools.EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstBacklog = Tools.listForKendoDropdown(lstBacklog, IDEA, hintsText)
        }
        String jsonData = lstBacklog as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'idea',
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
