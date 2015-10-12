package com.athena.mis.procurement.actions.cancelledpo

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.procurement.entity.ProcCancelledPO
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 * Show list of all cancelled PO
 * For details go through Use-Case doc named 'ShowCancelledPOActionService'
 */
class ShowCancelledPOActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Failed to populate PO list"
    private static final String PO_LIST = "purchasedOrderList"

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
     * @param params - parameter map from UI
     * @param obj - N/A
     * @return - purchased order list
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = DEFAULT_SORT_COLUMN
            }
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            List<ProcCancelledPO> purchaseOrderList = ProcCancelledPO.list([offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            int count = purchaseOrderList.size()
            result.put(PO_LIST, purchaseOrderList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
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
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List<ProcCancelledPO> purchaseOrderList = (List<ProcCancelledPO>) executeResult.get(PO_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List purchaseOrder = wrapPurchaseOrderListInGridEntityList(purchaseOrderList, start)
            Map output = [page: pageNumber, total: count, rows: purchaseOrder]
            result.put(PO_LIST, output)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
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
    private List wrapPurchaseOrderListInGridEntityList(List<ProcCancelledPO> purchaseOrderList, int start) {
        List purchaseOrders = [] as List
        try {
            int counter = start + 1
            String approvedByDirector
            String approvedByProjectDirector
            String createdOn
            String cancelledOn
            for (int i = 0; i < purchaseOrderList.size(); i++) {
                ProcCancelledPO purchaseOrderInstance = purchaseOrderList[i]
                GridEntity obj = new GridEntity()
                obj.id = purchaseOrderInstance.id
                approvedByDirector = purchaseOrderInstance.approvedByDirectorId ? Tools.YES : Tools.NO
                approvedByProjectDirector = purchaseOrderInstance.approvedByProjectDirectorId ? Tools.YES : Tools.NO
                createdOn = DateUtility.getDateForUI(purchaseOrderInstance.createdOn)
                cancelledOn = DateUtility.getDateForUI(purchaseOrderInstance.cancelledOn)
                Supplier supplier = (Supplier) supplierCacheUtility.read(purchaseOrderInstance.supplierId)
                obj.cell = [
                        counter,
                        purchaseOrderInstance.id,
                        createdOn,
                        cancelledOn,
                        purchaseOrderInstance.purchaseRequestId,
                        supplier.name,
                        purchaseOrderInstance.itemCount,
                        Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.discount),
                        Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.totalPrice),
                        Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.trCostTotal),
                        Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.totalVatTax),
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
