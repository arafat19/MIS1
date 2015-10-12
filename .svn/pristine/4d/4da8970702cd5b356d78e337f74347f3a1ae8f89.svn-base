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
 *  Show UI for transaction summary for admin & cashier
 *  For details go through Use-Case doc named 'ExhShowTransactionSummaryActionService'
 */
class ExhShowTransactionSummaryActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load page"
    private static final int DEFAULT_DATE_RANGE = 30

    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get formatted date & local currency symbol for UI
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            initPager(parameterMap)        // initialize params for flexGrid

            Date startDateStr = new Date() - DEFAULT_DATE_RANGE
            startDateStr = DateUtility.setFirstHour(startDateStr)
            Date endDateStr = new Date()
            endDateStr = DateUtility.setLastHour(endDateStr)
            // get local currency
            result = [createdDateFrom: DateUtility.getDateForUI(startDateStr),
                    createdDateTo: DateUtility.getDateForUI(endDateStr),
                    localCurrency: currencyCacheUtility.localCurrency.symbol
            ]
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
