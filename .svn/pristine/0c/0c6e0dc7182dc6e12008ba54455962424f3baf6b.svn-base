package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.*
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhSelectTaskActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    private static final String LST_BANK_BRANCHES = "lstBankBranches"
    private static final String LST_BANK = "lstBank"
    private static final String FAILED_TO_SELECT_TASK = "Failed to select task"
    private static final String ERROR_TASK_NOT_FOUND = "Task not found"
    private static final String TASK = "task"
    private static final String BENEFICIARY_INSTANCE = "beneficiaryInstance"
    private static final String SUM_OF_THREE_MOTHS = "sumOfThreeMoths"
    private static final String SUM_OF_SIX_MONTHS = "sumOfSixMonths"
    private static final String SUM_OF_ONE_YEAR = "sumOfOneYear"
    private static final String NOTE = "note"
    private static final String NOTE_CREATED_BY = "strNoteCreatedBy"

    ExhTaskService exhTaskService
    ExhBeneficiaryService exhBeneficiaryService
    ExhCustomerService exhCustomerService
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get task object by id  & check required parameters
     * Also retrieve customer transaction summary for taglib (cashier only)
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long id = Long.parseLong(parameterMap.id)
            ExhTask task = exhTaskService.read(id)
            if (!task) {
                result.put(Tools.MESSAGE, ERROR_TASK_NOT_FOUND)
                return result
            }
            result.put(TASK, task)
            setCustomerTagLibProperties(task.customerId, result)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SELECT_TASK)
            return result
        }
    }
