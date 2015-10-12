package com.athena.mis.fixedasset.actions.report.pendingfixedasset

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of Pending Fixed Asset
 * For details go through Use-Case doc named 'ListForCurrentFixedAssetActionService'
 */
class ListForPendingFixedAssetActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    FxdSessionUtil fxdSessionUtil

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to load Pending fixed asset report."
    private static final String LST_PENDING_FIXED_ASSET = "lstPendingFixedAsset"
    private static final String PO_NOT_FOUND = "PO not found with this Project."
    private static final String PROJECT_ID = "projectId"
    private static final String COUNT = "count"
    private static final String SORT_NAME = "item.name"
    private static final String SORT_ORDER = "ASC"

    /**
     * 1. check project existence
     * @param parameters - serialized parameters from UI.
     * @param obj - N/A
     * @return- a map containing isError msg(True/False) and relevant msg(if any)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
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
     * 2. get list of pending fixed asset by project id
     * 3. check purchase order details existence
     * 4. wrap pending fixed asset for grid entity
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing inventory project id & list of pending fixed asset
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

            long projectId = Long.parseLong(parameterMap.projectId.toString())
            LinkedHashMap serviceReturn = listPendingFixedAssetByProjectId(projectId)
            List<GroovyRowResult> lstPendingFixedAsset = (List<GroovyRowResult>) serviceReturn.lstPendingFixedAsset
            int count = (int) serviceReturn.count
            if (count <= 0) {
                result.put(Tools.MESSAGE, PO_NOT_FOUND)
                return result
            }
            List pendingFixedAssetList = wrapPendingFixedAssetListInGrid(lstPendingFixedAsset, start)
            result.put(PROJECT_ID, projectId)
            result.put(LST_PENDING_FIXED_ASSET, pendingFixedAssetList)
            result.put(COUNT, count)
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
     * Get wrapped pending fixed asset list for grid entity
     * @param obj - object receive from execute method
     * @return - wrapped grid output of pending fixed asset
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List BudgetWisePoSummaryListWrap = (List) executeResult.get(LST_PENDING_FIXED_ASSET)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: this.pageNumber, total: count, rows: BudgetWisePoSummaryListWrap]
            result.put(LST_PENDING_FIXED_ASSET, gridOutput)
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
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
     * Wrapped pending fixed asset for grid show
     * @param lstCurrentFixedAsset - list of pending fixed asset
     * @param start - starting point of index
     * @return - Wrapped pending fixed asset list
     */
    private List wrapPendingFixedAssetListInGrid(List<GroovyRowResult> lstPendingFixedAsset, int start) {
        List lstPendingFixedAssetSummary = [] as List

        int counter = start + 1
        GroovyRowResult pendingFixedAsset
        GridEntity obj

        for (int i = 0; i < lstPendingFixedAsset.size(); i++) {
            pendingFixedAsset = lstPendingFixedAsset[i]
            obj = new GridEntity()
            obj.id = pendingFixedAsset.id
            lstPendingFixedAssetSummary << obj
            obj.cell = [counter,
                    pendingFixedAsset.id,
                    pendingFixedAsset.item_name,
                    pendingFixedAsset.received,
                    pendingFixedAsset.asset_found,
                    pendingFixedAsset.asset_remaining
            ]
            counter++
        }
        return lstPendingFixedAssetSummary
    }

    private static final String COUNT_QUERY = """
        SELECT COUNT(pod.purchase_order_id) AS count
        FROM proc_purchase_order_details pod
        LEFT JOIN item ON item.id = pod.item_id
        WHERE item.category_id=:itemCategoryId
	    AND item.is_individual_entity=true
        AND (pod.quantity > pod.fixed_asset_details_count)
        AND pod.project_id =:projectId
        """

    // Inventory out list for grid
    public LinkedHashMap listPendingFixedAssetByProjectId(long projectId) {
        SystemEntity fxdItemSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, fxdSessionUtil.appSessionUtil.getCompanyId())
        long itemCategoryId = fxdItemSysEntityObject.id
        //@todo:model implement when procurement model is done
        String queryStr = """
        SELECT po.id AS id, item.name AS item_name,
               (to_char(COALESCE(SUM(pod.quantity),0),'${Tools.DB_QUANTITY_FORMAT}')) || '${Tools.SINGLE_SPACE}' || item.unit received,
               (to_char(COALESCE(SUM(pod.fixed_asset_details_count),0),'${Tools.DB_QUANTITY_FORMAT}')) || '${Tools.SINGLE_SPACE}' || item.unit asset_found,
        (to_char(COALESCE(SUM(pod.quantity - pod.fixed_asset_details_count),0),'${Tools.DB_QUANTITY_FORMAT}')) || '${Tools.SINGLE_SPACE}' || item.unit asset_remaining
        FROM proc_purchase_order_details pod
        LEFT JOIN proc_purchase_order po ON po.id = pod.purchase_order_id
        LEFT JOIN item ON item.id = pod.item_id
        WHERE item.category_id=:itemCategoryId
        AND item.is_individual_entity=true
        AND (pod.quantity > pod.fixed_asset_details_count)
        AND pod.project_id =:projectId
        GROUP BY po.id, item.name, item.unit
        ORDER BY ${sortColumn} ${sortOrder}
        LIMIT :resultPerPage  OFFSET :start
        """

        Map queryParams = [
                itemCategoryId: itemCategoryId,
                projectId: projectId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstPendingFixedAsset = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(COUNT_QUERY, queryParams)

        Map result = [lstPendingFixedAsset: lstPendingFixedAsset, count: countResult[0].count]
        return result
    }
}
