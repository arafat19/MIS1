package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.ApplicationSessionService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpSession

/**
 *  Show list of online users in grid
 *  For details go through Use-Case doc named 'ListOnlineUserActionService'
 */
class ListOnlineUserActionService extends BaseService implements ActionIntf {

    ApplicationSessionService applicationSessionService

    private static final String FAILED_TO_POPULATE_LIST = "Failed to populate online user list"
    private static final String SESSION_USER_MAP = "sessionUserMap"
    private static final String GRID_OBJ = "gridObj"
    private static final String DATE_FORMAT = "dd-MMM-yy [hh:mm:ss a]"
    private static final String AGO = ' ago'

    private Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of online user for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap) // initialize parameters for flexGrid
            Map sessionUserMap = list() // get list of online user
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(SESSION_USER_MAP, sessionUserMap)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_POPULATE_LIST)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap appUser list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            Map sessionUserMap = (Map) executeResult.get(SESSION_USER_MAP)
            List<Map> lstSessionInfo = sessionUserMap.sessionInfo
            List wrappedInfo = wrapSessionInfo(lstSessionInfo, start)   // wrap user list in grid entity
            Map gridObj = [page: pageNumber, total: sessionUserMap.count, rows: wrappedInfo]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_POPULATE_LIST)
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
            result.put(Tools.MESSAGE, FAILED_TO_POPULATE_LIST)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_POPULATE_LIST)
            return result
        }
    }

    /**
     * Wrap list of appUser in grid entity
     * @param lstSessionInfo -list of online user
     * @param start -starting index of the page
     * @return -list of wrapped appUser
     */
    private List wrapSessionInfo(List<Map> lstSessionInfo, int start) {
        List lstWrappedInfo = []
        int counter = start + 1
        for (int i = 0; i < lstSessionInfo.size(); i++) {
            Map eachModel = lstSessionInfo[i]
            HttpSession currSession = eachModel.session
            AppUser appUser = eachModel.appUser // get appUser
            String lastActivity = DateUtility.getDifference(new Date(currSession.lastAccessedTime), new Date()) + AGO
            String loginTime = new Date(currSession.creationTime).format(DATE_FORMAT)
            GridEntity obj = new GridEntity()
            obj.id = currSession.id
            obj.cell = [
                    counter,
                    appUser.id,
                    eachModel.appUser.username,
                    appUser.loginId,
                    loginTime,
                    eachModel.clientIP,
                    eachModel.clientBrowser,
                    eachModel.clientOS,
                    lastActivity
            ]
            lstWrappedInfo << obj
            counter++
        }
        return lstWrappedInfo
    }

    /**
     * Get list and count of online user
     * @return -a map containing list and count of online user
     */
    private Map list() {
        List<Map> result = []
        List<Map> lstSessionInfo = applicationSessionService.list()
        int max = (start + resultPerPage) > lstSessionInfo.size() ? lstSessionInfo.size() : (start + resultPerPage)
        for (int i = start; i < max; i++) {
            result << lstSessionInfo[i]
        }
        return [sessionInfo: result, count: lstSessionInfo.size()]
    }
}
