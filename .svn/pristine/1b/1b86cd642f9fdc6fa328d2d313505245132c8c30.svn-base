package com.athena.mis.accounting.actions.acctier3

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.service.AccTier3Service
import com.athena.mis.accounting.utility.AccTier3CacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete accTier3 object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteAccTier3ActionService'
 */
class DeleteAccTier3ActionService extends BaseService implements ActionIntf {

    AccTier3Service accTier3Service
    @Autowired
    AccTier3CacheUtility accTier3CacheUtility

    private static final String INVALID_INPUT_MESSAGE = "Could not delete tier3 due to invalid input"
    private static final String DELETE_SUCCESS_MESSAGE = "Tier-3 has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Tier-3 could not be deleted, please refresh the Tier3 list"
    private static final String HAS_ASSOCIATION_MESSAGE = " chart of account(s) are associated with this tier-3"
    private static final String DELETED = "deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Could not delete acc Tier-3"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete accTier3 object
     *      1) Check existence of required parameter
     *      2) Check existence of accTier3 object
     *      3) check association with accVoucher
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

            long accTier3Id = Long.parseLong(params.id.toString())
            AccTier3 accTier3 = (AccTier3) accTier3CacheUtility.read(accTier3Id)
            if (!accTier3) { //check existence of accTier3 object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            //check association
            Map associationResult = (Map) hasAssociation(accTier3)
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
     * delete accTier3 object from DB & cache
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

            int accTier3Id = Long.parseLong(params.id.toString())
            accTier3Service.delete(accTier3Id)
            accTier3CacheUtility.delete(accTier3Id)

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
     * @return -a map contains booleanValue(true) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
        return result
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
     * Check association on accTier3 with accVoucher
     * @param AccTier3 -accTier3
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(AccTier3 accTier3) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        int accTier3Id = accTier3.id
        int count = AccChartOfAccount.countByTier3(accTier3Id)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
