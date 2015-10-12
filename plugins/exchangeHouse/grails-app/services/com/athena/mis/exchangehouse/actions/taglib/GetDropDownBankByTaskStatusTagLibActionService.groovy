package com.athena.mis.exchangehouse.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.exchangehouse.utility.ExhTaskTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired


class GetDropDownBankByTaskStatusTagLibActionService extends BaseService implements ActionIntf{

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility

    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String TAB_INDEX = 'tabindex'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String FROM_DATE = 'from_date'
    private static final String TO_DATE = 'to_date'
    private static final String TASK_STATUS = 'task_status'
    private static final String TASK_TYPE = 'task_type'
    private static final String URL = 'url'
    private static final String SELECT_END = "</select>"

    private Logger log = Logger.getLogger(getClass())


    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Object executePreCondition(Object parameters, Object obj){
        Map dropDownAttributes = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            dropDownAttributes.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

            String name = attrs.get(NAME)
            String id = attrs.get(ID)
            String dataModelName = attrs.get(DATA_MODEL_NAME)
            if ((!id) || (!name) || (!dataModelName)) {
                return dropDownAttributes
            }
            dropDownAttributes.put(NAME, name)   // set name
            dropDownAttributes.put(ID, id)   // set name
            dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)   // set dataModelName
            dropDownAttributes.put(TASK_TYPE, attrs.task_type)   // set dataModelName

