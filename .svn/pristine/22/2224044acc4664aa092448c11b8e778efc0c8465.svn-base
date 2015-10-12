package com.athena.mis.exchangehouse.actions.agentCurrencyPosting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

class SearchExhAgentCurrencyPostingActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search agent currency posting"
    private static final String SEARCH_RESULT_LIST = "searchResultList"
    private static final String EXH_AGENT_CURRENCY_POSTING_LIST = "exhAgentCurrencyPostingList"

    public Object executePreCondition(Object parameters, Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Object executePostCondition(Object parameters, Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object execute(Object params, Object obj = null) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initSearch(params)
            Map searchResultList = searchExhAgentCurrencyPosting()
            result.put(SEARCH_RESULT_LIST, searchResultList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            Map searchResultList = (Map) executeResult.get(SEARCH_RESULT_LIST)
            List<GroovyRowResult> currencyPostingList = (List<GroovyRowResult>) searchResultList.currencyPostingList
            int count = (int) searchResultList.count
            List<GroovyRowResult> resultList = wrappingDataForGrid(currencyPostingList, start)
            Map output = [page: pageNumber, total: count, rows: resultList]
            result.put(EXH_AGENT_CURRENCY_POSTING_LIST, output)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: null,
                    isError: Boolean.TRUE, message: PAGE_LOAD_ERROR_MESSAGE]
            return result
        }
    }

    private List wrappingDataForGrid(List<GroovyRowResult> currencyPostingList, int start) {
        List lstCurrencyPosting = []
        try {
            int counter = start + 1
            for (int i = 0; i < currencyPostingList.size(); i++) {
                GroovyRowResult rowResultObj = currencyPostingList[i]
                String createdOn = DateUtility.getLongDateForUI(rowResultObj.createdOn)
                String updatedOn = DateUtility.getLongDateForUI(rowResultObj.updatedOn)
                GridEntity obj = new GridEntity()
                obj.id = rowResultObj.id
                obj.cell = [
                        counter,
                        rowResultObj.id,
                        rowResultObj.name,
                        rowResultObj.amount,
                        rowResultObj.createdBy,
                        createdOn,
                        updatedOn
                ]
                lstCurrencyPosting << obj
                counter++
            }
            return lstCurrencyPosting
        } catch (Exception ex) {
            log.error(ex.getMessage())
            lstCurrencyPosting = []
            return lstCurrencyPosting
        }
    }


    public Object buildFailureResultForUI(Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    private static final String QUERY_EXH_AGENT_CURRENCY_LIST =
        """
            SELECT eacp.id id, eacp.agent_id agentId, eac.name AS name, to_char(eacp.amount,'FM999,99,99,999.00') || ' ' || exc.symbol AS amount, au.username AS createdBy, eacp.created_on AS createdOn, eacp.updated_on AS updatedOn
                FROM exh_agent_currency_posting eacp
                    JOIN exh_agent eac ON eacp.agent_id = eac.id
                    JOIN app_user au ON eacp.created_by = au.id
                    JOIN currency exc ON eacp.currency_id = exc.id
                WHERE eac.name ILIKE :agentName AND  eacp.task_id = 0
                LIMIT :resultPerPage OFFSET :start
        """

    private static final String QUERY_EXH_AGENT_CURRENCY_LIST_COUNT =
        """
          SELECT COUNT(eacp.agent_id)
                           FROM exh_agent_currency_posting eacp
                           JOIN exh_agent eac ON eacp.agent_id = eac.id
                       WHERE eac.name ILIKE :agentName AND eacp.task_id = 0
        """
    private Map searchExhAgentCurrencyPosting() {
        Map queryParams = [resultPerPage: resultPerPage, start: start,agentName: Tools.PERCENTAGE + this.query + Tools.PERCENTAGE]
        List<GroovyRowResult> currencyPostingList = executeSelectSql(QUERY_EXH_AGENT_CURRENCY_LIST, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(QUERY_EXH_AGENT_CURRENCY_LIST_COUNT, queryParams)
        int count = (int) resultCount[0][0]
        return [currencyPostingList: currencyPostingList, count: count]
    }
}
