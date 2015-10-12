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
 *  Show list of object(s) of designation
 *  For details go through Use-Case doc named 'ShowDesignationActionService'
 */
class ShowDesignationActionService extends BaseService implements ActionIntf {

    @Autowired
    DesignationCacheUtility designationCacheUtility

    private final Logger log = Logger.getLogger(getClass())
    private static final String SHOW_FAILURE_MESSAGE = "Failed to load designation page"
    private static final String DESIGNATION_LIST = "designationList"
    private static final String COUNT = "count"
    private static final String GRID_OBJECT = "gridObject"
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get designation list for grid
     * 1. initialize parameters for flexGrid
     * 2. get designation list from cache utility
     * 3. get sorted designation list
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
            if ((!parameterMap.sortname) || (parameterMap.sortname.toString().equals(ID))) {
                parameterMap.sortname = designationCacheUtility.SORT_ON_NAME
                parameterMap.sortorder = ASCENDING_SORT_ORDER
            }
            initPager(parameterMap)
            int count = designationCacheUtility.count()
            List designationList = designationCacheUtility.list(this)
            result.put(DESIGNATION_LIST, designationList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List designationList = (List) executeResult.get(DESIGNATION_LIST)
            int count = (int) executeResult.get(COUNT)
            List wrappedDesignationList = wrapListInGridEntityList(designationList, start)
            Map gridObject = [page: pageNumber, total: count, rows: wrappedDesignationList]

            result.put(GRID_OBJECT, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
        try {
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
        } catch (Exception ex) {
            log.error(ex.getMessage())
            designations = []
            return designations
        }
    }
}