            // Set default values for optional parameters
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex ? attrs.tabindex : null)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(REQUIRED, Boolean.FALSE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)

            if (attrs.hints_text) {
                dropDownAttributes.put(HINTS_TEXT, attrs.hints_text)
            }
            if (attrs.show_hints) {
                boolean showHints = Boolean.parseBoolean(attrs.show_hints)
                dropDownAttributes.put(SHOW_HINTS, new Boolean(showHints))
            }
            if (attrs.required) {
                boolean required = Boolean.parseBoolean(attrs.required)
                dropDownAttributes.put(REQUIRED, new Boolean(required))
            }
            if (attrs.default_value) {
                String strDefaultValue = attrs.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                dropDownAttributes.put(DEFAULT_VALUE, new Long(defaultValue))
            }
            if (attrs.validationmessage) {
                dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validationmessage)
            }
            if (attrs.from_date) {
                dropDownAttributes.put(FROM_DATE, attrs.from_date)
            }
            if (attrs.to_date) {
                dropDownAttributes.put(TO_DATE, attrs.to_date)
            }
            if (attrs.task_status) {
                dropDownAttributes.put(TASK_STATUS, attrs.task_status)
            }
            if (attrs.url) {
                dropDownAttributes.put(URL, attrs.url)
            }
            dropDownAttributes.put(Tools.IS_ERROR, Boolean.FALSE)
            return dropDownAttributes
        } catch (Exception e) {
            log.error(e.getMessage())
            dropDownAttributes.put(Tools.IS_ERROR, Boolean.TRUE)
            return dropDownAttributes
        }
    }

    /**
     * Do nothing for executePostCondition
     */
    public Object executePostCondition(Object parameters, Object obj){
        return null
    }
    /** Get the list of bank
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    @Transactional(readOnly = true)
    public  Object execute(Object parameters, Object obj){
        try {
            Map dropDownAttributes = (Map) parameters
            List<GroovyRowResult> lstDropDown= lstForDropDown(dropDownAttributes)
            List lstBanks= buildListForDropDown(lstDropDown)
            String html = buildDropDown(lstBanks, dropDownAttributes)
            return html
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }
    /**
     * Do nothing for executePostCondition
     */

    public  Object buildSuccessResultForUI(Object obj){
        return null
    }

    /**
     * Do nothing for executePostCondition
     */
    public  Object buildFailureResultForUI(Object obj){
        return null
    }
    /**
     * Get list of bank for drop down
     * @param dropDownAttributes-returned from precondition
     * @return-list of bank
     */
    private List<GroovyRowResult> lstForDropDown(Map dropDownAttributes){
        String strDate=Tools.EMPTY_SPACE
        if(!dropDownAttributes.get(TASK_STATUS)){
            return []
        }
        Date fromDate=null
        Date toDate=null
        long taskStatus=Tools.parseLongInput(dropDownAttributes.get(TASK_STATUS).toString())
        long taskType=Tools.parseLongInput(dropDownAttributes.get(TASK_TYPE).toString())
        SystemEntity taskStatusEntity= (SystemEntity)exhTaskStatusCacheUtility.readByReservedAndCompany(taskStatus, exhSessionUtil.appSessionUtil.getCompanyId())
        SystemEntity taskTypeEntity= (SystemEntity)exhTaskTypeCacheUtility.readByReservedAndCompany(taskType, exhSessionUtil.appSessionUtil.getCompanyId())
        if(dropDownAttributes.get(FROM_DATE) || dropDownAttributes.get(TO_DATE)){
            fromDate=DateUtility.parseMaskedFromDate(dropDownAttributes.get(FROM_DATE).toString())
            toDate=DateUtility.parseMaskedToDate(dropDownAttributes.get(TO_DATE).toString())
            strDate="""
                AND t.created_on BETWEEN :fromDate AND :toDate
            """
        }
        //task_type is empty space for other bank user
        //other bank user will see all bank list except system bank
        if(!dropDownAttributes.get(TASK_TYPE)){
            long bankId=bankCacheUtility.getSystemBank().id
            String strQueryOtherBank="""
                SELECT DISTINCT b.name, b.id FROM exh_task t
                LEFT JOIN bank b ON t.outlet_bank_id=b.id
                WHERE current_status=:taskStatus
                ${strDate}
                AND t.company_id=:companyId
                AND t.outlet_bank_id <> :bankId
        """
            Map queryParamsOtherBank=[
                    taskStatus:taskStatusEntity.id,
                    fromDate:DateUtility.getSqlFromDateWithSeconds(fromDate),
                    toDate:DateUtility.getSqlToDateWithSeconds(toDate),
                    companyId:exhSessionUtil.appSessionUtil.getCompanyId(),
                    bankId:bankId
            ]
            List<GroovyRowResult> lstResultOtherBank= executeSelectSql(strQueryOtherBank,queryParamsOtherBank)
            return lstResultOtherBank
        }
        String strQuery="""
            SELECT DISTINCT b.name, b.id FROM exh_task t
            LEFT JOIN bank b ON t.outlet_bank_id=b.id
            WHERE current_status=:taskStatus
            AND task_type_id=:taskType
            ${strDate}
            AND t.company_id=:companyId
        """
        Map queryParams=[
                taskStatus:taskStatusEntity.id,
                taskType:taskTypeEntity.id,
                fromDate:DateUtility.getSqlFromDateWithSeconds(fromDate),
                toDate:DateUtility.getSqlToDateWithSeconds(toDate),
                companyId:exhSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstResult= executeSelectSql(strQuery,queryParams)
        return lstResult
    }
    /** Generate the html for select
     * @param lstBanks - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<GroovyRowResult> lstBanks, Map dropDownAttributes) {
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
            lstBanks = Tools.listForKendoDropdown(lstBanks, null, hintsText)
        }
        String jsonData = lstBanks as JSON

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
     * build dropDownList with id and name
     * @param lstBank-lst of banks
     * @return-lstDropDown
     */
    private List buildListForDropDown(List<GroovyRowResult> lstBank){
        List lstDropDown = []
        if((!lstBank)||(lstBank.size()<=0)){
            return lstDropDown
        }
        for (int i = 0; i < lstBank.size(); i++) {
            GroovyRowResult eachRow = (GroovyRowResult)lstBank[i]
            lstDropDown << [id: eachRow.id, name: eachRow.name]
        }
        return lstDropDown
    }
}
