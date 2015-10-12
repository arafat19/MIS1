package com.athena.mis.accounting.actions.accfinancialyear

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.service.AccFinancialYearService
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create FinancialYear object and to show on grid list
 *  For details go through Use-Case doc named 'CreateAccFinancialYearActionService'
 */
class CreateAccFinancialYearActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    AccFinancialYearService accFinancialYearService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility

    private static final String CREATE_SUCCESS_MSG = "Financial-Year has been successfully saved"
    private static final String CREATE_FAILURE_MSG = "Financial-Year has not been saved"
    private static final String ALREADY_EXISTS = "Financial-Year with same dates already exists"
    private static final String RESULT_MAP = "resultMap"
    private static final String FINANCIAL_YEAR = "financialYear"

    /**
     * Check duplicate Financial-Year dates for creating new accFinancialYear
     * @param params -N/A
     * @param obj -AccFinancialYear object send from controller
     * @return -a map containing accFinancialYear object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            AccFinancialYear financialYear = (AccFinancialYear) obj

            //check duplicate Financial-Year dates
            Date startDate = DateUtility.parseMaskedDate(params.startDate.toString())
            Date endDate = DateUtility.parseMaskedDate(params.endDate.toString())
            AccFinancialYear duplicateFinancialYear = accFinancialYearCacheUtility.checkDuplicateFinancialYear(startDate, endDate)
            if (duplicateFinancialYear) {
                result.put(Tools.MESSAGE, ALREADY_EXISTS)
                return result
            }
            result.put(FINANCIAL_YEAR, financialYear)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Save accFinancialYear object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -accFinancialYearObject send from controller
     * @return -newly created accFinancialYear object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccFinancialYear financialYear = (AccFinancialYear) preResult.get(FINANCIAL_YEAR)
            AccFinancialYear newFinancialYear = accFinancialYearService.create(financialYear)// save in DB
            // save in cache and keep the data sorted
            accFinancialYearCacheUtility.add(newFinancialYear, accFinancialYearCacheUtility.SORT_BY_ID, accFinancialYearCacheUtility.SORT_ORDER_ASCENDING)
            result.put(FINANCIAL_YEAR, newFinancialYear)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            //@todo:rollback
            throw new RuntimeException(CREATE_FAILURE_MSG)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
     * Wrap newly created accFinancialYear to show on grid
     * @param obj -newly created accFinancialYear object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccFinancialYear financialYear = (AccFinancialYear) executeResult.get(FINANCIAL_YEAR)
            GridEntity object = new GridEntity()
            AppUser systemUser = (AppUser) appUserCacheUtility.read(financialYear.createdBy)
            object.id = financialYear.id
            object.cell = [
                    Tools.LABEL_NEW,
                    financialYear.id,
                    DateUtility.getLongDateForUI(financialYear.startDate),
                    DateUtility.getLongDateForUI(financialYear.endDate),
                    financialYear.isCurrent ? Tools.YES : Tools.NO,
                    systemUser.username,
                    Tools.EMPTY_SPACE,
                    Tools.EMPTY_SPACE
            ]
            Map resultMap = [entity: object]
            result.put(RESULT_MAP, resultMap)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }
}