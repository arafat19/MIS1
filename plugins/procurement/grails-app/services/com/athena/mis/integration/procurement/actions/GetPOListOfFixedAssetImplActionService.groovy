package com.athena.mis.integration.procurement.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.procurement.utility.ProcSessionUtil
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get list of approved PO list of those have fixedAsset item
 *  For details go through Use-Case doc named 'GetPOListOfFixedAssetImplActionService'
 */
class GetPOListOfFixedAssetImplActionService extends BaseService implements ActionIntf {

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
     * get list of approved PO list of those have fixedAsset item
     * @param params1 - N/A
     * @param params2 -N/A
     * @return -List of groovyRowResult
     */
    @Transactional(readOnly = true)
    public Object execute(Object params1, Object params2) {
        try {
            return getPOListOfFixedAsset()
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

    private static final String PROC_PURCHASE_ORDER_DETAILS_SELECT_QUERY = """
                        SELECT DISTINCT pod.purchase_order_id AS id, pod.purchase_order_id AS name
                            FROM proc_purchase_order_details  pod
                            LEFT JOIN item ON item.id = pod.item_id
                            LEFT JOIN proc_purchase_order po ON po.id = pod.purchase_order_id
                            WHERE (pod.quantity-pod.fixed_asset_details_count) > 0
                            AND item.category_id =:itemCategoryId
                            AND item.is_individual_entity = true
                            AND po.approved_by_director_id > 0
                            AND po.approved_by_project_director_id > 0
                            ORDER BY pod.purchase_order_id
              """
    /*
     * get list of approved PO list of those have fixedAsset item
     * @return -list of groovyRowResult
     */

    private List<GroovyRowResult> getPOListOfFixedAsset() {
        long companyId = procSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity itemFxdSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, companyId)
        Map queryParams = [
                itemCategoryId: itemFxdSysEntityObject.id
        ]
        List<GroovyRowResult> resultList = executeSelectSql(PROC_PURCHASE_ORDER_DETAILS_SELECT_QUERY, queryParams)
        return resultList
    }
}
