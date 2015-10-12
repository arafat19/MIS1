package com.athena.mis.document.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppSysConfigurationCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired


class DocCountSubCategoriesTaglibActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String CATEGORY_ID = 'category_id'
    private static final String SYS_CONFIGURATION_OBJ = 'sysConfigurationObj'
    private static final String SUB_CATEGORY_COUNT = 'subCategoryCount'

    @Autowired(required = false)
    DocumentPluginConnector documentImplService

    @Autowired
    AppSysConfigurationCacheUtility appSysConfigurationCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil

    /**
     * Build a map containing properties of html listView
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

            String key = DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL
            SysConfiguration sysConfiguration = getObjectOfSysConfig(key, pluginId, companyId)
            if (!sysConfiguration) {
                return listAttributes
            }

            long categoryId = Long.parseLong(strCategoryId)
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
            if (!category) {
                return listAttributes
            }
            int subCategoryCount = mappedSubCategoryCount(categoryId)

            if (subCategoryCount == 0) {
                return listAttributes
            }

            listAttributes.put(CATEGORY_ID, new Long(categoryId))   // set categoryId
            listAttributes.put(SYS_CONFIGURATION_OBJ, sysConfiguration)
            listAttributes.put(SUB_CATEGORY_COUNT, subCategoryCount)
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
     * build the html view with count of SubCategory
     * @param parameters - map of given attributes
     * @param obj - N/A
     * @return - output string for 'count'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            String output
            Map lstAttributes = (Map) parameters
            Long categoryId = (Long) lstAttributes.get(CATEGORY_ID)
            int subCategoryCount = (int) lstAttributes.get(SUB_CATEGORY_COUNT)
            SysConfiguration sysConfiguration = (SysConfiguration) lstAttributes.get(SYS_CONFIGURATION_OBJ)
            // For Sub Category Count

            Map subCategoryCountMap = [categoryId: categoryId, subCategoryCount: subCategoryCount, subCategoryLabel: sysConfiguration.value]
            output = buildCountSubCategory(subCategoryCountMap)
            return output
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    private String buildCountSubCategory(Map categoryDetails) {
        // read map values
        String html = """
        <ul class="nav nav-pills nav-stacked" style="padding-bottom: 5px">
            <li class="active">
                    <a ${(categoryDetails?.subCategoryCount > 0) ? "href='#docSubCategory/showSubCategories?categoryId=${categoryDetails?.categoryId}'" : "&nbsp"}>
                        <span class="badge pull-right">${categoryDetails?.subCategoryCount}</span>${categoryDetails?.subCategoryLabel}</a>
            </li>
        </ul>
    """

        return html
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /*
* Get count of Mapped Sub Categories
* @param categoryId
* @return countSubCategory - count of Mapped Sub Categories
* */

    private int mappedSubCategoryCount(long categoryId) {
        AppUser appUser = docSessionUtil.appSessionUtil.getAppUser()
        Map queryParams = [
                companyId: appUser.companyId,
                categoryId: categoryId,
                userId: appUser.id,
        ]

        List<GroovyRowResult> result = executeSelectSql(MAPPED_SUB_CATEGORY_COUNT, queryParams)
        if (result.size() < 0) {
            throw new RuntimeException("Failed to get count of sub category")
        }
        int countSubCategory = (int) result[0].sunCatCount
        return countSubCategory
    }

    private static final String MAPPED_SUB_CATEGORY_COUNT = """
        SELECT COUNT(scum.sub_category_id) sunCatCount
        FROM doc_sub_category dsc
        LEFT JOIN doc_sub_category_user_mapping scum ON dsc.id = scum.sub_category_id
        WHERE scum.company_id = :companyId
        AND scum.category_id = :categoryId
        AND scum.user_id = :userId
        AND dsc.is_active =TRUE
    """

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
