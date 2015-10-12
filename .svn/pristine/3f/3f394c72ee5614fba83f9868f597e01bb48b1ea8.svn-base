package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.exchangehouse.config.ExhSysConfigurationCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.utility.ExhPaidByCacheUtility
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * update exhTask, exhBeneficiary for replace task
 * for details go through use-case doc named "UpdateTaskForSarbReplaceTaskActionService"
 */
class UpdateTaskForSarbReplaceTaskActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    private static final String TASK_AMOUNT_EXCEEDS_LIMIT = "Task amount exceeds limit. Max limit: "
    private static final String TASK_AMOUNT_LIMIT_CONFIG_NOT_FOUND = "Config for 'Max amount limit' not found"

    ExhTaskService exhTaskService
    ExhCustomerService exhCustomerService
    ExhBeneficiaryService exhBeneficiaryService

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhSysConfigurationCacheUtility exhSysConfigurationCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * read task, customer, beneficiary object
     * check amount limit of task
     * then update task, beneficiary in DB
     * @param parameters - params from sarb plugin
     * @param obj - n/a
     * @return
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            long taskId = Tools.parseLongInput(params.id)
            ExhTask task = exhTaskService.read(taskId)
            if (!task) return result
            String msg = checkPerTransactionAmountLimit(task)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
                return result
            }
            ExhCustomer customer = exhCustomerService.read(task.customerId)
            ExhBeneficiary beneficiary = exhBeneficiaryService.read(task.beneficiaryId)
            updateTaskAndBeneficiary(params, customer, beneficiary)       // update task object in DB
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Check amount limit of task from sys_configuration
     * @param task
     * @return
     */
    private String checkPerTransactionAmountLimit(ExhTask task) {
        SysConfiguration sysConfigAmountLimit = exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.EXH_TASK_PER_TRANSACTION_AMOUNT, exhSessionUtil.appSessionUtil.getCompanyId())
        if (!sysConfigAmountLimit) {
            return TASK_AMOUNT_LIMIT_CONFIG_NOT_FOUND
        }
        double maxAmountLimit = 0.0d
        try {
            maxAmountLimit = Double.parseDouble(sysConfigAmountLimit.value)
            maxAmountLimit = maxAmountLimit.round(2)
        } catch (Exception ignored) {
            maxAmountLimit = 0.0d
        }
        if (task.amountInLocalCurrency > maxAmountLimit) {
            return TASK_AMOUNT_EXCEEDS_LIMIT + maxAmountLimit.round(2)
        }
        return null
    }

    /**
     * update task and beneficiary
     * @param params - serialized params
     * @param customer - ExhCustomer obj
     * @param beneficiary - ExhBeneficiary obj
     */
    private void updateTaskAndBeneficiary(GrailsParameterMap params, ExhCustomer customer, ExhBeneficiary beneficiary) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        boolean isOtherBank, isBankDeposit, isCashCollection, isPaidByOnline
        long paymentMethod = Tools.parseLongInput(params.paymentMethod)
        long paidBy = Tools.parseLongInput(params.paidBy)
        SystemEntity bankDeposit = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(ExhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity cashCollection = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(ExhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)
        SystemEntity paidByOnline = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(ExhPaidByCacheUtility.PAID_BY_ID_ONLINE, companyId)
        SystemEntity sentToBank = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(ExhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity sentToOtherBank = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(ExhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)

        isOtherBank = params.isOtherBank ? Boolean.TRUE : Boolean.FALSE
        isBankDeposit = (paymentMethod == bankDeposit?.id) ? Boolean.TRUE : Boolean.FALSE
        isCashCollection = (paymentMethod == cashCollection?.id) ? Boolean.TRUE : Boolean.FALSE
        isPaidByOnline = (paidBy == paidByOnline?.id) ? Boolean.TRUE : Boolean.FALSE

        long id = Tools.parseLongInput(params.id)
        long remittancePurpose = Tools.parseLongInput(params.remittancePurpose)
        long outletBankId = Tools.parseLongInput(params.outletBankId)
        long outletDistrictId = Tools.parseLongInput(params.outletDistrictId)
        long outletBranchId = Tools.parseLongInput(params.outletBranchId)
        String accountNumber = params.accountNumber.toString()
        String bankName = params.bankName.toString()
        String bankBranchName = params.bankBranchName.toString()
        String districtName = params.districtName.toString()
        String identityType = params.identityType.toString()
        String identityNo = params.identityNo.toString()
        String paidByNo = params.paidByNo.toString()
        double conversionRate = Double.parseDouble(params.conversionRate)
        Integer fromCurrencyId = Integer.parseInt(params.fromCurrencyId).intValue()
        Integer toCurrencyId = Integer.parseInt(params.toCurrencyId).intValue()
        double amountInForeignCurrency = Double.parseDouble(params.hidAmountInForeignCurrency)
        double amountInLocalCurrency = Double.parseDouble(params.hidAmountInLocalCurrency)
        Bank systemBank = (Bank) bankCacheUtility.getSystemBank()

        Map queryParams = [
                id: id,
                customerName: customer.getFullName(),
                beneficiaryName: beneficiary.getFullName(),
                remittancePurpose: remittancePurpose,
                outletBankId: outletBankId,
                outletDistrictId: outletDistrictId,
                outletBranchId: outletBranchId,
                paymentMethod: paymentMethod,
                paidBy: paidBy,
                paidByNo: paidByNo,
                conversionRate: conversionRate,
                fromCurrencyId: fromCurrencyId,
                toCurrencyId: toCurrencyId,
                amountInForeignCurrency: amountInForeignCurrency,
                amountInLocalCurrency: amountInLocalCurrency,
                sentToBank: sentToBank.id,
                sentToOtherBank: sentToOtherBank.id,
                systemBank: systemBank.id
        ]
        String strOtherBank
        String strOnline = Tools.EMPTY_SPACE

        if (isOtherBank) {
            strOtherBank = """
                outlet_bank_id = :outletBankId,
                outlet_branch_id = :outletBranchId,
                outlet_district_id = :outletDistrictId,
                current_status = :sentToOtherBank
            """
        } else {
            strOtherBank = """
                outlet_bank_id = :systemBank,
                outlet_branch_id = null,
                outlet_district_id = null,
                current_status = :sentToBank
            """
        }
        if (isPaidByOnline) {
            strOnline = """
            paid_by_no = :paidByNo
            """
        }
        if (!strOnline.equals(Tools.EMPTY_SPACE)) strOtherBank += Tools.COMA

        String query = """
        UPDATE exh_task SET
        customer_name = :customerName,
        beneficiary_name = :beneficiaryName,
        remittance_purpose = :remittancePurpose,
        payment_method = :paymentMethod,
        paid_by = :paidBy,
        conversion_rate = :conversionRate,
        from_currency_id = :fromCurrencyId,
        to_currency_id = :toCurrencyId,
        amount_in_foreign_currency = :amountInForeignCurrency,
        amount_in_local_currency = :amountInLocalCurrency,
        ${strOtherBank} ${strOnline}
        WHERE id = :id
        """

        int updateCount = executeUpdateSql(query, queryParams) // update exh task object in DB
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Task for sarb replace task")
        }

        if (isBankDeposit) {
            updateBeneficiaryBankDepositInfo(customer.id, accountNumber, bankName, bankBranchName, districtName)
        }
        if (isCashCollection) {
            updateBeneficiaryCashCollectionInfo(customer.id, identityType, identityNo)
        }
    }

    /**
     * update beneficiary bank deposit info
     */
    private void updateBeneficiaryBankDepositInfo(Long id, String accountNumber, String bankName, String bankBranchName, String districtName) {
        String query = """
            UPDATE exh_beneficiary SET
            account_number = :accountNumber,
            bank_name = :bankName,
            bank_branch_name = :bankBranchName,
            district_name = :districtName
            WHERE id = :id
        """
        Map queryParams = [
                id: id,
                account_number: accountNumber,
                bank_name: bankName,
                bank_branch_name: bankBranchName,
                district_name: districtName
        ]
        int updateCount = executeUpdateSql(query, queryParams) // update exh beneficiary object in DB
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update beneficiary for sarb replace task")
        }
    }

    /**
     * update beneficiary cash collection info
     */
    private void updateBeneficiaryCashCollectionInfo(Long id, String identityType, String identityNo) {
        String query = """
            UPDATE exh_beneficiary SET
            photo_id_type = :identityType,
            photo_id_no = :identityNo
            WHERE id = :id
        """
        Map queryParams = [
                id: id,
                identityType: identityType,
                identityNo: identityNo,
        ]
        int updateCount = executeUpdateSql(query, queryParams) // update exh beneficiary object in DB
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update beneficiary for sarb replace task")
        }
    }
}
