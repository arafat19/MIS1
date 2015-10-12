package com.athena.mis.budget.actions.budgsprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.service.BudgSprintService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search sprint by specific key word and show specific list of sprints for grid
 *  For details go through Use-Case doc named 'SearchBudgSprintActionService'
 */
class SearchBudgSprintActionService extends BaseService implements ActionIntf {

    BudgSprintService budgSprintService
    @Autowired
    BudgSessionUtil budgSessionUtil

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load sprint list"
    private static final String LST_SPRINT = "lstSprint"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get sprint list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(parameterMap)
            long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
            List<BudgSprint> lstSprint = budgSprintService.findAllByNameIlikeAndCompanyId(query, companyId, this)
            int count = budgSprintService.countByNameIlikeAndCompanyId(query, companyId)
            result.put(LST_SPRINT, lstSprint)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap sprint list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List lstSprint = (List) executeResult.get(LST_SPRINT)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedSprint = wrapSprintList(lstSprint, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedSprint]
            return output
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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

    /**
     * Wrap list of sprints in grid entity
     * @param lstSprint -list of sprint object(s)
     * @param start -starting index of the page
     * @return -list of wrapped sprints
     */
    private List wrapSprintList(List<BudgSprint> lstSprint, int start) {
        List lstWrappedSprint = []
        int counter = start + 1
        for (int i = 0; i < lstSprint.size(); i++) {
            BudgSprint sprint = lstSprint[i]
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = sprint.id
            obj.cell = [
                    counter,
                    sprint.id,
                    sprint.name,
                    sprint.isActive ? Tools.YES : Tools.NO
            ]
            lstWrappedSprint << obj
            counter++
        }
        return lstWrappedSprint
    }
}
