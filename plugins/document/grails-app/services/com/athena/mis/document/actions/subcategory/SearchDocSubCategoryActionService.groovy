package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
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
import org.springframework.transaction.annotation.Transactional

class SearchDocSubCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load page :- '
    private static final String LIST_SUB_CATEGORY = 'subCategoryList'
    private static final String GRID_OBJ = 'gridObj'
    private static final String SUB_CATEGORY_LABEL = 'subCategoryLabel'
    private static final String SUB_CATEGORY_NAME = 'Sub Category'

    DocSubCategoryService docSubCategoryService

    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
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
     * @param parameters - serialize parameters from UI
     * @param obj - N/A
     * Get sub category list for grid through specific search
     * get sub category label from system configuration
     * @return - A map containing all objects of sub categoryList,count of sub categoryList for buildSuccessResultForUI
     * */

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String subCategoryName = SUB_CATEGORY_NAME

        try {
            long companyId = docSessionUtil.appSessionUtil.getCompanyId()

            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                subCategoryName = sysConfiguration.value
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            initSearch(params)

            long categoryId = Long.parseLong(params.categoryId.toString())
            Map searchResult = docSubCategoryCacheUtility.searchByCategoryId(categoryId, queryType, query, this)

            result.put(SUB_CATEGORY_LABEL, subCategoryName)
            result.put(LIST_SUB_CATEGORY, searchResult.list)
            result.put(Tools.COUNT, searchResult.count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + subCategoryName)
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
            List<DocSubCategory> subCatList = (List<DocSubCategory>) preResult.get(LIST_SUB_CATEGORY)
            int count = (int) preResult.get(Tools.COUNT)
            List lstSubCategory = wrapSubCategory(subCatList, start)
            Map gridObject = [page: pageNumber, total: count, rows: lstSubCategory]

            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
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
        try {
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
        } catch (Exception e) {
            log.error(e.getMessage())
            return subCategoryList
        }
    }
}
