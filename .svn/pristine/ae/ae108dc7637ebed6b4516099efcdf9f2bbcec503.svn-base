package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Currency
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
 *  Show UI for task CRUD for cashier and list of task for grid
 *  For details go through Use-Case doc named 'ShowExhTaskForCashierActionService'
 */
class ShowExhTaskForCashierActionService extends BaseService implements ActionIntf {
	private Logger log = Logger.getLogger(getClass())

	private static final String SYSTEM_CURRENCY_NAME = "systemCurrencyName"
	private static final String SHOW_FAILURE_MESSAGE = "Failed to load task information page"
	private static final String BENEFICIARY_NOT_APPROVED_MSG = "Beneficiary is not approved"
	private static final String LABEL_NO_RED = "<span style='color:red;'>NO</span>"

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
	@Autowired
	ExhTaskTypeCacheUtility exhTaskTypeCacheUtility

	/**
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
			result.put(Tools.IS_ERROR, Boolean.TRUE)            // set default value
			String systemCurrencyName = currencyCacheUtility.getLocalCurrency().symbol
			result.put(SYSTEM_CURRENCY_NAME, systemCurrencyName)
			GrailsParameterMap params = (GrailsParameterMap) parameters

			if (!params.customerId || !params.beneficiaryId) {        // check required parameters
				result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
				return result
			}

			long customerId = Tools.parseLongInput(params.customerId)
			long beneficiaryId = Tools.parseLongInput(params.beneficiaryId)

			if (customerId == 0 || beneficiaryId == 0) {                 // check parse exception
				result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
				return result
			}

			ExhCustomer customer = exhCustomerService.read(customerId)        // get customer object from DB
			if (!customer) {                                                   // check whether get customer object exists or not
				result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
				return result
			}

			ExhBeneficiary exhBeneficiary = exhBeneficiaryService.read(beneficiaryId)     // get beneficiary object from DB
			if (!exhBeneficiary) {                                            // check whether get beneficiary object exists or not
				result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
				return result
			}

			if (exhBeneficiary.approvedBy == 0) {                           // check whether get beneficiary object approved or not
				result.put(Tools.MESSAGE, BENEFICIARY_NOT_APPROVED_MSG)
				return result
			}

			boolean hasBeneficiary = exhBeneficiary.hasBeneficiary(customerId)         // check whether get beneficiary object exists or not by customer
			if (!hasBeneficiary) {
				result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
				return result
			}

			long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
			if (customer.companyId != companyId || exhBeneficiary.companyId != companyId) {     // check customer's or beneficiary's company
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
			GrailsParameterMap parameterMap = (GrailsParameterMap) params
			parameterMap.rp = DEFAULT_RESULT_PER_PAGE       // set result per page '15'
			initPager(parameterMap)                         // initialize params for flexGrid

			List<ExhTask> taskList = []
			int count = 0
			long beneficiaryId = 0L
			long customerId = 0L
			long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
			SystemEntity taskTypeObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_EXH_TASK, companyId)
			SystemEntity exhPaymentMethodBankDepositObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
			SystemEntity exhPaymentMethodCashCollectionObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)
			SystemEntity exhPaidByOnlineObj = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_ONLINE, companyId)

			if (parameterMap.beneficiaryId) {
				beneficiaryId = Long.parseLong(parameterMap.beneficiaryId.toString())
				taskList = listTask(beneficiaryId, companyId, taskTypeObj.id)                    // list of task by beneficiary
				count = countTask(beneficiaryId, companyId, taskTypeObj.id)
				customerId = Long.parseLong(parameterMap.customerId)
			} else {
				taskList = listTask(companyId, taskTypeObj.id)       // list of task
				count = countTask(companyId, taskTypeObj.id)
			}

			List tasks = wrapTaskList(taskList, start)                     // warp task for gird
			gridOutput = [page: pageNumber, total: count, rows: tasks]    // build grid obj

			List<ExhRemittancePurpose> lstRemittancePurpose = exhRemittancePurposeCacheUtility.list()      // get list of remittance purpose from cache

			Integer systemCurrency = currencyCacheUtility.getLocalCurrency().id
			String systemCurrencyName = currencyCacheUtility.getLocalCurrency().symbol                   // get local currency symbol ie AUD, GBP etc..
			Integer bdtCurrency = currencyCacheUtility.getForeignCurrency().id

			Double regularFee = Tools.TASK_REGULAR_FEE      // set default value ie 0.00
			long bankDepositId = exhPaymentMethodBankDepositObj.id
			long cashCollectionId = exhPaymentMethodCashCollectionObj.id
			long onlinePaymentId = exhPaidByOnlineObj.id

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
				beneficiaryInstance = exhBeneficiaryService.read(beneficiaryId)                // get beneficiary object from DB
				beneficiaryName = beneficiaryInstance.fullName
				ExhCustomer exhCustomer = exhCustomerService.read(customerId)                   // get customer object from DB
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

			result = [                                                  // build a map for UI
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
					systemCurrency: systemCurrency,
					systemCurrencyName: systemCurrencyName,
					bdtCurrency: bdtCurrency,
					initialRate: getInitialConversionRate(),
					regularFee: regularFee,
					bankDepositId: bankDepositId,
					cashCollectionId: cashCollectionId,
					onlinePaymentId: onlinePaymentId
			]
			return result

		} catch (Throwable e) {
			log.error(e.message)
			gridOutput = [page: pageNumber, total: 0, rows: null]
			result = [exHouseList: null, currencyList: null, lstPaymentMethod: null, taskInfoListJSON: gridOutput]
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
				LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from execute method
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
			obj = new GridEntity()      // build grid entity object
			obj.id = task.id
			payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key        // get payment method i.e. Cash Collection or Bank Deposit
			amount_gbp = task.amountInLocalCurrency
			temp_total_due = amount_gbp + task.regularFee - task.discount
			total_due = temp_total_due.round(2)
			String gatewayPayment = task.isGatewayPaymentDone ? Tools.YES : LABEL_NO_RED
			obj.cell = [
					counter,
					task.id,
					task.refNo,
					task.amountInForeignCurrency,
					amount_gbp,
					total_due,
					task.customerName,
					task.beneficiaryName,
					payMethod,
					task.regularFee,
					task.discount,
					gatewayPayment
			]
			tasks << obj
			counter++
		}
		return tasks
	}

	/**
	 * Get list of task which status is NEW & type of EXH_TASK  through gird navigate
	 * @param beneficiaryId
	 * @return -list of task
	 */
	private List<ExhTask> listTask(long beneficiaryId, long companyId, long taskTypeId) {
		SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)

