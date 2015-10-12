package com.athena.mis.accounting.actions.report.projectwiseexpense

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show UI for list of project wise expense for grid & populate project,group,date-range drop-down
 * For details go through Use-Case doc named 'ShowForProjectWiseExpenseActionService'
 */
class ShowForProjectWiseExpenseActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private static final String FAILURE_MSG = "Fail to load Project wise Budget."
    private static final String PROJECT_ID = "projectId"
    private static final String GRID_OBJ_COA = "gridObjCoa"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get all objects related to display grid.
     * @param parameters - serialized parameters received from UI
     * @param obj - N/A
     * @return - a map containing acc group list, grid object, date range & project ids
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            List<Long> projectIds = []
            long projectId = -1L

            if (params.projectId) {      // request from T.Balance
                projectId = Long.parseLong(params.projectId.toString())
                if (projectId == -1) {     //  search by ALL project
                    projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                    projectIds << new Long(0L)        // let the user see ledger entries where project id = 0
                } else {
                    projectIds << new Long(projectId)
                }
            } else {
                projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                projectIds << new Long(0L)        // let the user see ledger entries where project id = 0
            }

            // Get COA Information in right grid
            Map searchResult = accChartOfAccountCacheUtility.listForBankCashGroup(this)
            List<AccChartOfAccount> lstCOA = searchResult.list
            int count = searchResult.count
            List gridListCoa = wrapChartOfAccount(lstCOA, start)
            Map gridObjChartOfAcc = [page: pageNumber, total: count, rows: gridListCoa]

            result.put(GRID_OBJ_COA, gridObjChartOfAcc)
            result.put(PROJECT_ID, projectId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }
    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
    /**
     *
     * @param accChartOfAccountList
     * @param start
     * @return
     */
    private List wrapChartOfAccount(List<AccChartOfAccount> accChartOfAccountList, int start) {
        List accChartOfAccounts = []
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