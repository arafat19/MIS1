package com.athena.mis.procurement.actions.indent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.service.IndentService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific object of selected indent at grid row and show for UI
 *  For details go through Use-Case doc named 'SelectIndentActionService'
 */
class SelectIndentActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    IndentService indentService

    private static final String NOT_FOUND_MASSAGE = "Indent not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to get Indent"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get indent object by id
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            initPager(parameterMap)

            long indentId = Long.parseLong(parameterMap.id.toString())
            ProcIndent indent = (ProcIndent) indentService.read(indentId)
            if (!indent) {
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
                return result
            }

            result.put(Tools.ENTITY, indent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
     * Show selected object on the form
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            ProcIndent indent = (ProcIndent) executeResult.get(Tools.ENTITY)

            result.put(Tools.ENTITY, indent)
            result.put(FROM_DATE, indent.fromDate.format(DateUtility.dd_MM_yyyy_SLASH))
            result.put(TO_DATE, indent.toDate.format(DateUtility.dd_MM_yyyy_SLASH))
            result.put(Tools.VERSION, indent.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}

