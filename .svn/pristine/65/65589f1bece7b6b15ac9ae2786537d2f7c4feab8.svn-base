package com.athena.mis.accounting.actions.accleaseaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get list of accLeaseAccount object(s) to show on grid
 *  For details go through Use-Case doc named 'ListAccLeaseAccountActionService'
 */
class ListAccLeaseAccountActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccSessionUtil accSessionUtil

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load Lease-Account list"
    private static final String ACC_LEASE_ACCOUNT_LIST = "accLeaseAccountList"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of accLeaseAccount object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accLeaseAccountList and count
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            initPager(parameterMap)

            //get list of accLeaseAccount objects
            Map accLeaseAccountReturn = listForLeaseAccount()
            List<GroovyRowResult> accLeaseAccountList = (List<GroovyRowResult>) accLeaseAccountReturn.accLeaseAccountList
            int count = (int) accLeaseAccountReturn.count

            result.put(Tools.COUNT, count)
            result.put(ACC_LEASE_ACCOUNT_LIST, accLeaseAccountList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap accLeaseAccountObject list to show on grid
     * @param obj -a map contains accLeaseAccountObject list and count
     * @return -wrapped accLeaseAccountObject list to show on grid
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj

            List accLeaseAccountList = (List) executeResult.get(ACC_LEASE_ACCOUNT_LIST)
            int count = (int) executeResult.get(Tools.COUNT)

            List wrappedLeaseList = wrapListInGridEntityList(accLeaseAccountList, start)
            Map output = [page: pageNumber, total: count, rows: wrappedLeaseList]

            result.put(ACC_LEASE_ACCOUNT_LIST, output)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrappedLeaseAccountObject list for grid
     * @param lstAccLeaseAccount -list of GroovyRowResult
     * @param start -start index
     * @return -wrappedLeaseAccountObject list
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> lstAccLeaseAccount, int start) {
        List accLeaseAccountList = []
        int counter = start + 1
        for (int i = 0; i < lstAccLeaseAccount.size(); i++) {
            GridEntity obj = new GridEntity()
            GroovyRowResult singleRow = lstAccLeaseAccount[i]
            obj.id = singleRow.id
            obj.cell = [
                    counter,
                    singleRow.id,
                    singleRow.institution,
                    singleRow.item_name,
                    Tools.formatAmountWithoutCurrency(singleRow.amount),
                    Tools.formatAmountWithoutCurrency(singleRow.interest_rate),
                    singleRow.no_of_installment,
                    Tools.formatAmountWithoutCurrency(singleRow.installment_volume),
                    DateUtility.getLongDateForUI(singleRow.start_date),
                    DateUtility.getLongDateForUI(singleRow.end_date)
            ]
            accLeaseAccountList << obj
            counter++
        }
        return accLeaseAccountList
    }

    /**
     * get list of accLeaseAccount object
     * @return -a map contains accLeaseAccount objects and count
     */
    private Map listForLeaseAccount() {
        String queryStr = """
        SELECT ala.id id, ala.institution institution, ala.amount amount, ala.interest_rate interest_rate,
               ala.no_of_installment no_of_installment, ala.installment_volume installment_volume,
               ala.start_date start_date, ala.end_date end_date, it.name item_name
        FROM acc_lease_account ala
        LEFT JOIN item it ON it.id = ala.item_id
        WHERE ala.company_id=:companyId
        ORDER BY ala.institution
        LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
        SELECT COUNT(ala.id) count
        FROM acc_lease_account ala
        WHERE ala.company_id=:companyId
        """

        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getCompanyId(),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)
        int total = countResult[0].count
        return [accLeaseAccountList: result, count: total]
    }
}
