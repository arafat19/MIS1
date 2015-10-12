package com.athena.mis.fixedasset.actions.report.currentfixedasset

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of Current Fixed Asset
 * For details go through Use-Case doc named 'ListForCurrentFixedAssetActionService'
 */
class ListForCurrentFixedAssetActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService

    private static final String ITEM_NOT_FOUND = "Please Select an Item"
    private static final String FAILURE_MSG = "Fail to load Current fixed asset report."
    private static final String LST_CURRENT_FIXED_ASSET = "currentFixedAssetList"
    private static final String SORT_NAME = "fad.name"
    private static final String SORT_ORDER = "ASC"

    /**
     * 1. check item existence
     * @param parameters- serialized parameters from UI.
     * @param obj- N/A
     * @return- a map containing isError msg(True/False) and relevant msg(if any)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.itemId) {
                result.put(Tools.MESSAGE, ITEM_NOT_FOUND)
                return result
            }
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
     * 1. initialize pagination
     * 2. set item name= ALL & item = all fixed asset item by listAllFixedAsset() if no item is selected
     * 3. if select a category , pull only that specific item
     * 4. pull logged user assigned inventories
     * 5. get list of current fixed asset
     * 6. wrape current fixed asset for grid entity
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing inventory list of current fixed asset
     *  and isError(true/false) & relevant msg(if amy)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
                parameterMap.sortname = SORT_NAME
                parameterMap.sortorder = SORT_ORDER
            }

            initPager(parameterMap)

            List<Long> itemIds = []
            long itemId = Long.parseLong(parameterMap.itemId.toString())
            if (itemId < 0) {
                Map receiveReturn = listAllFixedAsset()
                itemIds = (List<Long>) receiveReturn.fixedAssetList.id
            } else {
                itemIds << new Long(itemId)
            }

            List<Long> userInventoryIdList = inventoryImplService.getUserInventoryIds()
            if (userInventoryIdList.size() <= 0) {
                userInventoryIdList << 0L
            }

            LinkedHashMap serviceReturn = listCurrentFixedAsset(itemIds, userInventoryIdList)
            List<GroovyRowResult> lstCurrentFixedAsset = (List<GroovyRowResult>) serviceReturn.currentFixedAssetList
            int count = (int) serviceReturn.count

            List currentFixedAssetList = wrapCurrentFixedAssetListInGrid(lstCurrentFixedAsset, start)
            result.put(LST_CURRENT_FIXED_ASSET, currentFixedAssetList)
            result.put(Tools.COUNT, count)
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
     * Get wrapped current fixed asset list for grid entity
     * @param obj - object receive from execute method
     * @return - wrapped grid output of current fixed asset
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List currentFixedAssetList = (List) executeResult.get(LST_CURRENT_FIXED_ASSET)
            int count = (int) executeResult.get(Tools.COUNT)
            Map gridOutput = [page: this.pageNumber, total: count, rows: currentFixedAssetList]
            result.put(LST_CURRENT_FIXED_ASSET, gridOutput)
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
     * Wrapped current fixed asset for grid show
     * @param lstCurrentFixedAsset - list of current fixed asset
     * @param start- starting point of index
     * @return - Wrapped current fixed asset list
     */
    private List wrapCurrentFixedAssetListInGrid(List<GroovyRowResult> lstCurrentFixedAsset, int start) {
        List lstCurrentFixedAssetSummary = [] as List

        int counter = start + 1
        GroovyRowResult currentFixedAsset
        GridEntity obj

        for (int i = 0; i < lstCurrentFixedAsset.size(); i++) {
            currentFixedAsset = lstCurrentFixedAsset[i]
            obj = new GridEntity()
            obj.id = currentFixedAsset.id
            obj.cell = [
                    counter,
                    currentFixedAsset.inventory,
                    currentFixedAsset.category,
                    currentFixedAsset.name,
                    currentFixedAsset.purchase_date,
                    currentFixedAsset.cost
            ]
            lstCurrentFixedAssetSummary << obj
            counter++
        }
        return lstCurrentFixedAssetSummary
    }

    private static final String SELECT_QUERY = """
        SELECT DISTINCT ON (item.name) item.name,fad.item_id as id
        FROM fxd_fixed_asset_details fad
        LEFT JOIN item on item.id=fad.item_id
        WHERE fad.company_id=:companyId
        """
    //get fixedAsset list for current fixed asset as category
    private Map listAllFixedAsset() {
        //@todo:model use existing sql
        long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
        Map queryParams = [
                companyId: companyId
        ]
        List<GroovyRowResult> fixedAssetList = executeSelectSql(SELECT_QUERY, queryParams)
        return [fixedAssetList: fixedAssetList]
    }

    // get of the Current Fixed Asset Info
    private Map listCurrentFixedAsset(List<Long> itemId, List<Long> userInventoryIdList) {
        String itemIds = Tools.buildCommaSeparatedStringOfIds(itemId)
        String lstUserInventoryIds = Tools.buildCommaSeparatedStringOfIds(userInventoryIdList)
        String queryStr = """
            SELECT fad.id as id,fad.name,to_char(fad.purchase_date,'dd-Mon-yyyy') as purchase_date,
                   to_char(fad.cost,'${Tools.DB_CURRENCY_FORMAT}') as cost,
                   (se.key||'${Tools.COLON}'||inv.name) as inventory, item.name as category
            FROM fxd_fixed_asset_details fad
            LEFT JOIN inv_inventory inv on inv.id=fad.current_inventory_id
            LEFT JOIN system_entity se on se.id=inv.type_id
            LEFT JOIN item on item.id=fad.item_id
            WHERE item_id IN (${itemIds}) AND
                  inv.id IN(${lstUserInventoryIds})
            GROUP BY fad.id,fad.name,fad.current_inventory_id,fad.purchase_date,fad.cost,se.key,inv.name,item.name
           ORDER BY ${sortColumn} ${sortOrder}
            LIMIT :resultPerPage  OFFSET :start
        """

        String queryCount = """
            SELECT COUNT(DISTINCT(id)) AS count
            FROM fxd_fixed_asset_details
            WHERE item_id IN (${itemIds}) AND
                  current_inventory_id IN(${lstUserInventoryIds})
        """
        Map queryParams = [
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> currentFixedAssetList = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> assetCount = executeSelectSql(queryCount)

        Map result = [currentFixedAssetList: currentFixedAssetList, count: assetCount[0].count]
        return result
    }
}
