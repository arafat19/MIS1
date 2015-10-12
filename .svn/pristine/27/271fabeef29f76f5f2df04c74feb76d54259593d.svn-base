package com.athena.mis.qs.actions.qsmeasurement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
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
 * Update QS Measurement & show in the grid
 * For details go through Use-Case doc named 'UpdateQsMeasurementActionService'
 */
class UpdateQsMeasurementActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    BudgetPluginConnector budgetImplService
    InventoryPluginConnector inventoryImplService
    QsMeasurementService qsMeasurementService

    @Autowired
    QsSessionUtil qsSessionUtil
    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String QS_UPDATE_SUCCESS = "QS Measurement has been updated successfully"
    private static final String QS_MEASUREMENT_UPDATE_FAILURE = "Could not update QS Measurement"
    private static final String QS_MEASUREMENT_OBJ = "qsMeasurement"
    private static final String QS_MEASUREMENT_NOT_FOUND = "QS Measurement not found"
    private static final String BUDGET_NOT_FOUND_ERROR_MESSAGE = "Budget not found"
    private static final String BUDGET_NOT_BILLABLE = "Budget is not billable"
    private static final String QS_EXISTS = "Qs Measurement of same budget, site and date already exists"

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

            long id = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())

            QsMeasurement oldQsMeasurement = QsMeasurement.read(id)
            if (!oldQsMeasurement || oldQsMeasurement.version != version) {
                result.put(Tools.MESSAGE, QS_MEASUREMENT_NOT_FOUND)
                return result
            }

            QsMeasurement newQsMeasurement = buildQsMeasurementObject(params, oldQsMeasurement)

            int countQsMeasurement = 0
            if ((newQsMeasurement.siteId != oldQsMeasurement.siteId) || (newQsMeasurement.budgetId != oldQsMeasurement.budgetId) || (newQsMeasurement.qsMeasurementDate.compareTo(oldQsMeasurement.qsMeasurementDate) != 0)) {
                countQsMeasurement = checkForExistenceOfQsMeasurement(newQsMeasurement)
                if (countQsMeasurement > 0) {
                    result.put(Tools.MESSAGE, QS_EXISTS)
                    return result
                }
            }

            Object budget = budgetImplService.readBudget(newQsMeasurement.budgetId)
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            if (!budget.billable) {
                result.put(Tools.MESSAGE, BUDGET_NOT_BILLABLE)
                return result
            }

            result.put(QS_MEASUREMENT_OBJ, newQsMeasurement)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, QS_MEASUREMENT_UPDATE_FAILURE)
            return result
        }
    }
    /**
     * 1. receive qsm object from pre execute method
     * 2. update qsm
     * @param parameters - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing budget object, newly built qsm object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            QsMeasurement newQsMeasurement = (QsMeasurement) preResult.get(QS_MEASUREMENT_OBJ)

            int updateQsMeasurement = qsMeasurementService.update(newQsMeasurement)
            if (updateQsMeasurement <= 0) {
                result.put(Tools.MESSAGE, QS_MEASUREMENT_UPDATE_FAILURE)
                return result
            }

            result.put(QS_MEASUREMENT_OBJ, newQsMeasurement)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, QS_MEASUREMENT_UPDATE_FAILURE)
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
            Object budget = budgetImplService.readBudget(qsMeasurement.budgetId)

            Object site = inventoryImplService.readInventory(qsMeasurement.siteId)
            SystemEntity unit = (SystemEntity) unitCacheUtility.read(budget.unitId)

            String qsMeasurementDate = DateUtility.getLongDateForUI(qsMeasurement.qsMeasurementDate)

            GridEntity object = new GridEntity()
            object.id = qsMeasurement.id
            object.cell = [
                    Tools.LABEL_NEW,
                    qsMeasurement.id,
                    budget.budgetItem,
                    site.name,
                    Tools.formatAmountWithoutCurrency(qsMeasurement.quantity) + Tools.SINGLE_SPACE + unit.key,
                    qsMeasurementDate
            ]
            result.put(Tools.MESSAGE, QS_UPDATE_SUCCESS)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, QS_MEASUREMENT_UPDATE_FAILURE)
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
                result.put(Tools.MESSAGE, QS_MEASUREMENT_UPDATE_FAILURE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, QS_MEASUREMENT_UPDATE_FAILURE)
            return result
        }
    }
    /**
     * Get qsMeasurement object
     * @param params - serialized parameters from UI
     * @param oldQsMeasurement - previous state of qsm object
     * @return - newly built qsMeasurement object
     */
    private QsMeasurement buildQsMeasurementObject(GrailsParameterMap params, QsMeasurement oldQsMeasurement) {
        AppUser user = qsSessionUtil.appSessionUtil.getAppUser()
        QsMeasurement newQsMeasurement = new QsMeasurement()

        newQsMeasurement.id = oldQsMeasurement.id
        newQsMeasurement.version = oldQsMeasurement.version
        newQsMeasurement.quantity = Double.parseDouble(params.quantity.toString())
        newQsMeasurement.budgetId = Long.parseLong(params.budgetId.toString())
        newQsMeasurement.projectId = Long.parseLong(params.projectId.toString())
        newQsMeasurement.siteId = Long.parseLong(params.siteId.toString())
        newQsMeasurement.qsMeasurementDate = DateUtility.parseMaskedDate(params.qsMeasurementDate)
        newQsMeasurement.comments = params.comments ? params.comments : null
        newQsMeasurement.updatedBy = user.id
        newQsMeasurement.updatedOn = new Date()
        newQsMeasurement.companyId = user.companyId

        newQsMeasurement.createdBy = oldQsMeasurement.createdBy
        newQsMeasurement.createdOn = oldQsMeasurement.createdOn

        newQsMeasurement.isGovtQs = Boolean.parseBoolean(params.isGovtQs)

        return newQsMeasurement
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
