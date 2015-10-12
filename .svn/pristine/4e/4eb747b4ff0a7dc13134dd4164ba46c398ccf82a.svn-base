package com.athena.mis.exchangehouse.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/*Renders html of 'show for showExhTaskForCashier*/

class CustomerSummaryForTaskTagLibActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String LOCAL_CURRENCY_SYMBOL = "localCurrencySymbol"
    private static final String SUM_OF_THREE_MOTHS = "sumOfThreeMoths"
    private static final String SUM_OF_SIX_MONTHS = "sumOfSixMonths"
    private static final String SUM_OF_ONE_YEAR = "sumOfOneYear"
    private static final String NOTE = "note"
    private static final String CREATED_BY = "createdBy"

    ExhCustomerService exhCustomerService

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for pre operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Render html to show following information of customer
     * 1. transaction total of last 3 months
     * 2. transaction total of last 6 months
     * 3. transaction total of last 12 months
     * 4. last note of customer
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - html string for 'showExhTaskForCashier'
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map result = initResultMap()
        try {
            Map attr = (Map) parameters
            if (!attr.customerId) {
                String html = buildTemplate(result)
                return html
            }
            long customerId = Long.parseLong(attr.customerId.toString())
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
            Map noteDetails = getCustomerNote(customer)
            if (noteDetails) {
                result.put(NOTE, noteDetails.note)
                result.put(CREATED_BY, noteDetails.strCreatedBy)
            }
            String html = buildTemplate(result)
            return html
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * Initialize map for customerId = 0
     * @return map with default key 7 values
     */
    private Map initResultMap() {
        Map result = new LinkedHashMap()
        result.put(LOCAL_CURRENCY_SYMBOL, currencyCacheUtility.getLocalCurrency())
        result.put(SUM_OF_THREE_MOTHS, new Double(0.0d))
        result.put(SUM_OF_SIX_MONTHS, new Double(0.0d))
        result.put(SUM_OF_ONE_YEAR, new Double(0.0d))
        result.put(NOTE, Tools.NOT_APPLICABLE)
        result.put(CREATED_BY, Tools.EMPTY_SPACE)
        return result
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
     * do nothing for success operation
     */
    Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for failure operation
     */
    Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Build & Get Customer Transaction Summary
     * @param result -a map which contains transaction amount of three, six & one year & currency symbol
     * @return template - a template kind of html
     */
    private String buildTemplate(Map result) {
        double sumOfThreeMoths = ((Double) result.get(SUM_OF_THREE_MOTHS)).doubleValue()
        double sumOfSixMonths = ((Double) result.get(SUM_OF_SIX_MONTHS)).doubleValue()
        double sumOfOneYear = ((Double) result.get(SUM_OF_ONE_YEAR)).doubleValue()
        String localCurrencySymbol = result.get(LOCAL_CURRENCY_SYMBOL)
        String strCreatedBy = result.get(CREATED_BY)
        String note = result.get(NOTE)
        int cutDownLength = 110
        String shortNote = Tools.makeDetailsShort(note, cutDownLength)
        String templateReadMore = Tools.EMPTY_SPACE

        if ((!note.equals(Tools.NOT_APPLICABLE)) && (note.length() > cutDownLength)) {
            templateReadMore = """
                           <a id="popNote" href="#" data-container="body" style="white-space: nowrap"
                                    data-toggle="popover" data-placement="bottom"  data-content="${note}">&nbsp;read more..</a>
                            """
        }

        String template = """
             <div class="panel panel-primary">
                <div class="panel-heading">
                <div class="panel-title">Customer Transaction Summary</div>
                </div>
                <div class="panel-body" style="padding: 1px">
                    <table class="table table-condensed table-striped" style="margin:0">
                        <thead>
                            <th>Duration</th>
                            <th>Amount (${localCurrencySymbol})</th>
                        </thead>
                        <tbody>
                            <tr>
                                <td>3 Months</td>
                                <td id='lblSum3Months'>${Tools.formatAmountWithoutCurrency(sumOfThreeMoths)}</td>
                            </tr>
                            <tr>
                                <td>6 Months</td>
                                <td id='lblSum6Months'>${Tools.formatAmountWithoutCurrency(sumOfSixMonths)}</td>
                            </tr>
                            <tr>
                                <td>12 Months</td>
                                <td id='lblSum12Months'>${Tools.formatAmountWithoutCurrency(sumOfOneYear)}</td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                <strong>${strCreatedBy}</strong>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                <span style="color:#981100">${shortNote}</span>${templateReadMore}
                                </td>
                            </tr>

                        </tbody>
                    </table>
                </div>
             </div>
        """
        return template
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
        String queryStr = """
            SELECT note, created_by, created_on
            FROM entity_note AS note
            WHERE company_id = ${customer.companyId}
            AND entity_id = ${customer.id} AND entity_type_id = ${exhNoteEntityTypeObject.id}
            ORDER BY id DESC LIMIT 1
    """
        List<GroovyRowResult> result = executeSelectSql(queryStr)
        if (result.size() > 0) {
            AppUser user = appUserCacheUtility.read(result[0].created_by)
            String createdOn = DateUtility.getLongDateForUI(result[0].created_on)
            String strCreatedBy = "Note By ${user.username} On ${createdOn}"
            return [note: result[0].note, strCreatedBy: strCreatedBy]
        }
        return null
    }
}
