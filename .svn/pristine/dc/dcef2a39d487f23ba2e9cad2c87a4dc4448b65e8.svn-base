package com.athena.mis.application.actions.systementity

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.*
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
 *  Delete system entity object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteSystemEntityActionService'
 */
class DeleteSystemEntityActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    SupplierService supplierService
    ItemService itemService
    ItemCacheUtility itemCacheUtility
    SystemEntityService systemEntityService
    AppUserEntityService appUserEntityService
    EntityContentService entityContentService
    EntityNoteService entityNoteService
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
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    GenderCacheUtility genderCacheUtility

    private static final String SYS_ENTITY_DELETE_SUCCESS_MSG = "System entity has been successfully deleted"
    private static final String SYS_ENTITY_DELETE_FAILURE_MSG = "System entity has not been deleted"
    private final static String DELETED = "deleted"
    private final static String SYSTEM_ENTITY_OBJECT = "systemEntity"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete system entity"
    private static final String IS_RESERVED_MSG = "The selected system entity is reserved"
    private static final String HAS_ASSOCIATION_PURCHASE_ORDER = " purchase order(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_BUDGET = " budget(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_COA = " chart of account(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_FXD_ASSET_DETAILS = " fixed asset details(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_VOUCHER = " voucher(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_VOUCHER_TYPE_COA = " voucher type coa(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_ENTITY_CONTENT = " entity content(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_ENTITY_NOTE = " entity note(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_CONTENT_CATEGORY = " content category(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_SUPPLIER = " supplier(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_ITEM = " item(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_PRODUCTION_DETAILS = " production details(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_INVENTORY_TRANSACTION = " inventory transaction(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_INVENTORY = " inventory(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_USER_ENTITY = " user entity mapping(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_EXH_TASK = " exh task(s) associated with selected system entity"
    private static final String HAS_ASSOCIATION_SARB_TASK = " sarb task(s) associated with selected system entity"

    /**
     * Check pre conditions before deleting the system entity object
     * 1. Check if user has access to delete system entity or not
     * 2. Pull system entity object
     * 3. Check the existence of system entity object
     * 4. Check the selected system entity object is Reserved or not
     * 5. Check association with other domains
     * @param parameters - parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE) // default value
            result.put(Tools.IS_ERROR, Boolean.TRUE)   // default value

            // Only development role type user can create system entity
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long systemEntityId = Long.parseLong(params.id.toString())
            SystemEntity systemEntity = systemEntityService.read(systemEntityId)
            // check the existence of system entity object
            if (!systemEntity) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            // check the system entity is reserved or not
            if (systemEntity.reservedId > 0) {
                result.put(Tools.MESSAGE, IS_RESERVED_MSG)
                return result
            }

            long companyId = appSessionUtil.getCompanyId()
            Map preResult = (Map) hasAssociation(systemEntity, companyId)
            // check the association of the selected system entity object
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(SYSTEM_ENTITY_OBJECT, systemEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Delete system entity object from DB and cache utility
     * 1. This method is in transactional block and will roll back in case of any exception
     * 2. Delete system entity
     * 3. Update cache utility
     * @param params - N/A
     * @param obj - object from executePreCondition method
     * @return - a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj
            SystemEntity systemEntity = (SystemEntity) preResult.get(SYSTEM_ENTITY_OBJECT)
            systemEntityService.delete(systemEntity.id)
            updateCacheUtility(systemEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return - a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, SYS_ENTITY_DELETE_SUCCESS_MSG)
        return result
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return - a map containing isError = true & relevant error message
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
            result.put(Tools.MESSAGE, SYS_ENTITY_DELETE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SYS_ENTITY_DELETE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Update several domains cache utility
     * @param sysEntity - object of SystemEntity
     * @return - a boolean value
     */
    private boolean updateCacheUtility(SystemEntity sysEntity) {
        switch (sysEntity.type) {
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
     * Association check with different domains
     * @param systemEntity - object of SystemEntity
     * @param companyId - company id
     * @return - a map containing hasAssociation(true/false) depending on method success
     */
    private LinkedHashMap hasAssociation(SystemEntity systemEntity, long companyId) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        long systemEntityId = systemEntity.id
        long systemEntityTypeId = systemEntity.type
        Integer count = 0

        switch (systemEntityTypeId) {
            case systemEntityTypeCacheUtility.TYPE_PAYMENT_METHOD:
                if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
                    count = countPurchaseOrder(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_PURCHASE_ORDER)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_SOURCE:
                if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
                    count = countCOAForSourceType(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_COA)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_VOUCHER:
                if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
                    count = countVoucherForVoucherType(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_VOUCHER)
                        return result
                    }

                    count = countVoucherTypeCOAForVoucherType(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_VOUCHER_TYPE_COA)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_INV_PRODUCTION_LINE_ITEM_TYPE:
                if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
                    count = countProductionDetails(systemEntityId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_PRODUCTION_DETAILS)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_UNIT:
                if (PluginConnector.isPluginInstalled(PluginConnector.BUDGET)) {
                    count = countBudget(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_BUDGET)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE:
                if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
                    count = countInventoryTransaction(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_INVENTORY_TRANSACTION)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_ENTITY_TYPE:
                if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
                    count = countInventoryTransactionForEntityType(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_INVENTORY_TRANSACTION)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_VALUATION_TYPE:
                count = itemCacheUtility.countByValuationTypeId(systemEntityId)
                if (count > 0) {
                    result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_ITEM)
                    return result
                }
                break
            case systemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE:
                if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
                    count = countInventory(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_INVENTORY)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_OWNER_TYPE:
                if (PluginConnector.isPluginInstalled(PluginConnector.FIXED_ASSET)) {
                    count = countFixedAssetDetails(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_FXD_ASSET_DETAILS)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_INSTRUMENT_TYPE:
                if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
                    count = countVoucher(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_VOUCHER)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE:
                count = appUserEntityService.countByEntityTypeId(systemEntityId)
                if (count > 0) {
                    result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_USER_ENTITY)
                    return result
                }
                break
            case systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY:
                count = entityContentService.countByEntityTypeIdAndCompanyId(systemEntityId, companyId)
                if (count > 0) {
                    result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_ENTITY_CONTENT)
                    return result
                }
                break
            case systemEntityTypeCacheUtility.TYPE_CONTENT_TYPE:
                count = contentCategoryCacheUtility.countByContentTypeId(systemEntityId)
                if (count > 0) {
                    result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_CONTENT_CATEGORY)
                    return result
                }
                break
            case systemEntityTypeCacheUtility.TYPE_NOTE_ENTITY:
                count = entityNoteService.countByEntityTypeIdAndCompanyId(systemEntityId, companyId)
                if (count > 0) {
                    result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_ENTITY_NOTE)
                    return result
                }
                break
            case systemEntityTypeCacheUtility.TYPE_SUPPLIER_TYPE:
                count = supplierCacheUtility.countBySupplierTypeId(systemEntityId)
                if (count > 0) {
                    result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_SUPPLIER)
                    return result
                }

                if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
                    count = countCOA(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_COA)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_ITEM_CATEGORY:
                count = itemCacheUtility.countByCategoryId(systemEntityId)
                if (count > 0) {
                    result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_ITEM)
                    return result
                }
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_PAID_BY:
                if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
                    count = countExhTaskForPaidBy(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_EXH_TASK)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD:
                if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
                    count = countExhTaskForPaymentMethod(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_EXH_TASK)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS:
                if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
                    count = countExhTaskForTaskStatus(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_EXH_TASK)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_TASK_TYPE:
                if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
                    count = countExhTaskForTaskType(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_EXH_TASK)
                        return result
                    }
                }
                break
            case systemEntityTypeCacheUtility.SARB_TASK_REVISE_STATUS:
                if (PluginConnector.isPluginInstalled(PluginConnector.SARB)) {
                    count = countSarbReviseTaskStatusType(systemEntityId, companyId)
                    if (count > 0) {
                        result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_SARB_TASK)
                        return result
                    }
                }
                break
            default:
                break
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String QUERY_PROC_PURCHASE_ORDER = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order
            WHERE payment_method_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count Purchase order id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of Purchase order id
     */
    private int countPurchaseOrder(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_PROC_PURCHASE_ORDER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_BUDG_BUDGET = """
            SELECT COUNT(id) AS count
            FROM budg_budget
            WHERE unit_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count Budget id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of budget id
     */
    private int countBudget(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_BUDG_BUDGET, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_CHART_OF_ACCOUNT = """
            SELECT COUNT(id) AS count
            FROM acc_chart_of_account
            WHERE source_category_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count chart of account id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of chart of account id
     */
    private int countCOA(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_ACC_CHART_OF_ACCOUNT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_COA_ACC_SOURCE_ID = """
            SELECT COUNT(id) AS count
            FROM acc_chart_of_account
            WHERE acc_source_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count chart of account id for source type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of chart of account id
     */
    private int countCOAForSourceType(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_COA_ACC_SOURCE_ID, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_FXD_FIXED_ASSET_DETAILS = """
            SELECT COUNT(id) AS count
            FROM fxd_fixed_asset_details
            WHERE owner_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count fixed asset details id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of fixed asset details id
     */
    private int countFixedAssetDetails(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_FXD_FIXED_ASSET_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_VOUCHER = """
            SELECT COUNT(id) AS count
            FROM acc_voucher
            WHERE instrument_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count voucher id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of voucher id
     */
    private int countVoucher(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_ACC_VOUCHER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_VOUCHER_TYPE_ID = """
            SELECT COUNT(id) AS count
            FROM acc_voucher
            WHERE voucher_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count voucher id for voucher type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of voucher id
     */
    private int countVoucherForVoucherType(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_VOUCHER_TYPE_ID, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_VOUCHER_TYPE_COA = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_type_coa
            WHERE acc_voucher_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count acc voucher type coa id for voucher type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of acc voucher type coa id
     */
    private int countVoucherTypeCOAForVoucherType(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_ACC_VOUCHER_TYPE_COA, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_INV_PRODUCTION_DETAILS = """
            SELECT COUNT(id) AS count
            FROM inv_production_details
            WHERE production_item_type_id = :systemEntityId
    """

    /**
     * Count inventory production details id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of inventory production details id
     */
    private int countProductionDetails(long systemEntityId) {

        Map queryParams = [
                systemEntityId: systemEntityId
        ]
        List results = executeSelectSql(QUERY_INV_PRODUCTION_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_INV_INVENTORY_TRANSACTION = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE transaction_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count inventory transaction id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of inventory transaction id
     */
    private int countInventoryTransaction(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_INV_INVENTORY_TRANSACTION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_TRANSACTION_ENTITY_TYPE_ID = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE transaction_entity_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count inventory transaction id for entity type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of inventory transaction id
     */
    private int countInventoryTransactionForEntityType(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_TRANSACTION_ENTITY_TYPE_ID, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_INV_INVENTORY = """
            SELECT COUNT(id) AS count
            FROM inv_inventory
            WHERE type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count inventory id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of inventory id
     */
    private int countInventory(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_INV_INVENTORY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_PAID_BY = """
            SELECT COUNT(id) AS count
            FROM exh_task
            WHERE paid_by = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count exchange task id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of exchange task id
     */
    private int countExhTaskForPaidBy(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_PAID_BY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_PAYMENT_METHOD = """
            SELECT COUNT(id) AS count
            FROM exh_task
            WHERE payment_method = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count exchange task id for payment method by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of exchange task id
     */
    private int countExhTaskForPaymentMethod(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_PAYMENT_METHOD, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_EXH_TASK = """
            SELECT COUNT(id) AS count
            FROM exh_task
            WHERE current_status = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count exchange task id for current status by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of exchange task id
     */
    private int countExhTaskForTaskStatus(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_EXH_TASK, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_TASK_TYPE_ID = """
            SELECT COUNT(id) AS count
            FROM exh_task
            WHERE task_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count exchange task id for task type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of exchange task id
     */
    private int countExhTaskForTaskType(long systemEntityId, long companyId) {

        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_TASK_TYPE_ID, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_SARB_REVISE_TASK_STATUS = """
            SELECT COUNT(id) AS count
            FROM sarb_task_details
            WHERE task_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count sarb task id for task type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of sarb task id
     */
    private int countSarbReviseTaskStatusType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_SARB_REVISE_TASK_STATUS, queryParams)
        int count = results[0].count
        return count
    }
}
