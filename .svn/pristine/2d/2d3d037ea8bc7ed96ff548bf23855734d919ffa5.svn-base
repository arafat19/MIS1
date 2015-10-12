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
 * Search Current Fixed Asset
 * For details go through Use-Case doc named 'SearchForCurrentFixedAssetActionService'
 */
class SearchForCurrentFixedAssetActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService

    private static final String FAILURE_MSG = "Fail to load Current fixed asset report."
    private static final String LST_CURRENT_FIXED_ASSET = "currentFixedAssetList"
    private static final String SORT_NAME = "fad.name"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
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

            initPager(parameterMap)

            if (!parameterMap.rp) {
                this.resultPerPage = 25
            }
            if (!parameterMap.sortname) {
                this.sortColumn = SORT_NAME
            }

            initSearch(parameterMap)
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

            LinkedHashMap serviceReturn = searchCurrentFixedAsset(itemIds, userInventoryIdList)
            result.put(LST_CURRENT_FIXED_ASSET, serviceReturn.currentFixedAssetList)
            result.put(Tools.COUNT, serviceReturn.count)
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
            LinkedHashMap returnResult = (LinkedHashMap) obj
            List<GroovyRowResult> lstCurrentFixedAsset = (List<GroovyRowResult>) returnResult.get(LST_CURRENT_FIXED_ASSET)

            List currentFixedAssetListWrap = wrapCurrentFixedAssetGrid(lstCurrentFixedAsset, start)
            int count = Integer.parseInt(returnResult.get(Tools.COUNT).toString())
            Map gridOutput = [page: pageNumber, total: count, rows: currentFixedAssetListWrap]
            result.put(LST_CURRENT_FIXED_ASSET, gridOutput)
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
            LinkedHashMap returnResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.message) {
                result.put(Tools.MESSAGE, returnResult.get(Tools.MESSAGE))
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
    private List wrapCurrentFixedAssetGrid(List<GroovyRowResult> lstCurrentFixedAsset, int start) {
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
        long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
        Map queryParams = [
                companyId: companyId
        ]
        List<GroovyRowResult> fixedAssetList = executeSelectSql(SELECT_QUERY, queryParams)
        return [fixedAssetList: fixedAssetList]
    }

    //query to search Item Stock
    private Map searchCurrentFixedAsset(List<Long> itemIds, List<Long> userInventoryIdList) {
        String itemId = Tools.buildCommaSeparatedStringOfIds(itemIds)
        String lstUserInventoryIds = Tools.buildCommaSeparatedStringOfIds(userInventoryIdList)
        String queryStr = """
        SELECT fad.id as id,fad.name,to_char(fad.purchase_date,'dd-Mon-yyyy') as purchase_date,
               to_char(fad.cost,'${Tools.DB_CURRENCY_FORMAT}') as cost,
               (se.key || '${Tools.COLON} ' || inv.name) as inventory,item.name as category
        FROM fxd_fixed_asset_details fad
        LEFT JOIN inv_inventory inv on inv.id=fad.current_inventory_id
        LEFT JOIN system_entity se on se.id=inv.type_id
        LEFT JOIN item on item.id=fad.item_id
        WHERE fad.item_id IN (${itemId})
          AND inv.id IN(${lstUserInventoryIds})
          AND fad.name ILIKE :query
        GROUP BY fad.id,fad.name,fad.current_inventory_id,fad.purchase_date,fad.cost,se.key,inv.name,item.name
         ORDER BY ${sortColumn} ${sortOrder}
        LIMIT :resultPerPage  OFFSET :start
        """

        String queryCount = """
           SELECT COUNT(DISTINCT(id)) AS count
            FROM fxd_fixed_asset_details
            WHERE item_id IN (${itemId})
              AND current_inventory_id IN(${lstUserInventoryIds})
              AND name ILIKE :query
        """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> currentFixedAssetList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [currentFixedAssetList: currentFixedAssetList, count: count]
    }
}