package com.athena.mis.projecttrack.actions.ptproject

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search specific list of Project
 *  For details go through Use-Case doc named 'SearchPtProjectActionService'
 */
class SearchPtProjectActionService extends BaseService implements ActionIntf {

    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load project List"
    private static final String LST_PROJECTS = "lstProjects"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get Project list through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initSearch(parameters)
            // get sub list and count of Project by search keyword from cache utility
            Map searchResult = ptProjectCacheUtility.search(queryType, query, this)
            List<PtProject> lstProjects = searchResult.list
            int count = searchResult.count
            result.put(LST_PROJECTS, lstProjects)
            result.put(Tools.COUNT, count.toInteger())
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
     * Wrap Project list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<PtProject> lstProjects = (List<PtProject>) executeResult.get(LST_PROJECTS)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedProjects = wrapProjects(lstProjects, start)        // Wrap list of Project in grid entity
            Map output = [page: pageNumber, total: count, rows: lstWrappedProjects]
            return output
        } catch (Exception ex) {
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
                LinkedHashMap preResult = (LinkedHashMap) obj
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
     * Wrap list of Project in grid entity
     * @param lstProjects -list of Project object
     * @param start -starting index of the page
     * @return -list of wrapped Projects
     */
    private List wrapProjects(List<PtProject> lstProjects, int start) {
        List lstWrappedProjects = []
        int counter = start + 1
        for (int i = 0; i < lstProjects.size(); i++) {
            PtProject project = lstProjects[i]
            GridEntity obj = new GridEntity()
            obj.id = project.id
            obj.cell = [
                    counter,
                    project.id,
                    project.name,
                    project.code
            ]
            lstWrappedProjects << obj
            counter++
        }
        return lstWrappedProjects
    }
}
