package com.athena.mis.inventory.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownInventoryConsumptionItemTagLibActionService extends BaseService implements ActionIntf {


    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    ItemCacheUtility itemCacheUtility

    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String TRANSACTION_ID = 'transaction_id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String ACTUAL_QUANTITY = 'actual_quantity'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String SELECT_END = "</select>"

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

            String id = attrs.get(ID)
            String url = attrs.get(URL)
            String name = attrs.get(NAME)
            String dataModelName = attrs.get(DATA_MODEL_NAME)
            String strTransId = attrs.get(TRANSACTION_ID)
            if ((!name) || (!dataModelName) || (!strTransId) || (strTransId.length() == 0)) {
                return dropDownAttributes
            }
            long transId = Long.parseLong(strTransId)
            dropDownAttributes.put(TRANSACTION_ID, new Long(transId))       // set transactionId
            dropDownAttributes.put(ID, id)                                  // set id
            dropDownAttributes.put(NAME, name)                              // set name
            dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)          // set dataModelName
            dropDownAttributes.put(URL, url)                                // set url

            // Set default values for optional parameters
            dropDownAttributes.put(CLASS, attrs.class)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(DEFAULT_VALUE, null)

            if (attrs.hints_text) {
                dropDownAttributes.put(HINTS_TEXT, attrs.hints_text)
            }
            if (attrs.show_hints) {
                boolean showHints = Boolean.parseBoolean(attrs.show_hints)
                dropDownAttributes.put(SHOW_HINTS, new Boolean(showHints))
            }
            if (attrs.default_value) {
                String strDefaultValue = attrs.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                dropDownAttributes.put(DEFAULT_VALUE, new Long(defaultValue))
            }
            if (attrs.actual_quantity) {
                String strActualQuantity = attrs.get(ACTUAL_QUANTITY)
                double actualQuantity = Double.parseDouble(strActualQuantity)
                dropDownAttributes.put(ACTUAL_QUANTITY, new Double(actualQuantity))
            }
            if (attrs.required) {
                dropDownAttributes.put(REQUIRED, REQUIRED)
                dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
                if (attrs.validation_message) {
                    dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validation_message)
                }
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

    /** Get the list of Inventory objects by type
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map dropDownAttributes = (Map) parameters
            Long transId = (Long) dropDownAttributes.get(TRANSACTION_ID)
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(transId)
            //itemList for drop-down to consume again
            List<GroovyRowResult> itemList = getItemListForConsumption(invInventoryTransaction.inventoryId, invInventoryTransaction.budgetId)
            String html = buildDropDown(itemList, dropDownAttributes)
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

    /** Generate the html for select
     * @param lstInventory - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstItem, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        Double actualQuantity = (Double) dropDownAttributes.get(ACTUAL_QUANTITY)

        Map attributes = (Map) dropDownAttributes
        String strAttributes = Tools.EMPTY_SPACE
        attributes.each {
            if (it.value) {
                strAttributes = strAttributes + "${it.key} = '${it.value}' "
            }
        }

        if (defaultValue) { // for edit mode
            modifyItemList(lstItem, defaultValue.longValue(), actualQuantity.doubleValue())
        }

        String strDefaultValue = defaultValue ? defaultValue : Tools.EMPTY_SPACE
        if (showHints.booleanValue()) {
            lstItem = Tools.listForKendoDropdown(lstItem, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String jsonData = lstItem as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    if (${dataModelName}){
                        ${dataModelName}.destroy();
                    }
                        \$('#${name}').kendoDropDownList({
                            dataTextField:'name',
                            dataValueField:'id',
                            dataSource:${jsonData},
                            value:'${strDefaultValue}'
                        });
                        ${dataModelName} = \$("#${name}").data("kendoDropDownList");
                });
            </script>
        """
        return html + script
    }

    private static final String LST_CONSUM_QUERY = """
        SELECT
            vcs.item_id as id, item.name, item.unit, vcs.consumeable_stock as quantity,
            budget_details.is_consumed_against_fixed_asset
        FROM vw_inv_inventory_consumable_stock vcs
            LEFT JOIN budg_budget_details budget_details ON vcs.item_id = budget_details.item_id
            LEFT JOIN item ON item.id = vcs.item_id
        WHERE vcs.inventory_id=:inventoryId
            AND budget_details.budget_id=:budgetId
            AND vcs.consumeable_stock > 0
            AND item.is_individual_entity = false
            ORDER BY item.name
    """
    /**
     * Get List of Consumable stock of available item from view(vw_inv_inventory_consumable_stock)
     *
     * @param inventoryId -Inventory.id
     * @param budgetId -Budget.id
     *
     * @return - a map containing list of GroovyRowResult(itemList)
     */
    private List<GroovyRowResult> getItemListForConsumption(long inventoryId, long budgetId) {
        Map queryParams = [inventoryId: inventoryId, budgetId: budgetId]
        List<GroovyRowResult> materialList = executeSelectSql(LST_CONSUM_QUERY, queryParams)
        return materialList
    }

    /**
     * Reset item list
     * 1. if item already exists in the drop down list then
     *    set SUM(current quantity + actual quantity of selected item)
     * @param lstItem - list of item
     * @param itemId - id of selected item
     * @param quantity - item actual quantity
     */
    private void modifyItemList(List<GroovyRowResult> lstItem, long itemId, double quantity) {
        boolean isItemExists = false
        for (int i = 0; i < lstItem.size(); i++) {
            GroovyRowResult eachRow = lstItem[i]
            if (itemId == eachRow.id) {
                eachRow.quantity = eachRow.quantity + quantity
                isItemExists = true
                break
            }
            i++
        }

        if (!isItemExists) {
            Item item = (Item) itemCacheUtility.read(itemId)
            lstItem << [id: item.id, name: item.name, unit: item.unit, quantity: quantity]
        }
    }
}
