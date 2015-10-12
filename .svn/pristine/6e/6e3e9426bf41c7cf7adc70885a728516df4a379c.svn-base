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
 *  Create new ProcessInstrumentMapping object
 *  For details go through Use-Case doc named 'CreateRmsProcessInstrumentMappingActionService'
 */
class CreateRmsProcessInstrumentMappingActionService extends BaseService implements ActionIntf {

    RmsProcessInstrumentMappingService rmsProcessInstrumentMappingService
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String ALREADY_EXIST = "Already exists"
    private static final String PRO_INS_MAPPING_OBJ = "proInsMapping"
    private static final String PRO_INS_MAPPING_CREATE_FAILURE_MSG = "Process Instrument Mapping has not been saved"
    private static final String PRO_INS_MAPPING_CREATE_SUCCESS_MSG = "Process Instrument Mapping has been successfully saved"

    /**
     * 1. Get parameters from UI and build ProcessInstrumentMapping object
     * 2. Check whether the object is already exists or not
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
            // build ProcessInstrumentMapping object
            RmsProcessInstrumentMapping proInsMapping = buildProInsMappingObject(parameterMap)
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            int count = rmsProcessInstrumentMappingService.countByProcessTypeAndInstrumentTypeAndCompanyId(proInsMapping, companyId)
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
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_CREATE_FAILURE_MSG)
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
     * Save ProcessInstrumentMapping object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from executePreCondition method
            RmsProcessInstrumentMapping proInsMapping = (RmsProcessInstrumentMapping) preResult.get(PRO_INS_MAPPING_OBJ)
            // save new ProcessInstrumentMapping object in DB
            RmsProcessInstrumentMapping savedProInsMappingObj = rmsProcessInstrumentMappingService.create(proInsMapping)
            result.put(PRO_INS_MAPPING_OBJ, savedProInsMappingObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build grid object to show a single row (newly created object) in grid
     * 1. Get processType key by id(processTypeId)
     * 2. Get instrumentType key by id(instrumentTypeId)
     * 3. build success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
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
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_CREATE_FAILURE_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj               // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRO_INS_MAPPING_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build new ProcessInstrumentMapping object
     * @param parameterMap -serialized parameters from UI
     * @return -new ProcessInstrumentMapping object
     */
    private RmsProcessInstrumentMapping buildProInsMappingObject(GrailsParameterMap parameterMap) {
        RmsProcessInstrumentMapping proInsMapping = new RmsProcessInstrumentMapping(parameterMap)
        proInsMapping.companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        return proInsMapping
    }
}
