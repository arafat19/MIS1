package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownBankTagLibActionService extends BaseService implements ActionIntf {

    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    private static final String ID = 'id'
    private static final String NAME = 'name'
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
    private static final String URL = 'url'
    private static final String PROCESS = 'process'
    private static final String INSTRUMENT = 'instrument'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String SELECT_END = "</select>"

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
            String id = attrs.get(ID)
            String dataModelName = attrs.get(DATA_MODEL_NAME)
            if ((!id) || (!dataModelName)) {
                return dropDownAttributes
            }
            dropDownAttributes.put(NAME, name)   // set name
            dropDownAttributes.put(ID, id)      // set id
            dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)   // set dataModelName

            // Set default values for optional parameters
            dropDownAttributes.put(CLASS, attrs.class ? attrs.class : null)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex ? attrs.tabindex : null)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange ? attrs.onchange : null)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(PROCESS, null)
            dropDownAttributes.put(INSTRUMENT, null)
            dropDownAttributes.put(REQUIRED, Boolean.FALSE)
            dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)

            if (attrs.hints_text) {
                dropDownAttributes.put(HINTS_TEXT, attrs.hints_text)
            }
            if (attrs.show_hints) {
                boolean showHints = Boolean.parseBoolean(attrs.show_hints)
                dropDownAttributes.put(SHOW_HINTS, new Boolean(showHints))
            }
            if (attrs.default_value) {
                String strDefaultValue = attrs.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                dropDownAttributes.put(DEFAULT_VALUE, new Long(defaultValue))
            }
            if (attrs.required) {
                boolean required = Boolean.parseBoolean(attrs.required)
                dropDownAttributes.put(REQUIRED, new Boolean(required))
            }
            if (attrs.validationmessage) {
                dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validationmessage)
            }
            if (attrs.url) {
                dropDownAttributes.put(URL, attrs.url)
            }
            if (attrs.process) {
                long processId = Tools.parseLongInput(attrs.process)
                dropDownAttributes.put(PROCESS, processId.longValue())
            }
            if (attrs.instrument) {
                long instrumentId = Tools.parseLongInput(attrs.instrument)
                dropDownAttributes.put(INSTRUMENT, instrumentId.longValue())
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

    /** Get the list of Bank
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            Map dropDownAttributes = (Map) parameters
            List<Bank> bankList = getBankListForDropDown(dropDownAttributes)
            String html = buildDropDown(bankList, dropDownAttributes)
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

    private List<Bank> getBankListForDropDown(Map dropDownAttributes){
        Long reservedProcessId = (Long) dropDownAttributes.get(PROCESS)
        Long reservedInstrumentId = (Long) dropDownAttributes.get(INSTRUMENT)
        List<Bank> bankList = []
        if (reservedProcessId && reservedProcessId.longValue() > 0) {
            Long reservedIssueId = (Long) armsImplService.getProcessTypeIssue()
            Long reservedForwardId = (Long) armsImplService.getProcessTypeForward()
            Long reservedPurchaseId = (Long) armsImplService.getProcessTypePurchase()
            long instrumentId = 0
            if (reservedInstrumentId && reservedInstrumentId.longValue() > 0) {
                SystemEntity instrumentObj = (SystemEntity) armsImplService.readByReservedInstrumentType(reservedInstrumentId.longValue(), appSessionUtil.getCompanyId())
                instrumentId = instrumentObj.id
            }
            switch (reservedProcessId) {
                case reservedIssueId:
                    bankList << bankCacheUtility.getSystemBank()
                    break
                case reservedForwardId:
                    bankList = bankCacheUtility.list()
                    break
                case reservedPurchaseId:
                    bankList = listBankForPurchase(instrumentId)
                    break
                default:
                    break
            }
        } else {
            bankList = bankCacheUtility.list()
        }
        return bankList
    }

    /** Generate the html for select
     * @param lstBank - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<Bank> lstBank, Map dropDownAttributes) {
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

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strDefaultValue = defaultValue ? defaultValue : Tools.EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstBank = Tools.listForKendoDropdown(lstBank, null, hintsText)
        }
        String jsonData = lstBank as JSON

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

    private List<Bank> listBankForPurchase(long instrumentId) {
        List<Bank> lstBank = []
        if (instrumentId <= 0) {
            return lstBank
        }
        String sql = """
        select distinct(bank_id) as bank_id from rms_purchase_instrument_mapping
        where instrument_type_id = :instrumentId
        """
        Map queryParam = [instrumentId: instrumentId]
        List<GroovyRowResult> lstResult = executeSelectSql(sql, queryParam)
        for (int i = 0; i < lstResult.size(); i++) {
            GroovyRowResult eachRow = lstResult[i]
            Bank bank = (Bank) bankCacheUtility.read(eachRow.bank_id)
            if (bank) lstBank << bank
        }
        return lstBank
    }

}
