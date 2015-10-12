package com.athena.mis.accounting.actions.accsubaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSubAccountCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get search result list of accSubAccount object(s) to show on grid
 *  For details go through Use-Case doc named 'SearchAccSubAccountActionService'
 */
class SearchAccSubAccountActionService extends BaseService implements ActionIntf {

    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to search sub account grid list"
    private static final String SERVICE_RETURN = "serviceReturn"
    private static final String SUB_ACCOUNT_LIST = "accSubAccountList"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get search result list of accSubAccount object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accSubAccountList and count
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortorder = accSubAccountCacheUtility.SORT_ORDER_DESCENDING
            }
            initSearch(params)
            Map serviceReturn = search()

            result.put(SERVICE_RETURN, serviceReturn)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap accSubAccountObjectList to show on grid
     * @param obj -a map contains accSubAccountObjectList and count
     * @return -wrapped accSubAccountObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            Map serviceReturn = (Map) executeResult.get(SERVICE_RETURN)
            List<GroovyRowResult> accSubAccountList = (List<GroovyRowResult>) serviceReturn.accSubAccountList
            int count = (int) serviceReturn.get(Tools.COUNT)
            List accSubAccount = wrapListInGridEntityList(accSubAccountList, start)
            Map output = [page: pageNumber, total: count, rows: accSubAccount]
            result.put(SUB_ACCOUNT_LIST, output)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrappedAccSubAccountObjectList for grid
     * @param accSubAccountList -list of accSubAccount objects
     * @param start -start index
     * @return -wrappedAccSubAccountObjectList
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> accSubAccountList, int start) {
        List accSubAccounts = [] as List
        int counter = start + 1
        for (int i = 0; i < accSubAccountList.size(); i++) {
            GroovyRowResult accSubAccount = accSubAccountList[i]
            GridEntity obj = new GridEntity()
            obj.id = accSubAccount.id
            obj.cell = [
                    counter,
                    accSubAccount.id,
                    accSubAccount.description,
                    accSubAccount.code,
                    accSubAccount.is_active ? Tools.YES : Tools.NO
            ]
            accSubAccounts << obj
            counter++
        }
        return accSubAccounts
    }

    /**
     * get search result list of accSubAccount object(s) for grid
     * @return -list of groovyRowResult
     */
    private Map search() {

        String queryStr = """
               SELECT acc_sub_account.id, acc_sub_account.description, acc_chart_of_account.code as code, acc_sub_account.is_active
               FROM acc_sub_account
               LEFT JOIN acc_chart_of_account ON acc_chart_of_account.id =acc_sub_account.coa_id
               WHERE acc_sub_account.description ILIKE :description AND
                     acc_sub_account.company_id = :companyId
               ORDER BY ${sortColumn} ${sortOrder} LIMIT ${resultPerPage} OFFSET ${start}
        """
        Map queryParams = [
                companyId : accSessionUtil.appSessionUtil.getCompanyId(),
                description: Tools.PERCENTAGE + query + Tools.PERCENTAGE
        ]
        List<GroovyRowResult> lstAccSubAccount = executeSelectSql(queryStr, queryParams)

        String queryCount = """
                SELECT COUNT(acc_sub_account.id)
                FROM acc_sub_account
               WHERE acc_sub_account.description ILIKE :description AND
                     acc_sub_account.company_id = :companyId
        """
        List<GroovyRowResult> resultCount = executeSelectSql(queryCount, queryParams)
        int count = (int) resultCount[0][0]
        return [accSubAccountList: lstAccSubAccount, count: count]
    }
}
