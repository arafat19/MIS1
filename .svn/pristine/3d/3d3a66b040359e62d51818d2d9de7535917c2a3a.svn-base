package com.athena.mis.accounting.actions.acctier1

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.service.AccTier1Service
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new Tier1 object and show in grid
 *  For details go through Use-Case doc named 'CreateAccTier1ActionService'
 */
class CreateAccTier1ActionService extends BaseService implements ActionIntf {

    AccTier1Service accTier1Service
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String ACC_TIER1_CREATE_FAILURE_MSG = "Tier1 has not been saved"
    private static final String ACC_TIER1_CREATE_SUCCESS_MSG = "Tier1 has been successfully saved"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create Tier1"
    private static final String ACC_TIER1_NAME_EXISTS = "Tier-1 name already exists"
    private static final String NEW = "New"
    private static final String ACC_TIER1 = "accTier1"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build Tier1 object
     * @param params -serialized parameters from UI
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
            AccTier1 accTier1 = buildAccTier1(parameterMap)   // build Tier1 object
            AccTier1 existingAccTier1 = accTier1CacheUtility.readByName(accTier1.name)
            if (existingAccTier1) {
                result.put(Tools.MESSAGE, ACC_TIER1_NAME_EXISTS)
                return result
            }
            accTier1.validate()
            if (accTier1.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR_MSG)
                return result
            }
            result.put(ACC_TIER1, accTier1)
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
     * Save tier1 object in DB and update cache utility accordingly
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
            LinkedHashMap preResult = (LinkedHashMap) obj      // cast map returned from executePreCondition method
            AccTier1 accTier1 = (AccTier1) preResult.get(ACC_TIER1)
            AccTier1 newAccTier1 = accTier1Service.create(accTier1)    // save new tier1 object in DB
            // add new tier1 object in cache utility and keep the data sorted
            accTier1CacheUtility.add(accTier1, accTier1CacheUtility.SORT_BY_NAME, accTier1CacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_TIER1, newAccTier1)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER1_CREATE_FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //there are not post condition
        return null
    }

    /**
     * Show newly created tier1 object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj      // cast map returned form execute method
            AccTier1 accTier1 = (AccTier1) executeResult.get(ACC_TIER1)
            String accTypeName = accTypeCacheUtility.read(accTier1.accTypeId)
            GridEntity object = new GridEntity()   // build grid object
            object.id = accTier1.id
            object.cell = [
                    NEW,
                    accTier1.id,
                    accTier1.name ? accTier1.name : Tools.EMPTY_SPACE,
                    accTypeName ? accTypeName : Tools.EMPTY_SPACE,
                    accTier1.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accTier1.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_TIER1_CREATE_SUCCESS_MSG)
            result.put(ACC_TIER1, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER1_CREATE_FAILURE_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj    // cast map returned form previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, ACC_TIER1_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER1_CREATE_FAILURE_MSG)
            return result
        }
    }
    /**
     * Build tier1 object
     * @param params -serialized parameters from UI
     * @return -new tier1 object
     */
    private AccTier1 buildAccTier1(GrailsParameterMap params) {
        AccTier1 accTier1 = new AccTier1(params)
        accTier1.companyId = accSessionUtil.appSessionUtil.getCompanyId()
        return accTier1
    }
}
