package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.entity.PtBug
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class DeletePtBugForSprintActionService extends BaseService implements ActionIntf {

    PtBugService ptBugService
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass());

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete bug"
    private static final String BUG_DELETE_SUCCESS_MSG = "Bug has been successfully deleted"
    private static final String NOT_APPLICABLE_TO_DELETE = "Fixed/Closed bug couldn't be deleted"
    private static final String BUG_MODULE = "moduleId"
    private static final String BUG_OBJ = "bugObj"
    private static final String BUG_DROP_DOWN_LIST = "dropDownBug"
    private static final String STR_TITLE = "title"
    private static final String HAS_OWNER = "Selected bug has owner"

    /**
     * Checking preconditions before deleting the sprint-bug mapping
     * 1. Check validity of params
     * 2. Check existence of mapped object
     * @param params - parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long bugId = Long.parseLong(parameterMap.id.toString())
            PtBug ptBug = ptBugService.read(bugId)
            if (!ptBug) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            if (ptBug.ownerId > 0) {
                result.put(Tools.MESSAGE, HAS_OWNER)
                return result
            }
            SystemEntity statusFixed = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.FIXED_RESERVED_ID,ptBug.companyId)
            SystemEntity statusClosed = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.CLOSED_RESERVED_ID,ptBug.companyId)

            if(ptBug.status == statusFixed.id || ptBug.status == statusClosed.id){
                result.put(Tools.MESSAGE, NOT_APPLICABLE_TO_DELETE)
                return result
            }
            result.put(BUG_OBJ, ptBug)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Update sprintId for delete the spring-bug mapped object
     * @param params - N/A
     * @param obj - parameters from executePreCondition
     * @returna - map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executePreResult = (LinkedHashMap) obj
            PtBug ptBug = (PtBug) executePreResult.get(BUG_OBJ)
            ptBugService.deleteBugForSprint(ptBug)    // delete BUG_OBJ object from DB
            result.put(BUG_OBJ, ptBug)
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
     * Build success message for delete
     * @param obj - map from execute method
     * @return - a map containing success message for UI
     */
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        LinkedHashMap executeResult = (LinkedHashMap) obj
        PtBug ptBug = (PtBug) executeResult.get(BUG_OBJ)
        List<PtBug> lstBug = ptBugService.findAllByModuleIdAndStatusAndCompanyId(ptBug)
        result.put(BUG_DROP_DOWN_LIST, Tools.listForKendoDropdown(lstBug, STR_TITLE, null))
        result.put(BUG_MODULE, ptBug.moduleId)
        result.put(Tools.MESSAGE, BUG_DELETE_SUCCESS_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
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
}
