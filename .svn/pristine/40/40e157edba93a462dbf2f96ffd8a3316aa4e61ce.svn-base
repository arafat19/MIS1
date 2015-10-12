package com.athena.mis.procurement.actions.indent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search indent and show related list of indent for grid
 *  For details go through Use-Case doc named 'SearchIndentActionService'
 */
class SearchIndentActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to Load indent list"
    private static final String INVALID_INDENT_TRACE_NO = "Invalid indent trace no"
    private static final String INDENT_NOT_FOUND = "Indent not found for trace no: "
    private static final String INDENT_LIST = "indentList"
    private static final String COUNT = "count"
    private static final String ID = "id"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get indent list for grid through specific search
     * 1. pull indent instance
     * 2. pull project id list
     * 3. pull indent list by project ids
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(parameterMap)

            List<ProcIndent> indentList = []
            int count = 0
            long indentId

            try {
                indentId = Long.parseLong(query.toString())
                ProcIndent indent = ProcIndent.read(indentId)
                if (!indent) {
                    result.put(Tools.MESSAGE, INDENT_NOT_FOUND + indentId)
                    return result
                }
            } catch (Exception ex) {
                result.put(Tools.MESSAGE, INVALID_INDENT_TRACE_NO)
                return result
            }

            List<Long> projectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
            if (projectIds.size() > 0) {
                if (this.queryType.equals(ID)) {         // search by id
                    indentList = ProcIndent.findAllByProjectIdInListAndId(projectIds, indentId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                    count = ProcIndent.countByProjectIdInListAndId(projectIds, indentId)
                } else {
                    indentList = ProcIndent.findAllByProjectIdInList(projectIds, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                    count = ProcIndent.countByProjectIdInList(projectIds)
                }
            }

            result.put(INDENT_LIST, indentList)
            result.put(COUNT, count)
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
     * Wrap indent list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List indentList = (List) executeResult.get(INDENT_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List resultList = wrapListInGridEntityList(indentList, start)
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
     * 1. pull project instance
     * 2. wrap list of indent in grid entity
     * @param indentList -list of indent object(s)
     * @param start -starting index of the page
     * @return -list of wrapped indent
     */
    private List wrapListInGridEntityList(List<ProcIndent> indentList, int start) {
        List lstIndent = [] as List
        int counter = start + 1

        for (int i = 0; i < indentList.size(); i++) {
            Project project = (Project) projectCacheUtility.read(indentList[i].projectId)
            GridEntity obj = new GridEntity()
            obj.id = indentList[i].id
            String fromDate = DateUtility.getLongDateForUI(indentList[i].fromDate)
            String toDate = DateUtility.getLongDateForUI(indentList[i].toDate)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(indentList[i].createdBy)
            obj.cell = [
                    counter,
                    indentList[i].id,
                    project.name,
                    fromDate,
                    toDate,
                    indentList[i].itemCount,
                    (indentList[i].approvedBy > 0) ? Tools.YES : Tools.NO,
                    Tools.formatAmountWithoutCurrency(indentList[i].totalPrice),
                    createdBy.username
            ]
            lstIndent << obj
            counter++
        }
        return lstIndent
    }
}