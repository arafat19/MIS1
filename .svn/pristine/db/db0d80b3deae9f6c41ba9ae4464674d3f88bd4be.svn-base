package com.athena.mis.application.actions.designation

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of designation for grid
 *  For details go through Use-Case doc named 'ListDesignationActionService'
 */
class ListDesignationActionService extends BaseService implements ActionIntf {

    @Autowired
    DesignationCacheUtility designationCacheUtility

    private final Logger log = Logger.getLogger(getClass())
    private static final String LIST_LOAD_FAILURE_MESSAGE = "Failed to load designation list"
    private static final String DESIGNATION_LIST = "designationList"
    private static final String COUNT = "count"
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null;
    }

    /**
     * Get designation list for grid
     * 1. initialize parameters for flexGrid
     * 2. get designation list from cache utility
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing designation list & count necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)   // initialize parameters for flexGrid
            int count = designationCacheUtility.count()       // get count total designation
            List designationList = designationCacheUtility.list(this) // get sub list of designation
            result.put(DESIGNATION_LIST, designationList)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LIST_LOAD_FAILURE_MESSAGE)
            return null
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap designation list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List designationList = (List) executeResult.get(DESIGNATION_LIST)   // cast map returned from execute method
            int count = (int) executeResult.get(COUNT)
            List wrappedDesignationList = wrapListInGridEntityList(designationList, start)
            result = [page: pageNumber, total: count, rows: wrappedDesignationList]

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: LIST_LOAD_FAILURE_MESSAGE]
            return result
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, LIST_LOAD_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LIST_LOAD_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Wrap list of designation in grid entity
     * @param designationList -list of designation object(s)
     * @param start -starting index of the page
     * @return -list of wrapped designation
     */
    private List wrapListInGridEntityList(List<Designation> designationList, int start) {
        List designations = [] as List
        int counter = start + 1
        for (int i = 0; i < designationList.size(); i++) {
            GridEntity obj = new GridEntity()
            obj.id = designationList[i].id
            obj.cell = [
                    counter,
                    designationList[i].name,
                    designationList[i].shortName
            ]
            designations << obj
            counter++
        }
        return designations
    }
}
