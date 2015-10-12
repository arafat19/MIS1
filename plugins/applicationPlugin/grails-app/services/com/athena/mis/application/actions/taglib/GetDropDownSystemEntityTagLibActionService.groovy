package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.*
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.projecttrack.ProjectTrackPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import com.athena.mis.utility.Tools
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders html of 'select' for  SystemEntity objects */

class GetDropDownSystemEntityTagLibActionService extends BaseService implements ActionIntf {

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
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService

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

    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String TYPE_ID = 'typeId'
    private static final String HINTS_TEXT = 'hintsText'
    private static final String SHOW_HINTS = 'showHints'
    private static final String DEFAULT_VALUE = 'defaultValue'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String ELEMENTS = 'elements'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'dataModelName'

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map dropDownAttributes = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            dropDownAttributes.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

            String strTypeId = attrs.get(TYPE_ID)
            String name = attrs.get(NAME)
            String dataModelName = attrs.get(DATA_MODEL_NAME)
            if ((!name) || (!dataModelName) || (!strTypeId) || (strTypeId.length() == 0)) {
                return dropDownAttributes
            }
            long systemEntityTypeId = Long.parseLong(strTypeId)
            dropDownAttributes.put(TYPE_ID, new Long(systemEntityTypeId)) // set the typeId
            dropDownAttributes.put(NAME, name)                            // set name
            dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)        // set dataModelName

            // Set default values for optional parameters
            dropDownAttributes.put(CLASS, attrs.class ? attrs.class : null)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex ? attrs.tabindex : null)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange ? attrs.onchange : null)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(ELEMENTS, [])
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(REQUIRED, Boolean.FALSE)
            dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)

            if (attrs.hintsText) {
                dropDownAttributes.put(HINTS_TEXT, attrs.hintsText)
            }
            if (attrs.showHints) {
                boolean showHints = Boolean.parseBoolean(attrs.showHints)
                dropDownAttributes.put(SHOW_HINTS, new Boolean(showHints))
            }
            if (attrs.defaultValue) {
                String strDefaultValue = attrs.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                dropDownAttributes.put(DEFAULT_VALUE, new Long(defaultValue))
            }
            if (attrs.elements) {
                List<Long> lstElements = (List<Long>) attrs.elements
                dropDownAttributes.put(ELEMENTS, lstElements)
            }
            if (attrs.required) {
                boolean required = Boolean.parseBoolean(attrs.required)
                dropDownAttributes.put(REQUIRED, new Boolean(required))
            }
            if (attrs.validationMessage) {
                dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validationMessage)
            }
            dropDownAttributes.put(Tools.IS_ERROR, Boolean.FALSE)
            return dropDownAttributes
        } catch (Exception e) {
            log.error(e.getMessage())
            dropDownAttributes.put(Tools.IS_ERROR, Boolean.TRUE)
            return dropDownAttributes
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /** Get the list of SystemEntity objects by type
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map dropDownAttributes = (Map) parameters
            Long systemEntityTypeId = (Long) dropDownAttributes.get(TYPE_ID)
            List<Long> lstElements = (List<Long>) dropDownAttributes.get(ELEMENTS)
            List<SystemEntity> lstSystemEntity = (List<SystemEntity>) listSystemEntity(systemEntityTypeId)
            String html
            if (lstElements.size() == 0) {
                html = buildDropDown(lstSystemEntity, dropDownAttributes)
            } else {
                List<SystemEntity> lstSystemEntityFiltered = []
                for (int i = 0; i < lstElements.size(); i++) {
                    long reservedId = lstElements[i]
                    for (int j = 0; j < lstSystemEntity.size(); j++) {
                        SystemEntity systemEntity = lstSystemEntity[j]
                        if (reservedId == systemEntity.reservedId) {
                            lstSystemEntityFiltered << systemEntity
                            break
                        }
                    }
                }
                html = buildDropDown(lstSystemEntityFiltered, dropDownAttributes)
            }
            return html
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /** Get the list from corresponding cacheUtility
     * @param systemEntityTypeId - typeId of systemEntity
     * @return -List of SystemEntity
     */
    private List listSystemEntity(long systemEntityTypeId) {
        List lstSystemEntity
        switch (systemEntityTypeId) {
            case systemEntityTypeCacheUtility.TYPE_GENDER:
                lstSystemEntity = genderCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_PAYMENT_METHOD:
                lstSystemEntity = paymentMethodCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_SOURCE:
                lstSystemEntity = accountingImplService ? accountingImplService.listAccSourceType() : []
                break
            case systemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS:
                lstSystemEntity = budgetImplService ? budgetImplService.listBudgetTaskStatus() : []
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_VOUCHER:
                lstSystemEntity = accountingImplService ? accountingImplService.listAccVoucherType() : []
                break
            case systemEntityTypeCacheUtility.TYPE_INV_PRODUCTION_LINE_ITEM_TYPE:
                lstSystemEntity = inventoryImplService ? inventoryImplService.listInvProductionItemType() : []
                break
            case systemEntityTypeCacheUtility.TYPE_UNIT:
                lstSystemEntity = unitCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE:
                lstSystemEntity = inventoryImplService ? inventoryImplService.listInvTransactionType() : []
                break
            case systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_ENTITY_TYPE:
                lstSystemEntity = inventoryImplService ? inventoryImplService.listInvTransactionEntityType() : []
                break
            case systemEntityTypeCacheUtility.TYPE_VALUATION_TYPE:
                lstSystemEntity = valuationTypeCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE:
                lstSystemEntity = inventoryImplService ? inventoryImplService.getInventoryTypeList() : []
                break
            case systemEntityTypeCacheUtility.TYPE_OWNER_TYPE:
                lstSystemEntity = ownerTypeCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY:
                lstSystemEntity = contentEntityTypeCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_CONTENT_TYPE:
                lstSystemEntity = contentTypeCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_NOTE_ENTITY:
                lstSystemEntity = noteEntityTypeCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_SUPPLIER_TYPE:
                lstSystemEntity = supplierTypeCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_ITEM_CATEGORY:
                lstSystemEntity = itemCategoryCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_ACC_INSTRUMENT_TYPE:
                lstSystemEntity = accountingImplService ? accountingImplService.listAccInstrumentType() : []
                break
            case systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE:
                lstSystemEntity = appUserEntityTypeCacheUtility.listByIsActive()
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_PAID_BY:
                lstSystemEntity = exchangeHouseImplService ? exchangeHouseImplService.listExhPaidByType() : []
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD:
                lstSystemEntity = exchangeHouseImplService ? exchangeHouseImplService.listExhPaymentMethodType() : []
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS:
                lstSystemEntity = exchangeHouseImplService ? exchangeHouseImplService.listExhTaskStatusType() : []
                break
            case systemEntityTypeCacheUtility.TYPE_EXH_TASK_TYPE:
                lstSystemEntity = exchangeHouseImplService ? exchangeHouseImplService.listExhTaskType() : []
                break
            case systemEntityTypeCacheUtility.PT_BACKLOG_PRIORITY:
                lstSystemEntity = projectTrackImplService ? projectTrackImplService.listBacklogPriority() : []
                break
            case systemEntityTypeCacheUtility.PT_BACKLOG_STATUS:
                lstSystemEntity = projectTrackImplService ? projectTrackImplService.listBacklogStatus() : []
                break
            case systemEntityTypeCacheUtility.PT_SPRINT_STATUS:
                lstSystemEntity = projectTrackImplService ? projectTrackImplService.listSprintStatus() : []
                break
            case systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_STATUS:
                lstSystemEntity = projectTrackImplService ? projectTrackImplService.listAcceptanceCriteriaStatus() : []
                break
            case systemEntityTypeCacheUtility.PT_BUG_STATUS:
                lstSystemEntity = projectTrackImplService ? projectTrackImplService.listBugStatus() : []
                break
            case systemEntityTypeCacheUtility.PT_BUG_SEVERITY:
                lstSystemEntity = projectTrackImplService ? projectTrackImplService.listBugSeverity() : []
                break
            case systemEntityTypeCacheUtility.PT_BUG_TYPE:
                lstSystemEntity = projectTrackImplService ? projectTrackImplService.listBugType() : []
                break
            case systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_TYPE:
                lstSystemEntity = projectTrackImplService ? projectTrackImplService.listAcceptanceCriteriaType() : []
                break
            case systemEntityTypeCacheUtility.ARMS_PROCESS_TYPE:
                lstSystemEntity = armsImplService ? armsImplService.listProcessType() : []
                break
            case systemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE:
                lstSystemEntity = armsImplService ? armsImplService.listInstrumentType() : []
                break
            case systemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD:
                lstSystemEntity = armsImplService ? armsImplService.listPaymentMethod() : []
                break
            case systemEntityTypeCacheUtility.ARMS_TASK_STATUS:
                lstSystemEntity = armsImplService ? armsImplService.listTaskStatus() : []
                break
            case systemEntityTypeCacheUtility.DOC_DATABASE_VENDOR:
                lstSystemEntity = documentImplService ? documentImplService.listDbVendor() : []
                break
            case systemEntityTypeCacheUtility.DOC_CONTENT_TYPE:
                lstSystemEntity = documentImplService ? documentImplService.listDocContentType() : []
                break
            case systemEntityTypeCacheUtility.SARB_TASK_REVISE_STATUS:
                lstSystemEntity = sarbImplService ? sarbImplService.listSarbTaskReviseStatus() : []
                break
            default:
                lstSystemEntity = []
        }
        return lstSystemEntity
    }

    private static final String SELECT_END = "</select>"
    private static final String KEY = "key"

    /** Generate the html for select
     * @param lstSystemEntity - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<SystemEntity> lstSystemEntity, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramClass = dropDownAttributes.get(CLASS)
        String paramTabIndex = dropDownAttributes.get(TAB_INDEX)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        Boolean required = dropDownAttributes.get(REQUIRED)
        String validationMessage = dropDownAttributes.get(VALIDATION_MESSAGE)

        String htmlClass = paramClass ? "class='${paramClass}'" : Tools.EMPTY_SPACE
        String htmlTabIndex = paramTabIndex ? "tabindex='${paramTabIndex}'" : Tools.EMPTY_SPACE
        String htmlRequired = required.booleanValue() ? "required" : Tools.EMPTY_SPACE
        String htmlValidationMessage = required.booleanValue() ? "validationMessage='${validationMessage}'" : Tools.EMPTY_SPACE

        String html = "<select name = '${name}' id = '${name}' ${htmlClass} ${htmlTabIndex} ${htmlRequired} ${htmlValidationMessage}>\n" + SELECT_END
        String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : Tools.EMPTY_SPACE
        String strDefaultValue = defaultValue ? defaultValue : Tools.EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstSystemEntity = Tools.listForKendoDropdown(lstSystemEntity, KEY, hintsText)
            // the KEY parameter indicates that dataTextField is key
        }
        String jsonData = lstSystemEntity as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'key',
                        dataValueField:'id',
                        dataSource:${jsonData},
                        value:'${strDefaultValue}',
                        ${strOnChange}
                    });
                });
                ${dataModelName} = \$("#${name}").data("kendoDropDownList");
            </script>
        """
        return html + script
    }
}
