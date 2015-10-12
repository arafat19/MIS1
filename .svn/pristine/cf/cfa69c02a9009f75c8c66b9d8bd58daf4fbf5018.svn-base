package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 * Gives indent list by project for Purchase Request
 * For details go through Use-Case doc named 'GetIndentListForProjectActionService'
 */
class GetIndentListForProjectActionService extends BaseService implements ActionIntf {

    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String FAILURE_MSG = "Fail to get indent information"
    private static final String PROJECT_NOT_FOUND = "Project not found"
    private static final String INDENT_LIST = "indentList"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Get budget object
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - budget object
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long projectId = Long.parseLong(parameterMap.projectId)
            Object project = projectCacheUtility.read(projectId)
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Get budget info list
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - A map containing budget list and isError msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long projectId = Long.parseLong(parameterMap.projectId)
            List<GroovyRowResult> lstIndent = listByProjectAndIndentId(projectId)
            result.put(INDENT_LIST, lstIndent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Wrap grid entity
     * @param parameters -N/A
     * @param obj - object from execute method
     * @return -map contains isError(true/false)& relative msg depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstIndent = (List<GroovyRowResult>) executeResult.get(INDENT_LIST)
            result.put(INDENT_LIST, Tools.listForKendoDropdown(lstIndent,'indent_date',null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    private static final String PROC_INDENT_SELECT_QUERY = """
            SELECT indent.id, indent.id || ' (From ' || to_char(from_date,'dd-Mon-yyyy') || ' To ' || to_char(to_date,'dd-Mon-yyyy')||')' AS indent_date
            FROM proc_indent indent
            WHERE project_id =:projectId
            AND indent.id IN(
                    SELECT id FROM proc_indent
                    WHERE project_id = :projectId
                    AND to_date >= :newDate
                    AND item_count > 0
                    AND approved_by > 0
            )
            ORDER BY indent.id ASC
    """

    private List<GroovyRowResult> listByProjectAndIndentId(long projectId) {
        Date newDate = DateUtility.getSqlDate(new Date())
        Map queryParams = [
                projectId: projectId,
                newDate: newDate
        ]
        List<GroovyRowResult> lstIntend = executeSelectSql(PROC_INDENT_SELECT_QUERY, queryParams);
        return lstIntend
    }
}