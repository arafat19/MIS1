package com.athena.mis.projecttrack.actions.ptSprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.utility.PtSprintStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for ptSprint CRUD and list of ptSprint for grid
 *  For details go through Use-Case doc named 'PtShowSprintActionService'
 */
class PtShowSprintActionService extends BaseService implements ActionIntf {

    SystemEntityService systemEntityService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtSprintStatusCacheUtility ptSprintStatusCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MSG_SHOW_SPRINT = "Failed to load ptSprint page"
    private static final String LST_SPRINT = "lstSprint"
    private static final String GRID_OBJ = "gridObj"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get ptSprint list for grid
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
            initPager(parameterMap)                     // initialize parameters for flexGrid
            List<GroovyRowResult> lstSprint = list()
            int count = lstSprint.size()
            result.put(LST_SPRINT, lstSprint)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_SPRINT)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }

    /**
     * Wrap ptSprint list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<GroovyRowResult> sprintList = (List<GroovyRowResult>) executeResult.get(LST_SPRINT)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List wrappedSprint = wrapSprintListGrid(sprintList, start)
            Map gridOutput = [page: pageNumber, total: count, rows: wrappedSprint]
            result.put(GRID_OBJ, gridOutput)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_SPRINT)
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
            if (!obj) {
                result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_SPRINT)
                return result
            }
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from execute method
            result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_SPRINT)
            return result
        }
    }

    private static final String LIST_QUERY = """
        SELECT ps.id, ps.name, ps.is_active, se.key, COUNT(pb.id) AS story_count,
         (SELECT COUNT(bug.id) FROM pt_bug bug WHERE bug.sprint_id = ps.id AND bug.company_id = :companyId) AS bug_count
        FROM pt_sprint ps
            LEFT JOIN pt_backlog pb ON pb.sprint_id = ps.id
            LEFT JOIN system_entity se ON se.id = ps.status_id
            LEFT JOIN app_user_entity ape ON ape.entity_id = ps.project_id
            LEFT JOIN app_user ap ON ap.id = ape.app_user_id
        WHERE ps.company_id = :companyId
            AND ape.entity_type_id = :entityTypeId
            AND ap.id = :appUserId
        GROUP BY ps.id, ps.name, ps.is_active, se.key, ap.username
        ORDER BY ps.id DESC
        LIMIT :resultPerPage OFFSET :start

    """

    /**
     * Get list of sprint for grid
     * @return -a list of sprint
     */
    private List list() {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity entityType = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.PT_PROJECT, companyId)
        Map queryParams = [
                companyId: companyId,
                appUserId: ptSessionUtil.appSessionUtil.getAppUser().id,
                entityTypeId: entityType.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstResult = executeSelectSql(LIST_QUERY, queryParams)
        return lstResult
    }

    /**
     * wrap list of ptSprint in grid entity
     * @param lstSprint -list of ptSprint objects
     * @param start - starting index of the page
     * @return -list of wrapped sprints
     */
    private List wrapSprintListGrid(List<GroovyRowResult> lstSprint, int start) {
        List ptSprintList = []
        int counter = start + 1
        for (int i = 0; i < lstSprint.size(); i++) {
            GroovyRowResult ptSprint = lstSprint[i]
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = ptSprint.id
            obj.cell = [
                    counter,
                    ptSprint.id,
                    ptSprint.name,
                    ptSprint.key,
                    ptSprint.is_active ? Tools.YES : Tools.NO,
                    ptSprint.story_count,
                    ptSprint.bug_count
            ]
            ptSprintList << obj
            counter++
        }
        return ptSprintList;
    }
}
