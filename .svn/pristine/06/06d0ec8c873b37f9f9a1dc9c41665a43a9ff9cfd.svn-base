package com.athena.mis.exchangehouse.integration.exchangehouse

import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.exchangehouse.actions.task.CreateExhTaskForSarbRefundTaskActionService
import com.athena.mis.exchangehouse.actions.task.UpdateTaskForSarbReplaceTaskActionService
import com.athena.mis.exchangehouse.config.ExhSysConfigurationCacheUtility
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.utility.*
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.springframework.beans.factory.annotation.Autowired

class ExchangeHouseImplService extends ExchangeHousePluginConnector {

    static transactional = false
    static lazyInit = false

    ExhCustomerService exhCustomerService
    ExhSchemaUpdateBootStrapService exhSchemaUpdateBootStrapService
    ExhDefaultDataBootStrapService exhDefaultDataBootStrapService
    ExchangeHouseBootStrapService exchangeHouseBootStrapService
    CreateExhTaskForSarbRefundTaskActionService createExhTaskForSarbRefundTaskActionService
    UpdateTaskForSarbReplaceTaskActionService updateTaskForSarbReplaceTaskActionService
    ExhTaskService exhTaskService

    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhSysConfigurationCacheUtility exhSysConfigurationCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility

    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(EXCHANGE_HOUSE, this);
        return true
    }

    @Override
    public String getName() {
        return EXCHANGE_HOUSE;
    }

    @Override
    public int getId() {
        return EXCHANGE_HOUSE_ID;
    }

    //return customer object
    @Override
    Object readCustomer(long id) {
        return exhCustomerService.read(id)
    }

    Object readTaskStatus(long id) {
        return exhTaskStatusCacheUtility.read(id)
    }

    Object readTaskTypeByReservedAndCompany(long id, long companyId) {
        return exhTaskTypeCacheUtility.readByReservedAndCompany(id, companyId)
    }

    long getExhTaskTypeId() {
        return exhTaskTypeCacheUtility.TYPE_EXH_TASK
    }

    long getCustomerTaskTypeId() {
        return exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK
    }

    long getAgentTaskTypeId() {
        return exhTaskTypeCacheUtility.TYPE_AGENT_TASK
    }

    // Re-initialize the Exh-PaidBy Cache
    public void initExhPaidByTypeCacheUtility() {
        exhPaidByCacheUtility.init()
    }

    public void initExhPaymentMethodCacheUtility() {
        exhPaymentMethodCacheUtility.init()
    }

    public void initExhTaskStatusCacheUtility() {
        exhTaskStatusCacheUtility.init()
    }

    public void initExhTaskTypeCacheUtility() {
        exhTaskTypeCacheUtility.init()
    }

    // get list of ExhPaidByType system entity
    public List<Object> listExhPaidByType() {
        return exhPaidByCacheUtility.listByIsActive()
    }

    // get list of ExhPaymentMethodType system entity
    public List<Object> listExhPaymentMethodType() {
        return exhPaymentMethodCacheUtility.listByIsActive()
    }

    // get list of ExhTaskStatusType system entity
    public List<Object> listExhTaskStatusType() {
        return exhTaskStatusCacheUtility.listByIsActive()
    }

    // get list of ExhTaskType system entity
    public List<Object> listExhTaskType() {
        return exhTaskTypeCacheUtility.listByIsActive()
    }

    public void initExhSysConfigCacheUtility() {
        exhSysConfigurationCacheUtility.init()
    }

    // init bootstrap of exh plugin
    public void bootStrap(boolean isSchema, boolean isData) {
        if (isSchema) exhSchemaUpdateBootStrapService.init()
        if (isData) exhDefaultDataBootStrapService.init()
        exchangeHouseBootStrapService.init()
    }

    //read taskStatus (SendToBank,SendToOtherBank,ResolvedByOtherBank)
    public List<Long> listTaskStatusForSarb() {
        return exhTaskStatusCacheUtility.listTaskStatusForSarb()
    }

    //read taskStatus (new,unApproved,pending)
    public List<Long> listTaskStatusForExcludingSarb() {
        return exhTaskStatusCacheUtility.listTaskStatusForExcludingSarb()
    }

    // get reserved system entity of paid by type
    public Object readByReservedPaidByType(long reservedId, long companyId) {
        return exhPaidByCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of payment method type
    public Object readByReservedPaymentMethodType(long reservedId, long companyId) {
        return exhPaymentMethodCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get system entity of reserved type (BANK DEPOSIT); used in sarb xml generation
    public SystemEntity readBankDepositPaymentMethod(long companyId) {
        SystemEntity payMethodBankDeposit = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        return payMethodBankDeposit
    }
    // get system entity of reserved type (Cash Collection); used in sarb
    public SystemEntity readCashCollectionPaymentMethod(long companyId) {
        SystemEntity payMethodCashCollection = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)
        return payMethodCashCollection
    }

    // get reserved system entity of task status type
    public Object readByReservedTaskStatusType(long reservedId, long companyId) {
        return exhTaskStatusCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    public SystemEntity readExhPaymentMethod(long id) {
        return (SystemEntity) exhPaymentMethodCacheUtility.read(id)
    }

    // check if new user registration is enabled through sysConfig
    public Boolean isNewUserRegistrationEnabled(long companyId) {
        SysConfiguration configuration = exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.ENABLE_NEW_USER_REGISTRATION, companyId)
        if (configuration && (Integer.parseInt(configuration.value) > 0)) {
            return Boolean.TRUE
        }
        return Boolean.FALSE
    }

    public void initSession() {
        exhSessionUtil.init()
    }

    public Object readExhRemittancePurpose(long id) {
        return exhRemittancePurposeCacheUtility.read(id)
    }

    public SystemEntity readExhPaidBy(long id) {
        return (SystemEntity) exhPaidByCacheUtility.read(id)
    }

    public SystemEntity readExhTaskStatusRefund(long companyId) {
        return (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_REFUND_TASK, companyId)
    }

    public Map createExhTaskForRefundTask(long taskId, double refundAmount) {
        Map params = [taskId: taskId, refundAmount: refundAmount]
        Map result = (Map) createExhTaskForSarbRefundTaskActionService.execute(params, null)
        return result
    }

    public Map updateExhTaskForReplaceTask(Map params) {
        Map result = (Map) updateTaskForSarbReplaceTaskActionService.execute(params, null)
        return result
    }

    // read exhTask object by id
    public Object readTask(long taskId) {
        return exhTaskService.read(taskId)
    }
}
