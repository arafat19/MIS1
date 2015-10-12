package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocSubCategoryService
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/*
* Select Sub Category from Grid
* */

class SelectDocSubCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND_ERROR_MESSAGE = ' is not found'
    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to select '
    private static final String SUB_CATEGORY_LABEL = 'subCategoryLabel'
    private static final String SUB_CATEGORY_NAME = 'Sub Category'

    DocSubCategoryService docSubCategoryService
    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility


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

            long id = Long.parseLong(parameterMap.id.toString())
            DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(id)

            if (!subCategory) {
                result.put(SUB_CATEGORY_LABEL, subCategoryName)
                result.put(Tools.MESSAGE, subCategoryName + NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            result.put(Tools.ENTITY, subCategory)
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
            DocSubCategory subCategory = (DocSubCategory) preResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, subCategory)
            result.put(Tools.VERSION, subCategory.version)
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
}
