package com.athena.mis.arms.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
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

class GetRmsTaskListDropDownTagLibActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String TASK_STATUS_LIST = 'task_status_list'
    private static final String IS_REVISED = 'is_revised'
    private static final String FROM_DATE = 'from_date'
    private static final String TO_DATE = 'to_date'
    private static final String EXCHANGE_HOUSE_ID = 'exchange_house_id'
    private static final String PROCESS = 'process'
    private static final String INSTRUMENT = 'instrument'
    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String ADD_BALANCE = 'add_balance'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DROP_DOWN_ATTRIBUTES = 'dropDownAttributes'
    private static final String FILTER_BRANCH = 'filter_branch'

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
            dropDownAttributes.put(EXCHANGE_HOUSE_ID, attrs.exchange_house_id)
            dropDownAttributes.put(FROM_DATE, attrs.from_date)
            dropDownAttributes.put(TO_DATE, attrs.to_date)
            dropDownAttributes.put(IS_REVISED, Boolean.FALSE)
            dropDownAttributes.put(PROCESS, attrs.process)
            dropDownAttributes.put(INSTRUMENT, attrs.instrument)
            dropDownAttributes.put(CLASS, attrs.class)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange)
            dropDownAttributes.put(ADD_BALANCE, Boolean.FALSE)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(FILTER_BRANCH, Boolean.FALSE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(TASK_STATUS_LIST, attrs.task_status_list)

            if (attrs.is_revised) {
                boolean isRevised = Boolean.parseBoolean(attrs.is_revised)
                dropDownAttributes.put(IS_REVISED, new Boolean(isRevised))
            }
            if (attrs.add_balance != null) {
                dropDownAttributes.put(ADD_BALANCE, attrs.add_balance)
            }
            if (attrs.hints_text) {
                dropDownAttributes.put(HINTS_TEXT, attrs.hints_text)
            }
            if (attrs.show_hints) {
                boolean showHints = Boolean.parseBoolean(attrs.show_hints)
                dropDownAttributes.put(SHOW_HINTS, new Boolean(showHints))
            }
            if (attrs.filter_branch) {
                boolean showHints = Boolean.parseBoolean(attrs.filter_branch)
                dropDownAttributes.put(FILTER_BRANCH, new Boolean(showHints))
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
            List lstTaskList = listForDropDown(dropDownAttributes)
            String html = buildDropDown(lstTaskList, dropDownAttributes)
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
     * @param lstTaskList - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstTaskList, Map dropDownAttributes) {
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
            lstTaskList = Tools.listForKendoDropdown(lstTaskList, null, hintsText)
        }
        String jsonData = lstTaskList as JSON

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
     * get list of taskList for dropdown
     * @return taskList list
     */
    private List<GroovyRowResult> listForDropDown(Map dropDownAttr) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        if (!dropDownAttr.get(EXCHANGE_HOUSE_ID)) {
            return []
        }
        long exchangeHouseId = Tools.parseLongInput(dropDownAttr.get(EXCHANGE_HOUSE_ID).toString())
        String strFromDate = (String) dropDownAttr.get(FROM_DATE)
        String strToDate = (String) dropDownAttr.get(TO_DATE)
        Date fromDate = DateUtility.parseMaskedFromDate(strFromDate)
        Date toDate = DateUtility.parseMaskedToDate(strToDate)
        boolean isRevised = (boolean) dropDownAttr.get(IS_REVISED)
        Boolean isFilterBranch = (Boolean) dropDownAttr.get(FILTER_BRANCH)
        long processTypeId = Tools.parseLongInput(dropDownAttr.get(PROCESS).toString())
        long instrumentTypeId = Tools.parseLongInput(dropDownAttr.get(INSTRUMENT).toString())
        String strStatus
        if (dropDownAttr.get(TASK_STATUS_LIST)) {
            List<Long> lstTaskStatus = []
            List<Long> lst = Tools.getIdsFromParams(dropDownAttr, TASK_STATUS_LIST) // separated by '_'
            for (int i = 0; i < lst.size(); i++) {
                SystemEntity sysEn = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(lst[i], companyId)
                lstTaskStatus << sysEn.id
            }
            strStatus = Tools.PARENTHESIS_START + Tools.buildCommaSeparatedStringOfIds(lstTaskStatus) + Tools.PARENTHESIS_END
        }
        long branchId = 0L
        if (isFilterBranch.booleanValue()) {
            boolean hasBranchRole = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_BRANCH_USER)
            if (hasBranchRole.booleanValue()) {
                BankBranch branch = rmsSessionUtil.appSessionUtil.getUserBankBranch()
                branchId = branch.id
            }
        }
        String strQueryProIns = Tools.EMPTY_SPACE
        String strQueryCurStatus = Tools.EMPTY_SPACE
        String strBankBranch = Tools.EMPTY_SPACE
        if (branchId > 0) {
            strBankBranch = """
                AND task.mapping_branch_id=:branchId
            """
        }
        if (processTypeId > 0) {
            processTypeId = rmsProcessTypeCacheUtility.readByReservedAndCompany(processTypeId, companyId).id
            strQueryProIns = """
                AND task.process_type_id = :processTypeId
            """
            if (instrumentTypeId > 0) {
                instrumentTypeId = rmsInstrumentTypeCacheUtility.readByReservedAndCompany(instrumentTypeId, companyId).id
                strQueryProIns += """
                    AND task.instrument_type_id = :instrumentTypeId
                """
            }
        }
        if (strStatus) {
            strQueryCurStatus = """
                AND task.current_status in ${strStatus}
            """
        }
        String queryStr = """
            SELECT DISTINCT list.id, list.name
            FROM rms_task task
                LEFT JOIN rms_task_list list ON list.id = task.task_list_id
            WHERE task.company_id = :companyId
                AND task.exchange_house_id = :exchangeHouseId
                AND task.created_on BETWEEN :fromDate AND :toDate
                AND task_list_id > 0
                AND is_revised = :isRevised
                ${strQueryCurStatus}
                ${strQueryProIns}
                ${strBankBranch}
            ORDER BY list.id
        """
        Map queryParams = [
                companyId: companyId,
                exchangeHouseId: exchangeHouseId,
                fromDate: DateUtility.getSqlFromDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlToDateWithSeconds(toDate),
                isRevised: isRevised,
                processTypeId: processTypeId,
                instrumentTypeId: instrumentTypeId,
                branchId: branchId
        ]
        List<GroovyRowResult> lstTaskList = executeSelectSql(queryStr, queryParams)
        return lstTaskList
    }
}
