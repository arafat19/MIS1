package com.athena.mis.accounting.actions.acctier1

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.service.AccTier1Service
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 *  Delete tier1 object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAccTier1ActionService'
 */
class DeleteAccTier1ActionService extends BaseService implements ActionIntf {

    AccTier1Service accTier1Service
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility

    private static final String DELETE_ACC_TIER1_SUCCESS_MESSAGE = "Tier-1 has been deleted successfully"
    private static final String DELETE_ACC_TIER1_FAILURE_MESSAGE = "Tier-1 could not be deleted, please refresh the Tier-1 list"
    private static final String HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT = " chart of account is associated with tier-1"
    private static final String HAS_ASSOCIATION_MESSAGE_TIER2 = " tier-2 is associated with this tier-1"
    private static final String HAS_ASSOCIATION_MESSAGE_TIER3 = " tier-3 is associated with this tier-1"
    private static final String DEFAULT_ERROR_MESSAGE = "Could not delete Tier-1"
    private static final String DELETED = "deleted"

    private Logger log = Logger.getLogger(getClass())
    /**
     * Checking pre condition and association before deleting the tier1 object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long accTier1Id = Long.parseLong(params.id.toString())
            AccTier1 accTier1 = (AccTier1) accTier1CacheUtility.read(accTier1Id)
            // check whether selected tier1 exists or not
            if (!accTier1) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check association of tier1 with relevant domains
            Map associationResult = (Map) hasAssociation(accTier1)
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
            result.put(Tools.MESSAGE, DELETE_ACC_TIER1_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Delete tier1 object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object Parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) Parameters

            int accTier1Id = Long.parseLong(params.id.toString())
            accTier1Service.delete(accTier1Id)      // delete tier1 object from DB
            accTier1CacheUtility.delete(accTier1Id)      // delete tier1 object from cache utility

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ACC_TIER1_FAILURE_MESSAGE)
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
        result.put(Tools.MESSAGE, DELETE_ACC_TIER1_SUCCESS_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj  // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_ACC_TIER1_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ACC_TIER1_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Check association of tier1 with relevant domains
     * @param tier1 -tier1 object
     * @return -a map containing isError(true/false) depending on association and relevant message
     */
    private LinkedHashMap hasAssociation(AccTier1 accTier1) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        int accTier1Id = accTier1.id
        int count = 0

        // check whether selected tier1 is used in ChartOfAccount or not
        count = AccChartOfAccount.countByTier1(accTier1Id)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT)
            return result
        }

        // check whether selected tier1 is used in Tier2 or not
        count = AccTier2.countByAccTier1Id(accTier1Id)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_TIER2)
            return result
        }

        // check whether selected tier1 is used in Tier3 or not
        count = AccTier3.countByAccTier1Id(accTier1Id)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_TIER3)
            return result
        }


        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
