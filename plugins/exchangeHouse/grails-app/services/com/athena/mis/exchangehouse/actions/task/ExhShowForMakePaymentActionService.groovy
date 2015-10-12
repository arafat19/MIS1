package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import grails.util.Environment
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to show Make payment landing page
 *  Used in : Cashier Task Grid -> Make Payment
 *  For details go through Use-Case doc named 'ExhShowForMakePaymentActionService'
 */
class ExhShowForMakePaymentActionService extends BaseService implements ActionIntf {

    ExhTaskService exhTaskService
    SpringSecurityService springSecurityService
    @Autowired
    CurrencyCacheUtility currencyCacheUtility


    private Logger log = Logger.getLogger(getClass())
    private static final String DEFAULT_FAILURE_MESSAGE = "Failed to show make payment page"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String EXH_TASK = "task"
    private static final String EXH_TASK_TOTAL_AMOUNT = "totalAmount"
    private static final String STR_CURRENCY = "strCurrency"
    private static final String KEY = "key"
    private static final String KEY_INSTALLATION_ID = "installationId"
    private static final String VAL_INSTALLATION_ID = "257963"
    private static final String TEST_MODE = "testMode"


    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * 1. Pull task object
     * 2. check if already payment done
     * 2. Put necessary parameters into map
     * 3. Generate key for pass through data(for further cross-check)
     * @param parameters - parameters from UI
     * @param obj -N/A
     * @return -a map required to show 'make payment page'
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.taskId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long taskId = Long.parseLong(params.taskId)
            ExhTask task = exhTaskService.read(taskId)
            if (task.isGatewayPaymentDone) {
                return result
            }
            Currency currency = (Currency) currencyCacheUtility.read(task.fromCurrencyId)
            String key = springSecurityService.encodePassword((task.id + task.customerId + task.beneficiaryId).toString())
            int testMode = 0    // default value for production
            Environment.executeForCurrentEnvironment { development { testMode = 1 } }
            BigDecimal totalAmount = getTotalAmount(task)
            result.put(EXH_TASK, task)
            result.put(EXH_TASK_TOTAL_AMOUNT, totalAmount)
            result.put(STR_CURRENCY, currency.symbol)
            result.put(KEY_INSTALLATION_ID, VAL_INSTALLATION_ID)
            result.put(KEY, key)
            result.put(TEST_MODE, testMode)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }


    public Object buildSuccessResultForUI(Object obj) {
        return null
    }


    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Calculate task total = amountLocal + regularFee - discount
     * @param task
     * @return -totalAmount
     */
    private BigDecimal getTotalAmount (ExhTask task) {
        BigDecimal amountLocal = new BigDecimal(Double.toString(task.amountInLocalCurrency.doubleValue()))
        BigDecimal regFee = new BigDecimal(Double.toString(task.regularFee.doubleValue()))
        BigDecimal discount = new BigDecimal(Double.toString(task.discount.doubleValue()))
        BigDecimal totalAmount = amountLocal + regFee - discount
        return totalAmount
    }

}
