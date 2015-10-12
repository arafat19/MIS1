package com.athena.mis.accounting.actions.accfinancialyear

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.service.AccFinancialYearService
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete accFinancialYear object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteAccFinancialYearActionService'
 */
class DeleteAccFinancialYearActionService extends BaseService implements ActionIntf {

    AccFinancialYearService accFinancialYearService
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility

    private static final String INVALID_INPUT_MESSAGE = "Could not delete Financial-Year due to invalid input"
    private static final String DELETE_SUCCESS_MSG = "Financial-Year has been successfully deleted"
    private static final String DELETE_FAILURE_MSG = "Financial-Year has not been deleted"
    private static final String DELETE_PROHIBITED_CURRENT_FI_YR_MSG = "Current Financial-Year can not be deleted"
    private static final String DELETE_PROHIBITED_FOR_ATTACHMENT_MSG = "This Financial-Year have "
    private static final String ATTACHMENT = " attachment."
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete Financial-Year"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete accFinancialYear object
     *      1) Check existence of required parameter
     *      2) Check existence of accFinancialYear object
     *      3) Check if current accFinancialYear or not
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {//Check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            long financialYearId = Long.parseLong(params.id.toString())
            AccFinancialYear financialYear = (AccFinancialYear) accFinancialYearCacheUtility.read(financialYearId)
            if (!financialYear) {//Check existence of accFinancialYear object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (financialYear.isCurrent) {//current financialYear could not be deleted
                result.put(Tools.MESSAGE, DELETE_PROHIBITED_CURRENT_FI_YR_MSG)
                return result
            }
            if (financialYear.contentCount > 0) {//those financial year have attachment are not permitted to delete
                result.put(Tools.MESSAGE, DELETE_PROHIBITED_FOR_ATTACHMENT_MSG +financialYear.contentCount+ATTACHMENT )
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * delete accFinancialYear object from DB & cache
     * @param parameters -parameter send from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params

            long financialYearId = Long.parseLong(parameters.id.toString())
            accFinancialYearService.delete(financialYearId)//delete from DB
            accFinancialYearCacheUtility.delete(financialYearId)//delete from cache
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains booleanValue(true) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_SUCCESS_MSG]
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
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        }
    }
}
