package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
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
import org.springframework.beans.factory.annotation.Autowired

/*Renders html of hidden field for reserved system entity object*/

class GetSystemEntityByReservedTagLibActionService extends BaseService implements ActionIntf {

    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
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

    private Logger log = Logger.getLogger(getClass())

    private static final String INPUT_END = ">"
    private static final String NAME = 'name'
    private static final String TYPE_ID = 'typeId'
    private static final String RESERVED_IDS = 'reservedId'

    /** Build a map containing properties of html hidden field
     *  Set values of properties
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map hiddenFieldAttributes = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            hiddenFieldAttributes.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

            String name = attrs.get(NAME)
            String strTypeId = attrs.get(TYPE_ID)
            def reservedIds = attrs.get(RESERVED_IDS)
            if ((!name) || (!reservedIds) || (!strTypeId) || (strTypeId.length() == 0)) {
                return hiddenFieldAttributes
            }
            List<Long> lstReservedIds = []
            if (reservedIds instanceof ArrayList) {
                lstReservedIds = reservedIds
            } else {
                lstReservedIds << (Long) reservedIds
            }
            long typeId = Long.parseLong(strTypeId)
            hiddenFieldAttributes.put(TYPE_ID, new Long(typeId))            // set the typeId
            hiddenFieldAttributes.put(RESERVED_IDS, lstReservedIds)    // set the reservedId
            hiddenFieldAttributes.put(NAME, name)                           // set name

            hiddenFieldAttributes.put(Tools.IS_ERROR, Boolean.FALSE)
            return hiddenFieldAttributes
        } catch (Exception e) {
            log.error(e.getMessage())
            hiddenFieldAttributes.put(Tools.IS_ERROR, Boolean.TRUE)
            return hiddenFieldAttributes
        }
    }

    /** Build the html for hidden field
     * @param parameters - map returned from executePreCondition
     * @param obj - N/A
     * @return - html string for hidden field
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map hiddenFieldAttributes = (Map) parameters
            Long typeId = (Long) hiddenFieldAttributes.get(TYPE_ID)
            List<Long> lstReservedIds = (List<Long>) hiddenFieldAttributes.get(RESERVED_IDS)
            long companyId = appSessionUtil.getCompanyId()
            List<Long> lstSysEntityIds = []
            lstReservedIds.each {
                long systemEntityId = getIdOfSystemEntity(typeId.longValue(), it.longValue(), companyId)
                lstSysEntityIds << Long.valueOf(systemEntityId)
            }
            String strSysEntityIds = Tools.buildCommaSeparatedStringOfIds(lstSysEntityIds)
            String html = buildHtmlForHiddenField(strSysEntityIds, hiddenFieldAttributes)
            return html
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get id of system entity
     * @param systemEntityTypeId - type id of system entity
     * @param reservedId - reserved id of system entity
     * @param companyId -id of company
     * @return - id of system entity
     */
    private long getIdOfSystemEntity(long systemEntityTypeId, long reservedId, long companyId) {
        SystemEntity systemEntity
        switch (systemEntityTypeId) {
            case systemEntityTypeCacheUtility.TYPE_PAYMENT_METHOD:
                systemEntity = (SystemEntity) paymentMethodCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_SOURCE:
                systemEntity = (SystemEntity) accountingImplService?.readByReservedSourceType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_VOUCHER:
                systemEntity = (SystemEntity) accountingImplService?.readByReservedVoucherType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_INV_PRODUCTION_LINE_ITEM_TYPE:
                systemEntity = (SystemEntity) inventoryImplService?.readByReservedProductionItemType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_UNIT:
                systemEntity = (SystemEntity) unitCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE:
                systemEntity = (SystemEntity) inventoryImplService?.readByReservedTransactionType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_ENTITY_TYPE:
                systemEntity = (SystemEntity) inventoryImplService?.readByReservedTransactionEntityType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_VALUATION_TYPE:
                systemEntity = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE:
                systemEntity = (SystemEntity) inventoryImplService?.readByReservedInventoryType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_OWNER_TYPE:
                systemEntity = (SystemEntity) ownerTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY:
                systemEntity = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_CONTENT_TYPE:
                systemEntity = (SystemEntity) contentTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_NOTE_ENTITY:
                systemEntity = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_SUPPLIER_TYPE:
                systemEntity = (SystemEntity) supplierTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_ITEM_CATEGORY:
                systemEntity = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_INSTRUMENT_TYPE:
                systemEntity = (SystemEntity) accountingImplService?.readByReservedInstrumentType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE:
                systemEntity = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_PAID_BY:
                systemEntity = (SystemEntity) exchangeHouseImplService?.readByReservedPaidByType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD:
                systemEntity = (SystemEntity) exchangeHouseImplService?.readByReservedPaymentMethodType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS:
                systemEntity = (SystemEntity) exchangeHouseImplService?.readByReservedTaskStatusType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_TASK_TYPE:
                systemEntity = (SystemEntity) exchangeHouseImplService?.readTaskTypeByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.PT_BACKLOG_PRIORITY:
                systemEntity = (SystemEntity) projectTrackImplService?.readByReservedBacklogPriorityType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.PT_BACKLOG_STATUS:
                systemEntity = (SystemEntity) projectTrackImplService?.readByReservedBacklogStatusType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.PT_SPRINT_STATUS:
                systemEntity = (SystemEntity) projectTrackImplService?.readByReservedSprintStatusType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_STATUS:
                systemEntity = (SystemEntity) projectTrackImplService?.readByReservedAcceptanceCriteriaStatusType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.PT_BUG_SEVERITY:
                systemEntity = (SystemEntity) projectTrackImplService?.readByReservedBugSeverityType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.PT_BUG_STATUS:
                systemEntity = (SystemEntity) projectTrackImplService?.readByReservedBugStatusType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.PT_BUG_TYPE:
                systemEntity = (SystemEntity) projectTrackImplService?.readByReservedBugType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_TYPE:
                systemEntity = (SystemEntity) projectTrackImplService?.readByReservedAcceptanceCriteriaType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.ARMS_PROCESS_TYPE:
                systemEntity = (SystemEntity) armsImplService?.readByReservedProcessType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE:
                systemEntity = (SystemEntity) armsImplService?.readByReservedInstrumentType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD:
                systemEntity = (SystemEntity) armsImplService?.readByReservedPaymentMethodType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.ARMS_TASK_STATUS:
                systemEntity = (SystemEntity) armsImplService?.readByReservedTaskStatusType(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_GENDER:
                systemEntity = (SystemEntity) genderCacheUtility.readByReservedAndCompany(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS:
                systemEntity = (SystemEntity) budgetImplService?.readByReservedBudgetTaskStatus(reservedId, companyId)
                break
            case systemEntityTypeCacheUtility.SARB_TASK_REVISE_STATUS:
                systemEntity = (SystemEntity) sarbImplService?.readByReservedSarbTaskReviseStatus(reservedId, companyId)
                break
            default:
                return 0L
        }
        return systemEntity.id
    }

    /** Generate the html for hidden field
     * @param strSysEntityIds - id of system entity
     * @param hiddenFieldAttributes - a map containing the attributes of hidden field
     * @return - html string for hidden field
     */
    private String buildHtmlForHiddenField(String strSysEntityIds, Map hiddenFieldAttributes) {
        // read map values
        String name = hiddenFieldAttributes.get(NAME)

        String html = "<input type='hidden' name= '${name}' id= '${name}' value= '${strSysEntityIds}'" + INPUT_END
        return html
    }
}
