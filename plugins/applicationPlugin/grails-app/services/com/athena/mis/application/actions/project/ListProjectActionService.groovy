package com.athena.mis.application.actions.project

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of project for grid
 *  For details go through Use-Case doc named 'ListProjectActionService'
 */
class ListProjectActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search project grid"

    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1. check user access as Admin role
     * @param params -N/A
     * @param obj - N/A
     * @return - a map containing isAccess(true/false) depending on method success &  relevant message.
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }
    /**
     * 1. initialize params for pagination of Flexi-grid
     * 2. pull project list from cache utility
     * @param params -serialize parameters from UI
     * @param obj -N/A
     * @return - project list
     */
    public Object execute(Object params, Object obj = null) {
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPagerKendo(parameterMap)
            int count = projectCacheUtility.count()
            List projectList = projectCacheUtility.list(this)
            return [projectList: projectList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }
    /**
     * Wrap list of projects in grid entity
     * @param projectList -list of projects
     * @param start -starting index of the page
     * @return -list of wrapped projects
     */
    private List wrapListInGridEntityList(List<Project> projectList, int start) {
        List projects = [] as List
        try {
            for (int i = 0; i < projectList.size(); i++) {
                Project project = projectList[i]
                Map gridRow = [id: project.id, name: project.name, code: project.code, description: project.description, contentCount: project.contentCount, createdOn: project.createdOn]
                projects << gridRow
            }
            return projects
        } catch (Exception ex) {
            log.error(ex.getMessage())
            projects = []
            return projects
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap project list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<Project> projectList = (List<Project>) executeResult.projectList
            int count = (int) executeResult.count
            List projects = wrapListInGridEntityList(projectList, start)
            return [data: projects,total: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
             return [data: [],total: 0]
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -N/A
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
        return result
    }
}
