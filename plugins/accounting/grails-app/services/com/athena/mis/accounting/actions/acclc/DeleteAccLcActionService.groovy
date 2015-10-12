package com.athena.mis.accounting.actions.acclc

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccLc
import com.athena.mis.accounting.entity.AccVoucherDetails
import com.athena.mis.accounting.service.AccLcService
import com.athena.mis.accounting.utility.AccLcCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete AccLc object from DB & from cache
 *  For details go through Use-Case doc named 'DeleteAccLcActionService'
 */
class DeleteAccLcActionService extends BaseService implements ActionIntf {

    AccLcService accLcService
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DELETE_SUCCESS_MSG = "LC has been successfully deleted"
    private static final String DELETE_FAILURE_MSG = "LC has not been deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete LC"
    private static final String ENTITY_NOT_FOUND_MSG = "Selected LC not found"
    private static final String HAS_ASSOCIATION_MESSAGE = " voucher(s) are associated with selected LC"
    private static final String DELETED = "deleted"

    /**
     * Check different criteria to delete accLc object
     *      1) Check existence of required parameter
     *      2) Check existence of accLc object
     *      3) Check association with accVoucherDetails
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.id) {//Check existence of required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long accLcId = Long.parseLong(parameterMap.id.toString())
            AccLc accLc = (AccLc) accLcCacheUtility.read(accLcId)
            if (!accLc) {//Check existence of object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_MSG)
                return result
            }

            long companyId = accSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity accSourceTypeLc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LC, companyId)
            int count = AccVoucherDetails.countBySourceTypeIdAndSourceId(accSourceTypeLc.id, accLc.id)
            if (count > 0) {  // Check association in accVoucherDetails
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        }
    }

    /**
     * delete accLc object from DB & from cache
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long accLcId = Long.parseLong(parameterMap.id.toString())

            //deleting from DB
            accLcService.delete(accLcId)

            //deleting from cache
            accLcCacheUtility.delete(accLcId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
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
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
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
