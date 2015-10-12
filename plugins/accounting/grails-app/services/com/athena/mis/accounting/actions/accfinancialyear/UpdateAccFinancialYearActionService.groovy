package com.athena.mis.accounting.actions.accfinancialyear

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.service.AccFinancialYearService
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
 *  Class to update FinancialYear object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateAccFinancialYearActionService'
 */
class UpdateAccFinancialYearActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    AccFinancialYearService accFinancialYearService
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String UPDATE_FAILURE_MESSAGE = "Financial-Year could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "Financial-Year has been updated successfully"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String OBJ_NOT_FOUND = "Selected Financial-Year not exists"
    private static final String ALREADY_EXISTS = "Financial-Year with same dates already exists"
    private static final String RESULT_MAP = "resultMap"
    private static final String FINANCIAL_YEAR = "financialYear"

    /**
     * Check different criteria to update accFinancialYear object
     *      1) Check existence of required parameters
     *      2) Check existence of selected(old) accFinancialYear object
     *      3) check duplicate Financial year dates
     * @param params -parameters send from UI
     * @param obj -N/A
     * @return -a map containing accFinancialYear object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            //Check existence of required parameters
            if ((!params.id) || (!params.version)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR_MSG)
                return result
            }

            int financialYearId = Integer.parseInt(params.id.toString())
            AccFinancialYear oldFinancialYear = (AccFinancialYear) accFinancialYearCacheUtility.read(financialYearId)
            if (!oldFinancialYear) {  //check existence of accFinancialYear object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            //Build Financial-Year object to update
            AccFinancialYear newFinancialYear = buildFinancialYear(params, oldFinancialYear)

            //check duplicate FinancialYear dates
            AccFinancialYear duplicateFinancialYear = accFinancialYearCacheUtility.checkDuplicateFinancialYearForUpdate(newFinancialYear.startDate, newFinancialYear.endDate, oldFinancialYear.id)
            if (duplicateFinancialYear) {
                result.put(Tools.MESSAGE, ALREADY_EXISTS)
                return result
            }
            result.put(FINANCIAL_YEAR, newFinancialYear)
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
     * update accFinancialYear object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -accFinancialYearObject send from executePreCondition
     * @return -updated accFinancialYear object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccFinancialYear financialYear = (AccFinancialYear) receivedResult.get(FINANCIAL_YEAR)
            AccFinancialYear newFinancialYear = accFinancialYearService.update(financialYear) //update in DB
            //update in cache and keep the data sorted
            accFinancialYearCacheUtility.update(newFinancialYear, accFinancialYearCacheUtility.SORT_BY_ID, accFinancialYearCacheUtility.SORT_ORDER_ASCENDING)
            result.put(FINANCIAL_YEAR, newFinancialYear)
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
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated accFinancialYear object to show on grid
     * @param obj -updated accFinancialYear object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccFinancialYear financialYearObj = (AccFinancialYear) receivedResult.get(FINANCIAL_YEAR)
            GridEntity object = new GridEntity()
            AppUser createdBy = (AppUser) appUserCacheUtility.read(financialYearObj.createdBy)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(financialYearObj.updatedBy)

            object.id = financialYearObj.id
            object.cell = [
                    Tools.LABEL_NEW,
                    financialYearObj.id,
                    DateUtility.getLongDateForUI(financialYearObj.startDate),
                    DateUtility.getLongDateForUI(financialYearObj.endDate),
                    financialYearObj.isCurrent ? Tools.YES : Tools.NO,
                    createdBy.username,
                    updatedBy.username,
                    financialYearObj.contentCount
            ]

            Map resultMap = [entity: object]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(RESULT_MAP, resultMap)
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
     * Build Financial-Year object to update
     * @param parameterMap -GrailsParameterMap
     * @param oldFinancialYear -AccFinancialYear
     * @return -AccFinancialYear object
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
        financialYear.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        financialYear.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())
        financialYear.isCurrent = oldFinancialYear.isCurrent
        financialYear.companyId = oldFinancialYear.companyId

        return financialYear
    }
}
