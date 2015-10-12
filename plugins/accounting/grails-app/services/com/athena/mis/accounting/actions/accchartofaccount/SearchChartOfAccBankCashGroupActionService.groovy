package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
/**
 *  Search chart of account (bank/cash group)
 *  For details go through Use-Case doc named 'SearchChartOfAccBankCashGroupActionService'
 */
class SearchChartOfAccBankCashGroupActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility

    private static final String GRID_OBJ_COA = "gridObjCoa"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load chart of account List"
    private static final String COUNT = "count"
    private static final int RESULT_PER_PAGE = 20
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap chart of account list for grid
     * @param parameters -map returned from execute method
     * @param obj -N/A
     * @return -a map containing all objects necessary for grid view
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.rp) {
                params.rp = RESULT_PER_PAGE
            }
            initSearch(params)       // initialize parameters for flexGrid Search
            Map searchResult = accChartOfAccountCacheUtility.searchForBankCashGroup(query, this)
            int count = searchResult.count
            List<AccChartOfAccount> lstCOA = searchResult.list

            List gridListCoa = wrapChartOfAccount(lstCOA, start)    // wrap coa object
            Map gridObjChartOfAcc = [page: pageNumber, total: count, rows: gridListCoa]

            result.put(COUNT, count)
            result.put(GRID_OBJ_COA, gridObjChartOfAcc)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     *
     * @param obj - received from execute method
     * @return  - a map containing coa object
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            Map gridObjCoa = (Map) executeResult.get(GRID_OBJ_COA)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ_COA, gridObjCoa)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Wrap list of coa in grid entity
     * @param accChartOfAccountList -list of coa object(s)
     * @param start -starting index of the page
     * @return -list of wrapped coa of bank/cash group
     */
    private List wrapChartOfAccount(List<AccChartOfAccount> accChartOfAccountList, int start) {
        List accChartOfAccounts = [] as List
        int counter = start + 1
        for (int i = 0; i < accChartOfAccountList.size(); i++) {
            AccChartOfAccount accChartOfAccount = accChartOfAccountList[i]
            GridEntity obj = new GridEntity()
            obj.id = accChartOfAccount.id
            obj.cell = [
                    accChartOfAccount.id,
                    accChartOfAccount.code,
                    accChartOfAccount.description
            ]
            accChartOfAccounts << obj
            counter++
        }
        return accChartOfAccounts
    }
}

