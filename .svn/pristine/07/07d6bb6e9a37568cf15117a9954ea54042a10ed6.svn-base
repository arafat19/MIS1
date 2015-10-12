package com.athena.mis.exchangehouse.actions.agentCurrencyPosting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

class ListExhAgentCurrencyPostingActionService extends BaseService implements ActionIntf {


    private Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load agent currency posting page"
    private static final String EXH_AGENT_CURRENCY_POSTING_LIST = "exhAgentCurrencyPostingList"
    private static final String LIST_RESULT = "listResult"

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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            this.initPager(parameterMap)
            Map lstResult = listExhAgentCurrencyPosting()
            result.put(LIST_RESULT, lstResult)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            Map listResult = (Map) executeResult.get(LIST_RESULT)
            List<GroovyRowResult> currencyPostingList = (List<GroovyRowResult>) listResult.currencyPostingList
            int count = (int) listResult.get(Tools.COUNT)
            List resultList = wrapListInGridEntityList(currencyPostingList, this.start)
            Map output = [page: this.pageNumber, total: count, rows: resultList]
            result.put(EXH_AGENT_CURRENCY_POSTING_LIST, output)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

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
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
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

    private Map listExhAgentCurrencyPosting() {
        Map queryParams = [resultPerPage: resultPerPage, start: start]
        List<GroovyRowResult> currencyPostingList = executeSelectSql(QUERY_EXH_AGENT_CURRENCY_LIST, queryParams)


        List<GroovyRowResult> resultCount = executeSelectSql(QUERY_EXH_AGENT_CURRENCY_LIST_COUNT)
        int count = (int) resultCount[0][0]
        return [currencyPostingList: currencyPostingList, count: count]
    }
}
