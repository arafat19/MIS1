package com.athena.mis.accounting.actions.report.acctrialbalance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * Show UI for list of trial balance that populates grid & populate project,date-range drop-down
 * For details go through Use-Case doc named 'ShowForTrialBalanceActionService'
 */
class ShowForTrialBalanceActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MSG = "Fail to load trial balance."
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get all objects related to display grid.
     * @param parameters - serialized parameters received from UI
     * @param obj - N/A
     * @return - a map containing project list, date range & isError msg(True/False)
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            String startDate = null
            String endDate = null


            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.currentFinancialYear
            if (accFinancialYear) {
                startDate = DateUtility.getDateForUI(accFinancialYear.startDate)
                endDate = DateUtility.getDateForUI(accFinancialYear.endDate)
            } else {
                startDate = DateUtility.getDateForUI(new Date())
                endDate = DateUtility.getDateForUI(new Date())
            }
            result.put(FROM_DATE, startDate)
            result.put(TO_DATE, endDate)
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
}
