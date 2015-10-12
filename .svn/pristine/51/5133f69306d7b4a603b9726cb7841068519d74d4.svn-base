package com.athena.mis.accounting.actions.report.supplierwisepayable

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
 * Show UI for list of supplier wise payable for grid & populate project drop-down &
 * set from date and to date according to financial year
 * For details go through Use-Case doc named 'AccShowForSupplierWisePayableActionService'
 */
class AccShowForSupplierWisePayableActionService extends BaseService implements ActionIntf {

    private static final String FAILURE_MSG = "Fail to load supplier wise payable report."
    private static final String PROJECT_LIST = "projectList"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())
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
     * Get project list & current financial year
     * @param parameters -N/A
     * @param obj -N/A
     * @return - a map containing project list and date range depending on financial year
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)      // default value

            String fromDate = null
            String toDate = null

            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.currentFinancialYear     // get current financial year
            if (accFinancialYear) {
                fromDate = DateUtility.getDateForUI(accFinancialYear.startDate)
                toDate = DateUtility.getDateForUI(accFinancialYear.endDate)
            } else {
                fromDate = DateUtility.getDateForUI(new Date())
                toDate = DateUtility.getDateForUI(new Date())
            }
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj      // cast object received from previous method
            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.message) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
}
