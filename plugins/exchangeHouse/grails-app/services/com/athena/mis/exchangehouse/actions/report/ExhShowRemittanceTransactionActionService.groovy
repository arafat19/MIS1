package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for transaction summary for admin & cashier
 *  For details go through Use-Case doc named 'ExhShowRemittanceTransactionActionService'
 */
class ExhShowRemittanceTransactionActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Can not load remittance transaction report"
    private static final String FROM_DATE = 'fromDate'
    private static final String FOREIGN_CURRENCY = 'foreignCurrency'

    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build UI properties for list of remittance transaction report
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Date fromDate = new Date() - DateUtility.DATE_RANGE_THIRTY
            String fromDateStr = DateUtility.getDateForUI(fromDate)

            Currency foreignCurrency = currencyCacheUtility.getForeignCurrency()
            result.put(FOREIGN_CURRENCY, foreignCurrency.symbol)
            result.put(FROM_DATE, fromDateStr)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
       return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.message) {
                result.put(Tools.MESSAGE, returnResult.get(Tools.MESSAGE))
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
}