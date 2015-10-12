package com.athena.mis.accounting.actions.accipc

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIpc
import com.athena.mis.accounting.entity.AccVoucherDetails
import com.athena.mis.accounting.service.AccIpcService
import com.athena.mis.accounting.utility.AccIpcCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete AccIpc object from DB & from cache
 *  For details go through Use-Case doc named 'DeleteAccIpcActionService'
 */
class DeleteAccIpcActionService extends BaseService implements ActionIntf {

    AccIpcService accIpcService
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String INVALID_INPUT_MESSAGE = "Could not delete IPC due to invalid input"
    private static final String DELETE_SUCCESS_MESSAGE = "IPC has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "IPC could not be deleted, please refresh the IPC list"
    private static final String HAS_ASSOCIATION_MESSAGE = " voucher(s) are associated with selected IPC"
    private static final String DELETED = "deleted"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete accIpc object
     *      1) Check existence of required parameter
     *      2) Check existence of accIpc object
     *      3) Check association with accVoucherDetails
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) { //Check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            long accIpcId = Long.parseLong(params.id.toString())
            AccIpc accIpc = (AccIpc) accIpcCacheUtility.read(accIpcId)
            if (!accIpc) { //Check existence of object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            long companyId = accSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity accSourceTypeIpc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_IPC, companyId)
            int count = AccVoucherDetails.countBySourceTypeIdAndSourceId(accSourceTypeIpc.id, accIpcId)
            if (count > 0) { // Check association in accVoucherDetails
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * delete accIpc object from DB & from cache
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object Parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) Parameters
            long accIpcId = Long.parseLong(params.id.toString())

            //deleting from DB
            accIpcService.delete(accIpcId)

            //deleting from cache utility
            accIpcCacheUtility.delete(accIpcId)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DELETE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
}
