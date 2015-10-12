package com.athena.mis.accounting.actions.accfinancialyear

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to set specific financial-year as current financial-year
 *  For details go through Use-Case doc named 'SetForCurrentFinancialYearActionService'
 */
class SetForCurrentFinancialYearActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String UPDATE_FAILURE_MESSAGE = "Financial-Year could not be updated"
    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected Financial-Year is not found"
    private static final String UPDATE_SUCCESS_MESSAGE = "Financial-Year has been updated successfully"
    private static final String FINANCIAL_YEAR = "financialYear"
    private static final String FINANCIAL_YEAR_LIST = "financialYearList"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred to invalid input"
    private static final String ALREADY_IS_CURRENT = "This Financial-Year is already current Financial-Year"

    /**
     * Check different criteria to set isCurrent of a FinancialYear object
     *      1) Check existence of required parameter
     *      2) Check existence of financialYear object
     *      3) Check if selected financialYear object is already CURRENT or not
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains FinancialYear object & isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {//Check existence of required parameter
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long financialYearId = Long.parseLong(params.id.toString())
            AccFinancialYear oldFinancialYear = (AccFinancialYear) accFinancialYearCacheUtility.read(financialYearId)
            if (!oldFinancialYear) {//Check existence of financialYear object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }

            if (oldFinancialYear.isCurrent) { //current financialYear could not be set current again
                result.put(Tools.MESSAGE, ALREADY_IS_CURRENT)
                return result
            }

            //build financialYear object to set as current financialYear
            AccFinancialYear accFinancialYear = buildFinancialYear(params, oldFinancialYear)
            result.put(FINANCIAL_YEAR, accFinancialYear)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Set selected FinancialYear object as current FinancialYear
     *      1) set is_current = FALSE of all FinancialYear objects in DB
     *      2) set is_current = TRUE of selected FinancialYear object in DB
     *      3) set is_current = FALSE of all FinancialYear objects in cache
     *      3) set is_current = TRUE of selected FinancialYear object in cache
     * @param parameters -parameter send from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccFinancialYear financialYear = (AccFinancialYear) receivedResult.get(FINANCIAL_YEAR)
            setAllFinancialYearFalse() // set is_current = FALSE of all FinancialYear objects in DB

            //set is_current = TRUE of selected FinancialYear object in DB
            AccFinancialYear updatedFinancialYear = setFinancialYearIsCurrent(financialYear)

            accFinancialYearCacheUtility.setCurrentFalse()   // set all financialYear false in utility
            // update financial-year in cache utility and keep the data sorted
            accFinancialYearCacheUtility.update(updatedFinancialYear, accFinancialYearCacheUtility.SORT_BY_ID, accFinancialYearCacheUtility.SORT_ORDER_ASCENDING)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * get list of financialYear object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains wrappedFinancialYearList and count
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            GrailsParameterMap params = (GrailsParameterMap) obj
            initPager(params)
            int count = accFinancialYearCacheUtility.count()
            List financialYearList = accFinancialYearCacheUtility.list(this)
            List resultFinancialYearList = wrapListInGridEntityList(financialYearList, start)
            Map output = [page: pageNumber, total: count, rows: resultFinancialYearList]
            result.put(FINANCIAL_YEAR_LIST, output)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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

    /**
     * build financialYear object to set as current financialYear
     * @param parameterMap -GrailsParameterMap
     * @param oldFinancialYear -AccFinancialYear
     * @return AccFinancialYear object
     */
    private AccFinancialYear buildFinancialYear(GrailsParameterMap parameterMap, AccFinancialYear oldFinancialYear) {
        AccFinancialYear financialYear = new AccFinancialYear(parameterMap)
        AppUser systemUser = accSessionUtil.appSessionUtil.getAppUser()

        financialYear.id = oldFinancialYear.id
        financialYear.version = oldFinancialYear.version
        financialYear.updatedBy = systemUser.id
        financialYear.updatedOn = new Date()
        financialYear.createdOn = oldFinancialYear.createdOn
        financialYear.createdBy = oldFinancialYear.createdBy
        financialYear.isCurrent = true
        financialYear.startDate = oldFinancialYear.startDate
        financialYear.endDate = oldFinancialYear.endDate
        financialYear.companyId = oldFinancialYear.companyId

        return financialYear
    }

    private static final String QUERY_UPDATE = """
                    UPDATE acc_financial_year
                      SET
                          is_current=false
                      WHERE
                          is_current=true AND
                          company_id=:companyId
                          """
    /**
     *  set is_current = FALSE of all FinancialYear objects
     */
    private void setAllFinancialYearFalse() {
        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getCompanyId()
        ]
        executeUpdateSql(QUERY_UPDATE, queryParams)
    }

    private static final String UPDATE_QUERY = """
                    UPDATE acc_financial_year SET
                          version=:newVersion,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          is_current=:isCurent
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     *  set is_current = TRUE of selected FinancialYear object
     * @param accFinancialYear -AccFinancialYear
     * @return -updated accFinancialYear object
     */
    private AccFinancialYear setFinancialYearIsCurrent(AccFinancialYear accFinancialYear) {
        Map queryParams = [
                id: accFinancialYear.id,
                newVersion: accFinancialYear.version + 1,
                version: accFinancialYear.version,
                updatedBy: accFinancialYear.updatedBy,
                updateOn: DateUtility.getSqlDateWithSeconds(accFinancialYear.updatedOn),
                isCurent: accFinancialYear.isCurrent,
                companyId: accFinancialYear.companyId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
        }
        accFinancialYear.version = accFinancialYear.version + 1
        return accFinancialYear
    }
}
