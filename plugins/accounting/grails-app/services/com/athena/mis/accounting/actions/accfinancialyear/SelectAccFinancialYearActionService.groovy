package com.athena.mis.accounting.actions.accfinancialyear

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select specific financialYear object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccFinancialYearActionService'
 */
class SelectAccFinancialYearActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected Financial-Year is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select Financial-Year"
    private static final String START_DATE = "startDate"
    private static final String END_DATE = "endDate"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get financialYear object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing financialYear object necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long financialYearId = Long.parseLong(parameterMap.id.toString())
            AccFinancialYear financialYear = (AccFinancialYear) accFinancialYearCacheUtility.read(financialYearId)
            if (!financialYear) { //check existence of selected financialYear object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }
            result.put(Tools.ENTITY, financialYear)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
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
     * Build a map with necessary objects to show on UI
     * @param obj -map contains financialYear object
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccFinancialYear financialYear = (AccFinancialYear) executeResult.get(Tools.ENTITY)
            String startDate = DateUtility.getDateForUI(financialYear.startDate)
            String endDate = DateUtility.getDateForUI(financialYear.endDate)

            result.put(Tools.ENTITY, financialYear)
            result.put(START_DATE, startDate)
            result.put(END_DATE, endDate)
            result.put(Tools.VERSION, financialYear.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}