package com.athena.mis.document.actions.category

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.service.DocCategoryService
import com.athena.mis.document.service.DocSubCategoryService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteDocCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass());

    private static final String DELETE_SUCCESS_MSG = " has been successfully deleted!"
    private static final String DELETE_FAILURE_MSG = " has not been deleted."
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete "
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String OBJ_NOT_FOUND = " not found"
    private static final String CATEGORY = 'docCategory'
    private static final String CATEGORY_NAME = 'Category'
    private static final String SUB_CATEGORY_NAME = 'Sub Category'
    private static final String HAS_ASSOCIATION = " Association with "


    DocCategoryService docCategoryService
    DocSubCategoryService docSubCategoryService

    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility


    /*
    * @params parameters - Serialize parameters from UI
    * @params obj - N/A
    * Category Label from System configuration
    * Check for invalid input, object
    * Check association
    * @return - A map of containing category label or error messages
    * */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String categoryLabel = CATEGORY_NAME
        String subCategoryLabel = SUB_CATEGORY_NAME
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                categoryLabel = sysConfiguration.value
            }
            SysConfiguration sysConfiguration1 = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
            if (sysConfiguration1) {
                subCategoryLabel = sysConfiguration1.value
            }

            GrailsParameterMap grailsParameterMap = (GrailsParameterMap) parameters
            if (!grailsParameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long categoryId = Long.parseLong(grailsParameterMap.id.toString())
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
            if (!category) {
                result.put(Tools.MESSAGE, categoryLabel + OBJ_NOT_FOUND)
                return result
            }

            Map preResult = (Map) hasAssociation(category, subCategoryLabel)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }


            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(CATEGORY, category)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + categoryLabel)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /**
     * Delete Category object in DB & delete cache utility accordingly
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for build result
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String categoryLabel = preResult.get(CATEGORY_LABEL)
        try {
            DocCategory category = (DocCategory) preResult.get(CATEGORY)
            long id = category.id
            docCategoryService.delete(id)
            docCategoryCacheUtility.delete(id)
            result.put(CATEGORY, category)
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(DELETE_FAILURE_MSG + categoryLabel)

            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG + categoryLabel)
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
        String categoryLabel = preResult.get(CATEGORY_LABEL)
        result.put(Tools.DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, categoryLabel + DELETE_SUCCESS_MSG)
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
        String categoryLabel = preResult.get(CATEGORY_LABEL)

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                } else {
                    result.put(Tools.MESSAGE, DELETE_FAILURE_MSG + categoryLabel)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG + categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * Check association
    * */
    private Map hasAssociation(DocCategory category, String subCategoryLabel) {
        Map result = new LinkedHashMap()
        int count = 0

        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        count = docSubCategoryService.countByCategoryId(category.id)
        if (count > 0) {
            result.put(Tools.MESSAGE, count + HAS_ASSOCIATION + subCategoryLabel)
            return result
        }


        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result

    }
}
