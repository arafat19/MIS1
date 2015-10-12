package com.athena.mis.accounting.actions.accipc

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get search result list of accIpc object(s) to show on grid
 *  For details go through Use-Case doc named 'SearchAccIpcActionService'
 */
class SearchAccIpcActionService extends BaseService implements ActionIntf {

    @Autowired
    AccSessionUtil accSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search IPC list"
    private static final String ACC_IPC_LIST = "accIpcList"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of accIpc object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accIpcList and count
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            initSearch(params)

            List<Long> projectIdList = []  //main list of projectIds
            List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
            if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                projectIdList << new Long(0)
            } else {  //if tempList is not null then set tempProjectIdList at main list
                projectIdList = tempProjectIdList
            }

            //get list of accIpc objects by projectIds
            LinkedHashMap accIpc = search(projectIdList)
            List<GroovyRowResult> accIpcList = accIpc.searchResult
            int total = (int) accIpc.count

            result.put(ACC_IPC_LIST, accIpcList)
            result.put(Tools.TOTAL, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap accIpcObject list to show on grid
     * @param obj -a map contains accIpcObject list and count
     * @return -wrapped accIpcObject list to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<GroovyRowResult> accIpcList = (List) receiveResult.get(ACC_IPC_LIST)
            int total = (int) receiveResult.get(Tools.TOTAL)
            List accIpcListWrap = wrapEntityIpcList(accIpcList, start)
            Map gridObject = [page: pageNumber, total: total, rows: accIpcListWrap]
            result.put(ACC_IPC_LIST, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
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
     * wrappedAccIpcObject list for grid
     * @param accIpcList -list of GroovyRowResult
     * @param start -start index
     * @return -wrappedAccIpcObject list
     */
    private List wrapEntityIpcList(List<GroovyRowResult> accIpcList, int start) {
        List accIpc = [] as List
        int counter = start + 1
        for (int i = 0; i < accIpcList.size(); i++) {
            GroovyRowResult singleRow = accIpcList[i]
            GridEntity obj = new GridEntity()
            obj.id = singleRow.id
            obj.cell = [
                    counter,
                    singleRow.id,
                    singleRow.ipc_no,
                    singleRow.project_name
            ]
            accIpc << obj
            counter++
        }
        return accIpc
    }

    /**
     * get search result list of accIpc object by user mapped projectIds
     * @param lstProjectIds -Project.id
     * @return -a map contains accIpc objects and count
     */
    private LinkedHashMap search(List<Long> lstProjectIds) {
        String strProjectIds = Tools.buildCommaSeparatedStringOfIds(lstProjectIds)

        String strQuery = """
               SELECT ipc.id id, ipc.ipc_no ipc_no, p.name project_name
                    FROM acc_ipc ipc
                    LEFT JOIN project p ON p.id = ipc.project_id
                    WHERE ${queryType} ilike :query
                            AND ipc.project_id IN (${strProjectIds})
                    ORDER BY ipc.id desc
                    LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]

        String queryCount = """
            SELECT COUNT(ipc.id) count
                    FROM acc_ipc ipc
                    LEFT JOIN project p ON p.id = ipc.project_id
                    WHERE ${queryType} ilike :query
                            AND ipc.project_id IN (${strProjectIds})
        """

        List<GroovyRowResult> result = executeSelectSql(strQuery, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)

        int total = (int) countResult[0].count
        return [searchResult: result, count: total]
    }
}
