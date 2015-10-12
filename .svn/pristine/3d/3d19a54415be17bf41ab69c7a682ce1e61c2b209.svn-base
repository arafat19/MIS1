package com.athena.mis.document.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppSysConfigurationCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDocSubCategoriesManagersTaglibActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SUB_CATEGORY_ID = 'sub_category_id'
    private static final String LST_MANAGER = 'lstManager'


    @Autowired(required = false)
    DocumentPluginConnector documentImplService

    @Autowired
    AppSysConfigurationCacheUtility appSysConfigurationCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility

    /**
     * Build a map containing properties of html listView
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map listAttributes = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            listAttributes.put(Tools.IS_ERROR, Boolean.TRUE)  // default value
            String strSubCategoryId = attrs.get(SUB_CATEGORY_ID)
            if ((!strSubCategoryId) || (strSubCategoryId.length() == 0)) {
                return listAttributes
            }

            long subcategoryId = Long.parseLong(strSubCategoryId)

            DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(subcategoryId)
            if (!subCategory) {
                return listAttributes
            }
            //For Managers Count
            List<GroovyRowResult> lstManager = listManager(subcategoryId)
            if (lstManager.size() == 0) {
                return listAttributes
            }
            listAttributes.put(LST_MANAGER, lstManager)   // set subcategoryId
            listAttributes.put(Tools.IS_ERROR, Boolean.FALSE)
            return listAttributes
        } catch (Exception e) {
            log.error(e.getMessage())
            listAttributes.put(Tools.IS_ERROR, Boolean.TRUE)
            return listAttributes
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get the list of Categories
     * build the lstMappedCategories for 'listView'
     * @param parameters - map of given attributes
     * @param obj - N/A
     * @return - lstCat string for 'listView'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            String output
            Map lstAttributes = (Map) parameters
            List lstManager = (List)lstAttributes.get(LST_MANAGER)
            //For Managers Count
            Map managersMap = [lstManager: lstManager as JSON]

            output = buildMyCategoryManagers(managersMap)
            return output
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    private String buildMyCategoryManagers(Map managersMap) {
        // read map values

        String html = """
                <div class="panel panel-primary" id="lstManagerDiv">
        <div class="panel-heading">
            <div class="panel-title">Manager(s)</div>
        </div>

        <div class="panel-body">
            <div class="form-group">
                <div id="lstManager" style="border-style: none"></div>
            </div>
        </div>
    </div> """

        String script = """\n
<script type="text/javascript">
    var lstManagers;
    \$(document).ready(function () {
        \$('#lstManagerDiv').hide();
        lstManagers = ${(managersMap?.lstManager) ? managersMap.lstManager : []};
        if (lstManagers.length > 0) {
            \$('#lstManagerDiv').show();
            initManagerList();
        }
    });

    function initManagerList() {
        \$("#lstManager").kendoListView({
            dataSource: lstManagers,
            template: "<div class='list-group-item'> #:manager#</div>"
        });
    }
</script>
"""

        return html + script
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private static final String MAPPED_MANAGER_LIST = """
                            SELECT Distinct(au.username) manager
                            FROM app_user au
                            LEFT JOIN doc_sub_category_user_mapping cum ON cum.user_id=au.id
                            WHERE cum.company_id=:companyId
                            AND cum.sub_category_id=:subCategoryId
                            AND cum.is_sub_category_manager=true
                    """
    /*
    * Get List of Mapped sub Categories
    * @return lstuser - mapped sub category List
    * */

    private List<GroovyRowResult> listManager(long subCategoryId) {
        AppUser appUser = docSessionUtil.appSessionUtil.getAppUser()
        Map queryParams = [
                companyId    : appUser.companyId,
                subCategoryId: subCategoryId
        ]

        List<GroovyRowResult> lstManager = executeSelectSql(MAPPED_MANAGER_LIST, queryParams)
        return lstManager
    }
}
