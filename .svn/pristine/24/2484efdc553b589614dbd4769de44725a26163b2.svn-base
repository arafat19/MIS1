package com.athena.mis.accounting.actions.accgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.entity.AccVoucherDetails
import com.athena.mis.accounting.service.AccGroupService
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete AccGroup object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteAccGroupActionService'
 */
class DeleteAccGroupActionService extends BaseService implements ActionIntf {

    AccGroupService accGroupService
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private static final String INVALID_INPUT_MESSAGE = "Could not delete Account-Group due to invalid input"
    private static final String DELETE_SUCCESS_MESSAGE = "Account-Group has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Account-Group could not be deleted, please refresh the Account-Group list"
    private static final String HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT = " chart of account is associated with this Account-Group"
    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS = " voucher details is associated with this Account-Group"
    private static final String RESERVED_ERROR = "Reserved Account-Group can't be deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete Account-Group"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete accGroup object
     *      1) Check existence of required parameter
     *      2) Check existence of accGroup object
     *      3) Check if Account-Group is reserved or not
     *      4) check association with different domain(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) { //Check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            long accGroupId = Long.parseLong(params.id.toString())
            AccGroup accGroup = (AccGroup) accGroupCacheUtility.read(accGroupId)
            if (!accGroup) {//Check existence of accGroup object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (accGroup.isReserved) {//reserve Account-Group could not be deleted
                result.put(Tools.MESSAGE, RESERVED_ERROR)
                return result
            }

            //check association
            Map associationResult = (Map) hasAssociation(accGroup.id)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
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
     * delete accGroup object from DB & cache
     * @param parameters -parameter send from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object Parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) Parameters
            long accGroupId = Long.parseLong(params.id.toString())
            accGroupService.delete(accGroupId) //delete from DB
            accGroupCacheUtility.delete(accGroupId)//delete from cache
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
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
     * @return -a map contains isError(true) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_SUCCESS_MESSAGE]
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

    /**
     * Check association on accGroup with accChartOfAccount & accVoucherDetails
     * @param accGroupId -AccGroup.id
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(long accGroupId) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        int count = 0

        count = AccChartOfAccount.countByAccGroupId(accGroupId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT)
            return result
        }

        count = AccVoucherDetails.countByGroupId(accGroupId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
