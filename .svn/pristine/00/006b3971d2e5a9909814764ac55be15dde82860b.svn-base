package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
/**
 *  Search chart of account for voucher
 *  For details go through Use-Case doc named 'SearchChartOfAccountForVoucherActionService'
 */
class SearchChartOfAccountForVoucherActionService extends BaseService implements ActionIntf {
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load chart of account List"
    private static final String ACC_CHART_OF_ACCOUNT_LIST = "accChartOfAccountList"
    private static final String COUNT = "count"
    private static final int RESULT_PER_PAGE = 20
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get chart of account list for voucher through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return - chart of account list for buildSuccessResultForUI
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.rp) {
                params.rp = RESULT_PER_PAGE
            }
            initSearch(params)
            Map searchResult = accChartOfAccountCacheUtility.searchByCodeAndDescription(query, this)
            List<AccChartOfAccount> accChartOfAccountList = searchResult.list
            int count = searchResult.count
            result.put(ACC_CHART_OF_ACCOUNT_LIST, accChartOfAccountList)
            result.put(COUNT, count)
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
     * Wrap chart of account list for voucher
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<AccChartOfAccount> accChartOfAccountList = (List<AccChartOfAccount>) executeResult.get(ACC_CHART_OF_ACCOUNT_LIST)
            int count = (int) executeResult.get(COUNT)
            List accChartOfAccountListWrap = wrapChartOfAccount(accChartOfAccountList, start)
            result = [page: pageNumber, total: count, rows: accChartOfAccountListWrap]
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
     * Wrap list of coa in grid entity for voucher
     * @param accChartOfAccountList -list of coa object(s) for voucher
     * @param start -starting index of the page
     * @return -list of wrapped coa
     */
    private List wrapChartOfAccount(List<AccChartOfAccount> accChartOfAccountList, int start) {
        List accChartOfAccounts = [] as List
        int counter = start + 1
        SystemEntity accSource
        for (int i = 0; i < accChartOfAccountList.size(); i++) {
            AccChartOfAccount accChartOfAccount = accChartOfAccountList[i]
            accSource = (SystemEntity) accSourceCacheUtility.read(accChartOfAccount.accSourceId)
            GridEntity obj = new GridEntity()
            obj.id = accChartOfAccount.id
            obj.cell = [
                    accChartOfAccount.id,
                    accChartOfAccount.code,
                    accChartOfAccount.description,
                    accSource.id
            ]
            accChartOfAccounts << obj
            counter++
        }
        return accChartOfAccounts
    }

}
