package com.athena.mis.procurement.actions.purchaseorderdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcPurchaseOrderDetails
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for purchase order details CRUD and list of purchase order details for grid
 *  For details go through Use-Case doc named 'ShowPurchaseOrderDetailsActionService'
 */
class ShowPurchaseOrderDetailsActionService extends BaseService implements ActionIntf {

    PurchaseOrderService purchaseOrderService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String PURCHASE_ORDER_MAP = "purchaseOrderMap"
    private static final String COUNT = "count"
    private static final String SERVER_ERROR_MESSAGE = "Fail to load Purchase Order Details"
    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase Order not found"
    private static final String PURCHASE_ORDER_DETAILS_LIST = "purchaseOrderDetailsList"

    /**
     * 1. check po existence
     * @param parameters - serialize parameters from UI
     * @param obj -N/A
     * @return - isError(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())

            if (purchaseOrderId < 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }

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
     * Get purchase order details object
     * 1. pull purchase order object
     * 2. po existence check
     * 3. pull po details list
     * 4. build purchase order map
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing po details list, po map
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long purchaseOrderId = Long.parseLong(parameterMap.purchaseOrderId.toString())
            List<ProcPurchaseOrderDetails> purchaseOrderDetailsList = []
            int count = 0
//            Map serviceReturn
            Map purchaseOrderMap = null

            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }
            purchaseOrderDetailsList = ProcPurchaseOrderDetails.findAllByPurchaseOrderId(purchaseOrderId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            count = ProcPurchaseOrderDetails.countByPurchaseOrderId(purchaseOrderId)

            purchaseOrderMap = buildPurchaseOrderMap(purchaseOrder)

            result.put(PURCHASE_ORDER_DETAILS_LIST, purchaseOrderDetailsList)
            result.put(COUNT, count)
            result.put(PURCHASE_ORDER_MAP, purchaseOrderMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
     * Wrap purchase order details  for grid show
     * @param obj
     * @return - wrapped purchase order details list, po object
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (LinkedHashMap) obj
            List<ProcPurchaseOrderDetails> purchaseOrderDetailsList = (List<ProcPurchaseOrderDetails>) executeResult.get(PURCHASE_ORDER_DETAILS_LIST)
            Map purchaseOrderMap = (Map) executeResult.get(PURCHASE_ORDER_MAP)
            int count = (int) executeResult.get(COUNT)
            List wrapPurchaseOrderDetailsList = (List) this.wrapPurchaseOrderDetailsListInGridEntityList(purchaseOrderDetailsList, start)
            Map gridObject = [page: pageNumber, total: count, rows: wrapPurchaseOrderDetailsList]

            result.put(PURCHASE_ORDER_MAP, purchaseOrderMap)
            result.put(PURCHASE_ORDER_DETAILS_LIST, gridObject)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Get wrapped purchase order details list
     * 1. Wrap grid object
     * 2. Show success message
     * @param purchaseOrderDetailsList -map from execute method
     * @param start starting index for grid
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    private List wrapPurchaseOrderDetailsListInGridEntityList(List<ProcPurchaseOrderDetails> purchaseOrderDetailsList, int start) {
        List purchaseOrders = [] as List
        int counter = start + 1
        String valDate
        Item item
        ItemType itemType
        String quantityWithUnit
        String createdOn
        AppUser createdBy
        for (int i = 0; i < purchaseOrderDetailsList.size(); i++) {
            ProcPurchaseOrderDetails purchaseOrderDetails = purchaseOrderDetailsList[i]
            GridEntity obj = new GridEntity()
            obj.id = purchaseOrderDetails.id
            item = (Item) itemCacheUtility.read(purchaseOrderDetails.itemId)
            itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            quantityWithUnit = Tools.formatAmountWithoutCurrency(purchaseOrderDetails.quantity) + Tools.SINGLE_SPACE + item.unit
            createdOn = DateUtility.getLongDateForUI(purchaseOrderDetails.createdOn)
            createdBy = (AppUser) appUserCacheUtility.read(purchaseOrderDetails.createdBy)
            double total = purchaseOrderDetails.quantity * purchaseOrderDetails.rate
            obj.cell = [
                    counter,
                    purchaseOrderDetails.id,
                    itemType.name,
                    item.name,
                    quantityWithUnit,
                    Tools.makeAmountWithThousandSeparator(purchaseOrderDetails.rate),
                    Tools.makeAmountWithThousandSeparator(total),
                    createdBy.username,
                    createdOn
            ]
            purchaseOrders << obj
            counter++
        }

        return purchaseOrders
    }
    /**
     * Build purchase order object
     * 1. pull project object
     * 2. pull budget object
     * 3. build po map
     * @param purchaseOrder -po object
     * @return - purchase order object
     */
    Map buildPurchaseOrderMap(ProcPurchaseOrder purchaseOrder) {
        Project project = (Project) projectCacheUtility.read(purchaseOrder.projectId)
        Map purchaseOrderMap = [
                purchaseOrderId: purchaseOrder.id,
                purchaseRequestId: purchaseOrder.purchaseRequestId,
                projectId: project.id,
                projectName: project.name
        ]
        return purchaseOrderMap
    }
}
