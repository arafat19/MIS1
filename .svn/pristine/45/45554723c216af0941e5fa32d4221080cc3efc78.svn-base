package com.athena.mis.application.actions.contentcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.ContentCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Get ContentCategory list by content type id (SystemEntity.id e.g: Document, Image) used in EntityContent CRUD
 *  For details go through Use-Case doc named 'GetContentCategoryListByContentTypeIdActionService'
 */
class GetContentCategoryListByContentTypeIdActionService  extends BaseService implements ActionIntf {

    private static final String FAILURE_MESSAGE = "Failed to get content category list"
    private static final String CONTENT_CATEGORY_LIST = "contentCategoryList"
    private static final String KEY="key";
    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get ContentCategory list by contentTypeId and isReserved (false)
     * @param params -parameter from UI
     * @param obj -N/A
     * @return -a map containing list of ContentCategory
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long contentTypeId = Long.parseLong(parameterMap.contentTypeId.toString())
            // get ContentCategory list by contentTypeId and isReserved (false)
            List lstContentCategory = contentCategoryCacheUtility.listByContentTypeId(contentTypeId)
            List lstDropDown=Tools.listForKendoDropdown(lstContentCategory, null, null)
            result.put(CONTENT_CATEGORY_LIST, lstDropDown)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        }catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
}
