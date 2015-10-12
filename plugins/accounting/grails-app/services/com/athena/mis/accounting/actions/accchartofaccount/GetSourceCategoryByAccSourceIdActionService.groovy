package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
/**
 *  Get source category by source id and use in onchange method of source drop-down in COA UI
 *  For details go through Use-Case doc named 'GetSourceCategoryByAccSourceIdActionService'
 */
class GetSourceCategoryByAccSourceIdActionService extends BaseService implements ActionIntf {

    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String SOURCE_CATEGORY_LIST = "sourceCategoryList"
    private static final String FAILURE_MESSAGE = "Error occurred to get source category list"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Get parameters from UI
     * @Params parameters -serialized parameters from UI
     * @Params obj -N/A
     * @Return -Map containing isError(true/false)
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)        // default value
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if (!parameters.sourceId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
    /**
     * Get parameters from UI
     * @Params parameters -serialized parameters from UI
     * @Params obj -N/A
     * @Return -Map containing isError(true/false) & source category list
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params

            long accSourceId = Long.parseLong(parameters.sourceId.toString())
            List sourceCategoryList = getSourceCategoryList(accSourceId)    // get source category list

            result.put(SOURCE_CATEGORY_LIST, Tools.listForKendoDropdown(sourceCategoryList,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for success operation
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
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj.message) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Get source category list
     * @param accSourceId -source id
     * @return -a list containing source category
     */
    private List getSourceCategoryList(long accSourceId) {
        List sourceCategoryList = []
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accSourceId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                sourceCategoryList = supplierTypeCacheUtility.list()
                sourceCategoryList = customSourceCategoryList(sourceCategoryList)
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                sourceCategoryList = itemTypeCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                sourceCategoryList = designationCacheUtility.list()
                break
            default:
                break
        }
        return sourceCategoryList
    }
    /**
     * Get custom source category list against source cateogry
     * @param sourceCategoryList -source category list
     * @return -a list containing custom source category
     */
    private List customSourceCategoryList(List sourceCategoryList) {
        List lstSourceCategory = []
        for (int i = 0; i < sourceCategoryList.size(); i++) {
            long id = sourceCategoryList[i].id
            String name = sourceCategoryList[i].key
            lstSourceCategory << [id: id, name: name]
        }
        return lstSourceCategory
    }
}