/**
 * @param customerId -Customer.id
 * @param result - pre declared Map, which contains customer transaction records & last note
 */

    private void setCustomerTagLibProperties(long customerId, Map result) {
        ExhCustomer customer = exhCustomerService.read(customerId)

        Date currDate = DateUtility.setLastHour(new Date())
        Date dateOfLastThreeMonths = DateUtility.setFirstHour(currDate - 90)
        Date dateOfLastSxiMonths = DateUtility.setFirstHour(currDate - 180)
        Date dateOfLastOneYear = DateUtility.setFirstHour(currDate - 365)
        String strStatusIds = buildValidStatusForSql()
        double sumOfThreeMoths = getSumOfThreeMonths(customer, currDate, dateOfLastThreeMonths, strStatusIds)

        double sumOfSixMonths = sumOfThreeMoths             // default value
        if (customer.createdOn <= dateOfLastThreeMonths) {
            sumOfSixMonths = getSumOfSixMonths(customer, currDate, dateOfLastSxiMonths, strStatusIds)
        }

        double sumOfOneYear = sumOfSixMonths            // default value
        if (customer.createdOn <= dateOfLastSxiMonths) {
            sumOfOneYear = getSumOfOneYear(customer, currDate, dateOfLastOneYear, strStatusIds)
        }
        result.put(SUM_OF_THREE_MOTHS, sumOfThreeMoths)
        result.put(SUM_OF_SIX_MONTHS, sumOfSixMonths)
        result.put(SUM_OF_ONE_YEAR, sumOfOneYear)
        Map noteMap = getCustomerNote(customer)
        result.put(NOTE, noteMap.note)
        result.put(NOTE_CREATED_BY, noteMap.strCreatedBy)
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with task object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhTask task = (ExhTask) executeResult.get(TASK)
            ExhBeneficiary beneficiary = exhBeneficiaryService.read(task.beneficiaryId)
            List<BankBranch> lstBankBranches = null
            List<GroovyRowResult> lstBank = null

            if (task.outletDistrictId && task.outletBranchId) {
                lstBankBranches = bankBranchCacheUtility.listByBankAndDistrict(task.outletBankId, task.outletDistrictId)
                lstBank = listExceptSystemBankByDistrict(task.outletDistrictId)
            }
            result.put(Tools.ENTITY, task)
            result.put(Tools.VERSION, task.version)
            result.put(BENEFICIARY_INSTANCE, beneficiary)
            result.put(LST_BANK, lstBank)
            result.put(LST_BANK_BRANCHES, lstBankBranches)
            result.put(Tools.IS_ERROR, Boolean.FALSE)

            result.put(SUM_OF_THREE_MOTHS, executeResult.get(SUM_OF_THREE_MOTHS))
            result.put(SUM_OF_SIX_MONTHS, executeResult.get(SUM_OF_SIX_MONTHS))
            result.put(SUM_OF_ONE_YEAR, executeResult.get(SUM_OF_ONE_YEAR))
            result.put(NOTE, executeResult.get(NOTE))
            result.put(NOTE_CREATED_BY, executeResult.get(NOTE_CREATED_BY))

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SELECT_TASK)
            return result
        }
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
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILED_TO_SELECT_TASK)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SELECT_TASK)
            return result
        }
    }

    /**
     * Get bank(s) except system bank by district
     * @param districtId
     * @return lstBanks
     */
    private List<GroovyRowResult> listExceptSystemBankByDistrict(Long districtId) {
        Bank systemBank = bankCacheUtility.getSystemBank()
        String queryStr = """ SELECT DISTINCT bank.id, bank.name
        FROM  bank
        INNER JOIN bank_branch bank_branch ON bank_branch.bank_id = bank.id
        INNER JOIN district district ON bank_branch.district_id = district.id
        WHERE district.id = ${districtId} AND bank.id <> ${systemBank.id}
        ORDER BY bank.name
        """
        List<GroovyRowResult> lstBanks = executeSelectSql(queryStr);
        return lstBanks
    }

    /**
     * Make comma separated string of TaskStatus IDs for query
     * @return comma separated string
     */
    private String buildValidStatusForSql() {
        List<Long> lstStatus = []
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        lstStatus << exhNewTaskSysEntityObject.id
        lstStatus << exhSentToBankSysEntityObject.id
        lstStatus << exhSentToOtherBankSysEntityObject.id
        lstStatus << exhResolvedByOtherBankSysEntityObject.id
        String strIds = Tools.buildCommaSeparatedStringOfIds(lstStatus)
        return strIds
    }

    /**
     * Get total transaction amount of customer within three month
     * @param customer
     * @param dateOfLastOneYear - an object of Date
     * @return sumOfOneYear
     */
    private double getSumOfThreeMonths(ExhCustomer customer, Date currDate, Date dateOfLastThreeMonths, String strStatusIds) {
        String queryStr = """
            SELECT COALESCE(sum(task.amount_in_local_currency),0) as total_amount
            FROM exh_task task
            WHERE
                task.company_id=${customer.companyId}
                AND task.customer_id = ${customer.id}
                AND task.current_status IN(${strStatusIds})
                AND task.created_on BETWEEN '${DateUtility.getDBDateFormatWithSecond(dateOfLastThreeMonths)}' AND '${
            DateUtility.getDBDateFormatWithSecond(currDate)
        }'
    """

        double sumOfThreeMoths = 0.0d
        List<GroovyRowResult> result = executeSelectSql(queryStr)
        if (result.size() > 0) {
            sumOfThreeMoths = result[0].total_amount
        }
        return sumOfThreeMoths
    }

    /**
     * Get total transaction amount of customer within six month
     * @param customer
     * @param dateOfLastOneYear - an object of Date
     * @return sumOfOneYear
     */
    private double getSumOfSixMonths(ExhCustomer customer, Date currDate, Date dateOfLastSixMonths, String strStatusIds) {
        double sumOfSixMonths = 0.0d
        String queryStr = """
            SELECT COALESCE(sum(task.amount_in_local_currency),0) as total_amount
            FROM exh_task task
            WHERE
                task.company_id=${customer.companyId}
                AND task.customer_id = ${customer.id}
                AND task.current_status IN(${strStatusIds})
                AND task.created_on BETWEEN '${DateUtility.getDBDateFormatWithSecond(dateOfLastSixMonths)}' AND '${
            DateUtility.getDBDateFormatWithSecond(currDate)
        }'
    """
        List<GroovyRowResult> result = executeSelectSql(queryStr)
        if (result.size() > 0) {
            sumOfSixMonths = result[0].total_amount
        }
        return sumOfSixMonths
    }

    /**
     * Get total transaction amount of customer within one year
     * @param customer
     * @param dateOfLastOneYear - an object of Date
     * @return sumOfOneYear
     */
    private double getSumOfOneYear(ExhCustomer customer, Date currDate, Date dateOfLastOneYear, String strStatusIds) {
        double sumOfOneYear = 0.0d
        String queryStr = """
            SELECT COALESCE(sum(task.amount_in_local_currency),0) as total_amount
            FROM exh_task task
            WHERE
                task.company_id=${customer.companyId}
                AND task.customer_id = ${customer.id}
                AND task.current_status IN(${strStatusIds})
                AND task.created_on BETWEEN '${DateUtility.getDBDateFormatWithSecond(dateOfLastOneYear)}' AND '${
            DateUtility.getDBDateFormatWithSecond(currDate)
        }'
    """
        List<GroovyRowResult> result = executeSelectSql(queryStr)
        if (result.size() > 0) {
            sumOfOneYear = result[0].total_amount
        }
        return sumOfOneYear
    }

    /**
     * Get customer note
     * @param customer -an object of ExhCustomer
     * @return note
     */
    private Map getCustomerNote(ExhCustomer customer) {
        SystemEntity exhNoteEntityTypeObject = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_CUSTOMER, customer.companyId)
        String note = Tools.NOT_APPLICABLE
        String strCreatedBy = Tools.EMPTY_SPACE
        String queryStr = """
            SELECT note,created_by,created_on
            FROM entity_note AS note
            WHERE company_id = ${customer.companyId}
            AND entity_id = ${customer.id} AND entity_type_id = ${exhNoteEntityTypeObject.id}
            ORDER BY id DESC LIMIT 1
    """
        List<GroovyRowResult> result = executeSelectSql(queryStr)
        if (result.size() > 0) {
            note = result[0].note
            AppUser user = appUserCacheUtility.read(result[0].created_by)
            String createdOn = DateUtility.getLongDateForUI(result[0].created_on)
            strCreatedBy = "Created By ${user.username} On ${createdOn}"
        }
        return [note: note, strCreatedBy: strCreatedBy]
    }

}
