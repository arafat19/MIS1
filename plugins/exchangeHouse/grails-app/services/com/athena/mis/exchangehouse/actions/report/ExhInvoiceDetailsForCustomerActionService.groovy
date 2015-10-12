package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.*
import com.athena.mis.application.utility.*
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.utility.*
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show invoice details for UI for Customer
 * For details go through Use-Case doc named 'ExhInvoiceDetailsForCustomerActionService'
 */
class ExhInvoiceDetailsForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String TASK_NOT_FOUND = "Task not found"
    private static final String FAILURE_MESSAGE = "Failed to generate invoice report"
    private static final String TASK_OBJECT = "task"
    private static final String TASK_MAP = "taskMap"
    private static final String DATE_FORMAT = 'dd-MMM-yyyy [hh:mm a]'
    private static final String ANY_BRANCH = 'Any Branch'
    private static final String INVOICE_TERMS_AND_CONDITION = "I hereby declare that the amount paid to you was or is not derived obtained through any illegal means or transactions, and I accept the terms & conditions as set out in the framework contract which is available at the premises and on the website."


    ExhTaskService exhTaskService
    ExhBeneficiaryService exhBeneficiaryService
    ExhCustomerService exhCustomerService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * 1. check necessary parameters
     * 2. check if task exists
     * 3. check taskTypeId of agent & currentStatus of PENDING, then show message
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.refNo) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            ExhTask task = readByRefNoAndCustomerId(params.refNo.toString())
            if (!task) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND)
                return result
            }
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)
            SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)
            if ((task.taskTypeId == agentTaskObj.id) &&
                    (task.currentStatus == exhPendingTaskSysEntityObject.id)
            ) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND)
                return result
            }

            result.put(TASK_OBJECT, task)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
     * Build a map for task details for UI
     * @param parameters -N/A
     * @param obj -a map object returned form previous method
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            ExhTask task = (ExhTask) preResult.get(TASK_OBJECT)

            Map taskMap = (Map) buildInvoiceMap(task)           // build map
            result.put(TASK_MAP, taskMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for build success operation
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
        LinkedHashMap result
        try {
            if (obj) {
                String message = (String) obj.message
                result = [isError: Boolean.TRUE, message: message]
            } else {
                result = [isError: Boolean.TRUE, message: FAILURE_MESSAGE]
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [isError: Boolean.TRUE, message: FAILURE_MESSAGE]
            return result
        }
    }

    /**
     * Build a map for invoice details
     * @param task - an object of ExhTask
     * @return result -a map containing invoice information for UI
     */
    private Map buildInvoiceMap(ExhTask task) {
        ExhCustomer customer = exhCustomerService.read(task.customerId)
        ExhBeneficiary beneficiary = exhBeneficiaryService.read(task.beneficiaryId)
        Currency currency = (Currency) currencyCacheUtility.getLocalCurrency()
        beneficiary.address = Tools.EMPTY_SPACE

        Map result = new LinkedHashMap()
        String payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)
        SystemEntity taskTypeObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_EXH_TASK, companyId)
        SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)

        int payMethodBankDeposit = exhPaymentMethodObj.id
        int payMethodCashCollection = exhPaymentMethodCashObj.id
        String remittancePurpose = exhRemittancePurposeCacheUtility.read(task.remittancePurpose).name
        // now evaluate collection point 
        String collectionPoint = Tools.NOT_APPLICABLE
        Bank systemBank = (Bank) bankCacheUtility.getSystemBank()

        if (task.paymentMethod == exhPaymentMethodCashObj.id) {
            if (task.outletBankId == systemBank.id) {
                collectionPoint = systemBank.name + Tools.COMA + ANY_BRANCH
            } else {
                Bank bank = (Bank) bankCacheUtility.read(task.outletBankId)
                BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(task.outletBranchId)
                District district = (District) districtCacheUtility.read(task.outletDistrictId)
                collectionPoint = bank.name + Tools.COMA + bankBranch.name + Tools.COMA + district.name
            }
        } else if (task.paymentMethod == exhPaymentMethodObj.id) {
            if (task.outletBankId != systemBank.id) {
                Bank bank = (Bank) bankCacheUtility.read(task.outletBankId)
                BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(task.outletBranchId)
                District district = (District) districtCacheUtility.read(task.outletDistrictId)
                collectionPoint = bank.name + Tools.COMA + bankBranch.name + Tools.COMA + district.name
            }
        }

        String paidBy = exhPaidByCacheUtility.read(task.paidBy).key
        AppUser taskCreator
        if (task.taskTypeId == agentTaskObj.id) {
            taskCreator = (AppUser) appUserCacheUtility.read(task.userId)
        } else if (task.taskTypeId == taskTypeObj.id) {
            taskCreator = (AppUser) appUserCacheUtility.read(task.userId)
        } else {
            taskCreator = (AppUser) appUserCacheUtility.read(task.approvedBy)
        }

        boolean isDownloadable = true;
        SystemEntity exhCanceledTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_CANCELLED_TASK, companyId)
        if ((task.currentStatus == exhPendingTaskSysEntityObject.id) ||
                (task.currentStatus == exhStatusUnApprovedSysEntityObject.id) ||
                (task.currentStatus == exhCanceledTaskSysEntityObject.id)) {
            isDownloadable = false;
        }
        SystemEntity cancelledTaskStatusObj = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(ExhTaskStatusCacheUtility.STATUS_CANCELLED_TASK, companyId)
        boolean taskCancelled = false
        if(task.currentStatus == cancelledTaskStatusObj.id) {
            taskCancelled = true
        }
        result = [
                success: true,
                message: null,
                date: task.createdOn.format(DATE_FORMAT),
                userName: taskCreator ? taskCreator.username : Tools.NOT_APPLICABLE,
                payMethod: payMethod,
                remittancePurpose: remittancePurpose,
                paidBy: paidBy,
                collectionPoint: collectionPoint,
                task: task,
                customerAccNo: customer.code,
                customerAddress: customer.address,
                beneficiary: beneficiary,
                payMethodBankDeposit: payMethodBankDeposit,
                payMethodCashCollection: payMethodCashCollection,
                termsAndConditions: INVOICE_TERMS_AND_CONDITION,
                isDownloadable: isDownloadable,
                currencyName: currency.symbol,
                taskCancelled: taskCancelled
        ]
        return result
    }

    /**
     * Get task by refNo
     * @param refNo
     * @return task
     */
    private ExhTask readByRefNoAndCustomerId(String refNo) {
        refNo = refNo.toUpperCase()
        ExhTask task = ExhTask.findByRefNoAndCustomerId(refNo, exhSessionUtil.getUserCustomerId(), [readOnly: true])
        return task
    }
}