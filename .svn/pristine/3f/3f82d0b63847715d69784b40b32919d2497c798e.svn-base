package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.utility.*
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for purchase order CRUD and list of purchase order for grid
 *  For details go through Use-Case doc named 'ShowPurchaseOrderActionService'
 */
class ShowPurchaseOrderActionService extends BaseService implements ActionIntf {

    private static final String SERVER_ERROR_MESSAGE = "Fail to load Purchase Order"
    private static final String PURCHASE_REQUEST_NOT_APPROVED_MESSAGE = "Selected Purchase Request is not approved."

    private final Logger log = Logger.getLogger(getClass())


    PurchaseRequestService purchaseRequestService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    PaymentMethodCacheUtility paymentMethodCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String IS_USER_DIRECTOR = "isUserDirector"
    private static final String IS_USER_PROJECT_DIRECTOR = "isUserProjectDirector"
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get purchase order list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            initPager(params)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            List<Long> projectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()

            LinkedHashMap serviceReturn
            List<ProcPurchaseOrder> purchaseOrderList = []
            int count = 0
            Map purchaseRequestMap = null
            if (projectIds.size() > 0) {
                if (parameterMap.purchaseRequestId) {
                    long purchaseRequestId = Long.parseLong(parameterMap.purchaseRequestId.toString())
                    ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseRequestId)
                    if (!purchaseRequest || purchaseRequest.approvedByDirectorId <= 0
                            || purchaseRequest.approvedByProjectDirectorId <= 0) {
                        return [isError: Boolean.TRUE, message: PURCHASE_REQUEST_NOT_APPROVED_MESSAGE]
                    }
                    purchaseOrderList = ProcPurchaseOrder.findAllByPurchaseRequestId(purchaseRequestId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                    count = ProcPurchaseOrder.countByPurchaseRequestId(purchaseRequestId)
                    purchaseRequestMap = buildPurchaseRequestMap(purchaseRequest)
                } else {
                    purchaseRequestMap = null
                    purchaseOrderList = ProcPurchaseOrder.findAllByProjectIdInList(projectIds, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                    count = ProcPurchaseOrder.countByProjectIdInList(projectIds)
                }
            }

            return [purchaseOrderList: purchaseOrderList, count: count, purchaseRequestMap: purchaseRequestMap, isError: Boolean.FALSE]
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
     * Wrap purchase order list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map output = null
        try {
            Map executeResult = (Map) obj
            List<ProcPurchaseOrder> purchaseOrderList = (List<ProcPurchaseOrder>) executeResult.purchaseOrderList
            Map purchaseRequestMap = (Map) executeResult.purchaseRequestMap
            int count = (int) executeResult.count
            List purchaseOrder = (List) wrapPurchaseOrderListInGridEntityList(purchaseOrderList, start)
            List paymentMethodList = paymentMethodCacheUtility.listByIsActive()


            boolean isUserDirector = procSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)
            boolean isUserProjectDirector = procSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)

            output = [page: pageNumber, total: count, rows: purchaseOrder]

            result = [purchaseOrderList: output, purchaseRequestMap: purchaseRequestMap, paymentMethodList: paymentMethodList]
            result.put(IS_USER_DIRECTOR, isUserDirector)
            result.put(IS_USER_PROJECT_DIRECTOR, isUserProjectDirector)

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            output = [page: pageNumber, total: 0, rows: null]
            result = [purchaseOrderList: output]
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
     * Get purchase request object
     * @param purchaseRequest - purchase request object
     * @return - purchase request object
     */
    Map buildPurchaseRequestMap(ProcPurchaseRequest purchaseRequest) {
        Project project = (Project) projectCacheUtility.read(purchaseRequest.projectId)
        Map purchaseRequestMap = [
                purchaseRequestId: purchaseRequest.id,
                projectId: project.id,
                projectName: project.name
        ]
        return purchaseRequestMap
    }
    /**
     * Wrap list of purchase order in grid entity
     * @param indentList -list of purchase order object(s)
     * @param start -starting index of the page
     * @return -list of wrapped purchase order
     */
    private List wrapPurchaseOrderListInGridEntityList(List<ProcPurchaseOrder> purchaseOrderList, int start) {
        List purchaseOrders = [] as List

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
    }
}
