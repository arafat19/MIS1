package com.athena.mis.arms.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsProcessInstrumentMappingCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetRmsInstrumentDropDownTagLibActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsProcessInstrumentMappingCacheUtility rmsProcessInstrumentMappingCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String KEY = 'key'
    private static final String URL = 'url'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String DROP_DOWN_ATTRIBUTES = 'dropDownAttributes'
    private static final String PROCESS_TYPE_ID = 'processTypeId'
    private static final String SELECT_END = "</select>"

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Object executePreCondition(Object parameters, Object obj) {
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
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(REQUIRED, Boolean.FALSE)
            dropDownAttributes.put(PROCESS_TYPE_ID, attrs.process_type_id)

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
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(DROP_DOWN_ATTRIBUTES, dropDownAttributes)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }
    /**
     * Do nothing for executePostCondition
     */

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /** Get the list of rmsExchangeHouse
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            Map result = (Map) parameters
            Map dropDownAttributes = (Map) result.get(DROP_DOWN_ATTRIBUTES)
            List<SystemEntity> lstInstrument = (List<SystemEntity>) listForDropDown(dropDownAttributes)
            List lstForDropDown = buildListForDropDown(lstInstrument)
            String html = buildDropDown(lstForDropDown, dropDownAttributes)
            return html
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }
    /**
     * Do nothing for buildSuccessResultForUI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }
    /**
     * Do nothing for buildFailureResultForUI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * get dropDownAttributes
     * @return lstInstrument
     */
    private List<SystemEntity> listForDropDown(Map dropDownAttributes) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        if (!dropDownAttributes.get(PROCESS_TYPE_ID)) {
            return []
        }

        long processTypeId = Tools.parseLongInput(dropDownAttributes.get(PROCESS_TYPE_ID).toString())
        SystemEntity process = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(processTypeId, companyId)

        List<Long> lstInstrumentIds = rmsProcessInstrumentMappingCacheUtility.findByProcessTypeId(process.id, companyId)
        List<SystemEntity> lstInstrument = []
        for (int i = 0; i < lstInstrumentIds.size(); i++) {
            long id = lstInstrumentIds[i]
            SystemEntity instrument = (SystemEntity) rmsInstrumentTypeCacheUtility.read(id)
            lstInstrument << instrument
        }
        return lstInstrument
    }

    /**
     * Build list for drop down
     * @param lstInstrument - list of instrument
     * @param addAllAttributes - boolean value (true/false)
     * @return - list for drop down
     */
    private List buildListForDropDown(List lstInstrument) {
        List lstDropDown = []
        if ((lstInstrument == null) || (lstInstrument.size() <= 0)) {
            return lstDropDown
        }
        for (int i = 0; i < lstInstrument.size(); i++) {
            SystemEntity instrument = (SystemEntity) lstInstrument[i]
            lstDropDown << instrument
        }
        return lstDropDown
    }

    /** Generate the html for select
     * @param lstInstrument - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<SystemEntity> lstInstrument, Map dropDownAttributes) {
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
            lstInstrument = Tools.listForKendoDropdown(lstInstrument, KEY, hintsText)
        }
        String jsonData = lstInstrument as JSON

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    if (${dataModelName}){
                        ${dataModelName}.destroy();
                    }
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'key',
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
}
