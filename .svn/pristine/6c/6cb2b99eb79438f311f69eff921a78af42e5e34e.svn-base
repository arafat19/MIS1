package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show UI for agent commission for Agent.
 * For details go through Use-Case doc named 'ShowAgentWiseCommissionForAgentActionService'
 */
class ShowAgentWiseCommissionForAgentActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load agent commission report"
    private static final int DEFAULT_DATE_RANGE_FOR_COMMISSION__DETAILS = 180

    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map for UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return map containing createdDateFrom, createdDateTo, agentList & localCurrency for UI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            initPager(parameterMap)

            Date startDateStr = new Date() - DateUtility.DATE_RANGE_HUNDREAD_EIGHTY        // set start date range
            startDateStr = DateUtility.setFirstHour(startDateStr)

            result = [createdDateFrom: DateUtility.getDateForUI(startDateStr)]
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return null
        }
    }

    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
