package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get Purchase Order list
 * For details go through Use-Case doc named 'ListPurchaseOrderActionService'
 */
class ListPurchaseOrderActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Failed to populate PO list"

    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get purchased order
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - purchased order list
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = DEFAULT_SORT_COLUMN
            }
            initSearch(params)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            List<Long> projectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
            List<ProcPurchaseOrder> purchaseOrderList = []
            int count = 0
            long purchaseRequestId
            if (parameterMap.purchaseRequestId) {
                purchaseRequestId = Long.parseLong(parameterMap.purchaseRequestId.toString())
                purchaseOrderList = ProcPurchaseOrder.findAllByPurchaseRequestId(purchaseRequestId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                count = ProcPurchaseOrder.countByPurchaseRequestId(purchaseRequestId)
            } else {
                if (projectIds.size() > 0) {
                    purchaseOrderList = ProcPurchaseOrder.findAllByProjectIdInList(projectIds, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                    count = ProcPurchaseOrder.countByProjectIdInList(projectIds)
                }
            }

            return [purchaseOrderList: purchaseOrderList, count: count]
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
     * Wrap for grid
     * @param obj - object from execute method
     * @return - wrapped purchase order object
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map output = null
        try {
            Map executeResult = (Map) obj
            List<ProcPurchaseOrder> purchaseOrderList = (List<ProcPurchaseOrder>) executeResult.purchaseOrderList
            int count = (int) executeResult.count
            List purchaseOrder = wrapPurchaseOrderListInGridEntityList(purchaseOrderList, start)
            return [page: pageNumber, total: count, rows: purchaseOrder]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            output = [page: pageNumber, total: 0, rows: null]
            return output
        }
    }
    /**
     * Get server error message
     * @param obj - object received from execute method
     * @return - array containing isError(True) and error message
     */
    public Object buildFailureResultForUI(Object obj) {
        return [isError: true, entity: obj, message: SERVER_ERROR_MESSAGE]
    }
    /**
     * Get wrapped purchase order list
     * 1. Wrap grid object
     * 2. Show success message
     * @param purchaseOrderList -map from execute method
     * @param start starting index for grid
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    private List wrapPurchaseOrderListInGridEntityList(List<ProcPurchaseOrder> purchaseOrderList, int start) {
        List purchaseOrders = [] as List
        try {
            int counter = start + 1
            Object budget
            String approvedByDirector
            String approvedByProjectDirector
            String createdOn
            for (int i = 0; i < purchaseOrderList.size(); i++) {
                ProcPurchaseOrder purchaseOrderInstance = purchaseOrderList[i]
                GridEntity obj = new GridEntity()
                obj.id = purchaseOrderInstance.id
                approvedByDirector = purchaseOrderInstance.approvedByDirectorId ? Tools.YES : Tools.NO
                approvedByProjectDirector = purchaseOrderInstance.approvedByProjectDirectorId ? Tools.YES : Tools.NO
                createdOn = DateUtility.getDateForUI(purchaseOrderInstance.createdOn)
                Supplier supplier = (Supplier) supplierCacheUtility.read(purchaseOrderInstance.supplierId)
                obj.cell = [
                        counter,
                        purchaseOrderInstance.id,
                        createdOn,
                        purchaseOrderInstance.purchaseRequestId,
                        supplier.name,
                        purchaseOrderInstance.itemCount,
                        Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.discount),
                        Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.totalPrice),
                        Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.trCostTotal),
                        Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.totalVatTax),
                        purchaseOrderInstance.sentForApproval ? Tools.YES : Tools.NO,
                        approvedByDirector,
                        approvedByProjectDirector
                ]
                purchaseOrders << obj
                counter++
            }

            return purchaseOrders
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return purchaseOrders
        }
    }
}
