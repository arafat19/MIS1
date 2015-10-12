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
 *  Show UI for task CRUD for Agent and list of task for grid
 *  For details go through Use-Case doc named 'ExhShowTaskForAgentActionService'
 */
class ExhShowTaskForAgentActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load task information page"
    private static final String BENEFICIARY_NOT_APPROVED_MSG = "Beneficiary is not approved"
    private static final String LABEL_ID = 'id'
    private static final String LABEL_SYMBOL = 'symbol'
    private static final String LABEL_RATE = 'rate'
    private static final String SYSTEM_CURRENCY_NAME = "systemCurrencyName"

    ExhCustomerService exhCustomerService
    ExhBeneficiaryService exhBeneficiaryService

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
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * Get parameters from UI and check customer, beneficiary and others object
     * 1. check necessary parameters
     * 2. check if customer exists
     * 3. check if beneficiary exits
     * 4. check if beneficiary is approved
     * 5. check if beneficiary is related with given customer
     * 6. check companyId of both customer & beneficiary
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)           // set default value
            String systemCurrencyName = currencyCacheUtility.getLocalCurrency().symbol
            result.put(SYSTEM_CURRENCY_NAME, systemCurrencyName)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.customerId || !params.beneficiaryId) {           // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long customerId = Tools.parseLongInput(params.customerId)
            long beneficiaryId = Tools.parseLongInput(params.beneficiaryId)

            if (customerId == 0 || beneficiaryId == 0) {                   // check parse exception
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            ExhCustomer customer = exhCustomerService.read(customerId)         // get customer object from DB
            if (!customer) {                              // check whether get customer object exists or not
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            ExhBeneficiary beneficiary = exhBeneficiaryService.read(beneficiaryId)       // get beneficiary object from DB
            if (!beneficiary) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            if (beneficiary.approvedBy == 0) {                   // check whether get beneficiary object approved or not
                result.put(Tools.MESSAGE, BENEFICIARY_NOT_APPROVED_MSG)
                return result
            }

            boolean hasBeneficiary = beneficiary.hasBeneficiary(customerId)        // check whether get beneficiary object exists or not by customer
            if (!hasBeneficiary) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            if (customer.companyId != companyId || beneficiary.companyId != companyId) {              // check customer's or beneficiary's company
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
    public Object execute(Object parameters, Object obj) {
        Map result
        Map gridOutput
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            params.rp = DEFAULT_RESULT_PER_PAGE       // set result per page '15'
            initPager(params)    // initialize params for flexGrid

            Map gridResult
            long beneficiaryId = 0L
            long customerId = 0L
            if (params.beneficiaryId && params.customerId) {
                beneficiaryId = Long.parseLong(params.beneficiaryId.toString())
                gridResult = listTaskForAgent(beneficiaryId)           // list of task by beneficiary
                customerId = Long.parseLong(params.customerId.toString())
            } else {
                gridResult = listTaskForAgent()
            }

            List tasks = wrapTaskList(gridResult.taskList, start)              // warp task for gird
            gridOutput = [page: pageNumber, total: gridResult.count, rows: tasks]      // build grid obj

            List<ExhRemittancePurpose> lstRemittancePurpose = exhRemittancePurposeCacheUtility.list()

            List lstCurrencyCombo = buildFromToCurrencyCombo()            // build currency combo list

            Integer systemCurrency = currencyCacheUtility.getLocalCurrency().id
            String systemCurrencyName = currencyCacheUtility.getLocalCurrency().symbol    // get local currency symbol ie AUD, GBP etc..
            Integer bdtCurrency = currencyCacheUtility.getForeignCurrency().id

            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity exhPaymentMethodBankDepositObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
            SystemEntity exhPaymentMethodCashCollectionObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)
			SystemEntity exhPaidByOnlineObj = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_ONLINE, companyId)

            Double regularFee = Tools.TASK_REGULAR_FEE
			long bankDepositId = exhPaymentMethodBankDepositObj.id
			long cashCollectionId = exhPaymentMethodCashCollectionObj.id
			long onlinePaymentId = exhPaidByOnlineObj.id
            List<District> lstDistricts = districtCacheUtility.listDistrictOfValidBranches()     // get district list from cache

            ExhBeneficiary beneficiaryInstance = null
            String beneficiaryName = null
            String customerName = null
            String customerCode = null
            String photoIdType = null
            String photoIdNo = null
            String accNo = null
            String benBank = null
            String benBranch = null
            String benDistrict = null

            if (beneficiaryId > 0L) {
                beneficiaryInstance = exhBeneficiaryService.read(beneficiaryId)      // get beneficiary object from DB
                beneficiaryName = beneficiaryInstance.fullName
                ExhCustomer exhCustomer = exhCustomerService.read(customerId)          // get customer object from DB
				String surname = exhCustomer.surname ? (Tools.SINGLE_SPACE + exhCustomer.surname) : Tools.EMPTY_SPACE
                customerName = exhCustomer.name + surname
                customerCode = exhCustomer.code
                photoIdType = beneficiaryInstance.photoIdType
                photoIdNo = beneficiaryInstance.photoIdNo
                accNo = beneficiaryInstance.accountNo
                benBank = beneficiaryInstance.bank
                benBranch = beneficiaryInstance.bankBranch
                benDistrict = beneficiaryInstance.district

            }

            result = [                              // build a map for UI
                    taskListJSON: gridOutput,
                    beneficiaryId: beneficiaryId,
                    beneficiaryName: beneficiaryName,
                    customerId: customerId,
                    customerName: customerName,
                    customerCode: customerCode,
                    photoIdType: photoIdType,
                    photoIdNo: photoIdNo,
                    accNo: accNo,
                    benBank: benBank,
                    benBranch: benBranch,
                    benDistrict: benDistrict,
                    lstRemittancePurpose: lstRemittancePurpose,
                    agentId: exhSessionUtil.getUserAgentId(),
                    lstDistricts: lstDistricts,
                    systemCurrency: systemCurrency,
                    systemCurrencyName: systemCurrencyName,
                    bdtCurrency: bdtCurrency,
                    lstCurrencyCombo: lstCurrencyCombo,
                    regularFee: regularFee,
                    bankDepositId: bankDepositId,
                    cashCollectionId:cashCollectionId,
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
                LinkedHashMap preResult = (LinkedHashMap) obj         // cast map returned from execute method
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
    private List wrapTaskList(List<ExhTask> lstTask, int start) {
        List tasks = []
        int counter = start + 1
        ExhTask task
        GridEntity obj
        String payMethod
        Double temp_total_due
        Double amount_gbp
        double total_due
        for (int i = 0; i < lstTask.size(); i++) {
            task = lstTask[i]
            obj = new GridEntity()        // build grid entity object
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key      // get payment method i.e. Cash Collection or Bank Deposit
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)

            obj.cell = [counter, task.id, task.refNo,
                    task.amountInForeignCurrency, amount_gbp, total_due,
                    task.customerName, task.beneficiaryName, payMethod, task.regularFee, task.discount]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Get list of task which status is PENDING through gird navigate
     */
    private LinkedHashMap listTaskForAgent(long beneficiaryId) {
        long agentId = exhSessionUtil.getUserAgentId()
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        List<ExhTask> taskList = ExhTask.findAllByAgentIdAndBeneficiaryIdAndCurrentStatus(
                agentId,
                beneficiaryId,
                exhPendingTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )

        int count = ExhTask.countByAgentIdAndBeneficiaryIdAndCurrentStatus(
                agentId,
                beneficiaryId,
                exhPendingTaskSysEntityObject.id
        )
        return [taskList: taskList, count: count]
    }

    /**
     * Get list of which status is PENDING task
     */
    private LinkedHashMap listTaskForAgent() {
        long agentId = exhSessionUtil.getUserAgentId()
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        List<ExhTask> taskList = ExhTask.findAllByAgentIdAndCurrentStatus(
                agentId,
                exhPendingTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )

        int count = ExhTask.countByAgentIdAndCurrentStatus(
                agentId,
                exhPendingTaskSysEntityObject.id
        )
        return [taskList: taskList, count: count]
    }

    /**
     * Get currency list for dropDown and with its rate
     */
    private List buildFromToCurrencyCombo() {
        Currency fromCurrency = currencyCacheUtility.getLocalCurrency()
        Currency toCurrency = currencyCacheUtility.getForeignCurrency()
        ExhCurrencyConversion gbpToBdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(fromCurrency.id, toCurrency.id)  // get local currency
        ExhCurrencyConversion bdtToGdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(toCurrency.id, fromCurrency.id)   // get foreign currency

        List lstCurrencyCombo = []
        Map currencyCombo = new LinkedHashMap()
        currencyCombo.put(LABEL_ID, fromCurrency.id)  // GBP, AUD
        currencyCombo.put(LABEL_SYMBOL, fromCurrency.symbol)
        currencyCombo.put(LABEL_RATE, gbpToBdtConversion ? gbpToBdtConversion.sellRate : 0)
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

