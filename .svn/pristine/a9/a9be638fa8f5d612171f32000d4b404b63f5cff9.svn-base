package com.athena.mis.application.actions.entitynote

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.EntityNote
import com.athena.mis.application.service.EntityNoteService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete EntityNote object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteEntityNoteActionService'
 */
class DeleteEntityNoteActionService extends BaseService implements ActionIntf {

    EntityNoteService entityNoteService

    private final Logger log = Logger.getLogger(getClass())

    private static final String DELETE_SUCCESS_MESSAGE = "Note has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Note could not be deleted"
    private static final String ENTITY_NOT_FOUND = "Selected note not found"
    private static final String DELETED = "deleted"

    /**
     * 1. check required parameters
     * 2. pull EntityNote object by id
     * 3. check existence of EntityNote object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long id = Long.parseLong(params.id.toString())
            EntityNote entityNote = entityNoteService.read(id)
            // check existence of EntityNote object
            if (!entityNote) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete EntityNote object from DB
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long id = Long.parseLong(params.id.toString())
            entityNoteService.delete(id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DELETE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
     * Contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains boolean value(true) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
}
