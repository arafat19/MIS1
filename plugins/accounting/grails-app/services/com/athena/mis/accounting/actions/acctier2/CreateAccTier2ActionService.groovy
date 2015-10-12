package com.athena.mis.accounting.actions.acctier2

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.service.AccTier2Service
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new Tier2 object and show in grid
 *  For details go through Use-Case doc named 'CreateAccTier2ActionService'
 */
class CreateAccTier2ActionService extends BaseService implements ActionIntf {

    AccTier2Service accTier2Service
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String ACC_TIER2_CREATE_FAILURE_MSG = "Tier-2 has not been saved"
    private static final String ACC_TIER2_CREATE_SUCCESS_MSG = "Tier-2 has been successfully saved"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create Tier-2"
    private static final String ACC_TIER2_NAME_EXISTS = "Tier-2 Name already exists"
    private static final String ACC_TIER2 = "accTier2"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Get parameters from UI and build Tier2 object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            AccTier2 accTier2 = buildAccTier2(parameterMap)         // build Tier2 object
            AccTier2 existingAccTier2 = accTier2CacheUtility.readByName(accTier2.name)
            if (existingAccTier2) {
                result.put(Tools.MESSAGE, ACC_TIER2_NAME_EXISTS)
                return result
            }

            accTier2.validate()
            if (accTier2.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR_MSG)
                return result
            }
            result.put(ACC_TIER2, accTier2)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Save tier2 object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj              // cast map returned from executePreCondition method
            AccTier2 accTier2 = (AccTier2) preResult.get(ACC_TIER2)
            AccTier2 newAccTier2 = accTier2Service.create(accTier2)    // save new tier1 object in DB
            // add new tier1 object in cache utility and keep the data sorted
            accTier2CacheUtility.add(accTier2, accTier2CacheUtility.SORT_BY_NAME, accTier2CacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_TIER2, newAccTier2)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER2_CREATE_FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //there are not post condition
        return null
    }
    /**
     * Show newly created tier2 object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj          // cast map returned form execute method
            AccTier2 accTier2 = (AccTier2) executeResult.get(ACC_TIER2)
            String accTypeName = accTypeCacheUtility.read(accTier2.accTypeId)
            String accTier1Name = accTier1CacheUtility.read(accTier2.accTier1Id)
            GridEntity object = new GridEntity()           // build grid object
            object.id = accTier2.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accTier2.id,
                    accTier1Name,
                    accTier2.name,
                    accTypeName,
                    accTier2.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accTier2.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_TIER2_CREATE_SUCCESS_MSG)
            result.put(ACC_TIER2, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER2_CREATE_FAILURE_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj           // cast map returned form previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, ACC_TIER2_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER2_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build tier2 object
     * @param params -serialized parameters from UI
     * @return -new tier2 object
     */
    private AccTier2 buildAccTier2(GrailsParameterMap params) {
        AccTier2 accTier2 = new AccTier2(params);
        accTier2.companyId = accSessionUtil.appSessionUtil.getAppUser().companyId;
        return accTier2
    }
}
