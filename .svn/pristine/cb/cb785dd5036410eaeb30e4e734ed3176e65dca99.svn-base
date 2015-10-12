package com.athena.mis.accounting.actions.acctier3

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.service.AccTier3Service
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import com.athena.mis.accounting.utility.AccTier3CacheUtility
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update AccTier3 object and show on grid list
 *  For details go through Use-Case doc named 'UpdateAccTier3ActionService'
 */
class UpdateAccTier3ActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    AccTier3Service accTier3Service
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTier3CacheUtility accTier3CacheUtility

    private static final String UPDATE_FAILURE_MESSAGE = "Fail to update Tier-3"
    private static final String UPDATE_SUCCESS_MESSAGE = "Tier-3 has been updated successfully"
    private static final String ONLY_NAME_UPDATE_MESSAGE = ", only name can be updated"
    private static final String NAME_EXISTS = "Same Tier-3 name already exists"
    private static final String HAS_ASSOCIATION_MESSAGE = " chart of account(s) are associated with this tier-3"
    private static final String ACC_TIER3 = "accTier3"

    /**
     * Check different criteria to update accTier3 object
     *      1) Check existence of same accTier3 name
     *      2) Validate accTier3 object
     *      2) Check association with COA
     * @param params -N/A
     * @param obj -AccTier3 object send from controller
     * @return -a map contains accTier3 object to update
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccTier3 accTier3 = (AccTier3) obj

            AccTier3 existingAccTier3 = accTier3CacheUtility.readByNameForUpdate(accTier3.name, accTier3.id)
            if (existingAccTier3) {//Check existence of same accTier3 name
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }

            //Validate accTier3 object
            accTier3.validate()
            if (accTier3.hasErrors()) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                return result
            }

            AccTier3 oldAccTier3 = (AccTier3) accTier3CacheUtility.read(accTier3.id)

            //Check association with COA
            Map associationResult = (Map) hasAssociation(accTier3)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue() && ((oldAccTier3.accTypeId != accTier3.accTypeId) ||
                    (oldAccTier3.accTier1Id != accTier3.accTier1Id) || (oldAccTier3.accTier2Id != accTier3.accTier2Id))) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE) + ONLY_NAME_UPDATE_MESSAGE)
                return result
            }

            result.put(ACC_TIER3, accTier3)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update accTier3 object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -AccTier3 object send from executePreCondition
     * @return -updated accTier3 object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccTier3 accTier3 = (AccTier3) preResult.get(ACC_TIER3)
            AccTier3 newAccTier3 = accTier3Service.update(accTier3)//update in DB
            //update in cache and keep the data sorted
            accTier3CacheUtility.update(newAccTier3, accTier3CacheUtility.SORT_BY_NAME, accTier3CacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_TIER3, newAccTier3)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated accTier3 to show on grid
     * @param obj -updated accTier3 object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccTier3 accTier3 = (AccTier3) executeResult.get(ACC_TIER3)
            String accTypeName = accTypeCacheUtility.read(accTier3.accTypeId)
            String accTier1Name = accTier1CacheUtility.read(accTier3.accTier1Id)
            String accTier2Name = accTier2CacheUtility.read(accTier3.accTier2Id)
            GridEntity object = new GridEntity()
            object.id = accTier3.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accTier3.id,
                    accTier3.name ? accTier3.name : Tools.EMPTY_SPACE,
                    accTypeName ? accTypeName : Tools.EMPTY_SPACE,
                    accTier1Name ? accTier1Name : Tools.EMPTY_SPACE,
                    accTier2Name ? accTier2Name : Tools.EMPTY_SPACE,
                    accTier3.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accTier3.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(ACC_TIER3, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Check association with AccChartOfAccount
     * @param accTier3 -AccTier3
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(AccTier3 accTier3) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        int accTier3Id = accTier3.id
        int count = AccChartOfAccount.countByTier3(accTier3Id)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}