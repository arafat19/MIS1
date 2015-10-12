package com.athena.mis.application.actions.role

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get search result list of roleObject to show on grid
 *  For details go through Use-Case doc named 'SearchRoleActionService'
 */
class SearchRoleActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    RoleService roleService
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search role grid list"

    /**
     * check access permission to search roleList
     * @param parameters -N/A
     * @param obj -N/A
     * @return -boolean value of access permission(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            //Only admin has right to search roleObjectList
            if (appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }

    /**
     * get search result list of roleObjects
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains roleList and count
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj = null) {
        LinkedHashMap result
        try {
            GrailsParameterMap parameters = (GrailsParameterMap) params
            initSearch(parameters)
            result = roleService.search(this)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap roleObjectList to show on grid
     * @param obj -a map contains roleObjectList and count
     * @return -wrapped roleObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<Role> roleList = (List<Role>) executeResult.roleList
            int count = (int) executeResult.count
            List inventory = wrapListInGridEntityList(roleList, start)
            return [page: pageNumber, total: count, rows: inventory]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
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
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrappedRoleObjectList for grid
     * @param roleList -list of role objects
     * @param start -start index
     * @return -wrappedRoleObjectList
     */
    private List wrapListInGridEntityList(List<Role> roleList, int start) {
        List roles = [] as List
        int counter = start + 1
        for (int i = 0; i < roleList.size(); i++) {
            Role role = roleList[i]
            GridEntity obj = new GridEntity()
            obj.id = role.id
            obj.cell = [
                    counter,
                    role.id,
                    role.name
            ]
            roles << obj
            counter++
        }
        return roles
    }
}
