package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

import javax.mail.Session

/**
 *  Class to get unassigned itemList to show on drop-down
 *  For details go through Use-Case doc named 'GetItemListSupplierItemActionService'
 */
class GetItemListSupplierItemActionService extends BaseService implements ActionIntf {

    private static final String SERVER_ERROR_MESSAGE = "Fail to load item List"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't Load item List"
    private static final String ITEM_LIST = "itemList"
    private static final String SUPPLIER_ID = "supplierId"
    private static final String ITEM_TYPE_ID = "itemTypeId"

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Check existence of required parameters
     * @param params -parameters send from UI
     * @param obj -N/A
     * @return -a map containing supplierId & itemId for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            //check required parameters
            if (!parameterMap.supplierId || !parameterMap.itemTypeId) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            long supplierId = Long.parseLong(parameterMap.supplierId.toString())
            long itemTypeId = Long.parseLong(parameterMap.itemTypeId.toString())

            result.put(SUPPLIER_ID, supplierId)
            result.put(ITEM_TYPE_ID, itemTypeId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * get unassigned itemList to show on drop-down
     * @param parameters -N/A
     * @param obj -map contains supplierId & itemId
     * @return -a map contains unassigned itemList
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long supplierId = (long) preResult.get(SUPPLIER_ID)
            long itemTypeId = (long) preResult.get(ITEM_TYPE_ID)

            List<GroovyRowResult> itemList = listItemBySupplierId(supplierId, itemTypeId)
            result.put(ITEM_LIST, itemList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with necessary objects to show on UI
     * @param obj -a map contains unAssigned itemList
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj

            //get unassigned itemList to show on drop-down
            List<GroovyRowResult> itemList = (List<GroovyRowResult>) receiveResult.get(ITEM_LIST)
            result.put(ITEM_LIST, Tools.listForKendoDropdown(itemList,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * get list of specific type itemList for drop-down
     * @param supplierId -Supplier.id
     * @param itemTypeId -ItemType.id
     * @return -list of groovyRowResult
     */
    private List<GroovyRowResult> listItemBySupplierId(long supplierId, long itemTypeId) {
        String queryStr = """
                        SELECT id, name,unit,code FROM item
                        WHERE item_type_id = ${itemTypeId} AND
                              company_id = ${appSessionUtil.getCompanyId()} AND
                              id NOT IN ( SELECT item_id FROM supplier_item
                                                WHERE supplier_id=${supplierId} )
                        ORDER BY name
                        """
        List<GroovyRowResult> lstItem = executeSelectSql(queryStr)
        return lstItem
    }
}
