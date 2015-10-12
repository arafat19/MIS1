package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Show details for sarb replace task
 * for details go through use-case doc named "ShowDetailsForReplaceTaskActionService"
 */
class ShowDetailsForReplaceTaskActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String EXH_TASK = "exhTask"
    private static final String EXH_BENEFICIARY = "exhBeneficiary"
    private static final String LOAD_FAILURE_MSG = "Failed to load task details"
    private static final String BENEFICIARY_NAME = "beneficiaryName"
    private static final String CUSTOMER_NAME = "customerName"
    private static final String BDT_CURRENCY = "bdtCurrency"

    ExhTaskService exhTaskService
    ExhBeneficiaryService exhBeneficiaryService
    ExhCustomerService exhCustomerService
    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    /**
     * do nothing
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * check if taskId exists, if task exists
     * @param parameters
     * @param obj
     * @return
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.taskId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long oldTaskId = Long.parseLong(params.taskId.toString())
            ExhTask exhTask = exhTaskService.read(oldTaskId)
            ExhBeneficiary exhBeneficiary = exhBeneficiaryService.read(exhTask.beneficiaryId)
            ExhCustomer exhCustomer = exhCustomerService.read(exhTask.customerId)
            Currency currency = (Currency) currencyCacheUtility.getForeignCurrency()
            result.put(EXH_TASK, exhTask)
            result.put(EXH_BENEFICIARY, exhBeneficiary)
            result.put(BENEFICIARY_NAME, exhBeneficiary.getFullName())
            result.put(CUSTOMER_NAME, exhCustomer.getFullName())
            result.put(BDT_CURRENCY, currency.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LOAD_FAILURE_MSG)
            return result
        }
    }

    /**
     * build success result for ui
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            ExhTask exhTask = (ExhTask) executeResult.get(EXH_TASK)
            ExhBeneficiary exhBeneficiary = (ExhBeneficiary) executeResult.get(EXH_BENEFICIARY)
            String customerName = (String) executeResult.get(CUSTOMER_NAME)
            String beneficiaryName = (String) executeResult.get(BENEFICIARY_NAME)
            Integer bdtCurrency = (Integer) executeResult.get(BDT_CURRENCY)
            result.put(EXH_TASK, exhTask)
            result.put(BENEFICIARY_NAME, customerName)
            result.put(CUSTOMER_NAME, beneficiaryName)
            result.put(EXH_BENEFICIARY, exhBeneficiary)
            result.put(BDT_CURRENCY, bdtCurrency)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LOAD_FAILURE_MSG)
            return result
        }
    }

    /**
     * build failure result for ui
     */
    Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, LOAD_FAILURE_MSG)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, true)
            result.put(Tools.MESSAGE, LOAD_FAILURE_MSG)
            return result
        }
    }
}
