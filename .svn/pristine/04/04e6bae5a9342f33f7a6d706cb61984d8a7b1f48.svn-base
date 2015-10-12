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
import com.athena.mis.exchangehouse.utility.*
import com.athena.mis.utility.Tools
import org.apache.commons.collections.map.LinkedMap
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show invoice details from task grid for Admin, Cashier or Agent
 * For details go through Use-Case doc named 'ExhShowInvoiceDetailsReportActionService'
 */
class ExhShowInvoiceDetailsReportActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to generate invoice report"
    private static final String TASK_NOT_FOUND_MESSAGE = "Task has not been found."
    private static final String TASK_OBJECT = "task"
    private static final String DATE_FORMAT = 'dd-MMM-yyyy [hh:mm a]'
    private static final String ANY_BRANCH = 'Any Branch'
    private static final String INVOICE_MAP = "invoiceMap"
    private static final String TASK_NOT_APPROVED = 'Task is not approved'
    private static final String INVOICE_TERMS_AND_CONDITION = "I hereby declare that the amount paid to you was or is not derived obtained through any illegal means or transactions, and I accept the terms & conditions as set out in the framework contract which is available at the premises and on the website."

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
    ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    /**
     * 1. check necessary parameters
     * 2. check if task exists
     * 3. check roleType of Agent & agentId of task
     * 3. check companyId of task
     * 4. check agentId ot task if logged user type Agent
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedMap result = new LinkedMap()

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)               // set default value

            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.taskId) {                   // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long taskId = Tools.parseLongInput(params.taskId.toString())
            if (taskId == 0) {                                      // check parse exception
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            ExhTask task = readWithExchangeHouse(taskId)
            if (!task) {                               // check task existence
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
                return result
            }

            boolean hasAgentRole = exhSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_EXH_AGENT)
            if (hasAgentRole && (task.agentId != exhSessionUtil.getUserAgentId())) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)
            SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)
            SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)

            if ((task.taskTypeId == customerTaskObj.id) && (
            (task.currentStatus == exhPendingTaskSysEntityObject.id) ||
                    (task.currentStatus == exhStatusUnApprovedSysEntityObject.id))
            ) {
                result.put(Tools.MESSAGE, TASK_NOT_APPROVED)
                return result
            }

            if (task.companyId != exhSessionUtil.appSessionUtil.getCompanyId()) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(TASK_OBJECT, task)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
     * Build a map for task for UI
     * @param parameters -N/A
     * @param obj -a map object returned form previous method
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();

        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            ExhTask task = (ExhTask) executeResult.get(TASK_OBJECT)
            Map taskMap = buildInvoiceMap(task)
            result.put(INVOICE_MAP, taskMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object result) {
        return null
    }

    /**
     * Build a map for invoice details
     * @param task - an object of ExhTask
     * @return result -a map containing invoice information for UI
     */
    private Map buildInvoiceMap(ExhTask task) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        ExhCustomer customer = exhCustomerService.read(task.customerId)
        ExhBeneficiary beneficiary = exhBeneficiaryService.read(task.beneficiaryId)
        beneficiary.address = Tools.EMPTY_SPACE

        Map result = new LinkedHashMap()
        String payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key
        int payMethodBankDeposit = exhPaymentMethodObj.id
        int payMethodCashCollection = exhPaymentMethodCashObj.id
        String remittancePurpose = exhRemittancePurposeCacheUtility.read(task.remittancePurpose).name
        // now evaluate collection point
        String collectionPoint = Tools.NOT_APPLICABLE
        Bank systemBank = bankCacheUtility.getSystemBank()

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

        if (task.taskTypeId != customerTaskObj.id) {  // check if task created by customer
            taskCreator = (AppUser) appUserCacheUtility.read(task.userId)
        } else {
            taskCreator = (AppUser) appUserCacheUtility.read(task.approvedBy)
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
                userName: taskCreator.username,
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
                currencyName: currencyCacheUtility.localCurrency.symbol,
                taskCancelled: taskCancelled
        ]
        return result
    }

    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get task object by id
     * @param id
     * @return task
     */
    private ExhTask readWithExchangeHouse(long id) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        ExhTask task = ExhTask.findByIdAndCompanyId(id, companyId, [readOnly: true])
        return task
    }
}
