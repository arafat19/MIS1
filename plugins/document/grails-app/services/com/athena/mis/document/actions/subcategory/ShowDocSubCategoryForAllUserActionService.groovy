package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/*
* Show UI for Sub Category Details
* For details go through Use-Case doc named 'ShowDocSubCategoryForAllUserActionService'
* */

class ShowDocSubCategoryForAllUserActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND_ERROR_MESSAGE = ' not found'
    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to retrieve '
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String SUB_CATEGORY_LABEL = 'subCategoryLabel'
    private static final String CATEGORY_NAME = 'Category'
    private static final String CATEGORY = 'category'
    private static final String SUB_CATEGORY_NAME = 'Sub Category'
    private static final String SUB_CATEGORY = 'subCategory'

    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility

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
    * Category and Sub Category Label from system configuration
    * check category and subcategory existence
    * get category and sub category object by urlInName from corresponding cacheUtility
    * @return - A map of Category and Sub Category Label, category, subcategory or error message
    * */

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String categoryName = CATEGORY_NAME

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            HttpServletRequest request = (HttpServletRequest) obj
            long companyId = getCompanyId(request)
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                categoryName = sysConfiguration.value
            }


            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.category) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            String urlInCategoryName = parameterMap.category.toString()
            DocCategory category = docCategoryCacheUtility.findByUrlInName(urlInCategoryName, companyId)

            if (!category) {
                result.put(CATEGORY_LABEL, categoryName)
                result.put(Tools.MESSAGE, categoryName + NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            String subCategoryName = SUB_CATEGORY_NAME
            sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                subCategoryName = sysConfiguration.value
            }

            String urlInSubCategoryName = parameterMap.sub.toString()
            DocSubCategory subCategory = docSubCategoryCacheUtility.findByUrlInSubCategoryName(urlInSubCategoryName, companyId)
            if (!subCategory) {
                result.put(SUB_CATEGORY_LABEL, subCategoryName)
                result.put(Tools.MESSAGE, subCategoryName + NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            result.put(CATEGORY, category)
            result.put(SUB_CATEGORY, subCategory)
            result.put(CATEGORY_LABEL, categoryName)
            result.put(SUB_CATEGORY_LABEL, subCategoryName)
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
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            DocCategory category = (DocCategory) preResult.get(CATEGORY)
            DocSubCategory subCategory = (DocSubCategory) preResult.get(SUB_CATEGORY)

            result.put(CATEGORY, category)
            result.put(SUB_CATEGORY, subCategory)
            result.put(CATEGORY_LABEL, categoryLabel)
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

    /**
     * retrieve Company from request
     * @param request
     * @return company ID
     */

    private long getCompanyId(HttpServletRequest request) {
        String fullUrl = Tools.getFullUrl(request, false)    // retrieve url with www
        Company company = companyCacheUtility.readByWebUrl(fullUrl) // compare with www
        if (company) {
            return company.id
        }
        // if company not found try to retrieve url without www
        fullUrl = Tools.getFullUrl(request, true)
        company = companyCacheUtility.readByWebUrlWithoutWWW(fullUrl)     // compare without www
        if (company) {
            return company.id
        } else {
            return 0L
        }
    }
}
