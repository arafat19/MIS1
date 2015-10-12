package com.athena.mis.integration.procurement.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get list of FixedAsset by PO-Id
 *  For details go through Use-Case doc named 'GetFixedAssetListByPOIdImplActionService'
 */
class GetFixedAssetListByPOIdImplActionService extends BaseService implements ActionIntf {

    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    ProcSessionUtil procSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     *
     * @param params1 - poId(PurchaseOrder.id)
     * @param params2 -N/A
     * @return -List of groovyRowResult
     */
    public Object execute(Object params1, Object params2) {
        try {
            long poId = Long.parseLong(params1.toString())
            return getFixedAssetListByPOId(poId)
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
     * Do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return false
    }

    /**
     * Do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * get fixedAssetList of approved purchaseOrder
     * @param poId -PurchaseOrder.id
     * @return -list of groovyRowResult
     */
    private List<GroovyRowResult> getFixedAssetListByPOId(long poId) {
        long companyId = procSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity itemFxdSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, companyId)

        String queryStr = """SELECT pod.item_id AS id,
                            item.name || '${Tools.PARENTHESIS_START}'
                            || to_char((pod.quantity-pod.fixed_asset_details_count),'${Tools.DB_QUANTITY_FORMAT}')
                            || '${Tools.SINGLE_SPACE}' || item.unit
                            || '${Tools.PARENTHESIS_END}'  AS name,
                            pod.rate, (pod.quantity-pod.fixed_asset_details_count) AS quantity
                            FROM proc_purchase_order_details  pod
                            LEFT JOIN item ON item.id = pod.item_id
                            LEFT JOIN proc_purchase_order po ON po.id = pod.purchase_order_id
                            WHERE (pod.quantity-pod.fixed_asset_details_count) > 0
                            AND item.category_id =:fxdCategoryId
                            AND item.is_individual_entity = true
                            AND pod.purchase_order_id =:poId
                            AND po.approved_by_director_id > 0
                            AND po.approved_by_project_director_id > 0
                            ORDER BY pod.purchase_order_id
                      """
        Map queryParams = [
                fxdCategoryId: itemFxdSysEntityObject.id,
                poId: poId
        ]
        List<GroovyRowResult> resultList = executeSelectSql(queryStr, queryParams)
        return resultList
    }
}
