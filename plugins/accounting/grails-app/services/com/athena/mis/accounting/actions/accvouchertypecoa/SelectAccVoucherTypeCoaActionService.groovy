package com.athena.mis.accounting.actions.accvouchertypecoa

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccVoucherTypeCoa
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.accounting.utility.AccVoucherTypeCoaCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific accVoucherTypeCoa object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccVoucherTypeCoaActionService'
 */
class SelectAccVoucherTypeCoaActionService extends BaseService implements ActionIntf {

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccVoucherTypeCoaCacheUtility accVoucherTypeCoaCacheUtility
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected Voucher-Type not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select voucher type"
    private static final String ACC_VOUCHER_TYPE_COA_CODE = "code"
    private static final String ACC_VOUCHER_TYPE_COA_ID = "accVoucherTypeId"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to select accVoucherTypeCoa object
     *      1) Check existence of required parameter
     *      2) Check existence of accVoucherTypeCoa object
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

            long id = Long.parseLong(parameterMap.id.toString())
            AccVoucherTypeCoa voucherTypeCoa = (AccVoucherTypeCoa) accVoucherTypeCoaCacheUtility.read(id)
            if (!voucherTypeCoa) {//check existence of accVoucherTypeCoa object
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
     * Get accVoucherTypeCoa object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing accVoucherTypeCoa object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long id = Long.parseLong(parameterMap.id.toString())
            AccVoucherTypeCoa voucherTypeCoa = (AccVoucherTypeCoa) accVoucherTypeCoaCacheUtility.read(id)
            result.put(Tools.ENTITY, voucherTypeCoa)
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
     * @param obj -map contains AccVoucherTypeCoa object
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccVoucherTypeCoa voucherTypeCoa = (AccVoucherTypeCoa) executeResult.get(Tools.ENTITY)
            AccChartOfAccount chartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(voucherTypeCoa.coaId)
            SystemEntity accVoucherType = (SystemEntity) accVoucherTypeCacheUtility.read(voucherTypeCoa.accVoucherTypeId)
            result.put(Tools.ENTITY, voucherTypeCoa)
            result.put(ACC_VOUCHER_TYPE_COA_CODE, chartOfAccount.code)
            result.put(ACC_VOUCHER_TYPE_COA_ID, accVoucherType.key)
            result.put(Tools.VERSION, voucherTypeCoa.version)
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
