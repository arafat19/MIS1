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
 * Show invoice details from task grid for Customer
 * For details go through Use-Case doc named 'ExhShowInvoiceDetailsReportActionService'
 */
class ExhShowInvoiceDetailsForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String INVOICE_MAP = "invoiceMap"
    private static final String ERROR_MESSAGE = "Failed to generate invoice."
    private static final String TASK_OBJECT = "task"
    private static final String DATE_FORMAT = 'dd-MMM-yyyy [hh:mm a]'
    private static final String ANY_BRANCH = 'Any Branch'
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
    CurrencyCacheUtility currencyCacheUtility

    /**
     * 1. check necessary parameters
     * 2. check if task exists
     * 3. check customerId of task
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedMap preResult = new LinkedMap()

        try {
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)              // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.taskId) {                     // check required parameter
                preResult.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return preResult
            }

            long taskId = Tools.parseLongInput(params.taskId.toString())

            if (taskId == 0) {              // check parse exception
                preResult.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return preResult
            }

            ExhTask task = readWithExchangeHouse(taskId)

            if (!task) {                         // check task existence
                preResult.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return preResult
            }

            if (exhSessionUtil.getUserCustomerId() != task.customerId) {
                preResult.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return preResult
            }
            preResult.put(TASK_OBJECT, task)
            preResult.put(Tools.IS_ERROR, Boolean.FALSE)
            return preResult

        } catch (Exception ex) {
            log.error(ex.getMessage())
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(Tools.MESSAGE, ERROR_MESSAGE)
            return preResult
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
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            ExhTask task = (ExhTask) receivedResult.get(TASK_OBJECT)
            Map invoiceMap = buildInvoiceMap(task)

            result.put(INVOICE_MAP, invoiceMap)
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
        ExhCustomer customer = exhCustomerService.read(task.customerId)
        ExhBeneficiary beneficiary = exhBeneficiaryService.read(task.beneficiaryId)
        beneficiary.address = Tools.EMPTY_SPACE

        Map result = new LinkedHashMap()
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)
        SystemEntity taskTypeObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_EXH_TASK, companyId)
        SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)

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
        if (task.taskTypeId == agentTaskObj.id) {
            taskCreator = (AppUser) appUserCacheUtility.read(task.userId)
        } else if (task.taskTypeId == taskTypeObj.id) {
            taskCreator = (AppUser) appUserCacheUtility.read(task.userId)
        } else {
            taskCreator = (AppUser) appUserCacheUtility.read(task.approvedBy)
        }

        boolean isDownloadable = true

        if ((task.currentStatus == exhPendingTaskSysEntityObject.id) ||
                (task.currentStatus == exhStatusUnApprovedSysEntityObject.id)) {
            isDownloadable = false;
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
                currencyName: currencyCacheUtility.localCurrency.symbol
        ]
        return result
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
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
