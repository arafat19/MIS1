package com.athena.mis.document.actions.category

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ListDocCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load page :- '
    private static final String LIST_CATEGORY = 'categoryList'
    private static final String GRID_OBJ = 'gridObj'
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String CATEGORY_NAME = 'Category'


    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        //Do nothing for pre - operation
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /*
    * get category label from system configuration
    * @param parameters - serialize parameters from UI
    * @param obj - N/A
    * @return - A map containing all objects of categoryList,count of categoryList for buildSuccessResultForUI
    * */

    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String categoryName = CATEGORY_NAME

        try {
            long companyId = docSessionUtil.appSessionUtil.getCompanyId()

            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                categoryName = sysConfiguration.value
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            List<DocCategory> categoryList = docCategoryCacheUtility.list(this)
            //get list of category from cache utility
            int count = docCategoryCacheUtility.count()


            result.put(CATEGORY_LABEL, categoryName)
            result.put(LIST_CATEGORY, categoryList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + categoryName)
            return result
        }

    }

    /*
    * Build Success Results for grid in UI
    * @params obj - Map return from execute method
    * Wrap Category list for grid
    * @return a map of containing all object necessary for show page
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String categoryLabel = preResult.get(CATEGORY_LABEL)
        try {
            List<DocCategory> catList = (List<DocCategory>) preResult.get(LIST_CATEGORY)
            int count = (int) preResult.get(Tools.COUNT)
            List categoryList = wrapCategory(catList, start)
            Map gridObject = [page: pageNumber, total: count, rows: categoryList]

            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(GRID_OBJ, gridObject)
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

    /*
    * Wrap Category List for Grid
    * @params catList - List of Category List
    * @params start - starting index of the page
    * @return List of wrapped category
    * */

    private List wrapCategory(List<DocCategory> catList, int start) {
        List categoryList = []
        try {
            int counter = start + 1
            for (int i = 0; i < catList.size(); i++) {
                GridEntity object = new GridEntity()
                DocCategory category = catList[i]
                object.id = category.id
                object.cell = [
                        counter,
                        category.id,
                        category.name,
                        category.description ? category.description : Tools.EMPTY_SPACE,
                        category.subCategoryCount,
                        category.isActive ? Tools.YES : Tools.NO
                ]
                categoryList << object
                counter++
            }
            return categoryList
        } catch (Exception e) {
            log.error(e.getMessage())
            return categoryList
        }
    }
}