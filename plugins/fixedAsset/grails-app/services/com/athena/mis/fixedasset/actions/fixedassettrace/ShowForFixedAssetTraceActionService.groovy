package com.athena.mis.fixedasset.actions.fixedassettrace

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Fixed Asset Show in Grid.
 * For details go through Use-Case doc named 'ShowForFixedAssetTraceActionService'
 */
class ShowForFixedAssetTraceActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService

    private static final String DEFAULT_ERROR_MESSAGE = "Can't Load Fixed Asset"
    private static final String FIXED_ASSET_TRACE_LIST_WRAP = "fixedAssetTraceListWrap"
    private static final String FIXED_ASSET_ITEM_LIST = "fixedAssetItemList"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. initialize for pagination
     * 2. pull user inventory list
     * 3. get fixed asset trace object
     * 4. wrap fixed asset trace for grid show
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - wrapped fixed asset trace object, fixed asset item
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initPager(parameterMap)

            List<GroovyRowResult> fixedAssetItemList = getFixedAssetItem()

            List<Long> userInventoryIdList = inventoryImplService.getUserInventoryIds()
            if (userInventoryIdList.size() <= 0) {
                userInventoryIdList << 0L
            }

            Map serviceReturn = list(userInventoryIdList)
            List<GroovyRowResult> fixedAssetTraceList = serviceReturn.fixedAssetTraceList
            int total = (int) serviceReturn.count

            List fixedAssetTraceListWrap = wrapFATGridEntityList(fixedAssetTraceList, start)

            result.put(FIXED_ASSET_TRACE_LIST_WRAP, fixedAssetTraceListWrap)
            result.put(FIXED_ASSET_ITEM_LIST, fixedAssetItemList)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. receive fixed asset trace from execute method
     * @param obj - object returned from execute method
     * @return - a map containing wrapped fixed asset trace for grid show  & fixed asset item
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj

            int count = (int) receiveResult.get(Tools.COUNT)
            List fixedAssetTraceList = (List) receiveResult.get(FIXED_ASSET_TRACE_LIST_WRAP)
            List<GroovyRowResult> fixedAssetItemList = (List) receiveResult.get(FIXED_ASSET_ITEM_LIST)

            result = [page: pageNumber, total: count, fixedAssetItemList: fixedAssetItemList,
                    rows: fixedAssetTraceList]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * wrap fixed asset trace for grid entity
     * @param fixedAssetTraceList - list of fixed asset trace
     * @param start - starting point of index
     * @return - wrapped fixed asset trace for grid entity
     */
    private wrapFATGridEntityList(List<GroovyRowResult> fixedAssetTraceList, int start) {
        List lstFixedAssetTrace = [] as List
        int counter = start + 1
        GroovyRowResult eachRow
        for (int i = 0; i < fixedAssetTraceList.size(); i++) {
            eachRow = fixedAssetTraceList[i]
            GridEntity gridEntity = new GridEntity()
            gridEntity.id = eachRow.id
            gridEntity.cell = [
                    counter,
                    eachRow.id,
                    eachRow.category_name,
                    eachRow.model_name,
                    eachRow.inventory_name,
                    eachRow.current ? Tools.YES : Tools.NO,
                    eachRow.transaction_date
            ]
            lstFixedAssetTrace << gridEntity
            counter++
        }
        return lstFixedAssetTrace

    }

    // List of Fixed Asset Trace for grid
    private LinkedHashMap list(List<Long> userInventoryIdList) {
        //@todo:model adjust using FixedAssetTraceModel.list()
        String lstUserInventoryIds = Tools.buildCommaSeparatedStringOfIds(userInventoryIdList)
        String queryStr = """
             SELECT fat.id, item.name AS category_name, fad.name AS model_name,
                    to_char(fat.transaction_date, 'dd-Mon-YYYY') AS transaction_date,
                    inventory.name AS inventory_name, fat.is_current AS current
            FROM fxd_fixed_asset_trace fat
                LEFT JOIN item ON item.id = fat.item_id
                LEFT JOIN fxd_fixed_asset_details fad ON fad.id = fat.fixed_asset_details_id
                LEFT JOIN inv_inventory inventory ON inventory.id = fat.inventory_id
            WHERE inventory.id IN(${lstUserInventoryIds})
            ORDER BY fat.id desc
            LIMIT ${resultPerPage}  OFFSET ${start}
        """

        String queryCount = """
            SELECT COUNT(fat.id) count
                FROM fxd_fixed_asset_trace fat
                LEFT JOIN inv_inventory inventory ON inventory.id = fat.inventory_id
            WHERE inventory.id IN(${lstUserInventoryIds})
        """

        List<GroovyRowResult> result = executeSelectSql(queryStr)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount)

        int total = (int) countResult[0].count
        return [fixedAssetTraceList: result, count: total]
    }

    private static final String SELECT_QUERY = """
            SELECT item.id, item.name
            FROM fxd_fixed_asset_details fad
                LEFT JOIN item ON item.id = fad.item_id
                WHERE  item.category_id =:itemCategoryId AND
                       item.company_id =:companyId
            GROUP BY  item.name, item.id
            ORDER BY item.name
        """
    // List of Fixed Asset Item for Category Drop Down In Fixed Asset Trace
    //@todo:model use existing query
    public List<GroovyRowResult> getFixedAssetItem() {
        long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity fxdItemSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, companyId)
        Map queryParams = [
                itemCategoryId: fxdItemSysEntityObject.id,
                companyId: companyId
        ]
        List<GroovyRowResult> result = executeSelectSql(SELECT_QUERY, queryParams)
        return result
    }
}

