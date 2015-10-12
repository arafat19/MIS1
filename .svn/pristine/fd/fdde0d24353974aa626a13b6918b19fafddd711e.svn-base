package com.athena.mis.accounting.actions.acctier2

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.service.AccTier2Service
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update tier2 object and grid data
 *  For details go through Use-Case doc named 'UpdateAccTier2ActionService'
 */
class UpdateAccTier2ActionService extends BaseService implements ActionIntf {

    // constants
    private static final String ACC_TIER2_UPDATE_FAILURE_MESSAGE = "Fail to update Tier-2"
    private static final String ACC_TIER2_UPDATE_SUCCESS_MESSAGE = "Tier-2 has been updated successfully"
    private static final String ONLY_NAME_UPDATE_MESSAGE = ", only name can be updated"
    private static final String ACC_TIER2_NAME_EXISTS = "Tier-2 Name already exists"
    private static final String ACC_TIER2_NOT_FOUND = "Tier-2 not found"
    private static final String HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT = " chart of account is associated with this tier-2"
    private static final String HAS_ASSOCIATION_MESSAGE_TIER3 = " tier-3 is associated with this tier-2"
    private static final String ACC_TIER2 = "accTier2"
    private static final String NEW = "New"

    private Logger log = Logger.getLogger(getClass())

    AccTier2Service accTier2Service
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    /**
     * Get parameters from UI and build tier2 object for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long tier2Id = Long.parseLong(parameterMap.id.toString())

            AccTier2 oldAccTier2 = (AccTier2) accTier2CacheUtility.read(tier2Id)
            if (!oldAccTier2) {
                result.put(Tools.MESSAGE, ACC_TIER2_NOT_FOUND)
                return result
            }
            AccTier2 tier2 = buildAccTier2(parameterMap, oldAccTier2)  // build tier2 object for update
            int countDuplicate = AccTier2.countByNameAndIdNotEqual(tier2.name, tier2Id)
            if (countDuplicate > 0) {
                result.put(Tools.MESSAGE, ACC_TIER2_NAME_EXISTS)
                return result
            }

            Map associationResult = (Map) hasAssociation(oldAccTier2)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue() && ((oldAccTier2.accTypeId != tier2.accTypeId) || (oldAccTier2.accTier1Id != tier2.accTier1Id))) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE) + ONLY_NAME_UPDATE_MESSAGE)
                return result
            }
            result.put(ACC_TIER2, tier2)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER2_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Update tier2 object in DB & update cache utility accordingly
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
            AccTier2 accTier2 = (AccTier2) preResult.get(ACC_TIER2)
            AccTier2 newAccTier2 = accTier2Service.update(accTier2)
            accTier2CacheUtility.update(newAccTier2, accTier2CacheUtility.SORT_BY_NAME, accTier2CacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_TIER2, newAccTier2)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ACC_TIER2_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
     * Show updated tier2 object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccTier2 accTier2ServiceReturn = (AccTier2) executeResult.get(ACC_TIER2)
            GridEntity object = new GridEntity()
            String accTypeName = accTypeCacheUtility.read(accTier2ServiceReturn.accTypeId)
            String accTier1Name = accTier1CacheUtility.read(accTier2ServiceReturn.accTier1Id)
            object.id = accTier2ServiceReturn.id
            object.cell = [NEW,
                    accTier2ServiceReturn.id,
                    accTier1Name,
                    accTier2ServiceReturn.name,
                    accTypeName,
                    accTier2ServiceReturn.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accTier2ServiceReturn.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_TIER2_UPDATE_SUCCESS_MESSAGE)
            result.put(ACC_TIER2, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER2_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, ACC_TIER2_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER2_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Check association of tier2 with relevant domains
     * @param tier2 -tier2 object
     * @return -a map containing isError(true/false) depending on association and relevant message
     */
    private LinkedHashMap hasAssociation(AccTier2 tier2) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        int tier2Id = tier2.id
        int count = 0

        count = AccTier3.countByAccTier2Id(tier2Id)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_TIER3)
            return result
        }
        count = AccChartOfAccount.countByTier2(tier2Id)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    /**
     * Build tier2 object for update
     * @param params -serialized parameters from UI
     * @param oldTier1 -old tier2 object
     * @return -updated tier2 object
     */
    private AccTier2 buildAccTier2(GrailsParameterMap params, AccTier2 oldTier2) {
        AccTier2 accTier2 = new AccTier2(params);
        accTier2.id = oldTier2.id
        accTier2.version = oldTier2.version
        accTier2.companyId = oldTier2.companyId
        return accTier2
    }
}
