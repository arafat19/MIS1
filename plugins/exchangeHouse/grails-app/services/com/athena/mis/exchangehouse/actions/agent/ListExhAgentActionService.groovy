package com.athena.mis.exchangehouse.actions.agent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

class ListExhAgentActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load Agent page"
    private static final String EXH_AGENT_LIST = "agentList"


    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            this.initPager(parameterMap)

            int count = ExhAgent.count()
            List<GroovyRowResult> agentList = agentLst()
            result.put(EXH_AGENT_LIST, agentList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<GroovyRowResult> exhAgentList, int start) {
        List<GroovyRowResult> exhAgentLst = []
        try {
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
        catch (Exception ex) {
            log.error(ex.getMessage())
            exhAgentLst = []
            return exhAgentLst
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List<GroovyRowResult> exhAgentList = (List<GroovyRowResult>) executeResult.get(EXH_AGENT_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List<GroovyRowResult> resultList = wrapListInGridEntityList(exhAgentList, this.start)
            Map output = [page: this.pageNumber, total: count, rows: resultList]
            result.put(EXH_AGENT_LIST, output)
            return result
        }
        catch (Exception ex) {
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

    private static final String QUERY_EXH_AGENT_LIST =
        """
           SELECT eac.id, eac.address, eac.city, eac.phone, eac.name, eac.balance, eac.credit_limit as "creditLimit", ec.symbol as symbol
                FROM exh_agent eac
                JOIN currency ec ON ec.id = eac.currency_id
                LIMIT :resultPerPage OFFSET :start
        """

    // It will return list of Exh Agent details
    private List<GroovyRowResult> agentLst() {
        Map queryParams = [
                resultPerPage: resultPerPage, start: start
        ]
        List<GroovyRowResult> lstStudentDetails = executeSelectSql(QUERY_EXH_AGENT_LIST, queryParams)
        return lstStudentDetails
    }
}
