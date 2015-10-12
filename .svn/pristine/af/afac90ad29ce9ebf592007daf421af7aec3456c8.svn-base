package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocCategoryService
import com.athena.mis.document.service.DocSubCategoryService
import com.athena.mis.document.service.DocSubCategoryUserMappingService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteDocSubCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass());

    private static final String DELETE_SUCCESS_MSG = " has been successfully deleted!"
    private static final String DELETE_FAILURE_MSG = " has not been deleted."
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete "
    private static final String SUB_CATEGORY_LABEL = 'subCategoryLabel'
    private static final String OBJ_NOT_FOUND = " not found"
    private static final String SUB_CATEGORY = 'subCategory'
    private static final String SUB_CATEGORY_NAME = 'Sub Category'
    private static final String CATEGORY = 'category'
    private static final String HAS_ASSOCIATION_WITH_APP_USER = " User(s) is associated with this "
    private static final String CATEGORY_MUST_BE_ACTIVE = ' must be active for this operation'

    DocSubCategoryService docSubCategoryService
    DocCategoryService docCategoryService
    DocSubCategoryUserMappingService docSubCategoryUserMappingService

    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility

    /*
    * @params parameters - Serialize parameters from UI
    * @params obj - N/A
    * Sub Category Label from System configuration
    * Check for invalid input, object
    * @return - A map of containing sub category label or error messages
    * */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String subCategoryLabel = SUB_CATEGORY_NAME
        String categoryLabel = CATEGORY
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                subCategoryLabel = sysConfiguration.value
            }
            SysConfiguration sysConfiguration1 = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration1) {
                categoryLabel = sysConfiguration1.value
            }

            GrailsParameterMap grailsParameterMap = (GrailsParameterMap) parameters
            if (!grailsParameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long subCategoryId = Long.parseLong(grailsParameterMap.id.toString())
            DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(subCategoryId)
            if (!subCategory) {
                result.put(Tools.MESSAGE, subCategoryLabel + OBJ_NOT_FOUND)
                return result
            }


            DocCategory category = (DocCategory) docCategoryCacheUtility.read(subCategory.categoryId)
            if (!category.isActive.booleanValue()) {
                result.put(Tools.MESSAGE, categoryLabel + CATEGORY_MUST_BE_ACTIVE)
                return result
            }

            Map preResult = (Map) hasAssociation(subCategory, subCategoryLabel)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(SUB_CATEGORY, subCategory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + subCategoryLabel)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /**
     * Delete sub Category object in DB
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for build result
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            DocSubCategory subCategory = (DocSubCategory) preResult.get(SUB_CATEGORY)
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(subCategory.categoryId)
            int incr = -1

            docSubCategoryService.delete(subCategory.id)
            docCategoryService.updateSubCategoryCount(category, incr)

            docSubCategoryCacheUtility.delete(subCategory.id)
            docCategoryCacheUtility.update(category, docCategoryCacheUtility.DEFAULT_SORT_ORDER, docCategoryCacheUtility.SORT_ORDER_ASCENDING)


            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(SUB_CATEGORY, subCategory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(DELETE_FAILURE_MSG + subCategoryLabel)

            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG + subCategoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * @params obj - map from execute method
    * Show success message
    * @return - A map containing all necessary object for show
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        result.put(Tools.DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, subCategoryLabel + DELETE_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result

    }

    /*
   * Build Failure result in case of any error
   * @params obj - A map from execute method
   * @return - A map containing all necessary message for show
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
                } else {
                    result.put(Tools.MESSAGE, DELETE_FAILURE_MSG + subCategoryLabel)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG + subCategoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * Check Association with appGroupEntity
    * */

    private Map hasAssociation(DocSubCategory subCategory, String subCategoryLabel) {
        Map result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        int countSubCategoryUserMapping = docSubCategoryUserMappingService.countByCompanyIdAndCategoryIdAndSubCategoryId(subCategory)
        if (countSubCategoryUserMapping > 0) {
            result.put(Tools.MESSAGE, countSubCategoryUserMapping + HAS_ASSOCIATION_WITH_APP_USER + subCategoryLabel)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result

    }
}
