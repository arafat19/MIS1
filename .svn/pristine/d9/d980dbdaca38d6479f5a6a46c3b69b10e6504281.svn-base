package com.athena.mis.sarb.actions.taskmodel

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * show task details for refund task
 * for details go through use-case named "ShowSarbTaskDetailsForRefundTaskActionService"
 */
class ShowSarbTaskDetailsForRefundTaskActionService extends BaseService implements ActionIntf {

    @Autowired
    ExchangeHousePluginConnector exchangeHouseImplService

    private final Logger log = Logger.getLogger(getClass())
    private static final String NOT_FOUND = "Task not found"
    private static final String FAILURE_MSG = "Failed to show task details"
    private static final String TASK_DETAILS = "taskDetails"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Check if taskId exists and read task from SarbTaskModel view
     * @param parameters - taskId
     * @param obj - n/a
     * @return - map for build success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.taskId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskId = Tools.parseLongInput(params.taskId)
            GroovyRowResult task = getExhTask(taskId)           // read exh task from db
            if (!task) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, task)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * build task details map for refund task ui
     * @param obj
     * @return
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            GroovyRowResult task = (GroovyRowResult) preResult.get(Tools.ENTITY)
            SystemEntity paymentMethod = exchangeHouseImplService.readExhPaymentMethod(Tools.parseLongInput(task.payment_method.toString()))
            Object remittancePurpose = exchangeHouseImplService.readExhRemittancePurpose(Tools.parseLongInput(task.remittance_purpose.toString()))
            SystemEntity paidBy = exchangeHouseImplService.readExhPaidBy(Tools.parseLongInput(task.paid_by.toString()))
            Map taskDetails = [
                    id: task.id,
                    refNo: task.ref_no,
                    customerName: task.customer_name,
                    customerId: task.customer_id,
                    beneficiaryName: task.beneficiary_name,
                    beneficiaryId: task.beneficiary_id,
                    paymentMethod: paymentMethod ? paymentMethod.key : Tools.EMPTY_SPACE,
                    remittancePurpose: remittancePurpose ? remittancePurpose.name : Tools.EMPTY_SPACE,
                    amountInLocalCurrency: task.amount_in_local_currency,
                    amountInForeignCurrency: task.amount_in_foreign_currency,
                    paidBy: paidBy ? paidBy.key : Tools.EMPTY_SPACE
            ]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(TASK_DETAILS, taskDetails)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * build failure msg for ui
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                String msg = preResult.get(Tools.MESSAGE)
                if (msg) {
                    result.put(Tools.MESSAGE, msg)
                } else {
                    result.put(Tools.MESSAGE, FAILURE_MSG)
                }
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    private GroovyRowResult getExhTask(long taskId) {
        String sql = """
        select id, ref_no,customer_name,customer_id,beneficiary_name,beneficiary_id,payment_method,remittance_purpose,
        amount_in_local_currency,amount_in_foreign_currency,paid_by from exh_task where id = :taskId
        """
        Map queryParam = [taskId: taskId]
        List<GroovyRowResult> lstTask = executeSelectSql(sql, queryParam)
        return (lstTask.size() > 0) ? lstTask[0] : null
    }
}
