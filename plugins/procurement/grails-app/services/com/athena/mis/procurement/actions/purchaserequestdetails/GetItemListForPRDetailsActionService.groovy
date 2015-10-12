package com.athena.mis.procurement.actions.purchaserequestdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Give Item List from budg_budget_details for Purchase Request
 * For details go through Use-Case doc named 'GetItemListForPRDetailsActionService'
 */
class GetItemListForPRDetailsActionService extends BaseService implements ActionIntf {

    private static final String SERVER_ERROR_MESSAGE = "Fail to load Item List"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't Load Item List"
    private static final String ENTITY_NOT_FOUND_ERROR_MESSAGE = "No entity found with this id or might have been deleted by someone!"
    private static final String ITEM_LIST = "itemList"
    private static final String PROJECT_ID = "projectId"
    private static final String ITEM_TYPE_ID = "itemTypeId"
    private static final String PURCHASE_REQUEST_ID = "purchaseRequestId"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Get budget id, item type id, pr id
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget id, item type id, pr id
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if (!parameterMap.projectId || !parameterMap.itemTypeId || !parameterMap.purchaseRequestId) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            long projectId = Long.parseLong(parameterMap.projectId.toString())
            long itemTypeId = Integer.parseInt(parameterMap.itemTypeId.toString())
            long purchaseRequestId = Long.parseLong(parameterMap.purchaseRequestId.toString())

            result.put(PROJECT_ID, projectId)
            result.put(ITEM_TYPE_ID, itemTypeId)
            result.put(PURCHASE_REQUEST_ID, purchaseRequestId)
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
     * Get item list
     * @param parameters - N/A
     * @param obj -object receive from pre execute method
     * @return - A map containing item list and isError msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long projectId = (long) preResult.get(PROJECT_ID)
            int itemTypeId = (int) preResult.get(ITEM_TYPE_ID)
            long purchaseRequestId = (long) preResult.get(PURCHASE_REQUEST_ID)

            List<GroovyRowResult> itemList = []
            itemList = listItemByBudgetForPR(projectId, itemTypeId, purchaseRequestId)
            result.put(ITEM_LIST, Tools.listForKendoDropdown(itemList, null, null))
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
        // do nothing for post operation
        return null
    }
    /**
     * Get item list
     * @param obj -object receive from execute method
     * @return - A map containing item list and isError msg(True/False)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<GroovyRowResult> itemList = (List<GroovyRowResult>) receiveResult.get(ITEM_LIST)
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

    private static final String BUDG_BUDGET_DETAILS_SELECT_QUERY = """
                    SELECT item_id AS id, item_name || ' (' || remaining_quantity || ')' AS name,
                    item_unit AS unit, remaining_quantity AS quantity
                        FROM vw_budget_project_item
                    WHERE item_type_id = :itemTypeId
                        AND project_id = :projectId
                        AND remaining_quantity > 0
                        AND item_id NOT IN (
                            SELECT item_id FROM proc_purchase_request_details
                            WHERE purchase_request_id=:purchaseRequestId
                    )
                    ORDER BY name
    """

    private List<GroovyRowResult> listItemByBudgetForPR(long projectId, int itemTypeId, long purchaseRequestId) {
        Map queryParams = [
                projectId: projectId,
                itemTypeId: itemTypeId,
                purchaseRequestId: purchaseRequestId
        ]
        List<GroovyRowResult> lstMaterials = executeSelectSql(BUDG_BUDGET_DETAILS_SELECT_QUERY, queryParams)
        return lstMaterials
    }
}
