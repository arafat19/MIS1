package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for Backlog CRUD and list of Backlog for grid
 *  For details go through Use-Case doc named 'ShowPtBacklogActionService'
 */
class ShowPtBacklogActionService extends BaseService implements ActionIntf{

	PtBacklogService ptBacklogService
    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    PtProjectModuleService ptProjectModuleService
	@Autowired
	AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
	@Autowired
	PtSessionUtil ptSessionUtil
	@Autowired
	PtModuleCacheUtility ptModuleCacheUtility
	@Autowired
	PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
	@Autowired
	PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility

	private final Logger log = Logger.getLogger(getClass())

	private static final String DEFAULT_FAILURE_MSG_SHOW_BACKLOG = "Failed to load backlog page"
	private static final String LST_BACKLOG = "lstBacklog"
	private static final String GRID_OBJ = "gridObj"

	/**
	 * Do nothing for pre operation
	 */
	public Object executePreCondition(Object params, Object obj) {
		return null
	}

	/**
	 * Get Backlog list for grid
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
            List<Long> lstModuleIds = mappedUserModule()
            LinkedHashMap backlogList = getBacklogList(lstModuleIds)
            List<GroovyRowResult> lstBacklog = backlogList.searchResult
            int count = (int) backlogList.count
            result.put(LST_BACKLOG, lstBacklog)
			result.put(Tools.COUNT, count)
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BACKLOG)
			return result
		}
	}

	/**
	 * Do nothing for pre operation
	 */
	public Object executePostCondition(Object params, Object obj) {
		return null
	}

	/**
	 * Wrap Backlog list for grid
	 * @param obj -map returned from execute method
	 * @return -a map containing all objects necessary for show page
	 * map -contains isError(true/false) depending on method success
	 */
	public Object buildSuccessResultForUI(Object obj) {
		Map result = new LinkedHashMap()
		try {
			Map executeResult = (Map) obj   // cast map returned from execute method
			List<GroovyRowResult> lstBacklog = (List) executeResult.get(LST_BACKLOG)
			Integer count = (Integer) executeResult.get(Tools.COUNT)
			List<GroovyRowResult> lstWrappedBacklogs = wrapBacklogs(lstBacklog, start)
			Map gridObj = [page: pageNumber, total: count, rows: lstWrappedBacklogs]
			result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BACKLOG)
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
			result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BACKLOG)
			return result
		} catch (Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BACKLOG)
			return result
		}
	}

	/**
	 * Wrap list of Backlog in grid entity
	 * @param lstBacklog -list of Backlog object(s)
	 * @param start -starting index of the page
	 * @return -list of wrapped Backlogs
	 */
	private List wrapBacklogs(List<GroovyRowResult> lstBacklog, int start) {
		List lstWrappedBacklogs = []
		int counter = start + 1
		for (int i = 0; i < lstBacklog.size(); i++) {
            GroovyRowResult backlog = lstBacklog[i]
			GridEntity obj = new GridEntity()
            obj.id = backlog.id
            obj.cell = [
                    counter,
                    backlog.id,
                    backlog.code,
                    backlog.priority,
                    backlog.purpose,
                    backlog.benefit,
                    backlog.count,
                    backlog.username
            ]
			lstWrappedBacklogs << obj
			counter++
		}
		return lstWrappedBacklogs
	}
    /**
     * Get those module ids that are mapped with logged user
     * @return - list of mapped user module ids
     */
    private List<Long> mappedUserModule(){
        List<Long> lstModuleIds = []
        List<Long> lstProjectIds = (List<Long>) ptSessionUtil.getUserPtProjectIds()
        List<PtProjectModule> lstProjectModule = ptProjectModuleService.findAllByProjectIdInList(lstProjectIds)

        for (int i = 0; i < lstProjectModule.size(); i++) {
            lstModuleIds << lstProjectModule[i].moduleId
        }
        if (lstModuleIds.size() == 0) {
            lstModuleIds << 0L
        }
        return lstModuleIds
    }

    /**
     * Get backlog list
     * @return -a lis of sprint according to getBacklogList result
     */
    private LinkedHashMap getBacklogList(List<Long> lstModuleIds) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        String lstUserModuleIds = Tools.buildCommaSeparatedStringOfIds(lstModuleIds)
        SystemEntity entityType = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.PT_PROJECT, companyId)

        String str_query = """
            SELECT backlog.id id,module.code code,priority.key priority,backlog.purpose,backlog.benefit,au.username username,
                (SELECT COUNT(acceptance.id) FROM pt_acceptance_criteria acceptance WHERE acceptance.backlog_id = backlog.id AND acceptance.company_id = :companyId) AS count
            FROM pt_backlog backlog
                LEFT JOIN system_entity priority ON priority.id = backlog.priority_id
                LEFT JOIN pt_module module ON module.id = backlog.module_id
                LEFT JOIN pt_project_module pm ON pm.module_id = backlog.module_id
                LEFT JOIN pt_project project ON project.id = pm.project_id
                LEFT JOIN app_user_entity ape ON ape.entity_id = project.id
                LEFT JOIN app_user au ON au.id = backlog.created_by
            WHERE backlog.company_id =:companyId
                AND module.id IN (${lstUserModuleIds})
                AND backlog.sprint_id = 0
            GROUP BY backlog.id, backlog.module_id,module.code, priority.key,backlog.purpose, backlog.benefit,au.username
            ORDER BY module.code ASC
            LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                companyId: companyId,
                appUserId: ptSessionUtil.appSessionUtil.getAppUser().id,
                entityTypeId: entityType.id,
                resultPerPage: resultPerPage,
                start: start
        ]

        String str_count = """
            SELECT COUNT(backlog.id)
            FROM pt_backlog backlog
             LEFT JOIN pt_module module ON module.id = backlog.module_id
            WHERE backlog.company_id = :companyId
                AND module.id IN (${lstUserModuleIds})
                AND backlog.sprint_id = 0
        """

        List<GroovyRowResult> lstResult = executeSelectSql(str_query, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(str_count, queryParams)

        int total = (int) countResult[0].count
        return [searchResult: lstResult, count: total]
    }

}
