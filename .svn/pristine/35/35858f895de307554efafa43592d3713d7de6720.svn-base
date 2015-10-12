package com.athena.mis.qs.actions.qsmeasurement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.qs.entity.QsMeasurement
import com.athena.mis.qs.service.QsMeasurementService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Delete QS Measurement & delete from the grid
 * For details go through Use-Case doc named 'DeleteQsMeasurementActionService'
 */
class DeleteQsMeasurementActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    QsMeasurementService qsMeasurementService

    private static final String DELETE_SUCCESS_MESSAGE = "QS Measurement has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "QS Measurement could not be deleted"
    private static final String QS_MEASUREMENT_NOT_FOUND = "QS Measurement not found"
    private static final String QS_DETAILS_OBJ = "qsMeasurement"
    private static final String DELETED = "deleted"

    /**
     * 1. pull qsm object
     * 2. check qsm existence
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing qsm object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long id = Long.parseLong(params.id.toString())
            QsMeasurement qsMeasurement = QsMeasurement.read(id)
            if (!qsMeasurement) {
                result.put(Tools.MESSAGE, QS_MEASUREMENT_NOT_FOUND)
                return result
            }
            result.put(QS_DETAILS_OBJ, qsMeasurement)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * 1. receive qsm object from pre execute method
     * 2. delete qsm
     * @param parameters - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing isError(True/False) & relevant msg
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj

            QsMeasurement qsMeasurement = (QsMeasurement) receiveResult.get(QS_DETAILS_OBJ)
            int deleteStatus = qsMeasurementService.delete(qsMeasurement)
            if (deleteStatus <= 0) {
                result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Set delete operation True
     * @param obj- N/A
     * @return - a map containing deleted=True and success msg
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
}