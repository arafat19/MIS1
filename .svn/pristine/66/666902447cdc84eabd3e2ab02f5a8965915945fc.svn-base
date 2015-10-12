package com.athena.mis.fixedasset.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.utility.FxdMaintenanceTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders html of 'select' for FxdMaintenanceType objects */

class GetDropDownFxdMaintenanceTypeTagLibActionService extends BaseService implements ActionIntf {

    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility

    private static final String ID = 'id'
    private static final String URL = 'url'
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
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String ITEM_ID = 'item_id'

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

            String id = attrs.get(ID)
            String url = attrs.get(URL)
            String name = attrs.get(NAME)
            String dataModelName = attrs.get(DATA_MODEL_NAME)
            String strItemId = attrs.get(ITEM_ID)
            if ((!name) || (!dataModelName) || (strItemId.length() == 0)) {
                return dropDownAttributes
            }
            long itemId = Long.parseLong(strItemId)
            dropDownAttributes.put(ID, id)                                  // set id
            dropDownAttributes.put(NAME, name)                              // set name
            dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)          // set dataModelName
            dropDownAttributes.put(URL, url)                                // set url
            dropDownAttributes.put(ITEM_ID, new Long(itemId))               // set the entityId

            // Set default values for optional parameters
            dropDownAttributes.put(CLASS, attrs.class)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
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
                dropDownAttributes.put(REQUIRED, REQUIRED)
                dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
                if (attrs.validation_message) {
                    dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validation_message)
                }
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

    /** Get the list of FxdMaintenanceTypes
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map dropDownAttributes = (Map) parameters
            Long itemId = (Long) dropDownAttributes.get(ITEM_ID)
            List<GroovyRowResult> lstMaintenanceType = (List<GroovyRowResult>) listMaintenanceType(itemId)
            String html = buildDropDown(lstMaintenanceType, dropDownAttributes)
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
     * @param lstMaintenanceType - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<GroovyRowResult> lstMaintenanceType, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
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

        String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : Tools.EMPTY_SPACE
        String strDefaultValue = defaultValue ? defaultValue : Tools.EMPTY_SPACE

        if (defaultValue) {
            FxdMaintenanceType maintenanceType =(FxdMaintenanceType) fxdMaintenanceTypeCacheUtility.read(defaultValue)
            lstMaintenanceType << [id: maintenanceType.id, name: maintenanceType.name]
            strDefaultValue = defaultValue
        }

        if (showHints.booleanValue()) {
            lstMaintenanceType = Tools.listForKendoDropdown(lstMaintenanceType, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String jsonData = lstMaintenanceType as JSON

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
                            value:'${strDefaultValue}',
                            ${strOnChange}
                        });
                        ${dataModelName} = \$("#${name}").data("kendoDropDownList");
                });
            </script>
        """
        return html + script
    }


    private static final String QUERY_FOR_LIST = """
                SELECT id, name
                FROM fxd_maintenance_type
                WHERE company_id =:companyId
                AND id NOT IN
                (
                    SELECT maintenance_type_id FROM fxd_category_maintenance_type
                    WHERE item_id =:itemId
                    AND company_id =:companyId
                )
        """

    private List<GroovyRowResult> listMaintenanceType(long itemId) {
        Map queryParams = [
                itemId: itemId,
                companyId: fxdSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstAppUser = executeSelectSql(QUERY_FOR_LIST, queryParams)
        return lstAppUser
    }
}
