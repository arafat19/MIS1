package com.athena.mis.application.actions.contentcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ContentCategoryCacheUtility
import com.athena.mis.application.utility.ContentTypeCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search ContentCategory and show specific list of ContentCategory for grid
 *  For details go through Use-Case doc named 'SearchContentCategoryActionService'
 */
class SearchContentCategoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Could not search content category list"
    private static final String LST_CONTENT_CATEGORY = "lstContentCategory"

    /**
     * Check if user has access to search ContentCategory
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)  // default value
            // only development role type user can search ContentCategory
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Get ContentCategory list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initSearch(params)  // initialize parameters for flexGrid
            // get sub list of ContentCategory by search keyword
            Map searchResult = contentCategoryCacheUtility.search(queryType, query, this)
            List<ContentCategory> lstContentCategory = searchResult.list
            int count = searchResult.count
            result.put(LST_CONTENT_CATEGORY, lstContentCategory)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap ContentCategory list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<ContentCategory> lstContentCategory = (List<ContentCategory>) executeResult.get(LST_CONTENT_CATEGORY)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedContentCategory = wrapContentCategoryList(lstContentCategory, start)
            // wrap list in grid entity
            Map output = [page: pageNumber, total: count, rows: lstWrappedContentCategory]
            result.put(LST_CONTENT_CATEGORY, output)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ContentCategory in grid entity
     * @param lstContentCategory -list of ContentCategory
     * @param start -starting index of the page
     * @return -list of wrapped ContentCategory objects
     */
    private List wrapContentCategoryList(List<ContentCategory> lstContentCategory, int start) {
        List lstWrappedContentCategory = []
        int counter = start + 1
        for (int i = 0; i < lstContentCategory.size(); i++) {
            ContentCategory contentCategory = lstContentCategory[i]
            SystemEntity contentType = (SystemEntity) contentTypeCacheUtility.read(contentCategory.contentTypeId)

            GridEntity obj = new GridEntity()
            obj.id = contentCategory.id
            obj.cell = [
                    counter,
                    contentCategory.id,
                    contentCategory.name,
                    contentType.key,
                    contentCategory.width > 0 ? contentCategory.width : Tools.EMPTY_SPACE,
                    contentCategory.height > 0 ? contentCategory.height : Tools.EMPTY_SPACE,
                    contentCategory.maxSize,
                    contentCategory.extension ? contentCategory.extension : Tools.EMPTY_SPACE
            ]
            lstWrappedContentCategory << obj
            counter++
        }
        return lstWrappedContentCategory
    }
}
