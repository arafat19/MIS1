package com.athena.mis.accounting.actions.accfinancialyear

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get list of financialYear object(s) to show on grid
 *  For details go through Use-Case doc named 'ListAccFinancialYearActionService'
 */
class ListAccFinancialYearActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Financial-Year list"
    private static final String FINANCIAL_YEAR_LIST = "financialYearList"
    private static final String COUNT = "count"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of financialYear object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains financialYearList and count
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)
            int count = accFinancialYearCacheUtility.count()
            List financialYearList = accFinancialYearCacheUtility.list(this)
            result.put(FINANCIAL_YEAR_LIST, financialYearList)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap financialYear object list to show on grid
     * @param obj -a map contains financialYear object list and count
     * @return -wrapped financialYear object list to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<AccFinancialYear> financialYearList = (List<AccFinancialYear>) executeResult.get(FINANCIAL_YEAR_LIST)
            int count = (int) executeResult.get(COUNT)
            List inGridEntityList = wrapListInGridEntityList(financialYearList, start)
            return [page: pageNumber, total: count, rows: inGridEntityList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
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
     * wrappedFinancialYear object list for grid
     * @param financialYearList -list of financialYear objects
     * @param start -start index
     * @return -wrappedFinancialYear object list
     */
    private List wrapListInGridEntityList(List<AccFinancialYear> financialYearList, int start) {
        List lstFinancialYear = [] as List

        int counter = start + 1
        for (int i = 0; i < financialYearList.size(); i++) {
            AccFinancialYear eachRow = financialYearList[i]
            AppUser createdBy = (AppUser) appUserCacheUtility.read(eachRow.createdBy)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(eachRow.updatedBy)
            String startDate = DateUtility.getLongDateForUI(eachRow.startDate)
            String endDate = DateUtility.getLongDateForUI(eachRow.endDate)
            boolean isCurrent = eachRow.isCurrent.booleanValue()
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.id,
                    startDate,
                    endDate,
                    isCurrent ? Tools.YES : Tools.NO,
                    createdBy.username,
                    updatedBy ? updatedBy.username : Tools.EMPTY_SPACE,
                    eachRow.contentCount
            ]
            lstFinancialYear << obj
            counter++
        }
        return lstFinancialYear
    }
}
