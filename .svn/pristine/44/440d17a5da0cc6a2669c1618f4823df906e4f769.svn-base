package com.athena.mis.intergation.fixedasset.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Get fixed asset details list by inventory id, used in inventory consumption details
 * For details go through Use-Case doc named 'GetFixedAssetListByInvIdImplActionService'
 */
class GetFixedAssetListByInvIdImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get fixed asset details list by inventory id
     * @param parameters -id of inventory
     * @param obj -N/A
     * @return -a list of fixed asset details
     */
    @Transactional(readOnly = true)
    public List<GroovyRowResult> execute(Object parameters, Object obj) {
        try {
            long inventoryId = Long.parseLong(parameters.toString())
            return getFixedAssetListByInvId(inventoryId)
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
           SELECT DISTINCT item.id, item.name
               FROM fxd_fixed_asset_details fad
               LEFT JOIN item ON item.id = fad.item_id
           WHERE fad.current_inventory_id =:inventoryId
    """

    /**
     * Get fixed asset details list by inventory id
     * @param inventoryId -id of inventory
     * @return -a list of fixed asset details
     */
    private List<GroovyRowResult> getFixedAssetListByInvId(long inventoryId) {
        Map queryParams = [
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> result = executeSelectSql(SELECT_QUERY, queryParams)
        if (result.size() > 0) {
            return result
        } else {
            return []
        }
    }
}
