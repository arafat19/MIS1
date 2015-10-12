package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders html of 'select' for ItemType objects */
class GetDropDownItemTypeTagLibActionService extends BaseService implements ActionIntf {

    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String CATEGORY_ID = 'categoryId'
    private static final String HINTS_TEXT = 'hintsText'
    private static final String SHOW_HINTS = 'showHints'
    private static final String DEFAULT_VALUE = 'defaultValue'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'dataModelName'

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

            String strCategoryId = attrs.get(CATEGORY_ID)
            if (strCategoryId) {
                long systemEntityId = Long.parseLong(strCategoryId)
                dropDownAttributes.put(CATEGORY_ID, new Long(systemEntityId)) // set the categoryId
            } else {
                dropDownAttributes.put(CATEGORY_ID, null)
            }

            // Set default values for optional parameters
            dropDownAttributes.put(CLASS, attrs.class ? attrs.class : null)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex ? attrs.tabindex : null)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange ? attrs.onchange : null)
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

    /** Get the list of ItemType objects by type
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map dropDownAttributes = (Map) parameters
            Long systemEntityId = 0L
            List<ItemType> lstItemType = []
            if (dropDownAttributes.get(CATEGORY_ID)) {
                systemEntityId = (Long) dropDownAttributes.get(CATEGORY_ID)
                SystemEntity itemSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(systemEntityId, appSessionUtil.getCompanyId())
                lstItemType = listItemType(itemSysEntityObject.id)
            }else{
                lstItemType = listItemType(systemEntityId)
            }
            String html = buildDropDown(lstItemType, dropDownAttributes)
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

    /** Get the list from ItemType domain
     * @param systemEntityId - id of systemEntity
     * @return - list of ItemType
     */
    private List<ItemType> listItemType(long systemEntityId) {
        List<ItemType> lstItemType
        if (systemEntityId > 0) {
            lstItemType = itemTypeCacheUtility.listByCategoryId(systemEntityId)
        } else {
            lstItemType = itemTypeCacheUtility.list()
        }
        return lstItemType
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstItemType - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<ItemType> lstItemType, Map dropDownAttributes) {
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
            lstItemType = Tools.listForKendoDropdown(lstItemType, null, hintsText)  // the null parameter indicates that dataTextField is name
        }
        String jsonData = lstItemType as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'name',
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
