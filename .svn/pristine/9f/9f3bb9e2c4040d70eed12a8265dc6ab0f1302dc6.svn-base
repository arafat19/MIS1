package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocSubCategoryService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired


/*
* Show UI for Sub Category CRUD
* List of Sub Category for grid
* */

class ShowDocSubCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load page :- '
    private static final String LIST_SUB_CATEGORY = 'subCategoryList'
    private static final String GRID_OBJ = 'gridObj'
    private static final String SUB_CATEGORY_LABEL = 'subCategoryLabel'
    private static final String SUB_CATEGORY_NAME = 'Sub Category'
    private static final String CATEGORY_NAME = 'Category'
    private static final String NOT_FOUND_ERROR_MESSAGE = ' is not found'
    private static final String CATEGORY = 'category'


    DocSubCategoryService docSubCategoryService

    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility

    /*
    * @params parameters - category id from UI
    * @params obj - N/A
    * Sub category  and category label from System configuration
    * Check invalid input, category object
    * @return - category object or error messages
    * */

    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String subCategoryName = SUB_CATEGORY_NAME          //Default Value
        String categoryName = CATEGORY_NAME                 //Default Value

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long companyId = docSessionUtil.appSessionUtil.getCompanyId()

            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                subCategoryName = sysConfiguration.value
            }

            SysConfiguration sysConfiguration1 = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration1) {
                categoryName = sysConfiguration1.value
            }

            GrailsParameterMap grailsParameterMap = (GrailsParameterMap) parameters
            if (!grailsParameterMap.categoryId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long id = Long.parseLong(grailsParameterMap.categoryId.toString())
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(id)
            if (!category) {
                result.put(Tools.MESSAGE, categoryName + NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            result.put(SUB_CATEGORY_LABEL, subCategoryName)
            result.put(CATEGORY, category)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + subCategoryName)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post-operation
        return null
    }

    /*
    * @param parameters - serialize parameters from UI
    * @param obj - N/A
    * @return - A map containing all objects of subcategoryList,count of subcategoryList for buildSuccessResultForUI
    * */

    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            DocCategory category = (DocCategory) preResult.get(CATEGORY)

            List<DocSubCategory> subCategoryList = docSubCategoryCacheUtility.listByCategoryId(category.id, this)
            //get list of subcategory from DB
            int count = docSubCategoryCacheUtility.countByCategoryId(category.id)

            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(CATEGORY, category)
            result.put(LIST_SUB_CATEGORY, subCategoryList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + subCategoryLabel)
            return result
        }

    }

    /*
    * Build Success Results for grid in UI
    * @params obj - Map return from execute method
    * Wrap Sub Category list for grid
    * @return a map of containing all object necessary for show page
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            DocCategory category = (DocCategory) preResult.get(CATEGORY)
            List<DocSubCategory> subCatList = (List<DocSubCategory>) preResult.get(LIST_SUB_CATEGORY)
            int count = (int) preResult.get(Tools.COUNT)
            List subCategoryList = wrapSubCategory(subCatList, start)
            Map gridObject = [page: pageNumber, total: count, rows: subCategoryList]

            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(CATEGORY, category)
            result.put(GRID_OBJ, gridObject)
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

    /*
    * Wrap Sub Category List for Grid
    * @params subCatList - List of Sub Category List
    * @params start - starting index of the page
    * @return List of wrapped sub category
    * */

    private List wrapSubCategory(List<DocSubCategory> subCatList, int start) {
        List subCategoryList = []
        int counter = start + 1
        for (int i = 0; i < subCatList.size(); i++) {
            GridEntity object = new GridEntity()
            DocSubCategory subCategory = subCatList[i]
            object.id = subCategory.id
            object.cell = [
                    counter,
                    subCategory.id,
                    subCategory.name,
                    subCategory.description ? subCategory.description : Tools.EMPTY_SPACE,
                    subCategory.isActive ? Tools.YES : Tools.NO,
                    subCategory.isEmailNotification ? Tools.YES : Tools.NO
            ]
            subCategoryList << object
            counter++
        }
        return subCategoryList
    }

}
