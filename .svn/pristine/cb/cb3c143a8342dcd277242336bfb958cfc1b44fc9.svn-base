package com.athena.mis.projecttrack.actions.ptSprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete ptSprint object from DB  and remove it from grid
 *  For details go through Use-Case doc named 'PtDeleteSprintActionService'
 */
class PtDeleteSprintActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService

    private final Logger log = Logger.getLogger(getClass())

    private static final String SPRINT_DELETE_SUCCESS_MSG = "Sprint has been successfully deleted"
    private static final String HAS_ASSOCIATION_BACKLOG = "  backlog(s) associated with selected sprint "
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete ptSprint"
    private static final String SPRINT_OBJ = "sprintObj"

    /**
     * Checking pre condition and association before deleting the ptSprint object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long sprintId = Long.parseLong(params.id.toString())
            PtSprint sprint = (PtSprint) ptSprintService.read(sprintId)
            if (!sprint) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            //check association
            Map preResult = (Map) hasAssociation(sprint)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }
            result.put(SPRINT_OBJ, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Delete ptSprint object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executePreResult = (LinkedHashMap) obj
            PtSprint ptSprint = (PtSprint) executePreResult.get(SPRINT_OBJ)
            ptSprintService.delete(ptSprint.id)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.MESSAGE, SPRINT_DELETE_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
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

    private LinkedHashMap hasAssociation(PtSprint ptSprint) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        long sprintId = ptSprint.id
        int count = 0

        count = countSprintBacklog(sprintId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_BACKLOG)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String QUERY_BACKLOG = """
            SELECT COUNT(id) AS count
            FROM pt_backlog
            WHERE sprint_id = :sprintId
        """
    /**
     * count number of sprint(s) mapped with backlog(s)
     * @param sprintId -PtSprint.id
     * @return -int value
     */
    private int countSprintBacklog(long sprintId) {
        List results = executeSelectSql(QUERY_BACKLOG, [sprintId: sprintId])
        int count = results[0].count
        return count
    }


}
