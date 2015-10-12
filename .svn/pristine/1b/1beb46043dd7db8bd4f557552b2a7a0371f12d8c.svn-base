package com.athena.mis.fixedasset.actions.report.consumptionagainstasset

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get Consumption Against Asset Details.
 * For details go through Use-Case doc named 'GetForConsumptionAgainstAssetDetailsActionService'
 */
class GetForConsumptionAgainstAssetDetailsActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    ItemCacheUtility itemCacheUtility

    private static final String CONSUMPTION_DETAILS_NOT_FOUND = "Consumption details not found"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to get consumption details list."
    private static final String DETAIL_LIST_WRAP = "detailListWrap"
    private static final String PROJECT_ID = "projectId"
    private static final String ITEM_ID = "itemId"

    /**
     * 1. check input validation
     * @param parameters - serialized parameters from UI.
     * @param obj - N/A
     * @return- a map containing project id & name and isError msg(True/False) and relevant msg(if any)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long projectId = Long.parseLong(params.projectId.toString())
            long itemId = Long.parseLong(params.itemId.toString())

            if (!projectId || !itemId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            result.put(PROJECT_ID, projectId)
            result.put(ITEM_ID, itemId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get wrapped consumption details for grid show
     * 1. receive project id & name from pre method
     * 2. set transaction type = consumption
     * 3. pull item object
     * 4. get item wise fixed asset consumption details
     * 5. check consumption details existence
     * 6. wrap consumption details for grid
     * @param parameters - serialized parameters from UI
     * @param obj - object receive from pre execute method
     * @return -a map containing consumption details and isError msg(True/False) and relevant msg(if any)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj
            generatePagination(parameterMap)

            long projectId = (long) preResult.get(PROJECT_ID)
            long itemId = (long) preResult.get(ITEM_ID)
            long transactionTypeConsumption = inventoryImplService.getInvTransactionTypeIdConsumption()
            Item item = (Item) itemCacheUtility.read(itemId)

            LinkedHashMap serviceReturn = getItemWiseFixedAssetConsumptionDetails(projectId, itemId, transactionTypeConsumption)
            List<GroovyRowResult> detailList = (List<GroovyRowResult>) serviceReturn.detailList

            if (detailList.size() <= 0) {
                result.put(Tools.MESSAGE, CONSUMPTION_DETAILS_NOT_FOUND)
                return result
            }

            List detailListWrap = wrapDetailList(detailList, item, start)
            result.put(DETAIL_LIST_WRAP, detailListWrap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Get wrapped grid output of consumption details
     * @param obj - object receive from execute method
     * @return - wrapped grid output of consumption details
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        Map gridOutput
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List detailListWrap = (List) executeResult.get(DETAIL_LIST_WRAP)
            gridOutput = [page: pageNumber, total: detailListWrap.size(), rows: detailListWrap]

            result.put(DETAIL_LIST_WRAP, gridOutput)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            gridOutput = [page: pageNumber, total: 0, rows: []]
            result.put(DETAIL_LIST_WRAP, gridOutput)
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
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.message) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Wrapped consumption details for grid show
     * @param detailList - consumption details list
     * @param item - item object
     * @param start - starting point of index
     * @return - wrapped consumption details list
     */
    private List wrapDetailList(List<GroovyRowResult> detailList, Item item, int start) {
        List lstDetails = [] as List
        int counter = start + 1
        GroovyRowResult eachRow
        GridEntity obj
        for (int i = 0; i < detailList.size(); i++) {
            eachRow = detailList[i]
            obj = new GridEntity()
            obj.id = detailList[i].id
            obj.cell = [
                    counter,
                    eachRow.inv_name,
                    eachRow.fixed_asset_name,
                    eachRow.total_quantity + Tools.SINGLE_SPACE + item.unit
            ]
            lstDetails << obj
            counter++
        }
        return lstDetails
    }
    /**
     * set pagination params
     */
    private void generatePagination(GrailsParameterMap params) {
        if (!params.page || !params.rp) {
            params.page = 1
            params.rp = 20
            params.currentCount = 0
            params.sortorder = sortOrder
        }
        initSearch(params)
    }

    private static final String SELECT_QUERY = """
            SELECT  inv.id, inv.name AS inv_name, item.name || ' (' || fad.name || ')' AS fixed_asset_name, COALESCE(SUM(iitd.actual_quantity),0) AS total_quantity
                 FROM inv_inventory_transaction_details  iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN fxd_fixed_asset_details fad ON fad.id = iitd.fixed_asset_details_id
            LEFT JOIN item ON item.id = iitd.fixed_asset_id
            LEFT JOIN inv_inventory inv ON inv.id = iitd.inventory_id
            WHERE iit.project_id = :projectId AND
                  iit.transaction_type_id =:transactionTypeConsumption AND
                  iitd.approved_by > 0 AND
                  iitd.fixed_asset_details_id > 0 AND
                  iitd.fixed_asset_id > 0 AND
                  iitd.item_id =:itemId AND
                  iitd.is_current = true
            GROUP BY inv.id, inv.name, fad.name, item.name
        """
    //@todo:model  change when inventory model implementation is done
    /**
     * Get item wise fixed asset consumption details
     * @param projectId - project id
     * @param itemId - item id
     * @param transactionTypeConsumption - transaction type = consumption
     * @return - a map containing consumption details list
     */
    private Map getItemWiseFixedAssetConsumptionDetails(long projectId, long itemId, long transactionTypeConsumption) {
        Map queryParams = [
                projectId: projectId,
                transactionTypeConsumption: transactionTypeConsumption,
                itemId: itemId
        ]
        List<GroovyRowResult> detailList = executeSelectSql(SELECT_QUERY, queryParams)
        Map result = [detailList: detailList]
        return result
    }
}
