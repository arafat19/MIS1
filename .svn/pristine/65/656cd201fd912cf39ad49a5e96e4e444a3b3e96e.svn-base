package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhPaymentResponseNotification
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhPaymentResponseNotificationService
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.service.ExhTaskTraceService
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to save Payment Response Notification, Both Failed & Success PRN will be saved
 *  Used in : Paypoint PRN request
 *  For details go through Use-Case doc named 'ExhProcessPaypointPRNActionService'
 */
class ExhProcessPaypointPRNActionService extends BaseService implements ActionIntf {

    ExhTaskService exhTaskService
    ExhTaskTraceService exhTaskTraceService
    SpringSecurityService springSecurityService
    ExhPaymentResponseNotificationService exhPaymentResponseNotificationService

    private Logger log = Logger.getLogger(getClass())
    private static final String DEFAULT_FAILURE_MESSAGE = "Failed to show make payment page"
    private static final String EXH_TASK = "task"
    private static final String EXH_PRN = "responseNotification"
    private static
    final String ERR_TASK_NOT_FOUND = 'ERROR IN ExhProcessPaypointPRNActionService: TASK NOT FOUND. id = '
    final String ERR_AMOUNT_MISMATCH = 'ERROR IN ExhProcessPaypointPRNActionService: AMOUNT MISMATCH. taskId = '
    final String ERR_KEY_MISMATCH = 'ERROR IN ExhProcessPaypointPRNActionService: KEY MISMATCH. taskId = '
    final String ERR_ALREADY_PAID = 'ERROR IN ExhProcessPaypointPRNActionService: TASK PAYMENT DONE. taskId = '

    /**
     * 1. Pull task object
     * 2. Put other necessary parameters into map
     * 3. Check if task amount is equal with prn amount
     * 4. Check Generated key from pass through data
     * @param parameters - parameters from UI
     * @param obj -N/A
     * @return -a map required to show 'make payment page'
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.strCartID) {
                return result
            }
            ExhPaymentResponseNotification prn = buildPRN(parameterMap)

            ExhTask task = exhTaskService.read(prn.taskId)
            if (!task) {
                log.error(ERR_TASK_NOT_FOUND + prn.taskId)
                return result
            }

/*            BigDecimal prnAmount = new BigDecimal(Float.toString(prn.fltAmount))
            BigDecimal taskAmount = getTotalAmount(task)

            // test if amount equals
            if (!prnAmount.equals(taskAmount)) {
                log.error(ERR_AMOUNT_MISMATCH + prn.taskId)
                return result
            }*/

            if (!checkKey(prn, task)) {
                log.error(ERR_KEY_MISMATCH + prn.taskId)
                return result
            }

            if (task.isGatewayPaymentDone) {
                log.error(ERR_ALREADY_PAID+ prn.taskId)
                return result
            }

            result.put(EXH_TASK, task)
            result.put(EXH_PRN, prn)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * 1. Pull task object
     * 2. Put other necessary parameters into map
     * 3. Generate key for pass through data(for further cross-check)
     * @param parameters - parameters from UI
     * @param obj -N/A
     * @return -a map required to show 'make payment page'
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            ExhTask task = (ExhTask) preResult.get(EXH_TASK)
            ExhPaymentResponseNotification prn = (ExhPaymentResponseNotification) preResult.get(EXH_PRN)

            exhPaymentResponseNotificationService.create(prn)
            task.isGatewayPaymentDone = true
            exhTaskService.updateForPaymentResponseNotification(task)
            exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException('Failed to create Payment Response Notification')
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
     * Generate new key & check with prn key
     * @param prn
     * @param task
     * @return - true/false
     */
    private boolean checkKey(ExhPaymentResponseNotification prn, ExhTask task) {
        String origialKey = springSecurityService.encodePassword((task.id + task.customerId + task.beneficiaryId).toString())
        return prn.key.equals(origialKey)
    }

    /**
     *  Build the Payment Response object
     * @param params
     * @return - built object
     */
    private ExhPaymentResponseNotification buildPRN(GrailsParameterMap params) {
        ExhPaymentResponseNotification paymentResponseNotification = new ExhPaymentResponseNotification(params)
        paymentResponseNotification.createdOn = new Date()
        paymentResponseNotification.key = params.PT_key
        paymentResponseNotification.taskId = Long.parseLong(params.strCartID)
        return paymentResponseNotification

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
