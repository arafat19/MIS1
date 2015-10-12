package com.athena.mis.exchangehouse.actions.agent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

class SearchExhAgentActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Agent list"
    private static final String EXH_AGENT_LIST = "agentList"


    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            this.initSearch(parameters)
            List<GroovyRowResult> exhAgentList = exhAgentSearch()
            int count = exhAgentListCount()
            result.put(EXH_AGENT_LIST, exhAgentList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }

    }

    private List wrapListInGridEntityList(List<GroovyRowResult> exhAgentList, int start) {
        List<GroovyRowResult> exhAgentLst = []
        int counter = start + 1
        for (int i = 0; i < exhAgentList.size(); i++) {
            GridEntity obj = new GridEntity()
            GroovyRowResult exhAgentEachRow = exhAgentList[i]
            obj.id = exhAgentEachRow.id

            obj.cell = [
                    counter,
                    exhAgentEachRow.id,
                    exhAgentEachRow.name,
                    exhAgentEachRow.address,
                    exhAgentEachRow.city,
                    exhAgentEachRow.phone,
                    exhAgentEachRow.symbol,
                    exhAgentEachRow.balance,
                    exhAgentEachRow.creditLimit
            ]
            exhAgentLst << obj
            counter++
        }
        return exhAgentLst
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> exhAgentList = (List<GroovyRowResult>) executeResult.get(EXH_AGENT_LIST)
            int count = (int) executeResult.get(Tools.COUNT)

            List resultList = wrapListInGridEntityList(exhAgentList, this.start)
            Map output = [page: this.pageNumber, total: count, rows: resultList]
            result.put(EXH_AGENT_LIST, output)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: this.pageNumber, total: 0, rows: null]
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap receiveResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        }
        catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }

    }

    // It will return list of Agent details
    private List<GroovyRowResult> exhAgentSearch() {
        String strQuery =
            """
                 SELECT eac.id, eac.address, eac.city, eac.phone, eac.name, eac.balance, eac.credit_limit as "creditLimit", ec.symbol as symbol
                    FROM exh_agent eac
                    JOIN currency ec ON ec.id = eac.currency_id
                    WHERE eac.name ilike :query
                    LIMIT :resultPerPage OFFSET :start
            """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage, start: start
        ]
        List<GroovyRowResult> lstExhAgent = executeSelectSql(strQuery, queryParams)
        return lstExhAgent
    }

    // It will return count of Agent details
    private int exhAgentListCount() {
        String queryCount =
            """SELECT COUNT(eac.id) FROM exh_agent eac
           LEFT JOIN currency ec ON ec.id = eac.currency_id
           WHERE eac.name ilike :query
        """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE
        ]
        List<GroovyRowResult> result = executeSelectSql(queryCount, queryParams)
        int count = (int) result[0][0]
        return count
    }
}
