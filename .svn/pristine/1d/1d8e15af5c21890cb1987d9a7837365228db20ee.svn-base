package com.athena.mis.procurement.actions.indent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show list of un approved indent for grid
 * For details go through Use-Case doc named 'ListUnApprovedIndentActionService'
 */
class ListUnApprovedIndentActionService extends BaseService implements ActionIntf {

    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to populate un-approved Indent list"
    private static final String UNAPPROVED_LIST = "unApprovedList"
    private static final String GRID_OBJ = "gridObj"
    private static final String INDENT_ID = "id"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get un-approved indent list for grid
     * 1. get all projects associated with user.
     * 2. get all un-approved indents associated those projects.
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            parameterMap.sortname = INDENT_ID
            if (!parameterMap.rp) {
                parameterMap.rp = 10  // default result per page =10
                parameterMap.page = 1
            }
            initPager(parameterMap)
            List<Long> projectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
            List<ProcIndent> upApprovedList = []
            int count = 0

            if (projectIds.size() > 0) {
                Date currentDate = DateUtility.getOnlyDate(new Date() - 1)
                upApprovedList = ProcIndent.findAllByProjectIdInListAndToDateGreaterThanAndApprovedBy(projectIds, currentDate, 0, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                count = ProcIndent.countByProjectIdInListAndToDateGreaterThanAndApprovedBy(projectIds, currentDate, 0)
            }
            result.put(UNAPPROVED_LIST, upApprovedList)
            result.put(Tools.COUNT, count)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap un-approved indent list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj

            List<ProcIndent> unApprovedList = (List<ProcIndent>) executeResult.get(UNAPPROVED_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List gridRows = wrapPo(unApprovedList, start)
            Map gridObj = [page: pageNumber, total: count, rows: gridRows]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Wrap list of un-approved indent in grid entity
     * @param indentList -list of un-approved indent object(s)
     * @param start -starting index of the page
     * @return -list of wrapped un-approved indent
     */
    private List wrapPo(List<ProcIndent> unApprovedList, int start) {
        List lstUnapprovedPOs = []
        int counter = this.start + 1
        AppUser user
        for (int i = 0; i < unApprovedList.size(); i++) {
            ProcIndent procIndent = unApprovedList[i]
            GridEntity obj = new GridEntity()
            obj.id = procIndent.id
            user = (AppUser) appUserCacheUtility.read(procIndent.createdBy)
            obj.cell = [
                    counter,
                    procIndent.id,
                    user.username,
                    procIndent.itemCount,
                    DateUtility.getLongDateForUI(procIndent.createdOn)
            ]
            counter++
            lstUnapprovedPOs << obj
        }
        return lstUnapprovedPOs
    }
}
