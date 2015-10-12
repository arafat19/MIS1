package com.athena.mis.integration.inventory.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get stock balance of inventory item category for a particular project, used in project status report
 * For details go through Use-Case doc named 'GetMaterialStockBalanceByProjectIdImplActionService'
 */
class GetMaterialStockBalanceByProjectIdImplActionService extends BaseService implements ActionIntf {

    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get stock balance of inventory item category for a particular project
     * @param params -id of project
     * @param obj -N/A
     * @return -stock balance of inventory item category
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            long projectId = Long.parseLong(params.toString())
            return getMaterialStockBalanceByProjectId(projectId)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return Tools.EMPTY_SPACE
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get stock balance of inventory item category for a particular project
     * @param projectId -id of project
     * @return -stock balance of inventory item category
     */
    private String getMaterialStockBalanceByProjectId(long projectId) {
        SystemEntity itemSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.INVENTORY, invSessionUtil.appSessionUtil.getCompanyId())
        String queryStr = """
        SELECT
            (to_char(COALESCE(SUM(iits.total_amount),0),'${Tools.DB_CURRENCY_FORMAT}')) AS material_stock_balance
        FROM vw_inv_inventory_valuation iits
        LEFT JOIN inv_inventory ii ON ii.id = iits.inventory_id
        LEFT JOIN item ON iits.item_id = item.id
        WHERE ii.project_id =:projectId AND
              item.category_id =:itemCategoryId
        """
        Map queryParams = [
                itemCategoryId: itemSysEntityObject.id,
                projectId: projectId
        ]
        List<GroovyRowResult> resultList = executeSelectSql(queryStr, queryParams)
        return resultList[0].material_stock_balance
    }
}