package com.athena.mis.arms.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Get exchange house for dropDown
 *  For details go through Use-Case doc named 'GetRmsExchangeHouseDropDownTagLibActionService'
 */
class GetRmsExchangeHouseDropDownTagLibActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String FILTER_MAPPING = 'filter_mapping'
    private static final String ADD_ALL_ATTRIBUTES = 'add_all_attributes'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String ADD_BALANCE = 'add_balance'
    private static final String DROP_DOWN_ATTRIBUTES = 'dropDownAttributes'

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
            String name = attrs.get(NAME)
            String id = attrs.get(ID)
            String dataModelName = attrs.get(DATA_MODEL_NAME)
            if ((!id) || (!name) || (!dataModelName)) {
                return result
            }
            dropDownAttributes.put(NAME, name)
            dropDownAttributes.put(ID, id)
            dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)

            // Set default values for optional parameters
            dropDownAttributes.put(URL, attrs.url)
            dropDownAttributes.put(CLASS, attrs.class)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange)
            dropDownAttributes.put(FILTER_MAPPING, Boolean.TRUE)
            dropDownAttributes.put(ADD_ALL_ATTRIBUTES, Boolean.FALSE)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(REQUIRED, Boolean.FALSE)
            dropDownAttributes.put(ADD_BALANCE, Boolean.FALSE)

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
                dropDownAttributes.put(REQUIRED, REQUIRED)
                dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
                if (attrs.validationmessage) {
                    dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validationmessage)
                }
            }
            if (attrs.filter_mapping) {
                boolean filterMapping = Boolean.parseBoolean(attrs.filter_mapping)
                dropDownAttributes.put(FILTER_MAPPING, new Boolean(filterMapping))
            }
            if (attrs.add_all_attributes) {
                boolean addAllAttributes = Boolean.parseBoolean(attrs.add_all_attributes)
                dropDownAttributes.put(ADD_ALL_ATTRIBUTES, new Boolean(addAllAttributes))
            }
            if (attrs.add_balance) {
                dropDownAttributes.put(ADD_BALANCE, attrs.add_balance)
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
            Boolean addAllAttributes = (Boolean) dropDownAttributes.get(ADD_ALL_ATTRIBUTES)
            Boolean addBalance = dropDownAttributes.get(ADD_BALANCE)
            List<GroovyRowResult> lstExchangeHouse = (List<GroovyRowResult>) listForDropDown(addBalance)
            List lstForDropDown = buildListForDropDown(lstExchangeHouse, addAllAttributes)
            String html = buildDropDown(lstForDropDown, dropDownAttributes)
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

    /**
     * Build list for drop down
     * @param lstExchangeHouse - list of exchange house
     * @param addAllAttributes - boolean value (true/false)
     * @return - list for drop down
     */
    private List buildListForDropDown(List<GroovyRowResult> lstExchangeHouse, boolean addAllAttributes) {
        List lstDropDown = []
        if ((lstExchangeHouse == null) || (lstExchangeHouse.size() <= 0))
            return lstDropDown
        if (addAllAttributes) {
            for (int i = 0; i < lstExchangeHouse.size(); i++) {
                GroovyRowResult exchangeHouse = lstExchangeHouse[i]
                lstDropDown << [id: exchangeHouse.id, name: exchangeHouse.name, code: exchangeHouse.code,
                        countryId: exchangeHouse.country_id, country: exchangeHouse.country, balance: exchangeHouse.balance]
            }
        } else {
            for (int i = 0; i < lstExchangeHouse.size(); i++) {
                GroovyRowResult exchangeHouse = lstExchangeHouse[i]
                lstDropDown << [id: exchangeHouse.id, name: exchangeHouse.name]
            }
        }
        return lstDropDown
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
            if(it.value){
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
    private List<GroovyRowResult> listForDropDown(boolean addBalance) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        String queryStr = """
            SELECT exh.id, exh.name, exh.code, c.id country_id, c.name country, '0' AS balance
            FROM rms_exchange_house exh
            LEFT JOIN country c ON c.id = exh.country_id
            WHERE exh.company_id = :companyId
            ORDER BY exh.name
        """


        String queryForBalance = """
            SELECT exh.id, exh.name, exh.code, c.id country_id, c.name country, balance
            FROM rms_exchange_house exh
            LEFT JOIN country c ON c.id = exh.country_id
            WHERE exh.company_id = :companyId
            ORDER BY exh.name
        """

        Map queryParams = [
                companyId: companyId
        ]

        if (addBalance) {
            List<GroovyRowResult> lstExchangeHouse = executeSelectSql(queryForBalance, queryParams)
            return lstExchangeHouse
        }
        List<GroovyRowResult> lstExchangeHouse = executeSelectSql(queryStr, queryParams)
        return lstExchangeHouse
    }
}
