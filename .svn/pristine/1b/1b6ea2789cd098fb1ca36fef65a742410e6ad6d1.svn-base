package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.service.AccFinancialYearService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  For details go through Use-Case doc named 'AccFinancialYearCacheUtility'
 */
@Component("accFinancialYearCacheUtility")
class AccFinancialYearCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AccFinancialYearService accFinancialYearService

    public static final String SORT_BY_ID = "id"

    /**
     * pull all list of AccFinancialYear objects and store list in cache
     */
    public void init() {
        List list = accFinancialYearService.list();
        super.setList(list)
    }

    /**
     * get currentFinancialYear object
     * @return -AccFinancialYear
     */
    public AccFinancialYear getCurrentFinancialYear() {
        List<AccFinancialYear> lstAll = (List<AccFinancialYear>) list();
        if (!lstAll || (lstAll.size() == 0)) {
            return null
        }
        AccFinancialYear currentFinancialYear = lstAll.find { it.isCurrent }
        return currentFinancialYear
    }

    /**
     * get accFinancialYear object within given date range
     *      Used in : CreateAccFinancialYearActionService
     * @param startDate -From date
     * @param endDate -To date
     * @return -if any accFinancialYear object found within given date range then return that object
     *          otherwise return NULL
     */
    public AccFinancialYear checkDuplicateFinancialYear(Date startDate, Date endDate) {
        List<AccFinancialYear> lstAll = (List<AccFinancialYear>) list()
        if (!lstAll || (lstAll.size() == 0)) {
            return null
        }
        AccFinancialYear duplicateFinancialYear = lstAll.find { (it.startDate.equals(startDate)) && (it.endDate.equals(endDate))}
        return duplicateFinancialYear
    }

    /**
     * get accFinancialYear object within given date range
     *      Used in : UpdateAccFinancialYearActionService
     * @param startDate -From date
     * @param endDate -To date
     * @param financialYearId -AccFinancialYear.id
     * @return -if any accFinancialYear object found within given date range then return that object
     *          otherwise return NULL
     */
    public AccFinancialYear checkDuplicateFinancialYearForUpdate(Date startDate, Date endDate, long financialYearId) {
        List<AccFinancialYear> lstAll = (List<AccFinancialYear>) list();
        if (!lstAll || (lstAll.size() == 0)) {
            return null
        }
        AccFinancialYear duplicateFinancialYear = lstAll.find { it.startDate.equals(startDate) && it.endDate.equals(endDate) && it.id != financialYearId }
        return duplicateFinancialYear
    }

    /**
     * set isCurrent = FALSE of all AccFinancialYear
     * @return -boolean value(true)
     */
    public boolean setCurrentFalse() {
        List<AccFinancialYear> lstAll = (List<AccFinancialYear>) list()
        if (!lstAll || (lstAll.size() == 0)) {
            return true
        }
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].isCurrent) {
                lstAll[i].isCurrent = false
            }
        }
        return true
    }
}