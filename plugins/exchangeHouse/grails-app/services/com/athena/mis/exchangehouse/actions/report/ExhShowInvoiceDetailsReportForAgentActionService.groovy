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
import com.athena.mis.exchangehouse.utility.ExhPaidByCacheUtility
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhRemittancePurposeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.commons.collections.map.LinkedMap
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhShowInvoiceDetailsReportForAgentActionService extends BaseService implements ActionIntf {

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
    CompanyCacheUtility companyCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to generate invoice report"
    private static final String TASK_NOT_FOUND_MESSAGE = "Task has not been found."
    private static final String TASK_OBJECT = "task"
    private static final String DATE_FORMAT = 'dd-MMM-yyyy [hh:mm a]'
    private static final String ANY_BRANCH = 'Any Branch'
    private static final String INVOICE_TERMS_AND_CONDITION = "I hereby declare that the amount paid to you was or is not derived obtained through any illegal means or transactions, and I accept the terms & conditions as set out in the framework contract which is available at the premises and on the website."

    Object executePreCondition(Object parameters, Object obj) {
        LinkedMap preResult = new LinkedMap()

        try {
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.taskId) {
                preResult.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return preResult
            }
            preResult.put(Tools.IS_ERROR, Boolean.FALSE)
            return preResult

        } catch (Exception ex) {
            log.error(ex.getMessage())
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(Tools.MESSAGE, ERROR_MESSAGE)
            return preResult
        }
    }

    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            Long taskId = Long.parseLong(params.taskId.toString())
            ExhTask task = readWithExchangeHouse(taskId)

            if (!task) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(TASK_OBJECT, task)

            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }

    }

    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object result) {

        try {
            LinkedHashMap executeResult = (LinkedHashMap) result
            ExhTask task = (ExhTask) executeResult.get(TASK_OBJECT)
            Map taskMap = buildInvoiceMap(task)
            return taskMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    private Map buildInvoiceMap(ExhTask task) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        ExhCustomer customer = exhCustomerService.read(task.customerId)
        ExhBeneficiary beneficiary = (ExhBeneficiary) exhBeneficiaryService.read(task.beneficiaryId)
        beneficiary.address = Tools.EMPTY_SPACE

        Map result = new LinkedHashMap()
        String payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key
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
        AppUser taskCreator = (AppUser)appUserCacheUtility.read(task.userId)

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
                termsAndConditions: INVOICE_TERMS_AND_CONDITION
        ]
        return result
    }

    Object buildFailureResultForUI(Object obj) {
        return null
    }

    private ExhTask readWithExchangeHouse(Long id) {
        Company company = (Company) companyCacheUtility.read(exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.findByIdAndCompanyId(id, company.id, [readOnly: true])
    }
}
