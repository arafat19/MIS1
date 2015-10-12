package com.athena.mis.qs.actions.qsmeasurement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.qs.entity.QsMeasurement
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Select the object of QS Measurement
class SelectQsMeasurementActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    BudgetPluginConnector budgetImplService

    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to select QS Measurement"
    private static final String QS_MEASUREMENT_NOT_FOUND = "QS Measurement not found"
    private static final String QS_MEASUREMENT_OBJ = "qsMeasurement"
    private static final String QS_MEASUREMENT_MAP = "qsMeasurementMap"

    /**
     * Get qsm object by id
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap paramsMap = (GrailsParameterMap) params
            long id = Long.parseLong(paramsMap.id.toString())

            QsMeasurement qsMeasurement = QsMeasurement.read(id)
            if (!qsMeasurement) {
                result.put(Tools.MESSAGE, QS_MEASUREMENT_NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(QS_MEASUREMENT_OBJ, qsMeasurement)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * @param parameters -N/A
     * @param obj - receive from pre execute method
     * @return  - qsm object
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            QsMeasurement qsMeasurement = (QsMeasurement) preResult.get(QS_MEASUREMENT_OBJ)
            result.put(QS_MEASUREMENT_OBJ, qsMeasurement)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Show selected object on the form
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            QsMeasurement qsMeasurement = (QsMeasurement) receiveResult.get(QS_MEASUREMENT_OBJ)
            Map qsMeasurementMap = buildQSMeasurementMap(qsMeasurement)
            result.put(Tools.ENTITY, qsMeasurement)
            result.put(Tools.VERSION, qsMeasurement.version)
            result.put(QS_MEASUREMENT_MAP, qsMeasurementMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     *  Get qsMeasurement object
     * @param qsMeasurement - serialized parameters from UI
     * @return - newly built qsMeasurement object
     */
    private Map buildQSMeasurementMap(QsMeasurement qsMeasurement) {
        Object budget = budgetImplService.readBudget(qsMeasurement.budgetId)
        Project project = (Project) projectCacheUtility.read(budget.projectId)
        SystemEntity unit = (SystemEntity) unitCacheUtility.read(budget.unitId)
        Map qsMeasurementMap = [
                budgetLineItem: budget.budgetItem,
                projectId: project.id,
                projectName: project.name,
                unitName: unit.key,
                qsMeasurementDate: DateUtility.getDateForUI(qsMeasurement.qsMeasurementDate)
        ]
        return qsMeasurementMap
    }
}