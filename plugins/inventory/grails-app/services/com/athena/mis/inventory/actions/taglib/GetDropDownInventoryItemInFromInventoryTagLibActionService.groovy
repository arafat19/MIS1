package com.athena.mis.inventory.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownInventoryItemInFromInventoryTagLibActionService extends BaseService implements ActionIntf {


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
    private static final String SUPPLIED_QUANTITY = 'supplied_quantity'
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
            dropDownAttributes.put(TRANSACTION_ID, new Long(transId))           // set transactionId
            dropDownAttributes.put(ID, id)                                  // set id
            dropDownAttributes.put(NAME, name)                              // set name
            dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)          // set dataModelName
            dropDownAttributes.put(URL, url)                                // set url

            // Set default values for optional parameters
            dropDownAttributes.put(CLASS, attrs.class )
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex )
            dropDownAttributes.put(ON_CHANGE, attrs.onchange)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(REQUIRED, Boolean.FALSE)
            dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)

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
            if (attrs.supplied_quantity) {
                String strSuppliedQuantity = attrs.get(SUPPLIED_QUANTITY)
                double suppliedQuantity = Double.parseDouble(strSuppliedQuantity)
                dropDownAttributes.put(SUPPLIED_QUANTITY, new Double(suppliedQuantity))
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
            // get inventory transaction object(parent object)
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(transId)
            List<GroovyRowResult> lstItem = getItemListOfInventoryOut(invInventoryTransaction.transactionId)
            String html = buildDropDown(lstItem, dropDownAttributes)
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
        Double suppliedQuantity = (Double) dropDownAttributes.get(SUPPLIED_QUANTITY)

        Map attributes = (Map) dropDownAttributes
        String strAttributes = Tools.EMPTY_SPACE
        attributes.each {
            if (it.value) {
                strAttributes = strAttributes + "${it.key} = '${it.value}' "
            }
        }

        if (defaultValue) { // for edit mode
            modifyItemList(lstItem, defaultValue.longValue(), suppliedQuantity.doubleValue())
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

    private static final String LST_ITEM_INV_OUT_QUERY = """
                SELECT iitd.id, iitd.version, (item.name ||'( ' ||iitd.actual_quantity || ' ' || item.unit || ')') AS name,
                iitd.actual_quantity AS quantity, item.unit
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN item ON item.id = iitd.item_id
                WHERE iitd.inventory_transaction_id=:inventoryTransactionId
                AND iitd.acknowledged_by <= 0
                AND iitd.is_current = true
                AND item.is_individual_entity = false
                ORDER BY item.name ASC
    """

    /**
     * Get item list of inventory out transaction details
     * @param inventoryTransactionId -id of inventory transaction out object
     * @return -list of unacknowledged item of inventory transaction out
     */
    private List<GroovyRowResult> getItemListOfInventoryOut(long inventoryTransactionId) {
        Map queryParams = [inventoryTransactionId: inventoryTransactionId]
        List<GroovyRowResult> itemList = executeSelectSql(LST_ITEM_INV_OUT_QUERY, queryParams)
        return itemList
    }

    /**
     * Reset item list
     * @param lstItem - list of item
     * @param itemId - id of selected item
     * @param quantity - item supplied quantity
     */
    private void modifyItemList(List<GroovyRowResult> lstItem, long itemId, double quantity) {
            Item item = (Item) itemCacheUtility.read(itemId)
            String name = item.name + Tools.PARENTHESIS_START + quantity + Tools.SINGLE_SPACE + item.unit + Tools.PARENTHESIS_END
            lstItem << [id: item.id, name: name , unit: item.unit, quantity: quantity]
    }
}
