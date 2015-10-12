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

/**
 *  Get filtered exchange house for dropDown
 *  For details go through Use-Case doc named 'GetRmsExchangeHouseFilteredDropDownTagLibActionService'
 */
class GetRmsExchangeHouseFilteredDropDownTagLibActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String TASK_STATUS_LIST = 'task_status_list'
    private static final String IS_REVISED = 'is_revised'
    private static final String FROM_DATE = 'from_date'
    private static final String TO_DATE = 'to_date'
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

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map dropDownAttributes = new LinkedHashMap()
            Map attrs = (Map) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

            if ((!attrs.id) || (!attrs.name) || (!attrs.url) || (!attrs.data_model_name) || (!attrs.from_date) || (!attrs.to_date)) {
                return result
            }
            dropDownAttributes.put(NAME, attrs.name)
            dropDownAttributes.put(ID, attrs.id)
            dropDownAttributes.put(URL, attrs.url)
            dropDownAttributes.put(DATA_MODEL_NAME, attrs.data_model_name)
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
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(FILTER_BRANCH, Boolean.FALSE)
            dropDownAttributes.put(TASK_STATUS_LIST, attrs.task_status_list)

            if (attrs.is_revised) {
                boolean ignoreIsRevise = Boolean.parseBoolean(attrs.is_revised)
                dropDownAttributes.put(IS_REVISED, new Boolean(ignoreIsRevise))
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
                boolean filterBranch = Boolean.parseBoolean(attrs.filter_branch)
                dropDownAttributes.put(FILTER_BRANCH, new Boolean(filterBranch))
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

    /** Get the list of rmsExchangeHouse
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        try {
            Map result = (Map) parameters
            Map dropDownAttributes = (Map) result.get(DROP_DOWN_ATTRIBUTES)
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            Boolean isRevised = dropDownAttributes.get(IS_REVISED)
            String fromDate = dropDownAttributes.get(FROM_DATE).toString()
            String toDate = dropDownAttributes.get(TO_DATE).toString()
            Boolean addBalance = dropDownAttributes.get(ADD_BALANCE)
            Boolean filterBranch = dropDownAttributes.get(FILTER_BRANCH)

            long processTypeId = 0
            long instrumentTypeId = 0
            if (dropDownAttributes.get(PROCESS)) {
                long processReservedId = Long.parseLong(dropDownAttributes.get(PROCESS).toString())
                SystemEntity process = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(processReservedId, companyId)
                processTypeId = process.id
                if (dropDownAttributes.get(INSTRUMENT)) {
                    long instrumentReservedId = Long.parseLong(dropDownAttributes.get(INSTRUMENT).toString())
                    SystemEntity instrument = (SystemEntity) rmsInstrumentTypeCacheUtility.readByReservedAndCompany(instrumentReservedId, companyId)
                    instrumentTypeId = instrument.id
                }
            }
            List<Long> lstTaskStatus = Tools.getIdsFromParams(dropDownAttributes, TASK_STATUS_LIST) // separated by '_'
            List<Long> taskStatusList = []
            if (lstTaskStatus && lstTaskStatus.size() > 0) {
                for (int i = 0; i < lstTaskStatus.size(); i++) {
                    SystemEntity taskStatus = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(lstTaskStatus[i], rmsSessionUtil.appSessionUtil.getCompanyId())
                    taskStatusList << taskStatus.id
                }
            }
            long branchId = 0
            if (filterBranch.booleanValue()) {
                boolean hasBranchRole = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_BRANCH_USER)
                if (hasBranchRole.booleanValue()) {
                    BankBranch bankBranch = rmsSessionUtil.appSessionUtil.getUserBankBranch()
                    branchId = bankBranch.id
                }
            }
            List<GroovyRowResult> lstExchangeHouse = (List<GroovyRowResult>) listForDropDown(taskStatusList, isRevised, fromDate, toDate, addBalance, processTypeId, instrumentTypeId, branchId)
            String html = buildDropDown(lstExchangeHouse, dropDownAttributes)
            return html
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    /**
     * Do nothing
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing
     */
    Object buildSuccessResultForUI(Object obj) {
        return null
    }
    /**
     * Do nothing
     */
    Object buildFailureResultForUI(Object obj) {
        return null
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstExchangeHouse - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstExchangeHouse, Map dropDownAttributes) {
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
            lstExchangeHouse = Tools.listForKendoDropdown(lstExchangeHouse, null, hintsText)
        }
        String jsonData = lstExchangeHouse as JSON

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
     * get list of ExchangeHouse for dropdown
     * @return ExchangeHouse list
     */
    private List<GroovyRowResult> listForDropDown(List<Long> lstTaskStatus, boolean isRevised, String fromDate, String toDate, boolean addBalance, long processTypeId, long instrumentTypeId, long branchId) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        String strQueryProIns = Tools.EMPTY_SPACE
        String strQueryCurStatus = Tools.EMPTY_SPACE
        String strBankBranch = Tools.EMPTY_SPACE
        if (processTypeId > 0) {
            strQueryProIns = """
                    AND task.process_type_id = :processTypeId
            """
            if (instrumentTypeId > 0)
                strQueryProIns += """
                    AND task.instrument_type_id = :instrumentTypeId
            """
        }
        if (lstTaskStatus.size() > 0) {
            strQueryCurStatus = """
                    AND task.current_status in (${Tools.buildCommaSeparatedStringOfIds(lstTaskStatus)})
            """
        }
        if (branchId > 0) {
            strBankBranch = """
                AND task.mapping_branch_id=:branchId
            """
        }
        String strQuery = """
            SELECT DISTINCT exh.id, exh.name
            FROM rms_task task
                LEFT JOIN rms_exchange_house exh ON exh.id = task.exchange_house_id
            WHERE task.company_id = :companyId
                AND task.created_on BETWEEN :fromDate AND :toDate
                AND is_revised = :isRevised
                ${strQueryCurStatus}
                ${strQueryProIns}
                ${strBankBranch}
                ORDER BY exh.name
        """
        String QUERY_FOR_BALANCE = """
        SELECT DISTINCT exh.id, exh.name, exh.balance
        FROM rms_task task
            LEFT JOIN rms_exchange_house exh ON exh.id = task.exchange_house_id
        WHERE task.created_on BETWEEN :fromDate AND :toDate
            ${strQueryCurStatus}
            ${strQueryProIns}
            ${strBankBranch}
            AND is_revised = :isRevised
            AND task.company_id = :companyId
        ORDER BY exh.name
        """

        Map queryParams = [
                fromDate: DateUtility.getSqlFromDateWithSeconds(DateUtility.parseMaskedDate(fromDate)),
                toDate: DateUtility.getSqlToDateWithSeconds(DateUtility.parseMaskedDate(toDate)),
                processTypeId: processTypeId,
                instrumentTypeId: instrumentTypeId,
                companyId: companyId,
                isRevised: isRevised,
                branchId:branchId
        ]
        if (addBalance) {
            List<GroovyRowResult> lstTaskList = executeSelectSql(QUERY_FOR_BALANCE, queryParams)
            return lstTaskList
        }
        List<GroovyRowResult> lstTaskList = executeSelectSql(strQuery, queryParams)
        return lstTaskList
    }
}
