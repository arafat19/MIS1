package com.athena.mis.application.actions.appgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.utility.AppGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
/**
 *  List of user group(appGroup) for grid
 *  For details go through Use-Case doc named 'ListAppGroupActionService'
 */
class ListAppGroupActionService extends BaseService implements ActionIntf {

    @Autowired
    AppGroupCacheUtility appGroupCacheUtility

    private final Logger log = Logger.getLogger(getClass())
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Group page"

    private static final String USER_GROUP_LIST = "userGroupList"
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
       return null;
    }
    /**
     * Get user group(appGroup) list for grid
     * 1. initialize pagination for Flexi-grid
     * 2. pull group list filtered by company
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing user group list necessary for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)

            int count = appGroupCacheUtility.count()
            List groupList = appGroupCacheUtility.list(this)

            result.put(USER_GROUP_LIST, groupList)
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
     * Wrap user group(appGroup) list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List groupList = (List) executeResult.get(USER_GROUP_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List resultList = wrapListInGridEntityList(groupList, start)
            Map output = [page: pageNumber, total: count, rows: resultList]
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
                LinkedHashMap receivedResult = (LinkedHashMap) obj
                String receivedMessage = receivedResult.get(Tools.MESSAGE)
                if (receivedMessage) {
                    result.put(Tools.MESSAGE, receivedMessage)
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
     * Wrap list of user group(appGroup) in grid entity
     * @param groupList -list of user group(appGroup)
     * @param start -starting index of the page
     * @return -list of wrapped user group(appUser)
     */
    private List wrapListInGridEntityList(List<AppGroup> groupList, int start) {
        List userGroups = [] as List
        int counter = start + 1
        for (int i = 0; i < groupList.size(); i++) {
            GridEntity obj = new GridEntity()
            obj.id = groupList[i].id
            obj.cell = [
                    counter,
                    groupList[i].id,
                    groupList[i].name
            ]
            userGroups << obj
            counter++
        }
        return userGroups
    }
}
