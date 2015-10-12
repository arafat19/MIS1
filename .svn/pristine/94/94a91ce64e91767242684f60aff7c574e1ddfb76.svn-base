package com.athena.mis.exchangehouse.actions.remittancepurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhRemittancePurpose
import com.athena.mis.exchangehouse.service.ExhRemittancePurposeService
import com.athena.mis.exchangehouse.utility.ExhRemittancePurposeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhUpdateRemittancePurposeActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())
    // constants
    private static String UPDATE_FAILURE_MSG = "Remittance Purpose could not be updated"
    private static String UPDATE_SUCCESS_MSG = "Remittance Purpose has been updated successfully"
    private static final String NOT_FOUND_MSG = "Selected remittance purpose not found"
    private static final String REMITTANCE_PURPOSE_OBJ = "remittancePurposeObj"

    ExhRemittancePurposeService exhRemittancePurposeService

    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                            // set default
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!parameterMap.id) {                                             // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long remittancePurposeId = Long.parseLong(parameterMap.id.toString())
            ExhRemittancePurpose oldRemittancePurpose = (ExhRemittancePurpose) exhRemittancePurposeCacheUtility.read(remittancePurposeId)     // get bank object from cache utitlity

            if (!oldRemittancePurpose) {                                                  // check whether selected bank object exists or not
                result.put(Tools.MESSAGE, NOT_FOUND_MSG)
                return result
            }
            ExhRemittancePurpose remittancePurpose = buildExhRemittancePurpose(parameterMap, oldRemittancePurpose)             // build bank object for update

            result.put(REMITTANCE_PURPOSE_OBJ, remittancePurpose)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }

    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj                    // cast map returned from executePreCondition method
            ExhRemittancePurpose remittancePurpose = (ExhRemittancePurpose) preResult.get(REMITTANCE_PURPOSE_OBJ)
            int updateCount = exhRemittancePurposeService.update(remittancePurpose)
            exhRemittancePurposeCacheUtility.update(remittancePurpose, exhRemittancePurposeCacheUtility.DEFAULT_SORT_PROPERTY, exhRemittancePurposeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(REMITTANCE_PURPOSE_OBJ, remittancePurpose)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(UPDATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     *  do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj                   // cast map returned from execute method
            ExhRemittancePurpose remittancePurpose = (ExhRemittancePurpose) executeResult.get(REMITTANCE_PURPOSE_OBJ)             // get bank object from executeResult
            GridEntity object = new GridEntity()                                // build grid entity object
            object.id = remittancePurpose.id;
            object.cell = [
                    Tools.LABEL_NEW,
                    remittancePurpose.id,
                    remittancePurpose.name,
                    remittancePurpose.code? remittancePurpose.code : Tools.EMPTY_SPACE
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, remittancePurpose.version)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }
    /**
     * Builds UI specific object on failure;
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap objMap = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, objMap.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    private ExhRemittancePurpose buildExhRemittancePurpose(GrailsParameterMap parameterMap, ExhRemittancePurpose oldRemittancePurpose) {
        ExhRemittancePurpose newRemittancePurpose = new ExhRemittancePurpose(parameterMap)
        oldRemittancePurpose.name = newRemittancePurpose.name
        oldRemittancePurpose.code=newRemittancePurpose.code
        return oldRemittancePurpose
    }
}
