package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.entity.District
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCurrencyConversion
import com.athena.mis.exchangehouse.entity.ExhRemittancePurpose
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.utility.*
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for task and list of task for grid
 *  For details go through Use-Case doc named 'ShowAgentTaskForCashierActionService'
 */
class ShowAgentTaskForCashierActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String LABEL_ID = 'id'
    private static final String LABEL_SYMBOL = 'symbol'
    private static final String LABEL_RATE = 'rate'


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
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
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
            parameterMap.rp = DEFAULT_RESULT_PER_PAGE           // set result per page '15'
            initPager(parameterMap)        // initialize params for flexGrid

            Map gridResult = listTask()        // list of task

            List tasks = wrapTaskInGridEntityList(gridResult.taskList, start)            // warp task for gird
            gridOutput = [page: pageNumber, total: gridResult.count, rows: tasks]        // build grid obj

            List<ExhRemittancePurpose> lstRemittancePurpose = buildRemittancePurpose()         // build remittance purpose for dropDown

            List lstCurrencyCombo = buildFromToCurrencyCombo()                // build currency combo list

            Integer systemCurrency = currencyCacheUtility.getLocalCurrency().id
            String systemCurrencyName = currencyCacheUtility.getLocalCurrency().symbol      // get local currency symbol ie AUD, GBP etc..
            Integer bdtCurrency = currencyCacheUtility.getForeignCurrency().id
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
            SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)
			SystemEntity exhPaidByOnlineObj = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_ONLINE, companyId)

            List<District> lstDistricts = districtCacheUtility.listDistrictOfValidBranches()       // get district list from cache
            Double regularFee = Tools.TASK_REGULAR_FEE              // set default value ie 0.00
			long bankDepositId = exhPaymentMethodObj.id
			long cashCollectionId = exhPaymentMethodCashObj.id
			long onlinePaymentId = exhPaidByOnlineObj.id

            result = [                                    // build a map for UI
                    taskListJSON: gridOutput,
                    lstRemittancePurpose: lstRemittancePurpose,
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
     * do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
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
            task = lstTask[i]
            obj = new GridEntity()           // build grid entity object
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key         // Cash Collection or Bank Deposit
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)

            obj.cell = [counter,
                    task.id,
                    task.refNo,
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
     * Get list of task which status is NEW and task type AGENT
     */
    private LinkedHashMap listTask() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)

        List<ExhTask> taskList = ExhTask.findAllByCompanyIdAndTaskTypeIdAndCurrentStatus(companyId,
                agentTaskObj.id, exhNewTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        int count = ExhTask.countByCompanyIdAndTaskTypeIdAndCurrentStatus(companyId,
                agentTaskObj.id,
                exhNewTaskSysEntityObject.id)
        return [taskList: taskList, count: count]
    }

    /**
     * Get currency list for dropDown and with its rate
     */
    private List buildFromToCurrencyCombo() {
        Currency fromCurrency = currencyCacheUtility.getLocalCurrency()
        Currency toCurrency = currencyCacheUtility.getForeignCurrency()
        ExhCurrencyConversion gbpToBdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(fromCurrency.id, toCurrency.id)    // get local currency
        ExhCurrencyConversion bdtToGdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(toCurrency.id, fromCurrency.id)    // get foreign currency

        List lstCurrencyCombo = []
        Map currencyCombo = new LinkedHashMap()
        currencyCombo.put(LABEL_ID, fromCurrency.id)   // GBP, AUD
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

    /**
     * Get remittance purpose list for dropDown
     */
    private List<ExhRemittancePurpose> buildRemittancePurpose() {
        List<ExhRemittancePurpose> lstRemittancePurpose = []
        List<ExhRemittancePurpose> tempLstRemittancePurpose = exhRemittancePurposeCacheUtility.list()      // get list of remittance purpose from cache

        for (int i = 0; i < tempLstRemittancePurpose.size(); i++) {
            ExhRemittancePurpose remittancePurpose = new ExhRemittancePurpose()
            remittancePurpose.properties = tempLstRemittancePurpose[i].properties
            remittancePurpose.id = tempLstRemittancePurpose[i].id
            lstRemittancePurpose << remittancePurpose
        }

        return lstRemittancePurpose
    }

	private Double getInitialConversionRate() {
		Currency fromCurrency = currencyCacheUtility.getLocalCurrency()
		Currency toCurrency = currencyCacheUtility.getForeignCurrency()
		ExhCurrencyConversion gbpToBdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(fromCurrency.id, toCurrency.id)   // get local currency
		return gbpToBdtConversion.sellRate
	}
}
