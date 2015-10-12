package com.athena.mis.arms.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetBankListDropDownTagLibActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String TASK_STATUS_LIST = 'task_status_list'
    private static final String TASK_LIST_ID = 'task_list_id'
    private static final String FROM_DATE = 'from_date'
    private static final String TO_DATE = 'to_date'
    private static final String PROCESS = 'process'
    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DROP_DOWN_ATTRIBUTES = 'dropDownAttributes'

    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map dropDownAttributes = new LinkedHashMap()
            Map attrs = (Map) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

            if ((!attrs.id) || (!attrs.name) || (!attrs.url) || (!attrs.data_model_name)) {
                return result
            }
            dropDownAttributes.put(NAME, attrs.name)
            dropDownAttributes.put(ID, attrs.id)
            dropDownAttributes.put(URL, attrs.url)
            dropDownAttributes.put(DATA_MODEL_NAME, attrs.data_model_name)
            dropDownAttributes.put(FROM_DATE, attrs.from_date)
            dropDownAttributes.put(TO_DATE, attrs.to_date)
            dropDownAttributes.put(PROCESS, attrs.process)
            dropDownAttributes.put(CLASS, attrs.class)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(TASK_STATUS_LIST, attrs.task_status_list)
            dropDownAttributes.put(TASK_LIST_ID, attrs.task_list_id)

            if (attrs.hints_text) {
                dropDownAttributes.put(HINTS_TEXT, attrs.hints_text)
            }
            if (attrs.show_hints) {
                boolean showHints = Boolean.parseBoolean(attrs.show_hints)
                dropDownAttributes.put(SHOW_HINTS, new Boolean(showHints))
            }
            if (attrs.required) {
                dropDownAttributes.put(REQUIRED, REQUIRED)
                dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
                if (attrs.validationmessage) {
                    dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validationmessage)
                }
            }
            if (attrs.default_value) {
                String strDefaultValue = attrs.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                dropDownAttributes.put(DEFAULT_VALUE, new Long(defaultValue))
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(DROP_DOWN_ATTRIBUTES, dropDownAttributes)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            Map result = (Map) parameters
            Map dropDownAttributes = (Map) result.get(DROP_DOWN_ATTRIBUTES)
            List lstBank = listForDropDown(dropDownAttributes)
            String html = buildDropDown(lstBank, dropDownAttributes)
            return html
        }
        catch (Exception e) {
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
     * @param lstBank - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstBank, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)

        Map attributes = (Map) dropDownAttributes
        String strAttributes = Tools.EMPTY_SPACE
        attributes.each {
            if (it.value) {
                strAttributes = strAttributes + "${it.key} = '${it.value}' "
            }
        }
        String strDefaultValue = defaultValue ? defaultValue : ''
        if (showHints.booleanValue()) {
            lstBank = Tools.listForKendoDropdown(lstBank, null, hintsText)
        }
        String jsonData = lstBank as JSON

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    if (${dataModelName}){
                        ${dataModelName}.destroy();
                    }
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'name',
                        dataValueField:'id',
                        dataSource:${jsonData},
                        value:'${strDefaultValue}'
                    });
                });
                ${dataModelName} = \$("#${name}").data("kendoDropDownList");
            </script>
        """

        return html + script
    }

    /**
     * get list of bank for dropdown
     * @return bank list
     */
    private List<GroovyRowResult> listForDropDown(Map dropDownAttr) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        long processTypeId = Tools.parseLongInput(dropDownAttr.get(PROCESS).toString())
        long taskListId = Tools.parseLongInput(dropDownAttr.get(TASK_LIST_ID).toString())
        if (processTypeId <= 0 || taskListId <= 0) {
            return []
        }
        SystemEntity processObj = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(processTypeId, companyId)
        String strFromDate = (String) dropDownAttr.get(FROM_DATE)
        String strToDate = (String) dropDownAttr.get(TO_DATE)
        Date fromDate = DateUtility.parseMaskedFromDate(strFromDate)
        Date toDate = DateUtility.parseMaskedToDate(strToDate)
        String strStatus = Tools.EMPTY_SPACE
        if (dropDownAttr.get(TASK_STATUS_LIST)) {
            List<Long> lstTaskStatus = []
            List<Long> lst = Tools.getIdsFromParams(dropDownAttr, TASK_STATUS_LIST)
            for (int i = 0; i < lst.size(); i++) {
                SystemEntity sysEn = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(lst[i], companyId)
                lstTaskStatus << sysEn.id
            }
            strStatus = Tools.PARENTHESIS_START + Tools.buildCommaSeparatedStringOfIds(lstTaskStatus) + Tools.PARENTHESIS_END
        }
        String query = """
        SELECT bank.id, bank.name FROM
        bank where bank.id IN
        (SELECT mapping_bank_id FROM rms_task WHERE task_list_id=:taskListId AND process_type_id=:processTypeId
        AND current_status IN ${strStatus} AND company_id=:companyId AND created_on BETWEEN :fromDate AND :toDate)
        """

        Map queryParams = [
                companyId: companyId,
                fromDate: DateUtility.getSqlFromDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlToDateWithSeconds(toDate),
                taskListId: taskListId,
                processTypeId: processObj.id
        ]
        List<GroovyRowResult> lstBank = executeSelectSql(query, queryParams)
        return lstBank
    }
}
