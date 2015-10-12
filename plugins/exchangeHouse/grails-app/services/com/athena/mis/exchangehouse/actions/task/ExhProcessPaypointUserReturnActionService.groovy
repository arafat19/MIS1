package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to show After Payment landing page
 *  Used in : Paypoint user return page
 *  For details go through Use-Case doc named 'ExhProcessPaypointUserReturnActionService'
 */
class ExhProcessPaypointUserReturnActionService extends BaseService implements ActionIntf {

    ExhTaskService exhTaskService
    @Autowired
    CurrencyCacheUtility currencyCacheUtility


    private Logger log = Logger.getLogger(getClass())
    private static final String DEFAULT_FAILURE_MESSAGE = "Failed to process PayPoint user return"
    private static final String EXH_TASK = "task"
    private static final String EXH_TASK_TOTAL_AMOUNT = "totalAmount"
    private static final String STR_CURRENCY = "strCurrency"
    private static
    final String ERR_INVALID_PARAMETER = 'ERROR IN ExhProcessPaypointUserReturnActionService: strCartID NOT GIVEN'
    private static
    final String ERR_TASK_NOT_FOUND = 'ERROR IN ExhProcessPaypointUserReturnActionService: TASK NOT FOUND. id = '
    private static
    final String USER_MESSAGE_SUCCESS = "<span style='color: green'>Payment received successfully!</span>"
    private static
    final String USER_MESSAGE_FAIL_DELAY = "<span style='color: red'>The payment process failed or delayed. Refresh the task grid to view up-to-date status.</span>"

    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * 1. Check required params
     * 2. Pull task object
     * 3. check if payment successful through isGatewayPaymentDone
     * 3. Put currency object
     * @param parameters - taskId
     * @param obj -N/A
     * @return -a map required to show 'payment success page'
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            //@todo: check domain metacharge.com

            if (!parameterMap.taskId) {
                log.error(ERR_INVALID_PARAMETER)
                return result
            }
            long taskId = Long.parseLong(parameterMap.taskId)

            ExhTask task = exhTaskService.read(taskId)
            if (!task) {
                log.error(ERR_TASK_NOT_FOUND + taskId)
                return result
            }
            if (task.isGatewayPaymentDone) {
                result.put(Tools.MESSAGE, USER_MESSAGE_SUCCESS)
            } else {
                result.put(Tools.MESSAGE, USER_MESSAGE_FAIL_DELAY)
            }

            BigDecimal totalAmount = getTotalAmount(task)
            Currency currency = (Currency) currencyCacheUtility.read(task.fromCurrencyId)

            result.put(EXH_TASK, task)
            result.put(EXH_TASK_TOTAL_AMOUNT, totalAmount)
            result.put(STR_CURRENCY, currency.symbol)
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
    private BigDecimal getTotalAmount(ExhTask task) {
        BigDecimal amountLocal = new BigDecimal(Double.toString(task.amountInLocalCurrency.doubleValue()))
        BigDecimal regFee = new BigDecimal(Double.toString(task.regularFee.doubleValue()))
        BigDecimal discount = new BigDecimal(Double.toString(task.discount.doubleValue()))
        BigDecimal totalAmount = amountLocal + regFee - discount
        return totalAmount
    }

}
