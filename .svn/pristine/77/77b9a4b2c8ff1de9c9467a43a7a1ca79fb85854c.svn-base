package com.athena.mis.document.actions.category

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.service.DocAllCategoryUserMappingService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional



class ViewDocMyCategoryDetailsActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND_ERROR_MESSAGE = ' is not found'
    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to select '
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String CATEGORY_NAME = 'Category'
    private static final String CATEGORY_DETAILS = 'categoryDetails'
    private static final String DETAILS_NOT_FOUND_ERROR_MESSAGE = ' details is not found'
    private static final String ON = ' on '
    private static final String BY = ' By '

    DocAllCategoryUserMappingService docAllCategoryUserMappingService

    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        //Do nothing for pre - operation
        return null
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /*
    * @params parameters - Serialize parameters from UI
    * @params obj - N/A
    *   1.Category Label from system configuration
    *   2.category details by categoryId
    *   3.sub category count, user count, manager list by categoryId
    * @return - A map of Category Label, Entity or error message
    * */

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String categoryName = CATEGORY_NAME
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                categoryName = sysConfiguration.value
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long categoryId = Long.parseLong(parameterMap.id.toString())
            Map category = categoryDetails(categoryId)
            if (!category) {
                result.put(CATEGORY_LABEL, categoryName)
                result.put(Tools.MESSAGE, categoryName + DETAILS_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            // For Sub Category Count
            int subCategoryCount = subCategoryCount(categoryId)

            //For User Count
            int countMember = countMember(categoryId)

            //For Managers Count
            List<GroovyRowResult> lstManager = listManager(categoryId)

            Map categoryDetails = [id: category.id, name: category.name,
                    createdBy: category.createdBy, createdOn: category.createdOn,
                    subCategoryCount: subCategoryCount, countMember: countMember, lstManager: lstManager as JSON]

            result.put(CATEGORY_DETAILS, categoryDetails)
            result.put(CATEGORY_LABEL, categoryName)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + categoryName)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
        }
    }

    /*
    * Build Success Results
    * @params obj - Map return from execute method
    * @return a map of containing all object necessary for edit/delete page
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String categoryLabel = preResult.get(CATEGORY_LABEL)
        try {
            result.put(CATEGORY_DETAILS, preResult.get(CATEGORY_DETAILS))
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * Build Failure result for UI
    * @params obj - A map from execute method
    * @return a Map containing IsError and default error message/relevant error message to display
    * */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String categoryLabel = preResult.get(CATEGORY_LABEL)
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + categoryLabel)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + categoryLabel)
            return result
        }
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
        Map categoryDetails = [id: category.id, name: category.name, createdBy: createdBy,
                createdOn: createdOn]

        return categoryDetails
    }

    public static final String MEMBER_COUNT_QUERY = """
                            SELECT DISTINCT(cum.user_id) countMember
                            FROM doc_all_category_user_mapping cum
                            WHERE cum.company_id=:companyId
                            AND cum.category_id=:categoryId
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

    private int subCategoryCount(long categoryId) {
        int countMappedCategories = listCategory(categoryId)
        int subCategoryCount = -1
        if (countMappedCategories > 0) {
            subCategoryCount = docSubCategoryCacheUtility.countByCategoryIdAndIsActive(categoryId)
        } else {
            subCategoryCount = mappedSubCategoryCount(categoryId)
        }
        return subCategoryCount
    }

    private static final String IS_CATEGORY_MAPPED = """
                      SELECT COUNT(cum.category_id) count
                        FROM doc_all_category_user_mapping cum
                        WHERE cum.company_id=:companyId
                        AND cum.category_id=:categoryId
                        AND cum.user_id=:userId
                        AND cum.sub_category_id=0
                    """
    /*
    * Get List of Mapped Categories
    * @return lstCategory - mapped category List
    * */

    private int listCategory(long categoryId) {
        AppUser appUser = docSessionUtil.appSessionUtil.getAppUser()
        Map queryParams = [
                companyId: appUser.companyId,
                categoryId: categoryId,
                userId: appUser.id
        ]

        List<GroovyRowResult> lstCategory = executeSelectSql(IS_CATEGORY_MAPPED, queryParams)
        int count = (int) lstCategory[0][0]
        return count
    }


    private static final String MAPPED_SUB_CATEGORY_COUNT = """
                         SELECT COUNT(cum.category_id) sunCatCount
                            FROM doc_sub_category sc
                            LEFT JOIN doc_all_category_user_mapping cum ON sc.id=cum.sub_category_id AND sc.is_active=true
                            WHERE cum.company_id=:companyId
                            AND cum.category_id=:categoryId
                            AND cum.user_id=:userId
                            AND cum.sub_category_id !=0
                    """
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

    private static final String MAPPED_MANAGER_LIST = """
                            SELECT Distinct(au.username) manager
                            FROM app_user au
                            LEFT JOIN doc_all_category_user_mapping cum ON cum.user_id=au.id AND cum.is_category_manager=true
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
