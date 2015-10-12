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
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired


class DocSubCategoriesDetailsTaglibActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SUB_CATEGORY_ID = 'sub_category_id'
    private static final String SYS_CONFIGURATION_OBJ = 'sysConfigurationObj'
    private static final String BY = ' By '
    private static final String URL = 'url'


    @Autowired(required = false)
    DocumentPluginConnector documentImplService

    @Autowired
    AppSysConfigurationCacheUtility appSysConfigurationCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
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
            String strSubCategoryId = attrs.get(SUB_CATEGORY_ID)
//            String url = attrs.get(URL)
            if ((!strSubCategoryId) || (strSubCategoryId.length() == 0)) {
                return listAttributes
            }

            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            long pluginId = PluginConnector.DOCUMENT_ID
            String key = DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL
            SysConfiguration sysConfiguration = getObjectOfSysConfig(key, pluginId, companyId)
            if (!sysConfiguration) {
                return listAttributes
            }

            long subcategoryId = Long.parseLong(strSubCategoryId)

            DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(subcategoryId)
            if (!subCategory) {
                return listAttributes
            }
            listAttributes.put(SUB_CATEGORY_ID, new Long(subcategoryId))   // set categoryId
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
            Long subcategoryId = (Long) lstAttributes.get(SUB_CATEGORY_ID)
            SysConfiguration sysConfiguration = (SysConfiguration) lstAttributes.get(SYS_CONFIGURATION_OBJ)
            Map subCatDetails = subCategoryDetails(subcategoryId)
            int countMember = countMember(subcategoryId)
            Map subCategoryDetails = [id: subCatDetails.id, name: subCatDetails.name,
                                      createdBy: subCatDetails.createdBy, createdOn: subCatDetails.createdOn, categoryId:subCatDetails.categoryId,
                                      countMember: countMember,subCategoryLabel:sysConfiguration.value]

            output = buildMyCategoryDetails(subCategoryDetails)
            return output
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    private String buildMyCategoryDetails(Map subCategoryDetails) {
        // read map values
        String html = """
                <div class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
             <div class="panel-title">${subCategoryDetails.subCategoryLabel} Details</div>
        </div>
    </div>

    <div class="panel-body">
        <form id='categoryDetailForm' name='categoryDetailForm' class="form-horizontal" role="form"
              method="post">
            <div class="form-group">
                <label class="col-md-3 control-label" for="name">Name:</label>
                <span id="name" class="col-md-9 pull-left">${subCategoryDetails?.name}</span>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label" for="createdBy">Created:</label>
                <span id="createdBy"
                      class="col-md-9 pull-left">${subCategoryDetails?.createdBy}${subCategoryDetails?.createdOn}</span>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label" for="members">Members:</label>
                <span id="members" class="col-md-9 pull-left">${subCategoryDetails?.countMember}</span>
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
     * @param subCategoryId - Category id
     * @return categoryDetails - A map containing category details
     */
    private Map subCategoryDetails(long subCategoryId) {
        DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(subCategoryId)
        DocCategory category = (DocCategory) docCategoryCacheUtility.read(subCategory.categoryId)
        AppUser appUser = (AppUser) appUserCacheUtility.read(subCategory.createdBy)
        String createdBY = BY + appUser.username
        String createdOn = '(' + DateUtility.getDateForSMS(subCategory.createdOn) + ')'

        Map subCategoryDetails = [id: subCategory.id, name: subCategory.name, createdBy: createdBY,
                                  createdOn: createdOn, categoryId:category.id]

        return subCategoryDetails
    }


    public static final String MEMBER_COUNT_QUERY = """
                            SELECT DISTINCT(cum.user_id) countMember
                            FROM doc_sub_category_user_mapping cum
                            WHERE cum.company_id=:companyId
                            AND cum.sub_category_id=:subCategoryId
                        """

    private int countMember(long subCategoryId) {
        AppUser appUser = docSessionUtil.appSessionUtil.getAppUser()
        Map queryParams = [
                companyId: appUser.companyId,
                subCategoryId: subCategoryId
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
