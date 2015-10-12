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
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders details for Categories*/
class DocCategoriesDetailsTaglibActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String CATEGORY_ID = 'category_id'
    private static final String SYS_CONFIGURATION_OBJ = 'sysConfigurationObj'
    private static final String BY = ' By '

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

    /**
     * Build a map containing properties of html
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     *  3. Check required parameters
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

            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            long pluginId = PluginConnector.DOCUMENT_ID
            String key = DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL
            SysConfiguration sysConfiguration = getObjectOfSysConfig(key, pluginId, companyId)
            if (!sysConfiguration) {
                return listAttributes
            }

            long categoryId = Long.parseLong(strCategoryId)
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
            if (!category) {
                return listAttributes
            }
            listAttributes.put(CATEGORY_ID, new Long(categoryId))   // set categoryId
            listAttributes.put(SYS_CONFIGURATION_OBJ, sysConfiguration)
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
     * Get category details
     * Get count of member
     * build the categoryDetails for html
     * @param parameters - map of given attributes
     * @param obj - N/A
     * @return - output string for html
     */
    public Object execute(Object parameters, Object obj) {
        try {
            String output
            Map lstAttributes = (Map) parameters
            Long categoryId = (Long) lstAttributes.get(CATEGORY_ID)
            SysConfiguration sysConfiguration = (SysConfiguration) lstAttributes.get(SYS_CONFIGURATION_OBJ)
            Map category = categoryDetails(categoryId)
            int countMember = countMember(categoryId)
            Map categoryDetails = [categoryId: category.id, name: category.name,
                                   createdBy: category.createdBy, createdOn: category.createdOn,
                                   countMember: countMember, categoryLabel:sysConfiguration.value]

            output = buildCategoryDetails(categoryDetails)
            return output
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    private String buildCategoryDetails(Map categoryDetails) {
        // read map values
        String html = """
                <div class="panel panel-primary">
                <div class="panel-heading">
                        <div class="panel-title">${categoryDetails.categoryLabel} Details</div>
                    </div>
             <div class="panel-body">
                <form id='categoryDetailForm' name='categoryDetailForm' class="form-horizontal" role="form">
            <div class="form-group">
                <label class="col-md-3 control-label" for="name">Name:</label>
                <span id="name" class="col-md-9 pull-left">${categoryDetails.name}</span>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label" for="createdBy">Created:</label>
                <span id="createdBy"
                      class="col-md-9 pull-left">${categoryDetails.createdBy}${categoryDetails.createdOn}</span>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label" for="members">Members:</label>
                <span id="members" class="col-md-9 pull-left">${categoryDetails.countMember}</span>
            </div>
        </form>
        </div>
                </div>
                """

        return html
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get Category Details by categoryId
     * get appUser and format date
     * @param categoryId - Category id
     * @return categoryDetails - A map containing category details
     */
    private Map categoryDetails(long categoryId) {
        DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
        AppUser appUser = (AppUser) appUserCacheUtility.read(category.createdBy)
        String createdBy = BY + appUser.username
        String createdOn = '(' + DateUtility.getDateForSMS(category.createdOn) + ')'
        Map categoryDetails = [id: category.id, name: category.name, createdBy: createdBy,createdOn: createdOn]
        return categoryDetails
    }

    private static final String MEMBER_COUNT_QUERY = """
        SELECT DISTINCT(scum.user_id) countMember
        FROM doc_sub_category_user_mapping scum
        WHERE scum.company_id =:companyId
        AND scum.category_id =:categoryId
    """

    /**
     * User count related with category
     * @param categoryId - category Id
     * @return countMembers - count of members
     */
    private int countMember(long categoryId) {
        AppUser appUser = docSessionUtil.appSessionUtil.getAppUser()
        Map queryParams = [
                companyId: appUser.companyId,
                categoryId: categoryId
        ]

        List<GroovyRowResult> listOfResults = executeSelectSql(MEMBER_COUNT_QUERY, queryParams)
        if (listOfResults.size() < 0) {
            throw new RuntimeException("Failed to get count of user")
        }
        return listOfResults.size()
    }

    /**
     * get object of sysConfiguration
     * @param key - key of sysConfiguration
     * @param pluginId - plugin id
     * @param companyId -id of company
     * @return - object of sysConfiguration
     */
    private SysConfiguration getObjectOfSysConfig(String key, long pluginId, long companyId) {
        SysConfiguration sysConfiguration
        switch (pluginId) {
            case PluginConnector.APPLICATION_ID:
                sysConfiguration = (SysConfiguration) appSysConfigurationCacheUtility.readByKeyAndCompanyId(key)
                break
            case PluginConnector.DOCUMENT_ID:
                sysConfiguration = (SysConfiguration) documentImplService.readSysConfig(key, companyId)
                break
            default:
                return null
        }
        return sysConfiguration
    }
}
