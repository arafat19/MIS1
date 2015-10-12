package com.athena.mis.exchangehouse.actions.agentCurrencyPosting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ShowExhAgentCurrencyPostingActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SHOW_COUNTRY_FAILURE_MESSAGE = "Failed to load Agent Currency Posting page"
    private static final String AGENT_LIST = "agentList"
    private static final String EXH_AGENT_CURRENCY_POSTING_LIST = "exhAgentCurrencyPostingList"
    private static final String SHOW_RESULT_LIST = "showResultList"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            initPager(params)
            //List agentList = buildAgentList()
            Map showResultListMapObj = showExhAgentCurrencyPostingMap()
            //result.put(AGENT_LIST, agentList)
            result.put(SHOW_RESULT_LIST, showResultListMapObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_COUNTRY_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            Map showResultListMapObj = (Map) executeResult.get(SHOW_RESULT_LIST)
            List<GroovyRowResult> currencyPostingList = (List<GroovyRowResult>) showResultListMapObj.currencyPostingList
            int count = (int) showResultListMapObj.get(Tools.COUNT)
            List resultListObj = wrapListInGridEntityList(currencyPostingList, start)
            Map output = [page: pageNumber, total: count, rows: resultListObj]
            //result.put(AGENT_LIST, executeResult.get(AGENT_LIST))
            result.put(EXH_AGENT_CURRENCY_POSTING_LIST, output)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_COUNTRY_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SHOW_COUNTRY_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_COUNTRY_FAILURE_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<GroovyRowResult> exhAgentCurrencyPostingList, int start) {
        List listCurrencyPostings = []
        try {
            int counter = start + 1
            for (int i = 0; i < exhAgentCurrencyPostingList.size(); i++) {
                GroovyRowResult rowResultObj = exhAgentCurrencyPostingList[i]
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
                listCurrencyPostings << obj
                counter++
            }
            return listCurrencyPostings
        } catch (Exception ex) {
            log.error(ex.getMessage())
            listCurrencyPostings = []
            return listCurrencyPostings
        }
    }

    private static final String QUERY_EXH_AGENT_CURRENCY_LIST = """
            SELECT eacp.id id, eacp.agent_id agentId, eac.name AS name, to_char(eacp.amount,'FM999,99,99,999.00') || ' ' || exc.symbol AS amount, au.username AS createdBy, eacp.created_on AS createdOn, eacp.updated_on AS updatedOn
                FROM exh_agent_currency_posting eacp
                    JOIN exh_agent eac ON eacp.agent_id = eac.id
                    JOIN app_user au ON eacp.created_by = au.id
                    JOIN currency exc ON eacp.currency_id = exc.id
                    WHERE eacp.task_id = 0
                    LIMIT :resultPerPage OFFSET :start
        """

    private static final String QUERY_EXH_AGENT_CURRENCY_LIST_COUNT = """
               SELECT COUNT(eacp.agent_id)
                   FROM exh_agent_currency_posting eacp
                   JOIN exh_agent eac ON eacp.agent_id = eac.id
                     WHERE eacp.task_id = 0
        """

    private Map showExhAgentCurrencyPostingMap() {
        Map queryParams = [resultPerPage: resultPerPage, start: start]
        List<GroovyRowResult> currencyPostingList = executeSelectSql(QUERY_EXH_AGENT_CURRENCY_LIST, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(QUERY_EXH_AGENT_CURRENCY_LIST_COUNT)
        int count = (int) resultCount[0][0]
        return [currencyPostingList: currencyPostingList, count: count]
    }

/*    private static final String QUERY_EXH_AGENT_LIST =
        """
            SELECT eac.id, eac.name, ec.symbol as symbol
                FROM exh_agent eac
                JOIN currency ec ON ec.id = eac.currency_id
                ORDER BY eac.name
        """

    // It will return list of agent  details
    private List buildAgentList() {
        List<GroovyRowResult> lstExhAgent = executeSelectSql(QUERY_EXH_AGENT_LIST)
        return lstExhAgent
    }*/
}



