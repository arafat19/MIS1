package com.athena.mis.document.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders html of 'select' for AppUser objects by category id*/

class GetDropDownAppUserDocCategoryTagLibActionService extends BaseService implements ActionIntf {

    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String CATEGORY_ID = 'category_id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'

    private Logger log = Logger.getLogger(getClass())


    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

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

            String strCategoryId = attrs.get(CATEGORY_ID)
            String name = attrs.get(NAME)
            String id = attrs.get(ID)
            String url = attrs.get(URL)
            String dataModelName = attrs.get(DATA_MODEL_NAME)
            if ((!id) || (!name) || (!dataModelName) || (!strCategoryId) || (strCategoryId.length() == 0) || (!url) || (url.length() == 0)) {
                return dropDownAttributes
            }
            long categoryId = Long.parseLong(strCategoryId)
            dropDownAttributes.put(CATEGORY_ID, new Long(categoryId))         // set the categoryId
            dropDownAttributes.put(NAME, name)                              // set name
            dropDownAttributes.put(ID, id)                                  // set id
            dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)          // set dataModelName
            dropDownAttributes.put(URL, url)                                // set url

            // Set default values for optional parameters
            dropDownAttributes.put(CLASS, attrs.class ? attrs.class : null)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex ? attrs.tabindex : null)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange ? attrs.onchange : null)
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
                if (attrs.validationmessage) {
                    dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validationmessage)
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

    /** Get the list of AppUser objects by categoryId
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map dropDownAttributes = (Map) parameters
            Long categoryId = (Long) dropDownAttributes.get(CATEGORY_ID)
            List<GroovyRowResult> lstAppUser = (List<GroovyRowResult>) listAppUser(categoryId)
            String html = buildDropDown(lstAppUser, dropDownAttributes)
            return html
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * get list of User by categoryId
     * @param categoryId - categoryId
     * @return lstAppUser - List of User
     */
    private List<GroovyRowResult> listAppUser(long categoryId) {
        long companyId = docSessionUtil.appSessionUtil.getCompanyId()
        String queryForList = """
            SELECT id, username || ' (' || id || ')' as name
            FROM app_user
            WHERE company_id = :companyId
            AND enabled = true
            AND id NOT IN
                (
                    SELECT user_id
                    FROM doc_category_user_mapping
                    WHERE category_id = :categoryId
                    AND company_id = :companyId
                )
            ORDER BY name
        """
        Map queryParams = [categoryId: categoryId,companyId: companyId]
        List<GroovyRowResult> lstAppUser = executeSelectSql(queryForList, queryParams)
        return lstAppUser
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     *  1.set attributes to strAttributes
     *  2.set value for refresh dropdown in strDefaultValue
     *  3.Build list for kendo dropdown
     * @param lstAppUser - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<GroovyRowResult> lstAppUser, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        String strDefaultValue = defaultValue ? defaultValue : Tools.EMPTY_SPACE

        Map attributes = (Map) dropDownAttributes
        String strAttributes = Tools.EMPTY_SPACE
        attributes.each {
            if (it.value) {
                strAttributes = strAttributes + "${it.key} = '${it.value}' "
            }
        }

        if (defaultValue) {
            AppUser user = (AppUser) appUserCacheUtility.read(defaultValue)
            lstAppUser << [id: user.id, name: user.username]
            strDefaultValue = defaultValue
        }

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : Tools.EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstAppUser = Tools.listForKendoDropdown(lstAppUser, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String jsonData = lstAppUser as JSON

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
                });
                ${dataModelName} = \$("#${name}").data("kendoDropDownList");
            </script>
        """
        return html + script
    }
}
