package com.athena.mis.projecttrack.actions.ptprojectmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.utility.PtProjectModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search specific list of ptProjectModule
 *  For details go through Use-Case doc named 'SearchPtProjectModuleActionService'
 */
class SearchPtProjectModuleActionService extends BaseService implements ActionIntf {

    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_PROJECT_MODULE_FAILURE_MESSAGE = "Failed to load project module page"
    private static final String LST_PROJECT_MODULE = "lstPtProjectModule"
    private static final String PROJECT_NOT_FOUND = "Project not found"
    /**
     * Do nothing for pre condition
     */
    public Object executePreCondition(Object params, Object obj) {

        return null
    }
    /**
     * Get ptProjectModule list for grid through specific search
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
            initSearch(parameterMap) // initialize parameters for flexGrid
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            // checking is system entity type id or not
            if (projectId < 0) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }
            //search ptProjectModule object from DB
            List<GroovyRowResult> lstProjectModule = search(projectId, this)
            Integer count = searchCount(projectId, this)
            result.put(LST_PROJECT_MODULE, lstProjectModule)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_PROJECT_MODULE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }
    /**
     * wrap ptProjectModule list for grid
     * @param obj -map returned from execute method
     * @return- map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List<GroovyRowResult> lstProjectModule = (List<GroovyRowResult>) executeResult.get(LST_PROJECT_MODULE)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedProjectModule = wrapProjectModule(lstProjectModule, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedProjectModule]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_PROJECT_MODULE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_PROJECT_MODULE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_PROJECT_MODULE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ptProjectModule in grid entity
     * @param lstProjectModule -list of ptProjectModule object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ptProjectModule
     */
    private List wrapProjectModule(List<GroovyRowResult> lstProjectModule, int start) {
        List lstWrappedProjectModule = []
        int counter = start + 1
        for (int i = 0; i < lstProjectModule.size(); i++) {
            GroovyRowResult eachRow = lstProjectModule[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.module_name
            ]
            lstWrappedProjectModule << obj
            counter++
        }
        return lstWrappedProjectModule
    }
    /**
     * Search Sub list of ptProjectModule objects based on baseService pagination & search criteria
     * strCountQuery - count to total list
     * @param baseService - BaseService object
     * @return a map containing [lstPtProjectModule - List of PtProjectModule objects , count: int count of PtProjectModule objects]
     */
    private List<GroovyRowResult> search(long projectId, BaseService baseService) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        String strQuery = """
            SELECT pm.id, m.name module_name
            FROM pt_project_module pm
                LEFT JOIN pt_module m ON pm.module_id= m.id
            WHERE pm.company_id=:companyId
                AND ${baseService.queryType} ilike :query
                AND pm.project_id = :projectId
            LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                companyId: companyId,
                projectId: projectId,
                query: Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE,
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstProjectModule = executeSelectSql(strQuery, queryParams)
        return lstProjectModule
    }

    private int searchCount(long projectId, BaseService baseService) {
        String searchCountQuery =
                """
            SELECT COUNT(pm.id)
            FROM pt_project_module pm LEFT JOIN pt_module m ON pm.module_id=m.id
            WHERE pm.company_id = :companyId
            AND pm.project_id = :projectId
            AND ${baseService.queryType} ilike :query
        """

        Map queryParams = [
                query: Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE,
                companyId: ptSessionUtil.appSessionUtil.getCompanyId(),
                projectId: projectId
        ]
        List<GroovyRowResult> result = (List<GroovyRowResult>) executeSelectSql(searchCountQuery, queryParams)
        int count = (int) result[0][0]
        return count
    }
}
