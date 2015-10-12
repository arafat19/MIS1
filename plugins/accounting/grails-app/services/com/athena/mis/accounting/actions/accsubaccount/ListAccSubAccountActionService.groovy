package com.athena.mis.accounting.actions.accsubaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSubAccountCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get list of accSubAccount object(s) to show on grid
 *  For details go through Use-Case doc named 'ListAccSubAccountActionService'
 */
class ListAccSubAccountActionService extends BaseService implements ActionIntf {

    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load sub account grid list"
    private static final String SUB_ACCOUNT_LIST = "accSubAccountList"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of accSubAccount object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accSubAccountList and count
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortorder = DESCENDING_SORT_ORDER
            }
            initPager(params)
            List accSubAccountList = list()
            int count = accSubAccountCacheUtility.count()
            result.put(SUB_ACCOUNT_LIST, accSubAccountList)
            result.put(Tools.COUNT, count)
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
            List accSubAccountList = (List) executeResult.get(SUB_ACCOUNT_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List wrappedSubAccountList = wrapListInGridEntityList(accSubAccountList, start)
            Map output = [page: pageNumber, total: count, rows: wrappedSubAccountList]
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
     * get list of accSubAccount object(s) for grid
     * @return -list of groovyRowResult
     */
    private List list() {
        String queryStr = """
                SELECT acc_sub_account.id, acc_sub_account.description,acc_chart_of_account.code as code, acc_sub_account.is_active
                   FROM acc_sub_account
                   LEFT JOIN acc_chart_of_account ON acc_chart_of_account.id = acc_sub_account.coa_id
                WHERE acc_sub_account.company_id =:companyId
                ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                resultPerPage: resultPerPage,
                start: start,
                companyId: accSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstAccSubAccount = executeSelectSql(queryStr, queryParams)
        return lstAccSubAccount
    }
}
