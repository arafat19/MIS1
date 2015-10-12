package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show customer history for Grid.
 * For details go through Use-Case doc named 'ExhShowForCustomerRemittanceActionService'
 */
class ExhShowForCustomerRemittanceActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load page"
    private static final String SORTNAME = 'sortname'
    private static final String BENEFICIARY_NAME = 'beneficiary_name'
    private static final String SORTORDER = 'sortorder'
    private static final String CREATED_DATE_FROM = "createdDateFrom"
    private static final String CREATED_DATE_TO = "createdDateTo"
    private static final String LOCAL_CURRENCY_NAME = "localCurrencyName"


    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    /**
     * 1. check necessary parameters
     * 2. check if customer exists
     * 3. check if customer id parse exception
     * 4. check companyId of customer
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
            Date startDateStr = new Date() - DateUtility.DATE_RANGE_HUNDREAD_EIGHTY
            result.put(CREATED_DATE_FROM, DateUtility.getDateForUI(startDateStr))

            if (!params.customerId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long customerId = Tools.parseLongInput(params.customerId)
            if (customerId == 0) {                        // check parse exception
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            ExhCustomer customer = readWithExchangeHouse(customerId)
            if (!customer) {                           // check customer existence
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            if (customer.companyId != exhSessionUtil.appSessionUtil.getCompanyId()) {        // check companyId of customer
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
     * Get list of remittance details corresponding customer for grid
     * list contains such columns are date, refNo, beneficiaryName, Amount(BDT), Amount(Local)
     * Wrap list of remittance(s) for flexgrid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameters = (GrailsParameterMap) params
            parameters.put(RESULT_PER_PAGE_PARAM, DEFAULT_RESULT_PER_PAGE)
            parameters.put(SORTNAME, BENEFICIARY_NAME)
            parameters.put(SORTORDER, ASCENDING_SORT_ORDER)

            initSearch(parameters)               // initialize params for flexGrid

            Map gridOutput = new LinkedHashMap()
            gridOutput = [page: pageNumber, total: 0, rows: []]
            String strCustomerId = Tools.EMPTY_SPACE
            String strCustomerName = Tools.EMPTY_SPACE
            String strCustomerCode = Tools.EMPTY_SPACE
            String nationality = Tools.EMPTY_SPACE
            String currencySymbol = Tools.EMPTY_SPACE
            String declarationAmountStr = Tools.EMPTY_SPACE

            if (parameters.customerId) {
                Long customerId = Long.parseLong(parameters.customerId)
                ExhCustomer customer = readWithExchangeHouse(customerId)          // get customer
                if (customer) {
                    strCustomerId = customer.id
                    strCustomerName = customer.fullName
                    strCustomerCode = customer.code
                    declarationAmountStr = Tools.formatAmountWithoutCurrency(customer.declarationAmount)      // set currency format e.g. '##,##,##0.00'
                    nationality = countryCacheUtility.read(customer.countryId).nationality
                    Map serviceReturn = (Map) getRemittanceDetailsByCustomer(customerId)                      // get customer remittance details
                    List tasks = wrapTaskInGridEntityList(serviceReturn.taskList, start)                      // wrap data for grid
                    gridOutput.put(Tools.TOTAL, serviceReturn.count)
                    gridOutput.put(Tools.ROWS, tasks)
                }

            }

            Date startDateStr = new Date() - DateUtility.DATE_RANGE_HUNDREAD_EIGHTY
            result = [                          // build a map for customer detais
                    strCustomerId: strCustomerId,
                    strCustomerCode: strCustomerCode,
                    strCustomerName: strCustomerName,
                    declarationAmount: declarationAmountStr,
                    nationality: nationality,
                    remittanceListJSON: gridOutput,
                    createdDateFrom: DateUtility.getDateForUI(startDateStr)
            ]
            return result

        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for success operation
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
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                result.put(CREATED_DATE_FROM, preResult.get(CREATED_DATE_FROM))
                result.put(CREATED_DATE_TO, preResult.get(CREATED_DATE_TO))
                result.put(LOCAL_CURRENCY_NAME, preResult.get(LOCAL_CURRENCY_NAME))
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
     * Wrap list of task in grid entity
     * @param taskList -list of task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */
    private List wrapTaskInGridEntityList(List taskList, int start) {
        List tasks = []
        LinkedHashMap task
        GridEntity obj
        Date created_on
        String created_on_str
        String beneficiaryName
        int counter = start + 1

        for (int i = 0; i < taskList.size(); i++) {
            task = (LinkedHashMap) taskList[i]
            obj = new GridEntity()              // build grid object
            obj.id = task.id
            created_on = (Date) task.created_on
            created_on_str = DateUtility.getLongDateForUI(created_on)    // get date format e.g. 'dd-Mon-yyyy'
            beneficiaryName = task.full_name

            obj.cell = ["${counter}",
                    created_on_str,
                    task.ref_no,
                    beneficiaryName,
                    task.amount_in_foreign_currency,
                    task.amount_in_local_currency
            ]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Get customer object by id
     * @param customerId
     * @return customer
     */
    private ExhCustomer readWithExchangeHouse(long customerId) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        ExhCustomer customer = ExhCustomer.findByIdAndCompanyId(customerId, companyId, [readOnly: true])
        return customer
    }

    /**
     * Get customer remittance details
     * @param customerId
     * @return returnResult -a map containing list of task & count
     */
    private Map getRemittanceDetailsByCustomer(Long customerId) {
        Date fromDate = new Date() - DateUtility.DATE_RANGE_HUNDREAD_EIGHTY
        Date toDate = new Date()
        Map returnResult = new LinkedHashMap()
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhCanceledTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_CANCELLED_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String query = """
            SELECT
                task.id AS task_id,
                task.ref_no AS ref_no,
                task.created_on AS created_on,
                task.amount_in_foreign_currency,
                task.amount_in_local_currency,
                beneficiary.id AS beneficiary_id,
                ARRAY_TO_STRING(ARRAY[beneficiary.first_name,beneficiary.middle_name,beneficiary.last_name],' ') full_name
            FROM
                exh_task task,
                exh_beneficiary beneficiary
            WHERE
                task.beneficiary_id = beneficiary.id AND
                task.customer_id = ${customerId} AND
                task.current_status IN
                (${exhNewTaskSysEntityObject.id},
                ${exhSentToBankSysEntityObject.id},
                ${exhSentToOtherBankSysEntityObject.id},
                ${exhResolvedByOtherBankSysEntityObject.id},
                ${exhCanceledTaskSysEntityObject.id}) AND
                task.created_on BETWEEN '${DateUtility.getDBDateFormatWithSecond(fromDate)}' AND '${DateUtility.getDBDateFormatWithSecond(toDate)}'
            ORDER BY task.created_on desc
            OFFSET ${start}
            LIMIT ${resultPerPage}
        """
        String count_query = """
            SELECT
                COUNT(task.id) AS count
            FROM
                exh_task task
            WHERE
                task.customer_id = ${customerId} AND
            task.current_status IN
            (${exhNewTaskSysEntityObject.id},
            ${exhSentToBankSysEntityObject.id},
            ${exhSentToOtherBankSysEntityObject.id},
            ${exhResolvedByOtherBankSysEntityObject.id},
            ${exhCanceledTaskSysEntityObject.id}) AND
            task.created_on BETWEEN '${DateUtility.getDBDateFormatWithSecond(fromDate)}' AND '${DateUtility.getDBDateFormatWithSecond(toDate)}'
            """
        List lstTaskList = executeSelectSql(query)
        List count = executeSelectSql(count_query)
        returnResult = [taskList: lstTaskList, count: count[0].count]
        return returnResult
    }
}
