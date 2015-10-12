package com.athena.mis.qs.actions.qsmeasurement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.qs.entity.QsMeasurement
import com.athena.mis.qs.service.QsMeasurementService
import com.athena.mis.qs.utility.QsSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create QS Measurement & show in the grid
 * For details go through Use-Case doc named 'CreateQsMeasurementActionService'
 */
class CreateQsMeasurementActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String SAVE_SUCCESS_MESSAGE = "QS Measurement has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Can not saved QS Measurement "
    private static final String BUDGET_OBJ = "budgetObj"
    private static final String QS_MEASUREMENT_OBJ = "qsMeasurement"
    private static final String BUDGET_NOT_FOUND_ERROR_MESSAGE = "Budget not found"
    private static final String BUDGET_NOT_BILLABLE = "Budget is not billable"
    private static final String QS_EXISTS = "Qs Measurement of same budget, site and date already exists"

    QsMeasurementService qsMeasurementService
    BudgetPluginConnector budgetImplService
    InventoryPluginConnector inventoryImplService

    @Autowired
    QsSessionUtil qsSessionUtil
    @Autowired
    UnitCacheUtility unitCacheUtility
    /**
     * 1. build qsm object
     * 2. check qsm existence
     * 3. pull budget object
     * 4. check budget billable or not
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget object, qsm object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            QsMeasurement newQSMObj = buildQsMeasurementObject(params)

            int countQsMeasurement = checkForExistenceOfQsMeasurement(newQSMObj)
            if (countQsMeasurement > 0) {
                result.put(Tools.MESSAGE, QS_EXISTS)
                return result
            }

            Object budget = budgetImplService.readBudget(newQSMObj.budgetId)
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (!budget.billable) {
                result.put(Tools.MESSAGE, BUDGET_NOT_BILLABLE)
                return result
            }

            result.put(BUDGET_OBJ, budget)
            result.put(QS_MEASUREMENT_OBJ, newQSMObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive qsm object from pre execute method
     * 2. create qsm
     * @param parameters - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing budget object, newly built qsm object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            QsMeasurement qsMeasurementObj = (QsMeasurement) preResult.get(QS_MEASUREMENT_OBJ)

            QsMeasurement newQsMeasurement = qsMeasurementService.create(qsMeasurementObj)
            if (!newQsMeasurement) {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
                return result
            }
            result.put(BUDGET_OBJ, preResult.get(BUDGET_OBJ))
            result.put(QS_MEASUREMENT_OBJ, newQsMeasurement)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
     * Wrap qsm object for grid show
     * @param obj - object receive from previous method
     * @return - grid entity
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj

            QsMeasurement qsMeasurement = (QsMeasurement) executeResult.get(QS_MEASUREMENT_OBJ)
            Object budget = executeResult.get(BUDGET_OBJ)

            GridEntity object = new GridEntity()
            SystemEntity unit = (SystemEntity) unitCacheUtility.read(budget.unitId)
            Object site = inventoryImplService.readInventory(qsMeasurement.siteId)
            String qsMeasurementDate = DateUtility.getLongDateForUI(qsMeasurement.qsMeasurementDate)

            object.id = qsMeasurement.id
            object.cell = [
                    Tools.LABEL_NEW,
                    qsMeasurement.id,
                    budget.budgetItem,
                    site.name,
                    Tools.formatAmountWithoutCurrency(qsMeasurement.quantity) + Tools.SINGLE_SPACE + unit.key,
                    qsMeasurementDate
            ]
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     *  Get qsMeasurement object
     * @param params - serialized parameters from UI
     * @return - newly built qsMeasurement object
     */
    private QsMeasurement buildQsMeasurementObject(GrailsParameterMap params) {
        AppUser user = qsSessionUtil.appSessionUtil.getAppUser()
        QsMeasurement qsMeasurement = new QsMeasurement(params)

        qsMeasurement.version = 0
        qsMeasurement.budgetId = Long.parseLong(params.budgetId.toString())
        qsMeasurement.projectId = Long.parseLong(params.projectId.toString())
        qsMeasurement.quantity = Double.parseDouble(params.quantity.toString())
        qsMeasurement.comments = params.comments ? params.comments : null
        qsMeasurement.siteId = Long.parseLong(params.siteId.toString())
        qsMeasurement.qsMeasurementDate = DateUtility.parseMaskedDate(params.qsMeasurementDate)
        qsMeasurement.companyId = user.companyId
        qsMeasurement.createdBy = user.id
        qsMeasurement.createdOn = new Date()
        qsMeasurement.updatedBy = 0L
        qsMeasurement.updatedOn = null
        qsMeasurement.isGovtQs = Boolean.parseBoolean(params.isGovtQs)

        return qsMeasurement
    }

    private static final String COUNT_QUERY = """
            SELECT COUNT(qsm.id) AS count
            FROM qs_measurement qsm
            WHERE qsm.budget_id =:budgetId AND
                  qsm.site_id =:siteId AND
                  qsm.qs_measurement_date =:qsMeasurementDate AND
                  qsm.is_govt_qs =:isGovtQs
        """
    //For checking existing QS Measurement
    private int checkForExistenceOfQsMeasurement(QsMeasurement qsMeasurement) {
        //@todo:model adjust using dynamic finder
        Map queryParams = [
                budgetId: qsMeasurement.budgetId,
                siteId: qsMeasurement.siteId,
                isGovtQs: qsMeasurement.isGovtQs,
                qsMeasurementDate: DateUtility.getSqlDate(qsMeasurement.qsMeasurementDate)
        ]
        List resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = resultCount[0].count

        return count
    }
}

