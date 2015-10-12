package com.athena.mis.accounting.actions.accdivision

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccDivision
import com.athena.mis.accounting.utility.AccDivisionCacheUtility
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get list of accDivision object(s) to show on grid
 *  For details go through Use-Case doc named 'ListAccDivisionActionService'
 */
class ListAccDivisionActionService extends BaseService implements ActionIntf {

    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load division List"
    private static final String ACC_DIVISION_LIST = "accDivisionList"
    private static final String COUNT = "count"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of accDivision object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accDivisionList and count
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)
            int count = accDivisionCacheUtility.count()
            List accDivisionList = accDivisionCacheUtility.list(this)
            result.put(ACC_DIVISION_LIST, accDivisionList)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap accDivisionObjectList to show on grid
     * @param obj -a map contains accDivisionObjectList and count
     * @return -wrapped accDivisionObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<AccDivision> accDivisionList = (List<AccDivision>) executeResult.get(ACC_DIVISION_LIST)
            int count = (int) executeResult.get(COUNT)
            List accDivisionListWrap = wrapListInGridEntityList(accDivisionList, start)
            return [page: pageNumber, total: count, rows: accDivisionListWrap]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrappedAccDivisionObjectList for grid
     * @param accDivisionList -list of accDivision objects
     * @param start -start index
     * @return -wrappedAccDivisionObjectList
     */
    private List wrapListInGridEntityList(List<AccDivision> accDivisionList, int start) {
        List accDivisions = [] as List
        int counter = start + 1
        for (int i = 0; i < accDivisionList.size(); i++) {
            Project project = (Project) projectCacheUtility.read(accDivisionList[i].projectId)
            GridEntity obj = new GridEntity()
            obj.id = accDivisionList[i].id
            obj.cell = [
                    counter,
                    accDivisionList[i].id,
                    accDivisionList[i].name,
                    project.name,
                    accDivisionList[i].isActive ? Tools.YES : Tools.NO
            ]
            accDivisions << obj
            counter++
        }
        return accDivisions
    }
}
