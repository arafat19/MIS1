package com.athena.mis.document.actions.category

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocCategoryService
import com.athena.mis.document.service.DocSubCategoryService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateDocCategoryActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String CATEGORY = 'docCategory'
    private static final String UPDATE_SUCCESS_MESSAGE = ' has been successfully updated'
    private static final String UPDATE_FAILURE_MESSAGE = 'Failed to saved '
    private static final String NAME_MUST_BE_UNIQUE = ' name must be unique'
    private static final String OBJ_NOT_FOUND = " not found"
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String CATEGORY_NAME = 'Category'

    DocCategoryService docCategoryService
    DocSubCategoryService docSubCategoryService
    LinkGenerator grailsLinkGenerator

    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
    /*
    * @params parameters - Serialize parameters from UI
    * @params obj - N/A
    * Category Label from System configuration
    * Check for invalid input, object
    * Build new category
    * check for duplicate name
    * @return - A map on containing new category, category label or error messages
    * */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String categoryLabel = CATEGORY_NAME     // default value
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                categoryLabel = sysConfiguration.value
            }

            GrailsParameterMap grailsParameterMap = (GrailsParameterMap) parameters
            if (!grailsParameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long categoryId = Long.parseLong(grailsParameterMap.id.toString())
            DocCategory oldCategory = (DocCategory) docCategoryService.read(categoryId)
            if (!oldCategory) {
                result.put(Tools.MESSAGE, categoryLabel + OBJ_NOT_FOUND)
                return result
            }

            DocCategory newCategory = buildCategory(grailsParameterMap, oldCategory)
            int duplicateCount = docCategoryCacheUtility.countByNameIlikeAndIdNotEqual(newCategory.name, newCategory.id)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, categoryLabel + NAME_MUST_BE_UNIQUE)
                return result
            }

            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(CATEGORY, newCategory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE + categoryLabel)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do Nothing ofr post - operation
        return null
    }

    /**
     * Update Category object in DB & update cache utility accordingly
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
            List<DocSubCategory> lstSubCategory = docSubCategoryCacheUtility.listByCategoryId(category.id)
            docCategoryService.update(category)
            if (!category.isActive.booleanValue() && lstSubCategory) {
                docSubCategoryService.updateForIsActiveFalse(category)
                docSubCategoryCacheUtility.updateSubCategoryForIsActiveFalse(category.id)
                docCategoryCacheUtility.update(category, docCategoryCacheUtility.DEFAULT_SORT_ORDER, docCategoryCacheUtility.SORT_ORDER_ASCENDING)
            } else {
                docCategoryCacheUtility.update(category, docCategoryCacheUtility.DEFAULT_SORT_ORDER, docCategoryCacheUtility.SORT_ORDER_ASCENDING)
            }
            result.put(CATEGORY, category)
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE + categoryLabel)

            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE + categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * @params obj - map from execute method
    * Show newly updated category to grid
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
            result.put(Tools.VERSION, category.version)
            result.put(Tools.MESSAGE, categoryLabel + UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE + categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
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
                    result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE + categoryLabel)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE + categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * Build Category for update
    * */

    private DocCategory buildCategory(GrailsParameterMap parameterMap, DocCategory oldCategory) {
        DocCategory newCategory = new DocCategory(parameterMap)
        String urlInName = newCategory.name.replaceAll(" ", "-").toLowerCase()
        String url = grailsLinkGenerator.link(controller: 'docCategory', action: 'showCategory', id: urlInName, absolute: true)
        oldCategory.name = newCategory.name
        oldCategory.description = newCategory.description
        oldCategory.urlInName = urlInName
        oldCategory.url = url
        oldCategory.isActive = newCategory.isActive
        oldCategory.updatedOn = new Date()
        oldCategory.updatedBy = docSessionUtil.appSessionUtil.getAppUser().id
        return oldCategory
    }
}
