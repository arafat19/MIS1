package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.entity.District
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.exchangehouse.entity.*
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.utility.*
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for task CRUD for Customer and list of task for grid
 *  For details go through Use-Case doc named 'ExhShowTaskForCustomerActionService'
 */
class ExhShowTaskForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SYSTEM_CURRENCY_NAME = "systemCurrencyName"
    private static final String LABEL_ID = 'id'
    private static final String LABEL_SYMBOL = 'symbol'
    private static final String LABEL_RATE = 'rate'
    private static final String SHOW_FAILURE_MESSAGE = "Failed to load task information page"


    ExhBeneficiaryService exhBeneficiaryService
    ExhCustomerService exhCustomerService

    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
	@Autowired
	ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * 1. check necessary parameters
     * 2. check if customer exists
     * 3. check if beneficiary exits
     * 4. check if beneficiary is related with given customer
     * 5. check companyId of both customer & beneficiary
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)            // set default value
            String systemCurrencyName = currencyCacheUtility.getLocalCurrency().symbol
            result.put(SYSTEM_CURRENCY_NAME, systemCurrencyName)

            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.customerId || !params.beneficiaryId) {         // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long beneficiaryId = Tools.parseLongInput(params.beneficiaryId)
            long customerId = Tools.parseLongInput(params.customerId)

            if (customerId == 0 || beneficiaryId == 0) {               // check parse exception
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            if (customerId != exhSessionUtil.getUserCustomerId()) {            // check login user
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            ExhBeneficiary beneficiary = exhBeneficiaryService.read(beneficiaryId)     // get beneficiary object from DB
            if (!beneficiary) {                                         // check whether get beneficiary object exists or not
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            boolean hasBeneficiary = beneficiary.hasBeneficiary(customerId)      // check whether get beneficiary object exists or not by customer
            if (!hasBeneficiary) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Get list of remittance purpose, district for dropDown and Task list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result
        Map gridOutput
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap) params
            parameterMap.rp = DEFAULT_RESULT_PER_PAGE       // set result per page '15'
            initPager(parameterMap)                         // initialize params for flexGrid

            List<ExhTask> taskList = []
            int count = 0
            long beneficiaryId = 0L
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)
            SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
            SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)
			SystemEntity exhPaidByOnlineObj = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_ONLINE, companyId)

            if (parameterMap.beneficiaryId) {
                beneficiaryId = Long.parseLong(parameterMap.beneficiaryId.toString())
                taskList = listTask(beneficiaryId, companyId, customerTaskObj.id)                                // list of task by beneficiary
                count = countTask(beneficiaryId, companyId, customerTaskObj.id)
            } else {
                taskList = listTask(companyId, customerTaskObj.id)
                count = countTask(companyId, customerTaskObj.id)
            }

            List tasks = wrapTaskInGridEntityList(taskList, start)              // warp task for gird
            gridOutput = [page: pageNumber, total: count, rows: tasks]          // build grid obj

            List<ExhRemittancePurpose> lstRemittancePurpose = exhRemittancePurposeCacheUtility.list()      // get list of remittance purpose from cache

            List lstCurrencyCombo = buildFromToCurrencyCombo()           // build currency combo list

            Integer systemCurrency = currencyCacheUtility.getLocalCurrency().id
            String systemCurrencyName = currencyCacheUtility.getLocalCurrency().symbol        // get local currency symbol ie AUD, GBP etc..
            Integer bdtCurrency = currencyCacheUtility.getForeignCurrency().id

            Double regularFee = Tools.TASK_REGULAR_FEE           // set default value ie 0.00
			long bankDepositId = exhPaymentMethodObj.id
			long cashCollectionId = exhPaymentMethodCashObj.id
			long onlinePaymentId = exhPaidByOnlineObj.id
            List<District> lstDistricts = districtCacheUtility.listDistrictOfValidBranches()    // get district list from cache

            ExhBeneficiary beneficiaryInstance = null
            ExhCustomer customerInstance = null

            if (beneficiaryId > 0L) {
                beneficiaryInstance = exhBeneficiaryService.read(beneficiaryId)      // get beneficiary object from DB
                customerInstance = exhCustomerService.read(exhSessionUtil.getUserCustomerId())    // get customer object from DB
            }
			String surname = customerInstance? (customerInstance.surname? (Tools.SINGLE_SPACE + customerInstance.surname) : Tools.EMPTY_SPACE) : Tools.EMPTY_SPACE
            result = [                                 // build a map for UI
                    taskListJSON: gridOutput,
                    beneficiaryId: beneficiaryId,
                    beneficiaryName: beneficiaryInstance ? beneficiaryInstance.fullName : null,
                    customerId: customerInstance ? customerInstance.id : null,
                    customerCode: customerInstance ? customerInstance.code : null,
                    customerName: customerInstance ? (customerInstance.name + surname) : null,
                    photoIdType: beneficiaryInstance ? beneficiaryInstance.photoIdType : null,
                    photoIdNo: beneficiaryInstance ? beneficiaryInstance.photoIdNo : null,
                    accNo: beneficiaryInstance ? beneficiaryInstance.accountNo : null,
                    benBank: beneficiaryInstance ? beneficiaryInstance.bank : null,
                    benBranch: beneficiaryInstance ? beneficiaryInstance.bankBranch : null,
                    benDistrict: beneficiaryInstance ? beneficiaryInstance.district : null,
                    lstRemittancePurpose: lstRemittancePurpose,
                    agentId: exhSessionUtil.getUserAgentId(),
                    lstDistricts: lstDistricts,
                    systemCurrency: systemCurrency,
                    systemCurrencyName: systemCurrencyName,
                    bdtCurrency: bdtCurrency,
                    lstCurrencyCombo: lstCurrencyCombo,
                    regularFee: regularFee,
                    bankDepositId: bankDepositId,
                    cashCollectionId: cashCollectionId,
					onlinePaymentId: onlinePaymentId,
					initialRate: getInitialConversionRate()
            ]
            return result

        } catch (Throwable e) {
            log.error(e.message)
            gridOutput = [page: pageNumber, total: 0, rows: null]
            result = [exHouseList: null, currencyList: null, taskInfoListJSON: gridOutput]
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
     * do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message and default value to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj      // cast map returned from execute method
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                result.put(SYSTEM_CURRENCY_NAME, preResult.get(SYSTEM_CURRENCY_NAME))
            } else {
                result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of Task in grid entity
     * @param lstTask -list of Task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */
    private List wrapTaskInGridEntityList(List<ExhTask> lstTask, int start) {
        List tasks = []
        int counter = start + 1
        ExhTask task
        GridEntity obj
        String payMethod
        Double temp_total_due
        Double amount_gbp
        double total_due
        for (int i = 0; i < lstTask.size(); i++) {
            task = lstTask[i];
            obj = new GridEntity();           // build grid entity
            obj.id = task.id;
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key   // get payment method i.e. Cash Collection or Bank Deposit
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)
            obj.cell = [
                    counter,
                    task.id, task.refNo,
                    task.amountInForeignCurrency,
                    amount_gbp,
                    total_due,
                    task.customerName,
                    task.beneficiaryName,
                    payMethod,
                    task.regularFee,
                    task.discount
            ]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Get list of task which status is PENDING and task type of CUSTOMER through gird navigate
     */
    private List<ExhTask> listTask(long beneficiaryId, long companyId, long customerTaskId) {
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.findAllByCompanyIdAndUserIdAndTaskTypeIdAndBeneficiaryIdAndCurrentStatus(
                companyId,
                exhSessionUtil.appSessionUtil.getAppUser().id,
                customerTaskId,
                beneficiaryId, exhPendingTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
    }

    private int countTask(long beneficiaryId, long companyId, long customerTaskId) {
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.countByCompanyIdAndUserIdAndTaskTypeIdAndBeneficiaryIdAndCurrentStatus(
                companyId,
                exhSessionUtil.appSessionUtil.getAppUser().id,
                customerTaskId,
                beneficiaryId, exhPendingTaskSysEntityObject.id
        )
    }

    /**
     * Get list of task which status is PENDING and task type of CUSTOMER
     */
    private List<ExhTask> listTask(long companyId, long customerTaskId) {
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.findAllByCompanyIdAndUserIdAndTaskTypeIdAndCurrentStatus(
                companyId,
                exhSessionUtil.appSessionUtil.getAppUser().id,
                customerTaskId,
                exhPendingTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
    }

    /**
     * Get count of task which status is PENDING and task type of CUSTOMER
     */
    private int countTask(long companyId, long customerTaskId) {
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.countByCompanyIdAndUserIdAndTaskTypeIdAndCurrentStatus(
                companyId,
                exhSessionUtil.appSessionUtil.getAppUser().id,
                customerTaskId,
                exhPendingTaskSysEntityObject.id
        )
    }

    /**
     * Get currency list for dropDown and with its rate
     */
    private List buildFromToCurrencyCombo() {
        Currency fromCurrency = currencyCacheUtility.getLocalCurrency()
        Currency toCurrency = currencyCacheUtility.getForeignCurrency()
        ExhCurrencyConversion gbpToBdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(fromCurrency.id, toCurrency.id)   // get local currency
        ExhCurrencyConversion bdtToGdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(toCurrency.id, fromCurrency.id)    // get foreign currency

        List lstCurrencyCombo = []
        Map currencyCombo = new LinkedHashMap()
        currencyCombo.put(LABEL_ID, fromCurrency.id)    // GBP, AUD
        currencyCombo.put(LABEL_SYMBOL, fromCurrency.symbol)
        currencyCombo.put(LABEL_RATE, gbpToBdtConversion ? gbpToBdtConversion.buyRate : 0)
        lstCurrencyCombo << currencyCombo

        currencyCombo = new LinkedHashMap()
        currencyCombo.put(LABEL_ID, toCurrency.id)   // BDT
        currencyCombo.put(LABEL_SYMBOL, toCurrency.symbol)
        currencyCombo.put(LABEL_RATE, bdtToGdtConversion ? bdtToGdtConversion.sellRate : 0)
        lstCurrencyCombo << currencyCombo
        return lstCurrencyCombo
    }

	private Double getInitialConversionRate() {
		Currency fromCurrency = currencyCacheUtility.getLocalCurrency()
		Currency toCurrency = currencyCacheUtility.getForeignCurrency()
		ExhCurrencyConversion gbpToBdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(fromCurrency.id, toCurrency.id)   // get local currency
		return gbpToBdtConversion.sellRate
	}
}
