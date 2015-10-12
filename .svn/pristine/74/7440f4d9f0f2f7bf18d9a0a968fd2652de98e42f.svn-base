package com.athena.mis.document.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppSysConfigurationCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired


class DocCategoriesManagersTaglibActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String CATEGORY_ID = 'category_id'
    private static final String LST_MANAGER = 'lstManager'
    private static final String SYS_CONFIGURATION = 'sysConfiguration'
    private static final String BY = ' By '
    private static final String URL = 'url'


    @Autowired(required = false)
    DocumentPluginConnector documentImplService

    @Autowired
    AppSysConfigurationCacheUtility appSysConfigurationCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
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
            String strCategoryId = attrs.get(CATEGORY_ID)
            if ((!strCategoryId) || (strCategoryId.length() == 0)) {
                return listAttributes
            }

            long categoryId = Long.parseLong(strCategoryId)
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
            if (!category) {
                return listAttributes
            }
            List<GroovyRowResult> lstManager = listManager(categoryId)
            if (lstManager.size() == 0) {
                return listAttributes
            }
            listAttributes.put(CATEGORY_ID, new Long(categoryId))   // set categoryId
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
            Map categoryDetails = [lstManager:lstManager as JSON]

            output = buildMyCategoryManagers(categoryDetails)
            return output
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    private String buildMyCategoryManagers(Map categoryDetails) {
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
        lstManagers = ${(categoryDetails?.lstManager) ? categoryDetails.lstManager : []};
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
                            LEFT JOIN doc_category_user_mapping cum ON cum.user_id=au.id AND cum.is_category_manager=true
                            WHERE cum.company_id=:companyId
                            AND cum.category_id=:categoryId
                    """

    /**
     * Manager count related with category
     * @param categoryId - category Id
     * @return lstManager - list of manager
     */

    private List<GroovyRowResult> listManager(long categoryId) {
        AppUser appUser = docSessionUtil.appSessionUtil.getAppUser()
        Map queryParams = [
                companyId: appUser.companyId,
                categoryId: categoryId
        ]

        List<GroovyRowResult> lstManager = executeSelectSql(MAPPED_MANAGER_LIST, queryParams)
        return lstManager
    }
}
