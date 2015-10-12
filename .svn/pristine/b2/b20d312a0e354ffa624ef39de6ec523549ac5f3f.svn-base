package com.athena.mis.accounting.actions.acctier3

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
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
 *  Class to create AccTier3 object and show on grid list
 *  For details go through Use-Case doc named 'CreateAccTier3ActionService'
 */
class CreateAccTier3ActionService extends BaseService implements ActionIntf {

    AccTier3Service accTier3Service
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTier3CacheUtility accTier3CacheUtility

    private static final String CREATE_FAILURE_MSG = "Tier3 has not been saved"
    private static final String CREATE_SUCCESS_MSG = "Tier3 has been successfully saved"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create Tier3"
    private static final String NAME_EXISTS = "Tier3 name already exists"
    private static final String ACC_TIER3 = "accTier3"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria for creating new accTier3
     *      1) Check existence of duplicate accTier3 object
     *      2) Validate accTier3 object
     * @param params -N/A
     * @param obj -AccTier3 object send from controller
     * @return -a map contains accTier3 object for create
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccTier3 accTier3 = (AccTier3) obj
            AccTier3 existingAccTier3 = accTier3CacheUtility.readByName(accTier3.name)
            if (existingAccTier3) {//Check existence of duplicate accTier3 object
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }

            //Validate accTier3 object
            accTier3.validate()
            if (accTier3.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR_MSG)
                return result
            }
            result.put(ACC_TIER3, accTier3)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Save accTier3 object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -AccTier3 object send from executePreCondition
     * @return -newly created accTier3 object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccTier3 accTier3 = (AccTier3) preResult.get(ACC_TIER3)
            accTier3Service.create(accTier3)
            accTier3CacheUtility.add(accTier3, accTier3CacheUtility.SORT_BY_NAME, accTier3CacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_TIER3, accTier3)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
     * Wrap newly created accTier3 to show on grid
     * @param obj -newly created accTier3 object from execute method
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
            result.put(ACC_TIER3, resultMap)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }
}
