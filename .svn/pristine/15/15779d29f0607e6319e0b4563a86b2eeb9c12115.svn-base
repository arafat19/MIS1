package com.athena.mis.arms.actions.rmsprocessinstrumentmapping

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsProcessInstrumentMapping
import com.athena.mis.arms.service.RmsProcessInstrumentMappingService
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update ProcessInstrumentMapping object
 *  For details go through Use-Case doc named 'UpdateRmsProcessInstrumentMappingActionService'
 */
class UpdateRmsProcessInstrumentMappingActionService extends BaseService implements ActionIntf {

    RmsProcessInstrumentMappingService rmsProcessInstrumentMappingService
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String OBJ_NOT_FOUND = "Selected process instrument mapping not found"
    private static final String ALREADY_EXIST = "Already exists"
    private static final String PRO_INS_MAPPING_OBJ = "proInsMapping"
    private static final String PRO_INS_MAPPING_UPDATE_FAILURE_MESSAGE = "Process instrument mapping could not be updated"
    private static final String PRO_INS_MAPPING_UPDATE_SUCCESS_MESSAGE = "Process instrument mapping has been updated successfully"


    /**
     * Get parameters from UI and build ProcessInstrumentMapping object for update
     * 1. Check validity for input
     * 2. Check existence of ProcessInstrumentMapping object
     * 3. Check whether the object is already exists or not
     * 4. Build ProcessInstrumentMapping object with new parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long proInsMappingId = Long.parseLong(parameterMap.id)
            // Get ProcessInstrumentMapping object by id from DB
            RmsProcessInstrumentMapping oldProInsMapping = rmsProcessInstrumentMappingService.read(proInsMappingId)
            if (!oldProInsMapping) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            //Build ProcessInstrumentMapping object with new parameters
            RmsProcessInstrumentMapping proInsMapping = buildProInsMappingObject(parameterMap, oldProInsMapping)
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            int count = rmsProcessInstrumentMappingService.countByProcessTypeAndInstrumentTypeAndCompanyIdAndIdNotEqual(proInsMapping, companyId)
            // Check whether the object is already exists or not
            if(count > 0) {
                result.put(Tools.MESSAGE, ALREADY_EXIST)
                return result
            }
            result.put(PRO_INS_MAPPING_OBJ, proInsMapping)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Update ProcessInstrumentMapping object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj               // cast map returned from executePreCondition method
            RmsProcessInstrumentMapping processInstrumentMapping = (RmsProcessInstrumentMapping) preResult.get(PRO_INS_MAPPING_OBJ)
            rmsProcessInstrumentMappingService.update(processInstrumentMapping)      // update new ProcessInstrumentMapping object in DB
            result.put(PRO_INS_MAPPING_OBJ, processInstrumentMapping)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build grid object to show a single row (updated object) in grid
     * 1. Get processType key by id(processTypeId)
     * 2. Get instrumentType key by id(instrumentTypeId)
     * 3. build success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj               // cast map returned from execute method
            RmsProcessInstrumentMapping proInsMapping = (RmsProcessInstrumentMapping) executeResult.get(PRO_INS_MAPPING_OBJ)
            long processTypeId = proInsMapping.processType
            // Pull SystemEntity object by id for processType
            SystemEntity processType = (SystemEntity) rmsProcessTypeCacheUtility.read(processTypeId)
            long instrumentTypeId = proInsMapping.instrumentType
            // Pull SystemEntity object by id for instrumentType
            SystemEntity instrumentType = (SystemEntity) rmsInstrumentTypeCacheUtility.read(instrumentTypeId)
            GridEntity object = new GridEntity()                            // build grid object
            object.id = proInsMapping.id
            object.cell = [
                    Tools.LABEL_NEW,
                    proInsMapping.id,
                    processType.key,
                    instrumentType.key
            ]
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, proInsMapping.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj           // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Make existing/old object Up-to-date with new parameters
     * @param parameterMap -serialized parameters from UI
     * @param oldProInsMapping -old ProcessInstrumentMapping object
     * @return -updated ProcessInstrumentMapping object
     */
    private RmsProcessInstrumentMapping buildProInsMappingObject(GrailsParameterMap parameterMap, RmsProcessInstrumentMapping oldProInsMapping) {
        RmsProcessInstrumentMapping newProInsMapping = new RmsProcessInstrumentMapping(parameterMap)
        oldProInsMapping.processType = newProInsMapping.processType
        oldProInsMapping.instrumentType = newProInsMapping.instrumentType
        return oldProInsMapping
    }
}
