package com.athena.mis.intergation.fixedasset.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Get fixed asset details list by inventory id and item id, used in inventory consumption details
 * For details go through Use-Case doc named 'ListByInvIdAndItemIdImplActionService'
 */
class ListByInvIdAndItemIdImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get fixed asset details list by inventory id and item id
     * @param parameters -id of inventory
     * @param obj -id of item
     * @return -a list of fixed asset details
     */
    @Transactional(readOnly = true)
    public List<GroovyRowResult> execute(Object parameters, Object obj) {
        try {
            long inventoryId = Long.parseLong(parameters.toString())
            long itemId = Long.parseLong(obj.toString())
            return listByInvIdAndItemId(inventoryId, itemId)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
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

    private static final String SELECT_QUERY = """
            SELECT fad.id, fad.name
                FROM fxd_fixed_asset_details fad
            WHERE fad.current_inventory_id =:inventoryId
            AND fad.item_id =:itemId
    """

    /**
     * Get fixed asset details list by inventory id and item id
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -a list of fixed asset details
     */
    private List<GroovyRowResult> listByInvIdAndItemId(long inventoryId, long itemId) {
        Map queryParams = [
                inventoryId: inventoryId,
                itemId: itemId
        ]
        List<GroovyRowResult> result = executeSelectSql(SELECT_QUERY, queryParams)
        if (result.size() > 0) {
            return result
        } else {
            return null
        }
    }
}

