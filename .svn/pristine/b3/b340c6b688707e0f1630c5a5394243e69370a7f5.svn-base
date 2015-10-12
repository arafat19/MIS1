package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
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
 * Show list customer remittance details for Grid for admin or cashier.
 * For details go through Use-Case doc named 'ExhListRemittanceDetailsByCustomerActionService'
 */
class ExhListRemittanceDetailsByCustomerActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String CUSTOMER_REMITTANCE_MAP = 'customerRemittanceMap'
    private static String CUSTOMER_NOT_FOUND = "Customer not found."
    private static String CUSTOMER_INSTANCE = "customerInstance"
    private static String ERROR_MESSAGE = "Failed to get customer remittance details"

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * 1. check necessary parameters
     * 2. check if customer exists
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)             // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.customerId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            if ((!params.createdDateFrom) || (!params.createdDateTo)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long customerId = Long.parseLong(params.customerId)
            ExhCustomer customer = readWithExchangeHouse(customerId)           // get customer by id
            if (!customer) {
                result.put(Tools.MESSAGE, CUSTOMER_NOT_FOUND)
                return result
            }
            result.put(CUSTOMER_INSTANCE, customer)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
     * Get list of remittance details corresponding customer for grid
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from previous methods
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()

        try {
            Map preResult = (Map) obj               // cast map returned from previous method
            GrailsParameterMap params = (GrailsParameterMap) parameters
            ExhCustomer customer = (ExhCustomer) preResult.get(CUSTOMER_INSTANCE)

            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE
            }

            initSearch(params)              // initialize params for flexGrid

            Date startDate = DateUtility.parseMaskedFromDate(params.createdDateFrom)
            Date endDate = DateUtility.parseMaskedToDate(params.createdDateTo)

            Map customerRemittanceMap = (Map) getRemittanceDetailsByCustomer(customer.id, startDate, endDate)   // get remittance details of customer

            if (!customerRemittanceMap) {
                result.put(CUSTOMER_REMITTANCE_MAP, null)
            } else {
                result.put(CUSTOMER_REMITTANCE_MAP, customerRemittanceMap)
            }

            result.put(CUSTOMER_INSTANCE, customer)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap task list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object result) {
        LinkedHashMap output = new LinkedHashMap()
        try {
            Map resultMap = (Map) result
            Map customerRemittanceMap = (Map) resultMap.get(CUSTOMER_REMITTANCE_MAP)

            if (customerRemittanceMap == null) {
                output = [page: pageNumber, total: currentCount, rows: null]
                return output
            }

            List lstTask = (List) customerRemittanceMap.taskList
            int count = (int) customerRemittanceMap.count

            List tasks = wrapTaskList(lstTask, start)            //  wrap task

            output = [page: pageNumber, total: count, rows: tasks]
            return output
        } catch (Exception e) {
            log.error(e.getMessage());
            output.put(Tools.IS_ERROR, Boolean.TRUE)
            output.put(Tools.MESSAGE, ERROR_MESSAGE)
            return output
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap();
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.message)
            } else {
                result.put(Tools.MESSAGE, ERROR_MESSAGE)
            }

            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of task in grid entity
     * @param taskList -list of task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */
    private List wrapTaskList(List taskList, int start) {
        List tasks = []
        LinkedHashMap task
        GridEntity obj
        Date created_on
        String created_on_str
        String beneficiaryName
        int counter = start + 1
        for (int i = 0; i < taskList.size(); i++) {
            task = (LinkedHashMap) taskList[i]
            obj = new GridEntity()
            obj.id = task.id
            created_on = (Date) task.created_on
            created_on_str = DateUtility.getLongDateForUI(created_on)       // get date format e.g. 'dd-Mon-yyyy'
            beneficiaryName = task.full_name
            obj.cell = [
                    "${counter}",
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
    private ExhCustomer readWithExchangeHouse(long id) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        ExhCustomer customer = ExhCustomer.findByIdAndCompanyId(id, companyId, [readOnly: true])
        return customer
    }

    /**
     * Get customer remittance details
     * @param customerId
     * @return returnResult -a map containing list of task & count
     */
    private Map getRemittanceDetailsByCustomer(Long customerId, Date fromDate, Date toDate) {
        Map returnResult = new LinkedHashMap();
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhCanceledTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_CANCELLED_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String queryStr = """
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
        String queryCount = """
            SELECT
                COUNT(task.id) AS count
            FROM
                exh_task task
            WHERE
                task.customer_id = ${customerId} AND
                current_status IN
                (${exhNewTaskSysEntityObject.id},
                ${exhSentToBankSysEntityObject.id},
                ${exhSentToOtherBankSysEntityObject.id},
                ${exhResolvedByOtherBankSysEntityObject.id},
                ${exhCanceledTaskSysEntityObject.id}) AND
                 task.created_on BETWEEN '${DateUtility.getDBDateFormatWithSecond(fromDate)}' AND '${DateUtility.getDBDateFormatWithSecond(toDate)}'
            """
        List lstTaskList = executeSelectSql(queryStr)
        List count = executeSelectSql(queryCount)

        int lstSize = lstTaskList.size()
        if (lstSize > 0) {
            returnResult = [taskList: lstTaskList, count: count[0].count]
        } else {
            returnResult = null
        }

        return returnResult
    }

}

