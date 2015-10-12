package com.athena.mis.application.actions.systementity

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.*
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.projecttrack.ProjectTrackPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new system entity object, add to respective cache utility & show in grid
 *  For details go through Use-Case doc named 'CreateSystemEntityActionService'
 */
class CreateSystemEntityActionService extends BaseService implements ActionIntf {

    private static final String SYSTEM_ENTITY = "system_entity"
    private static final String CREATE_FAILURE_MSG = "System entity information has not been saved"
    private static final String CREATE_SUCCESS_MSG = "System entity information has been successfully saved"
    private static final String INVALID_ENTITY_MSG = "Invalid entity type"
    private static final String DUPLICATE_ENTITY_MSG = "Same system entity already exists"
    private static final String TYPE_NOT_FOUND_MSG = "System entity type not found"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"

    private final Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    ProjectTrackPluginConnector projectTrackImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    SystemEntityTypeCacheUtility systemEntityTypeCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired
    OwnerTypeCacheUtility ownerTypeCacheUtility
    @Autowired
    PaymentMethodCacheUtility paymentMethodCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    GenderCacheUtility genderCacheUtility

    /**
     * Check pre conditions before creating system entity
     * 1. Check if user has access to create system entity or not
     * 2. Check required params existence
     * 3. Check if system entity type object exists or not
     * 4. Build SystemEntity object with parameters
     * 5. Validate the system entity object
     * 6. Check uniqueness of system entity name by type and company
     * 7. Check if system entity type is valid or not
     * @param parameters - serialized params from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for execute
     * map contains isError(true/false) and hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)   // default value
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)  // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // Only development role type user can create system entity
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            // Check here for required params are present
            if ((!parameterMap.systemEntityTypeId)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long systemEntityTypeId = Long.parseLong(parameterMap.systemEntityTypeId.toString())
            // Check if system entity type object exists or not
            SystemEntityType systemEntityType = (SystemEntityType) systemEntityTypeCacheUtility.read(systemEntityTypeId)
            if (!systemEntityType) {
                result.put(Tools.MESSAGE, TYPE_NOT_FOUND_MSG)
                return result
            }
            // Build SystemEntity object with parameters
            SystemEntity systemEntity = buildSystemEntityObject(parameterMap, systemEntityType)
            // Validate the system entity object as per domain field type
            systemEntity.validate()
            if (systemEntity.hasErrors()) {
                result.put(Tools.IS_VALID, Boolean.FALSE)
                return result
            }
            // Uniqueness check of system entity name
            int duplicateCount = systemEntityService.countByTypeAndKeyIlikeAndCompanyId(systemEntity.type, systemEntity.key, systemEntity.companyId)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, DUPLICATE_ENTITY_MSG)
                return result
            }
            // Check the type of system entity
            int typeCount = systemEntityService.countByType(systemEntity.type)
            if (typeCount == 0) {
                result.put(Tools.MESSAGE, INVALID_ENTITY_MSG)
                return result
            }
            result.put(SYSTEM_ENTITY, systemEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
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
     * Save system entity object in DB and update cache utility
     * 1. Get system entity object from executePreCondition
     * 2. Update cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - map returned from executePreCondition method
     * @return - saved system entity
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)   // default value
            LinkedHashMap preResult = (LinkedHashMap) obj // cast map returned from executePreCondition method
            SystemEntity sysEntity = (SystemEntity) preResult.get(SYSTEM_ENTITY)
            SystemEntity returnSysEntity = systemEntityService.create(sysEntity)
            // save new SystemEntity object in DB
            updateCacheUtility(returnSysEntity) // add new SystemEntity object in respective cache utility
            result.put(SYSTEM_ENTITY, returnSysEntity)
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
     * Show newly created system entity object in grid
     * Show success message
     * @param obj - object of system entity
     * @return - a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            SystemEntity sysEntity = (SystemEntity) receiveResult.get(SYSTEM_ENTITY)
            GridEntity object = new GridEntity()
            object.id = sysEntity.id
            object.cell = [
                    Tools.LABEL_NEW,
                    sysEntity.id,
                    sysEntity.key,
                    sysEntity.value,
                    sysEntity.isActive ? Tools.YES : Tools.NO,
                    sysEntity.reservedId > 0 ? Tools.YES : Tools.NO,
            ]

            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
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
     * @return -a map containing isError = true & relevant error message
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

    /**
     * Update cache utility
     * @param returnSysEntity - object of SystemEntity
     * @return - a boolean value
     */
    private boolean updateCacheUtility(SystemEntity returnSysEntity) {
        switch (returnSysEntity.type) {
            case systemEntityTypeCacheUtility.TYPE_PAYMENT_METHOD:
                paymentMethodCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_SOURCE:
                accountingImplService?.initAccSourceCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS:
                budgetImplService?.initBudgetTaskStatusCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_VOUCHER:
                accountingImplService?.initAccVoucherTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_INV_PRODUCTION_LINE_ITEM_TYPE:
                inventoryImplService?.initInvProductionItemTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_UNIT:
                unitCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE:
                inventoryImplService?.initInvTransactionTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_ENTITY_TYPE:
                inventoryImplService?.initInvTransactionEntityTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_VALUATION_TYPE:
                valuationTypeCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE:
                inventoryImplService?.initInvInventoryTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_OWNER_TYPE:
                ownerTypeCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY:
                contentEntityTypeCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_CONTENT_TYPE:
                contentTypeCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_NOTE_ENTITY:
                noteEntityTypeCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_SUPPLIER_TYPE:
                supplierTypeCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_ITEM_CATEGORY:
                itemCategoryCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_INSTRUMENT_TYPE:
                accountingImplService?.initAccInstrumentTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE:
                appUserEntityTypeCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_PAID_BY:
                exchangeHouseImplService?.initExhPaidByTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD:
                exchangeHouseImplService?.initExhPaymentMethodCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS:
                exchangeHouseImplService?.initExhTaskStatusCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_TASK_TYPE:
                exchangeHouseImplService?.initExhTaskTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.PT_BACKLOG_PRIORITY:
                projectTrackImplService?.initBacklogPriorityCacheUtility()
                break
            case systemEntityTypeCacheUtility.PT_BACKLOG_STATUS:
                projectTrackImplService?.initBacklogStatusCacheUtility()
                break
            case systemEntityTypeCacheUtility.PT_SPRINT_STATUS:
                projectTrackImplService?.initSprintStatusCacheUtility()
                break
            case systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_STATUS:
                projectTrackImplService?.initAcceptanceCriteriaStatusCacheUtility()
                break
            case systemEntityTypeCacheUtility.PT_BUG_SEVERITY:
                projectTrackImplService?.initBugSeverityCacheUtility()
                break
            case systemEntityTypeCacheUtility.PT_BUG_STATUS:
                projectTrackImplService?.initBugStatusCacheUtility()
                break
            case systemEntityTypeCacheUtility.PT_BUG_TYPE:
                projectTrackImplService?.initBugTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_TYPE:
                projectTrackImplService?.initAcceptanceCriteriaTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.ARMS_PROCESS_TYPE:
                armsImplService?.initProcessTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE:
                armsImplService?.initInstrumentTypeCacheUtility()
                break
            case systemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD:
                armsImplService?.initPaymentMethodCacheUtility()
                break
            case systemEntityTypeCacheUtility.ARMS_TASK_STATUS:
                armsImplService?.initTaskStatusCacheUtility()
                break
            case systemEntityTypeCacheUtility.TYPE_GENDER:
                genderCacheUtility.init()
                break
            case systemEntityTypeCacheUtility.SARB_TASK_REVISE_STATUS:
                sarbImplService.initSarbTaskReviseStatus()
                break
            default:
                return false
        }
    }

    /**
     * Build system entity object
     * @param params - serialized params from UI
     * @param systemEntityType - object of SystemEntityType
     * @return - SystemEntity object
     */
    private SystemEntity buildSystemEntityObject(GrailsParameterMap params, SystemEntityType systemEntityType) {

        SystemEntity systemEntity = new SystemEntity(params)
        long companyId = appSessionUtil.getCompanyId()
        long pluginId = systemEntityType.pluginId
        long systemEntityId = systemEntityService.getSystemEntityId(companyId, pluginId)
        systemEntity.id = systemEntityId
        systemEntity.type = systemEntityType.id
        systemEntity.companyId = companyId
        systemEntity.reservedId = 0
        systemEntity.pluginId = pluginId
        return systemEntity
    }
}
