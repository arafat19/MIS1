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
 *  Class to get available item list of a given PO
 *  For details go through Use-Case doc named 'ListItemByPurchaseOrderImplActionService'
 */
class ListItemByPurchaseOrderImplActionService extends BaseService implements ActionIntf {

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
     * get available item list of a given PO
     * @param params1 - poId(PurchaseOrder.id)
     * @param params2 -N/A
     * @return -List of groovyRowResult
     */
    @Transactional(readOnly = true)
    public Object execute(Object params1, Object obj) {
        try {
            long purchaseOrderId = Long.parseLong(params1.toString())
            return listItemByPurchaseOrder(purchaseOrderId)
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
            SELECT DISTINCT pod.item_id AS id, item.name AS name, item.unit,
                   pod.id AS purchase_request_details_id, pod.purchase_request_id as purchase_request_id,
                   (pod.quantity - pod.store_in_quantity) AS current_po_limit
            FROM proc_purchase_order_details pod
            LEFT JOIN item ON item.id = pod.item_id
            WHERE pod.purchase_order_id =:purchaseOrderId
             AND (pod.quantity -  pod.store_in_quantity) > 0
             AND (item.category_id = :categoryInvId OR item.category_id =:categoryFxdId)
             AND item.is_individual_entity = false
            ORDER BY name
        """
    /**
     * get available item list of a given PO
     * @param purchaseOrderId -PurchaseOrder.Id
     * @return -list of groovyRowResult
     */
    private List<GroovyRowResult> listItemByPurchaseOrder(long purchaseOrderId) {
        long companyId = procSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity itemInvSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.INVENTORY, companyId)
        SystemEntity itemFxdSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, companyId)
        Map queryParams = [
                categoryInvId: itemInvSysEntityObject.id,
                categoryFxdId: itemFxdSysEntityObject.id,
                purchaseOrderId: purchaseOrderId
        ]
        List<GroovyRowResult> lstMaterials = executeSelectSql(PROC_PURCHASE_ORDER_DETAILS_SELECT_QUERY, queryParams)
        return lstMaterials
    }
}
