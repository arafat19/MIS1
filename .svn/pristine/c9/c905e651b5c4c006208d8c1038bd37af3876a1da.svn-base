package com.athena.mis.document.actions.category

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.service.DocCategoryService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/*
* Create new category to DB and Cache Utility
* Show new category to grid
* */

class CreateDocCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String CATEGORY = 'docCategory'
    private static final String CREATE_SUCCESS_MESSAGE = ' has been successfully saved'
    private static final String CREATE_FAILURE_MESSAGE = 'Failed to saved '
    private static final String NAME_MUST_BE_UNIQUE = ' name must be unique'
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String CATEGORY_NAME = 'Category'


    DocCategoryService docCategoryService
    LinkGenerator grailsLinkGenerator

    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility

    /**
     * @params parameters - serialize parameters form UI
     * @params obj - N/A
     * Build parameters to Category object
     * Check duplicate category name
     * get category label from system configuration
     * @return - A map of containing category object or error message
     * */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String categoryName = CATEGORY_NAME
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, docSessionUtil.appSessionUtil.companyId)
            if (sysConfiguration) {
                categoryName = sysConfiguration.value
            }

            int duplicateCount = docCategoryCacheUtility.countByNameIlike(params.name.toString())
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, categoryName + NAME_MUST_BE_UNIQUE)
                return result
            }

            result.put(CATEGORY_LABEL, categoryName)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + categoryName)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /**
     * Create Category object to DB also add to Category cache utility
     * @params parameters - N/A
     * @params obj - category object from executePreCondition
     * @return - A map of saved category object or error messages
     * Ths method is in Transactional boundary so will rollback in case of any exception
     * */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String categoryLabel = preResult.get(CATEGORY_LABEL)
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            DocCategory category = buildCategory(parameterMap)     //build category object

            docCategoryService.create(category)
            docCategoryCacheUtility.add(category, docCategoryCacheUtility.DEFAULT_SORT_ORDER, docCategoryCacheUtility.SORT_ORDER_ASCENDING)
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(CATEGORY, category)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(CREATE_FAILURE_MESSAGE + categoryLabel)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * @params obj - map from execute method
     * Show newly created category to grid
     * Show success message
     * @return - A map containing all necessary object for show
     * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String categoryLabel = preResult.get(CATEGORY_LABEL)

        try {
            DocCategory category = (DocCategory) preResult.get(CATEGORY)
            GridEntity object = new GridEntity()
            object.id = category.id
            object.cell = [
                    Tools.LABEL_NEW,
                    category.id,
                    category.name,
                    category.description ? category.description : Tools.EMPTY_SPACE,
                    category.subCategoryCount,
                    category.isActive ? Tools.YES : Tools.NO
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, categoryLabel + CREATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
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
                    result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + categoryLabel)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
   * Build Category for create
   * */

    private DocCategory buildCategory(GrailsParameterMap parameterMap) {
        AppUser appUser = docSessionUtil.appSessionUtil.appUser
        String categoryName = parameterMap.name.toString()
        String urlInName = categoryName.replaceAll(" ", "-").toLowerCase()
        String url = grailsLinkGenerator.link(controller: 'docCategory', action: 'showCategory', id: urlInName, absolute: true)
        DocCategory newCategory = new DocCategory(parameterMap)
        newCategory.urlInName = urlInName
        newCategory.url = url
        newCategory.companyId = appUser.getCompanyId()
        newCategory.createdBy = appUser.id
        newCategory.createdOn = new Date()
        newCategory.updatedBy = 0L
        newCategory.updatedOn = null
        return newCategory
    }
}
