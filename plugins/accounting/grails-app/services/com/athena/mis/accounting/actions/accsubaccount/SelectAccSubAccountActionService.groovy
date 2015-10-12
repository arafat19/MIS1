package com.athena.mis.accounting.actions.accsubaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccSubAccount
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccSubAccountCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific accSubAccount object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccSubAccountActionService'
 */
class SelectAccSubAccountActionService extends BaseService implements ActionIntf {

    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static String OBJ_NOT_FOUND_MASSAGE = "Selected sub account is not found"
    private static String DEFAULT_ERROR_MASSAGE = "Failed to select sub account"
    private static String SUB_ACCOUNT_CODE = "code"

    /**
     * Check different criteria to select accSubAccount object
     *      1) Check existence of required parameter
     *      2) Check existence of accSubAccount object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {//check existence of required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long subAccountId = Long.parseLong(parameterMap.id.toString())
            AccSubAccount subAccount = (AccSubAccount) accSubAccountCacheUtility.read(subAccountId)
            if (!subAccount) {//check existence of AccSubAccount object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }

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
     * Get AccSubAccount object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing AccSubAccount object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long subAccountId = Long.parseLong(parameterMap.id.toString())
            AccSubAccount subAccount = (AccSubAccount)accSubAccountCacheUtility.read(subAccountId)

            result.put(Tools.ENTITY, subAccount)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with necessary objects to show on UI
     * @param obj -map contains AccSubAccount object
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccSubAccount accSubAccountInstance = (AccSubAccount) executeResult.get(Tools.ENTITY)
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(accSubAccountInstance.coaId)
            result.put(Tools.ENTITY, accSubAccountInstance)
            result.put(SUB_ACCOUNT_CODE, accChartOfAccount.code)
            result.put(Tools.VERSION, accSubAccountInstance.version)
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
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
