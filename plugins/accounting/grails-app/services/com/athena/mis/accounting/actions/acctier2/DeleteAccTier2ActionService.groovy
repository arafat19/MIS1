package com.athena.mis.accounting.actions.acctier2

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.service.AccTier2Service
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 *  Delete tier2 object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAccTier2ActionService'
 */
class DeleteAccTier2ActionService extends BaseService implements ActionIntf {

    AccTier2Service accTier2Service
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility

    private static final String DELETE_ACC_TIER2_SUCCESS_MESSAGE = "Tier-2 has been deleted successfully"
    private static final String DELETE_ACC_TIER2_FAILURE_MESSAGE = "Tier-2 could not be deleted, please refresh the Tier2 list"
    private static final String HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT = " chart of account is associated with this tier-2"
    private static final String HAS_ASSOCIATION_MESSAGE_TIER3 = " tier-3 is associated with this tier-2"
    private static final String DEFAULT_ERROR_MESSAGE = "Could not delete acc Tier-2"
    private static final String DELETED = "deleted"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Checking pre condition and association before deleting the tier2 object
     * @param parameters - parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)            // default value
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long accTier2Id = Long.parseLong(params.id.toString())

            AccTier2 accTier2 = (AccTier2) accTier2CacheUtility.read(accTier2Id)        // get tier2 object
            // check whether selected tier2 exists or not
            if (!accTier2) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check association of tier2 with relevant domains
            Map associationResult = (Map) hasAssociation(accTier2)

            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ACC_TIER2_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Delete tier2 object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param Parameters - parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object Parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) Parameters

            int accTier2Id = Long.parseLong(params.id.toString())
            accTier2Service.delete(accTier2Id)           // delete tier2 object from DB
            accTier2CacheUtility.delete(accTier2Id)      // delete tier2 object from cache utility

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ACC_TIER2_FAILURE_MESSAGE)
            return result
        }

    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, DELETE_ACC_TIER2_SUCCESS_MESSAGE)
        return result
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
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
            result.put(Tools.MESSAGE, DELETE_ACC_TIER2_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ACC_TIER2_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Check association of tier2 with relevant domains
     * @param accTier2 -tier2 object
     * @return -a map containing isError(true/false) depending on association and relevant message
     */
    private LinkedHashMap hasAssociation(AccTier2 accTier2) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        int accTier2Id = accTier2.id
        int count = 0

        count = AccChartOfAccount.countByTier2(accTier2Id)    // count coa associated with this tier2
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT)
            return result
        }

        count = AccTier3.countByAccTier2Id(accTier2Id)       // count tier3 associated with this tier2
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_TIER3)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
