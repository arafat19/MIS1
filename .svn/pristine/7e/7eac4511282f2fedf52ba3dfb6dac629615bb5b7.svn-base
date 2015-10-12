package com.athena.mis.accounting.actions.acctier1

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.service.AccTier1Service
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update tier1 object and grid data
 *  For details go through Use-Case doc named 'UpdateAccTier1ActionService'
 */
class UpdateAccTier1ActionService extends BaseService implements ActionIntf {

    private static final String ACC_TIER1_UPDATE_FAILURE_MESSAGE = "Failed to update Tier-1"
    private static final String ACC_TIER1_UPDATE_SUCCESS_MESSAGE = "Tier-1 has been updated successfully"
    private static final String ONLY_NAME_UPDATE_MESSAGE = ", only name can be updated"
    private static final String ACC_TIER1_NAME_EXISTS = "Tier-1 Name already exists"
    private static final String ACC_TIER1_NOT_FOUND = "Tier-1 not found"
    private static final String ACC_TIER1 = "accTier1"
    private static final String HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT = " chart of account is associated with tier-1"
    private static final String HAS_ASSOCIATION_MESSAGE_TIER2 = " tier-2 is associated with this tier-1"
    private static final String HAS_ASSOCIATION_MESSAGE_TIER3 = " tier-3 is associated with this tier-1"

    private Logger log = Logger.getLogger(getClass())

    AccTier1Service accTier1Service
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    /**
     * Get parameters from UI and build tier1 object for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long tier1Id = Long.parseLong(parameterMap.id.toString())

            AccTier1 oldAccTier1 = (AccTier1) accTier1CacheUtility.read(tier1Id)
            if (!oldAccTier1) {
                result.put(Tools.MESSAGE, ACC_TIER1_NOT_FOUND)
                return result
            }
            AccTier1 tier1 = buildAccTier1(parameterMap, oldAccTier1)  // build tier1 object for update
            int countDuplicate = AccTier1.countByNameIlikeAndIdNotEqualAndCompanyId(tier1.name, tier1.id, tier1.companyId)
            if (countDuplicate > 0) {
                result.put(Tools.MESSAGE, ACC_TIER1_NAME_EXISTS)
                return result
            }
            Map associationResult = hasAssociation(oldAccTier1)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue() && oldAccTier1.accTypeId != tier1.accTypeId) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE) + ONLY_NAME_UPDATE_MESSAGE)
                return result
            }

            result.put(ACC_TIER1, tier1)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER1_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Update tier1 object in DB & update cache utility accordingly
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccTier1 accTier1 = (AccTier1) preResult.get(ACC_TIER1)
            AccTier1 newAccTier1 = accTier1Service.update(accTier1)
            accTier1CacheUtility.update(newAccTier1, accTier1CacheUtility.SORT_BY_NAME, accTier1CacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_TIER1, newAccTier1)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ACC_TIER1_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER1_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }
    /**
     * Show updated tier1 object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccTier1 accTier1ServiceReturn = (AccTier1) executeResult.get(ACC_TIER1)
            GridEntity object = new GridEntity()
            String accTypeName = accTypeCacheUtility.read(accTier1ServiceReturn.accTypeId)
            object.id = accTier1ServiceReturn.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accTier1ServiceReturn.id,
                    accTier1ServiceReturn.name ? accTier1ServiceReturn.name : Tools.EMPTY_SPACE,
                    accTypeName ? accTypeName : Tools.EMPTY_SPACE,
                    accTier1ServiceReturn.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accTier1ServiceReturn.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_TIER1_UPDATE_SUCCESS_MESSAGE)
            result.put(ACC_TIER1, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER1_UPDATE_FAILURE_MESSAGE)
            return result
        }
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
            result.put(Tools.MESSAGE, ACC_TIER1_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER1_UPDATE_FAILURE_MESSAGE)
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

        count = AccTier3.countByAccTier1Id(accTier1Id)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_TIER3)
            return result
        }
        count = AccTier2.countByAccTier1Id(accTier1Id)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_TIER2)
            return result
        }
        count = AccChartOfAccount.countByTier1(accTier1Id)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
    /**
     * Build tier1 object for update
     * @param params -serialized parameters from UI
     * @param oldTier1 -old tier1 object
     * @return -updated tier1 object
     */
    private AccTier1 buildAccTier1(GrailsParameterMap params, AccTier1 oldTier1) {
        AccTier1 accTier1 = new AccTier1(params);
        accTier1.id = oldTier1.id
        accTier1.version = oldTier1.version
        accTier1.companyId = oldTier1.companyId
        return accTier1
    }
}