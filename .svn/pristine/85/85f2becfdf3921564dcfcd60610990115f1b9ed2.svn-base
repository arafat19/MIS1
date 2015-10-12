package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.*
import com.athena.mis.application.utility.*
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.entity.ExhTaskTrace
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.exchangehouse.wp.TaskDetailsMap
import com.athena.mis.exchangehouse.wp.TaskQuery
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.sql.DataSource

/**
 *   Search task details through refNo or pinNo for Agent
 *  For details go through Use-Case doc named 'ExhGetDetailsByRefOrPinForAgentActionService'
 */
class ExhGetDetailsByRefOrPinForAgentActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Failed to get task details"
    private static final String GRID_OUTPUT = "gridOutput"
    private static final String TASK_INFO_MAP = "taskInfoMap"
    private static final String TASK_QUERY_OBJECT = "taskQuery"
    private static final String SECURITY_TYPE_PIN = "Pin No"
    private static final String SECURITY_TYPE_REF_NO = "Ref No"
    private static final String TASK_OBJECT = "task"
    private static final String TASK_STATUS = "taskStatus"

    private static final String QUERY_STR_PIN = "pin_no"
    private static final String QUERY_STR_TRANSACTION_REF_NO = "transaction_ref_no"
    private static final String TASK_NOT_FOUND_MESSAGE = "Task not found."
    private static final String QUERY_STR_REF_NO = "ref_no"

    private static final String IS_PAID = "Paid"
    private static final String IS_CANCELLED = "Cancelled"
    private static final String IS_UNPAID = "Unpaid"
    private static final int ARMS_CANCELLED_TASK_STATUS = 6
    private static final int ARMS_DECISION_TAKEN_TASK_STATUS = 30
    private static final int ARMS_WAITING_FOR_RECONCILIATION_TASK_STATUS = 60

    // declare datasource for ARMS
    DataSource dataSource_arms
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * 1. check necessary parameters
     * 2. pull task by refNo or pinNo
     * 3. check if task exits
     * 5. check hasRole customer and if found then check task owners of customer
     * 6. check hasRole agent and if found then check task owners of agent
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, true)
        try {

            GrailsParameterMap parameters = (GrailsParameterMap) params
            if ((!parameters.securityType) || (!parameters.securityNo) ||
                    (!parameters.createdDateFrom) || (!parameters.createdDateTo)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            Date createdDateFrom = DateUtility.parseMaskedFromDate(parameters.createdDateFrom)
            Date createdDateTo = DateUtility.parseMaskedToDate(parameters.createdDateTo)

            String securityType = parameters.securityType.toString().trim()
            TaskQuery taskQuery = new TaskQuery()
            taskQuery.securityNo = parameters.securityNo.toString().trim()
            taskQuery.createdDateFrom = createdDateFrom
            taskQuery.createdDateTo = createdDateTo
            taskQuery.companyId = exhSessionUtil.appSessionUtil.getCompanyId()

            ExhTask task = null
            if (securityType.equals(SECURITY_TYPE_PIN)) {
                taskQuery.securityType = SECURITY_TYPE_PIN
                task = searchTaskByPinNo(taskQuery.securityNo, taskQuery.createdDateFrom, taskQuery.createdDateTo)
            } else if (securityType.equals(SECURITY_TYPE_REF_NO)) {
                taskQuery.securityType = SECURITY_TYPE_REF_NO
                task = searchTaskByRefNo(taskQuery.securityNo, taskQuery.createdDateFrom, taskQuery.createdDateTo)
            }

            if (!task) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
                return result
            }

            AppUser user = exhSessionUtil.appSessionUtil.getAppUser()

            if (exhSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_EXH_CUSTOMER)) {
                if (exhSessionUtil.getUserCustomerId() != task.customerId) {
                    result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
                    return result
                }
            }

            if (exhSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_EXH_AGENT)
                    && task.agentId != exhSessionUtil.getUserAgentId()) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
                return result
            }
            result.put(TASK_OBJECT, task)
            result.put(TASK_QUERY_OBJECT, taskQuery)
            result.put(Tools.IS_ERROR, Boolean.FALSE)

            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Get task details for UI
     * @param params -N/A
     * @param obj -map returned from previous methods
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            TaskQuery taskQuery = (TaskQuery) preResult.get(TASK_QUERY_OBJECT)
            ExhTask task = (ExhTask) preResult.get(TASK_OBJECT)
            TaskDetailsMap taskDetailsMap = null
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity exhCanceledTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_CANCELLED_TASK, companyId)
            SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)
            SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
            SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
            SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
            SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

            if (task.currentStatus == exhSentToBankSysEntityObject.id) {
                taskDetailsMap = getTaskDetails(taskQuery)
            } else if (task.currentStatus == exhSentToOtherBankSysEntityObject.id || task.currentStatus == exhResolvedByOtherBankSysEntityObject.id) {
                taskDetailsMap = getTaskDetailsFromSFSL(taskQuery)
            } else if (task.currentStatus == exhPendingTaskSysEntityObject.id ||
                    task.currentStatus == exhNewTaskSysEntityObject.id ||
                    task.currentStatus == exhCanceledTaskSysEntityObject.id) {
                taskDetailsMap = getTaskDetailsFromSFSLBeforeSentToBank(taskQuery)
                if(task.currentStatus == exhCanceledTaskSysEntityObject.id) {
                    taskDetailsMap.isPaid = IS_CANCELLED
                }
            }
            SystemEntity taskStatus= (SystemEntity)exhTaskStatusCacheUtility.read(task.currentStatus)
            if (taskDetailsMap.success) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                result.put(TASK_INFO_MAP, taskDetailsMap)
                result.put(TASK_OBJECT, task)
                result.put(TASK_STATUS,taskStatus.key)
            } else {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(TASK_INFO_MAP, null)
                result.put(Tools.MESSAGE, taskDetailsMap.message)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            result.put(TASK_INFO_MAP, null)
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
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, true)
            if (obj && obj.message) {
                result.put(Tools.MESSAGE, obj.message)
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            result.put(GRID_OUTPUT, null)
            result.put(TASK_INFO_MAP, null)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            result.put(GRID_OUTPUT, null)
            result.put(TASK_INFO_MAP, null)
            return result
        }

    }

    /**
     * Get a task by pinNo
     * @param securityNo
     * @param createdDateFrom
     * @param createdDateTo
     * @return exhTask - a object of ExhTask
     */
    private ExhTask searchTaskByPinNo(String securityNo, Date createdDateFrom, Date createdDateTo) {
        securityNo = securityNo.toUpperCase()
        ExhTask exhTask = ExhTask.findByPinNoAndCreatedOnBetween(securityNo, createdDateFrom, createdDateTo, [readOnly: true])
        return exhTask
    }

    /**
     * Get a task by refNo
     * @param securityNo
     * @param createdDateFrom
     * @param createdDateTo
     * @return exhTask - a object of ExhTask
     */
    private ExhTask searchTaskByRefNo(String securityNo, Date createdDateFrom, Date createdDateTo) {
        securityNo = securityNo.toUpperCase()
        ExhTask exhTask = ExhTask.findByRefNoAndCreatedOnBetween(securityNo, createdDateFrom, createdDateTo, [readOnly: true])
        return exhTask
    }

    /**
     * Build task details through TaskDetailsMap
     * @param taskQuery
     * @return taskDetailsMap -a update object of TaskDetailsMap
     */
    private TaskDetailsMap getTaskDetails(TaskQuery taskQuery) {
        TaskDetailsMap taskDetailsMap = new TaskDetailsMap()
        taskDetailsMap.success = false
        try {
            GroovyRowResult result = null
            if (taskQuery.securityType.equals(SECURITY_TYPE_PIN)) {
                String pinNo = taskQuery.securityNo
                result = readTaskFromARMS(QUERY_STR_PIN, pinNo, taskQuery.companyId, taskQuery.createdDateFrom, taskQuery.createdDateTo)
            } else if (taskQuery.securityType.equals(SECURITY_TYPE_REF_NO)) {
                String refNo = taskQuery.securityNo
                result = readTaskFromARMS(QUERY_STR_TRANSACTION_REF_NO, refNo, taskQuery.companyId, taskQuery.createdDateFrom, taskQuery.createdDateTo)
            }

            if (result == null) {
                taskDetailsMap.message = TASK_NOT_FOUND_MESSAGE
                return taskDetailsMap
            }

            buildTaskDetailsMap(taskDetailsMap, result)

            taskDetailsMap.success = true
            taskDetailsMap.message = Tools.EMPTY_SPACE

            return taskDetailsMap
        } catch (Exception e) {
            log.error(e.getMessage())
            taskDetailsMap.message = 'Failed to get task details from ARMS. Reason: ' + e.getMessage()
            return taskDetailsMap
        }
    }

    /**
     * Get taskInfo properties from ARMS DB
     * @param search_type
     * @param search_keyword
     * @param companyId
     * @param createdDateFrom
     * @param createdDateTo
     * @return task
     */
    private GroovyRowResult readTaskFromARMS(String search_type, String search_keyword, Long companyId, Date createdDateFrom, Date createdDateTo) {
        String query = """
            SELECT e.name exchange_house_name,c.name country_name, t.transaction_ref_no,
            t.pin_no,t.value_date,t.amount,t.current_status,t.status_updated,
            b.name || ', ' || br.name || ', ' || d.name mapping_bank_info,
            t.sender_name, t.sender_mobile, t.beneficiary_name, t.beneficiary_phone,
            t.identity_type, t.identity_no, pm.name payment_method, list.name task_list_name,
            t.outlet_name, t.outlet_branch_name, t.outlet_district_name, t.outlet_thana_name,
            t.account_number
            FROM task_info t
            LEFT JOIN exchange_house e ON t.exchange_house_id=e.id
            LEFT JOIN country c ON t.country_id = c.id
            LEFT JOIN bank b ON t.mapping_bank_id=b.id
            LEFT JOIN bank_branch br ON t.mapping_branch_id=br.id
            LEFT JOIN district d ON t.mapping_district_id=d.id
            LEFT JOIN task_list list ON t.task_list_id=list.id
            LEFT JOIN payment_method pm ON pm.id = t.payment_method
            WHERE t.${search_type} = :search_keyword
            AND t.exchange_house_id=${companyId}
            AND t.created_on BETWEEN '${DateUtility.getDBDateFormat(createdDateFrom)}' AND '${DateUtility.getDBDateFormat(createdDateTo)}'
        """
        Map queryParams = [search_keyword: search_keyword.toUpperCase()]

        Sql sql = new Sql(dataSource_arms)
        log.debug(query)
        List<GroovyRowResult> lstTaskInfo = sql.rows(query, queryParams)
        if (lstTaskInfo.size() > 0) {
            return lstTaskInfo[0]
        }
        return null
    }

    /**
     *  Build taskDetailsMap
     * @param taskDetailsMap
     * @param result
     */
    private void buildTaskDetailsMap(TaskDetailsMap taskDetailsMap, GroovyRowResult result) {
        String isPaid = null
        String paidOn = Tools.NOT_APPLICABLE
        // Waiting for reconciliation status = 60
        if (result.current_status >= ARMS_WAITING_FOR_RECONCILIATION_TASK_STATUS) {
            isPaid = IS_PAID
            paidOn = DateUtility.getDateTimeFormatAsString(result.status_updated)
        } else if (result.current_status == ARMS_CANCELLED_TASK_STATUS) {
            isPaid = IS_CANCELLED
            paidOn = DateUtility.getDateTimeFormatAsString(result.status_updated)
        } else {
            isPaid = IS_UNPAID
        }

        String pinNo = Tools.NOT_APPLICABLE
        String taskListName = Tools.NOT_APPLICABLE
        if (result.pin_no && (result.pin_no.toString().length() > 0)) {                         // Cash Collection
            pinNo = result.pin_no
        } else if (result.task_list_name && (result.task_list_name.toString().length() > 0)) {  // Bank Deposit
            taskListName = result.task_list_name
        }

        String mappingBankInfo = Tools.NOT_APPLICABLE
        if (result.mapping_bank_info) {
            if (result.current_status >= ARMS_DECISION_TAKEN_TASK_STATUS) {
                mappingBankInfo = result.mapping_bank_info
            }
        }

        String fullOutletName = Tools.EMPTY_SPACE
        if (result.outlet_name) {
            fullOutletName = result.outlet_name + Tools.EMPTY_SPACE_COMA + result.outlet_branch_name + Tools.EMPTY_SPACE_COMA + result.outlet_district_name
        } else {
            fullOutletName = result.outlet_district_name + Tools.EMPTY_SPACE_COMA + result.outlet_thana_name
        }

        taskDetailsMap.isAccessible = true
        taskDetailsMap.exchangeHouse = result.exchange_house_name
        taskDetailsMap.country = result.country_name
        taskDetailsMap.transactionRefNum = result.transaction_ref_no
        taskDetailsMap.pinNo = pinNo
        taskDetailsMap.valueDate = DateUtility.getDateFormatAsString(result.value_date)
        taskDetailsMap.amount = result.amount
        taskDetailsMap.listName = taskListName
        taskDetailsMap.isPaid = isPaid
        taskDetailsMap.paidOn = paidOn
        taskDetailsMap.mappingBankInfo = mappingBankInfo
        taskDetailsMap.senderName = result.sender_name
        taskDetailsMap.senderMobile = result.sender_mobile
        taskDetailsMap.beneficiaryName = result.beneficiary_name
        taskDetailsMap.beneficiaryPhone = result.beneficiary_phone
        taskDetailsMap.identityType = result.identity_type
        taskDetailsMap.identityNo = result.identity_no

        taskDetailsMap.paymentMethodName = result.payment_method
        taskDetailsMap.accountNumber = result.account_number
        taskDetailsMap.beneficiaryBankInfo = fullOutletName
    }

    /**
     *  Get task details from SFSL
     * @param taskQuery
     * @return taskDetailsMap
     */
    private TaskDetailsMap getTaskDetailsFromSFSL(TaskQuery taskQuery) {
        TaskDetailsMap taskDetailsMap = new TaskDetailsMap()
        taskDetailsMap.success = false
        try {
            GroovyRowResult result = null
            if (taskQuery.securityType.equals(SECURITY_TYPE_PIN)) {
                String pinNo = taskQuery.securityNo
                result = readTaskFromSFSL(QUERY_STR_PIN, pinNo, taskQuery.companyId, taskQuery.createdDateFrom, taskQuery.createdDateTo)
            } else if (taskQuery.securityType.equals(SECURITY_TYPE_REF_NO)) {
                String refNo = taskQuery.securityNo
                result = readTaskFromSFSL(QUERY_STR_REF_NO, refNo, taskQuery.companyId, taskQuery.createdDateFrom, taskQuery.createdDateTo)
            }

            if (result == null) {
                taskDetailsMap.message = TASK_NOT_FOUND_MESSAGE
                return taskDetailsMap
            }

            buildTaskDetailsMapForSFSL(taskDetailsMap, result)

            taskDetailsMap.success = true
            taskDetailsMap.message = Tools.EMPTY_SPACE

            return taskDetailsMap
        } catch (Exception e) {
            log.error(e.getMessage())
            taskDetailsMap.message = 'Failed to get task details. Reason: ' + e.getMessage()
            return taskDetailsMap
        }
    }

    /**
     * Get taskInfo properties from SFSL DB
     * @param search_type
     * @param search_keyword
     * @param companyId
     * @param createdDateFrom
     * @param createdDateTo
     * @return task
     */
    private GroovyRowResult readTaskFromSFSL(String search_type, String search_keyword, Long companyId, Date createdDateFrom, Date createdDateTo) {

        String strFromDate = DateUtility.getFromDateWithSecond(createdDateFrom)
        String strToDate = DateUtility.getToDateWithSecond(createdDateTo)
        String query = """SELECT t.id, t.company_id, t.ref_no,
                    t.pin_no,t.created_on,t.amount_in_foreign_currency,t.current_status,
                    t.outlet_bank_id, t.outlet_district_id, t.outlet_branch_id,
                    (cust.name || ' ' || COALESCE(cust.surname,'')) AS customer_name, cust.phone AS customer_phone,
                    (ben.first_name || COALESCE(' ' || ben.middle_name,'') || COALESCE(' ' || ben.last_name,'')) AS beneficiary_name,
                    ben.phone As beneficiary_phone, ben.photo_id_type, ben.photo_id_no,
                    t.payment_method,
                    ben.bank AS beneficiary_bank, ben.bank_branch AS beneficiary_branch, ben.district AS beneficiary_district,
                    ben.thana AS beneficiary_thana, ben.account_no
            FROM exh_task t
            LEFT JOIN exh_customer cust ON cust.id=t.customer_id
            LEFT JOIN exh_beneficiary ben ON ben.id=t.beneficiary_id
            WHERE t.${search_type} = :search_keyword
            AND t.company_id=${companyId}
            AND t.created_on BETWEEN '${strFromDate}' AND '${strToDate}'
        """
        Map queryParams = [search_keyword: search_keyword.toUpperCase()]

        List<GroovyRowResult> lstTask = executeSelectSql(query, queryParams)
        if (lstTask.size() > 0) {
            return lstTask[0]
        }
        return null
    }

    /**
     *
     * @param taskDetailsMap
     * @param result
     */
    private void buildTaskDetailsMapForSFSL(TaskDetailsMap taskDetailsMap, GroovyRowResult result) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String isPaid = IS_UNPAID      //default value
        String paidOn = Tools.NOT_APPLICABLE   //default value
        // Waiting for reconciliation status = 60
        if (result.current_status == exhResolvedByOtherBankSysEntityObject.id) {
            isPaid = IS_PAID

            ExhTaskTrace taskTrace = readByTaskId(result.id)
            paidOn = DateUtility.getDateTimeFormatAsString(taskTrace.actionDate)
        }
        long paymentMethodId = Long.parseLong(result.payment_method.toString())
        String paymentMethodName = exhPaymentMethodCacheUtility.read(paymentMethodId).key

        String pinNo = Tools.NOT_APPLICABLE
        String taskListName = Tools.NOT_APPLICABLE
        if (paymentMethodId == exhPaymentMethodCashObj.id) {
            pinNo = result.pin_no
        }

        String fullOutletName = Tools.NOT_APPLICABLE

        Bank outletBank = (Bank) bankCacheUtility.read(result.outlet_bank_id)
        District outletDistrict = (District) districtCacheUtility.read(result.outlet_district_id)
        BankBranch outletBankBranch = (BankBranch) bankBranchCacheUtility.read(result.outlet_branch_id)

        Company company = (Company) companyCacheUtility.read(result.company_id)
        Country country = (Country) countryCacheUtility.read(company.countryId)

        if (paymentMethodId == exhPaymentMethodObj.id) {
            fullOutletName = result.beneficiary_bank + Tools.EMPTY_SPACE_COMA + result.beneficiary_branch + Tools.EMPTY_SPACE_COMA + result.beneficiary_district
        }

        String mappingBankInfo = outletBank.name + Tools.EMPTY_SPACE_COMA + outletBankBranch.name + Tools.EMPTY_SPACE_COMA + outletDistrict.name

        taskDetailsMap.isAccessible = true
        taskDetailsMap.exchangeHouse = company.name
        taskDetailsMap.country = country.name
        taskDetailsMap.transactionRefNum = result.ref_no
        taskDetailsMap.pinNo = pinNo
        taskDetailsMap.valueDate = DateUtility.getDateFormatAsString(result.created_on)
        taskDetailsMap.amount = result.amount_in_foreign_currency
        taskDetailsMap.listName = taskListName
        taskDetailsMap.isPaid = isPaid
        taskDetailsMap.paidOn = paidOn
        taskDetailsMap.mappingBankInfo = mappingBankInfo
        taskDetailsMap.senderName = result.customer_name
        taskDetailsMap.senderMobile = result.customer_phone
        taskDetailsMap.beneficiaryName = result.beneficiary_name
        taskDetailsMap.beneficiaryPhone = result.beneficiary_phone
        taskDetailsMap.identityType = result.photo_id_type
        taskDetailsMap.identityNo = result.photo_id_no

        taskDetailsMap.paymentMethodName = paymentMethodName
        taskDetailsMap.accountNumber = result.account_no
        taskDetailsMap.beneficiaryBankInfo = fullOutletName
    }

    /**
     * Build taskDetailsMap
     * @param taskQuery
     * @return taskDetailsMap -updated TaskDetailsMap object
     */
    private TaskDetailsMap getTaskDetailsFromSFSLBeforeSentToBank(TaskQuery taskQuery) {
        TaskDetailsMap taskDetailsMap = new TaskDetailsMap()
        taskDetailsMap.success = false
        try {
            GroovyRowResult result = null
            if (taskQuery.securityType.equals(SECURITY_TYPE_PIN)) {
                String pinNo = taskQuery.securityNo
                result = readTaskFromSFSL(QUERY_STR_PIN, pinNo, taskQuery.companyId, taskQuery.createdDateFrom, taskQuery.createdDateTo)
            } else if (taskQuery.securityType.equals(SECURITY_TYPE_REF_NO)) {
                String refNo = taskQuery.securityNo
                result = readTaskFromSFSL(QUERY_STR_REF_NO, refNo, taskQuery.companyId, taskQuery.createdDateFrom, taskQuery.createdDateTo)
            }

            buildTaskDetailsMapForSFSLBeforeSentToBank(taskDetailsMap, result)        // build TaskDetailsMap

            taskDetailsMap.success = true
            taskDetailsMap.message = Tools.EMPTY_SPACE

            return taskDetailsMap
        } catch (Exception e) {
            log.error(e.getMessage())
            taskDetailsMap.message = 'Failed to get task details. Reason: ' + e.getMessage()
            return taskDetailsMap
        }
    }

    /**
     * Build taskDetailsMap
     * @param taskDetailsMap -TaskDetailsMap object
     * @param result -GroovyRowResult object
     */
    private void buildTaskDetailsMapForSFSLBeforeSentToBank(TaskDetailsMap taskDetailsMap, GroovyRowResult result) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        long paymentMethodId = Long.parseLong(result.payment_method.toString())
        String paymentMethodName = exhPaymentMethodCacheUtility.read(paymentMethodId).key

        String pinNo = Tools.NOT_APPLICABLE
        if (paymentMethodId == exhPaymentMethodCashObj.id) {
            pinNo = result.pin_no
        }

        String fullOutletName = Tools.NOT_APPLICABLE
        Company company = (Company) companyCacheUtility.read(result.company_id)
        Country country = (Country) countryCacheUtility.read(company.countryId)

        if (paymentMethodId == exhPaymentMethodObj.id) {
            fullOutletName = result.beneficiary_bank + Tools.EMPTY_SPACE_COMA + result.beneficiary_branch + Tools.EMPTY_SPACE_COMA + result.beneficiary_district
        }

        taskDetailsMap.isAccessible = true
        taskDetailsMap.exchangeHouse = company.name
        taskDetailsMap.country = country.name
        taskDetailsMap.transactionRefNum = result.ref_no
        taskDetailsMap.pinNo = pinNo
        taskDetailsMap.valueDate = DateUtility.getDateFormatAsString(result.created_on)
        taskDetailsMap.amount = result.amount_in_foreign_currency
        taskDetailsMap.listName = Tools.NOT_APPLICABLE
        taskDetailsMap.isPaid = IS_UNPAID
        taskDetailsMap.paidOn = Tools.NOT_APPLICABLE
        taskDetailsMap.mappingBankInfo = Tools.NOT_APPLICABLE
        taskDetailsMap.senderName = result.customer_name
        taskDetailsMap.senderMobile = result.customer_phone
        taskDetailsMap.beneficiaryName = result.beneficiary_name
        taskDetailsMap.beneficiaryPhone = result.beneficiary_phone
        taskDetailsMap.identityType = result.photo_id_type
        taskDetailsMap.identityNo = result.photo_id_no

        taskDetailsMap.paymentMethodName = paymentMethodName
        taskDetailsMap.accountNumber = result.account_no
        taskDetailsMap.beneficiaryBankInfo = fullOutletName
    }

    /**
     * Get current task details info by task id
     * @param taskId
     * @return taskTrace
     */
    private ExhTaskTrace readByTaskId(Long taskId) {
        ExhTaskTrace taskTrace = ExhTaskTrace.findByTaskId(taskId, [readOnly: true])
        return taskTrace
    }
}