		return ExhTask.findAllByCompanyIdAndTaskTypeIdAndBeneficiaryIdAndCurrentStatus(companyId,
				taskTypeId,
				beneficiaryId,
				exhNewTaskSysEntityObject.id,
				[max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
	}

	/**
	 * Get count of task which status is NEW & type of EXH_TASK  through gird navigate
	 * @param beneficiaryId
	 * @return -count of task
	 */
	private int countTask(long beneficiaryId, long companyId, long taskTypeId) {
		SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
		return ExhTask.countByCompanyIdAndTaskTypeIdAndBeneficiaryIdAndCurrentStatus(companyId,
				taskTypeId,
				beneficiaryId,
				exhNewTaskSysEntityObject.id)
	}

	/**
	 * Get list of task which status is NEW & type of EXH_TASK
	 */
	private List<ExhTask> listTask(long companyId, long taskTypeId) {
		SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
		return ExhTask.findAllByCompanyIdAndTaskTypeIdAndCurrentStatus(companyId,
				taskTypeId,
				exhNewTaskSysEntityObject.id,
				[max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
	}

	private int countTask(long companyId, long taskTypeId) {
		SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
		return ExhTask.countByCompanyIdAndTaskTypeIdAndCurrentStatus(companyId,
				taskTypeId,
				exhNewTaskSysEntityObject.id)
	}

	private Double getInitialConversionRate() {
		Currency fromCurrency = currencyCacheUtility.getLocalCurrency()
		Currency toCurrency = currencyCacheUtility.getForeignCurrency()
		ExhCurrencyConversion gbpToBdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(fromCurrency.id, toCurrency.id)   // get local currency
		return gbpToBdtConversion.sellRate
	}
}
