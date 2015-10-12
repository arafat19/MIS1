package com.athena.mis.projecttrack.actions.ptacceptancecriteria

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtAcceptanceCriteria
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for AcceptanceCriteria CRUD and list of AcceptanceCriteria for grid
 *  For details go through Use-Case doc named 'ShowPtAcceptanceCriteriaActionService'
 */
class ShowPtAcceptanceCriteriaActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    PtAcceptanceCriteriaTypeCacheUtility ptAcceptanceCriteriaTypeCacheUtility
    @Autowired
    PtAcceptanceCriteriaStatusCacheUtility ptAcceptanceCriteriaStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MSG_SHOW_ACCEPTANCE_CRITERIA = "Failed to load acceptance criteria page"
    private static final String LST_ACCEPTANCE_CRITERIA = "lstAcceptanceCriteria"
    private static final String GRID_OBJ = "gridObj"
    private static final String BACKLOG_ID = "backlogId"
    private static final String BACKLOG_IDEA = "backlogIdea"

    /**
     * Get parameters from UI and check if backlogId exists in parameterMap
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.backlogId) {
                result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_ACCEPTANCE_CRITERIA)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_ACCEPTANCE_CRITERIA)
            return result
        }
    }

    /**
     * Get AcceptanceCriteria list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            long backlogId = Long.parseLong(parameterMap.backlogId.toString())
            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            List<PtAcceptanceCriteria> lstAcceptanceCriteria = ptAcceptanceCriteriaService.findAllByCompanyIdAndBacklogId(this, companyId, backlogId)
            // get ptAcceptanceCriteria List from map
            int count = ptAcceptanceCriteriaService.countByCompanyIdAndBacklogId(companyId, backlogId)
            // get total count from map
            PtBacklog backlog = (PtBacklog) ptBacklogService.read(backlogId)
            result.put(LST_ACCEPTANCE_CRITERIA, lstAcceptanceCriteria)
            result.put(BACKLOG_ID, backlogId)
            result.put(BACKLOG_IDEA, backlog.idea)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_ACCEPTANCE_CRITERIA)
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
     * Wrap AcceptanceCriteria list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            Map executeResult = (Map) obj   // cast map returned from execute method
            List<PtAcceptanceCriteria> lstAcceptanceCriteria = (List<PtAcceptanceCriteria>) executeResult.get(LST_ACCEPTANCE_CRITERIA)
            Long backlogId = (Long) executeResult.get(BACKLOG_ID)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List wrappedAcceptanceCriteria = wrapAcceptanceCriteria(lstAcceptanceCriteria, start)
            Map gridObj = [page: pageNumber, total: count, rows: wrappedAcceptanceCriteria]
            result.put(BACKLOG_ID, backlogId)
            result.put(GRID_OBJ, gridObj)
            result.put(BACKLOG_IDEA, executeResult.get(BACKLOG_IDEA))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_ACCEPTANCE_CRITERIA)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_ACCEPTANCE_CRITERIA)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_ACCEPTANCE_CRITERIA)
            return result
        }
    }

    /**
     * Wrap list of AcceptanceCriteria in grid entity
     * @param lstAcceptanceCriteria -list of AcceptanceCriteria object(s)
     * @param start -starting index of the page
     * @return -list of wrapped AcceptanceCriteria
     */
    private List wrapAcceptanceCriteria(List<PtAcceptanceCriteria> lstAcceptanceCriteria, int start) {
        List lstWrappedAcceptanceCriteria = []
        int counter = start + 1
        for (int i = 0; i < lstAcceptanceCriteria.size(); i++) {
            PtAcceptanceCriteria acceptanceCriteria = lstAcceptanceCriteria[i]
            AppUser appUser = (AppUser) appUserCacheUtility.read(acceptanceCriteria.createdBy)
            String accCreatedBy = appUser ? appUser.username : Tools.EMPTY_SPACE
            SystemEntity accCriteriaType = (SystemEntity) ptAcceptanceCriteriaTypeCacheUtility.read(acceptanceCriteria.type)
            SystemEntity acceptanceCriteriaStatus = (SystemEntity) ptAcceptanceCriteriaStatusCacheUtility.read(acceptanceCriteria.statusId)
            GridEntity obj = new GridEntity()
            obj.id = acceptanceCriteria.id
            obj.cell = [
                    counter,
                    accCriteriaType.key,
                    acceptanceCriteria.criteria,
                    acceptanceCriteriaStatus.key,
                    DateUtility.getDateTimeFormatAsString(acceptanceCriteria.createdOn),
                    accCreatedBy
            ]
            lstWrappedAcceptanceCriteria << obj
            counter++
        }
        return lstWrappedAcceptanceCriteria
    }
}
