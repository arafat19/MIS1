package com.athena.mis.projecttrack.actions.ptflow

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtFlow
import com.athena.mis.projecttrack.service.PtFlowService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ListPtFlowActionService extends BaseService implements ActionIntf {

    PtFlowService ptFlowService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load flow page"
    private static final String LST_FLOW = "lstFlow"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get PtFlow list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long backlogId = Long.parseLong(parameterMap.backlogId.toString())

            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            initPager(parameterMap) // initialize parameters for flexGrid
            // get flow List from map
            List<PtFlow> lstFlow = ptFlowService.findAllByCompanyIdAndBacklogId(this, companyId, backlogId)
            int count = ptFlowService.countByCompanyIdAndBacklogId(companyId, backlogId)
            // get total count from map
            result.put(LST_FLOW, lstFlow)
            result.put(Tools.COUNT, count.toInteger())
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Wrap PtFlow list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List<PtFlow> lstFlow = (List<PtFlow>) executeResult.get(LST_FLOW)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrapFlow = wrapFlow(lstFlow, start)
            Map output = [page: pageNumber, total: count, rows: lstWrapFlow]
            return output
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
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
     * Wrap list of PtFlow in grid entity
     * @param lstFlow -list of PtFlow object(s)
     * @param start -starting index of the page
     * @return -list of wrapped PtFlow
     */
    private List wrapFlow(List<PtFlow> lstFlow, int start) {
        List lstWrappedFlow = []
        int counter = start + 1
        for (int i = 0; i < lstFlow.size(); i++) {
            PtFlow ptFlow = lstFlow[i]
            AppUser appUser = (AppUser) appUserCacheUtility.read(ptFlow.createdBy)
            String accCreatedBy = appUser ? appUser.username : Tools.EMPTY_SPACE
            GridEntity obj = new GridEntity()
            obj.id = ptFlow.id
            obj.cell = [
                    counter,
                    ptFlow.id,
                    ptFlow.flow,
                    DateUtility.getDateTimeFormatAsString(ptFlow.createdOn),
                    accCreatedBy
            ]
            lstWrappedFlow << obj
            counter++
        }
        return lstWrappedFlow
    }
}
