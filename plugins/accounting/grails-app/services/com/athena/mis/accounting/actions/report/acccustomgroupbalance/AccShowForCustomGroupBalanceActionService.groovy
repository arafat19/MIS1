package com.athena.mis.accounting.actions.report.acccustomgroupbalance

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
 *  Show UI for custom group balance and list of custom group balance for grid
 *  For details go through Use-Case doc named 'AccShowForCustomGroupBalanceActionService'
 */
class AccShowForCustomGroupBalanceActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MSG = "Fail to load Custom Group Balance."
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
     * Get start date and end date and project list for drop down
     * @param parameters - N/A
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
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
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for build failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}