package com.athena.mis.projecttrack.actions.ptproject

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Get list of Project
 * For details go through Use-Case doc named 'ShowPtProjectActionService'
 */
class ShowPtProjectActionService extends BaseService implements ActionIntf {

    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String LST_PROJECTS = "lstProjects"
    private static final String DEFAULT_FAILURE_MSG_SHOW_PROJECT = "Failed to load project page"
    private static final String GRID_OBJ = "gridObj"

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
     * Get Project list and count of total Project for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)         // initialize parameters
            // get sub list of Project from cache utility
            List<PtProject> lstProjects = (List<PtProject>) ptProjectCacheUtility.list(this)
            int count = ptProjectCacheUtility.count()       // get count of total Project from cache utility
            result.put(LST_PROJECTS, lstProjects)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROJECT)
            return result
        }
    }

    /**
     * Wrap Project list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj                   // cast map returned from execute method
            List<PtProject> lstProjects = (List<PtProject>) executeResult.get(LST_PROJECTS)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedProjects = wrapProjects(lstProjects, start)        // Wrap list of Project in grid entity
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedProjects]
            result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROJECT)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROJECT)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROJECT)
            return result
        }
    }

    /**
     * Wrap list of Project in grid entity
     * @param lstProjects -list of Project object(s)
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
