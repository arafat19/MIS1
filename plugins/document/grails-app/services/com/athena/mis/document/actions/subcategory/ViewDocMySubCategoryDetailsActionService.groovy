package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocSubCategoryService
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

/*
* Select Sub Category from Grid
* */

class ViewDocMySubCategoryDetailsActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to select '
    private static final String SUB_CATEGORY_LABEL = 'subCategoryLabel'
    private static final String SUB_CATEGORY_NAME = 'Sub Category'
    private static final String SUB_CATEGORY_DETAILS = 'subCategoryDetails'
    private static final String DETAILS_NOT_FOUND_ERROR_MESSAGE = ' details is not found'
    private static final String BY = ' By '


    DocSubCategoryService docSubCategoryService
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
    * Sub Category Label from system configuration
    * get sub category object by id
    * @return - A map of sub Category Label, Entity or error message
    * */

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String subCategoryName = SUB_CATEGORY_NAME

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                subCategoryName = sysConfiguration.value
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long subcategoryId = Long.parseLong(parameterMap.id.toString())
            Map subCatDetails = subCategoryDetails(subcategoryId)

            if (!subCatDetails) {
                result.put(SUB_CATEGORY_LABEL, subCategoryName)
                result.put(Tools.MESSAGE, subCategoryName + DETAILS_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            int countMember = countMember(subcategoryId)
            List<GroovyRowResult> lstManager = listManager(subcategoryId)
            Map subCategoryDetails = [id: subCatDetails.id, name: subCatDetails.name,
                    createdBy: subCatDetails.createdBy, createdOn: subCatDetails.createdOn, categoryId:subCatDetails.categoryId,countMember: countMember, lstManager: lstManager as JSON]

            result.put(SUB_CATEGORY_DETAILS, subCategoryDetails)
            result.put(SUB_CATEGORY_LABEL, subCategoryName)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + subCategoryName)
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
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            result.put(SUB_CATEGORY_DETAILS, preResult.get(SUB_CATEGORY_DETAILS))
            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + subCategoryLabel)
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
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + subCategoryLabel)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + subCategoryLabel)
            return result
        }
    }

    public static final String MEMBER_COUNT_QUERY = """
                            SELECT DISTINCT(cum.user_id) countMember
                            FROM doc_all_category_user_mapping cum
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

    private static final String MAPPED_MANAGER_LIST = """
                            SELECT Distinct(au.username) manager
                            FROM app_user au
                            LEFT JOIN doc_all_category_user_mapping cum ON cum.user_id=au.id AND cum.is_sub_category_manager=true
                            WHERE cum.company_id=:companyId
                            AND cum.sub_category_id=:subCategoryId
                    """
    /*
    * Get List of Mapped sub Categories
    * @return lstuser - mapped sub category List
    * */

    private List<GroovyRowResult> listManager(long subCategoryId) {
        AppUser appUser = docSessionUtil.appSessionUtil.getAppUser()
        Map queryParams = [
                companyId: appUser.companyId,
                subCategoryId: subCategoryId
        ]

        List<GroovyRowResult> lstManager = executeSelectSql(MAPPED_MANAGER_LIST, queryParams)
        return lstManager
    }
}
