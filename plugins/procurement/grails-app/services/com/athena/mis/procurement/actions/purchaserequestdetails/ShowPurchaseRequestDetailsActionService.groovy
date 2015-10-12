package com.athena.mis.procurement.actions.purchaserequestdetails

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
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for purchase Request details CRUD and list of purchase Request details for grid
 *  For details go through Use-Case doc named 'ShowPurchaseRequestDetailsActionService'
 */
class ShowPurchaseRequestDetailsActionService extends BaseService implements ActionIntf {

    PurchaseRequestService purchaseRequestService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private final Logger log = Logger.getLogger(getClass())
    private static final String FAILURE_MSG = "Failed to load purchase request"
    private static final String PURCHASE_REQUEST_DETAILS_LIST_WRAP = "prDetailsListWrap"
    private static final String COUNT = "count"
    private static final String PURCHASE_REQUEST_MAP = "purchaseRequestMap"

    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap preResult = new LinkedHashMap()
        try {
            preResult.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return preResult
        } catch (Exception e) {
            log.error(e.getMessage())
            preResult.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return preResult
        }
    }
    /**
     * Get purchase request details wrapped list for grid
     * 1. pull pr object
     * 2. build Purchase Request Map
     * 3. wrap Purchase Request List In Grid Entity List
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            List<ProcPurchaseRequestDetails> purchaseRequestDetailsList = []
            initPager(parameterMap)
            int count = 0
            Map serviceReturn = new LinkedHashMap()

            long purchaseRequestId
            purchaseRequestId = Long.parseLong(parameterMap.purchaseRequestId.toString())
            ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseRequestId)

            if (purchaseRequest) {
                Map purchaseRequestMap = buildPurchaseRequestMap(purchaseRequest)
                result.put(PURCHASE_REQUEST_MAP, purchaseRequestMap)
                purchaseRequestDetailsList = ProcPurchaseRequestDetails.findAllByPurchaseRequestId(purchaseRequestId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                count = ProcPurchaseRequestDetails.countByPurchaseRequestId(purchaseRequestId)
            }

            List purchaseRequestDetailsListWrap = (List) wrapPurchaseRequestListInGridEntityList(purchaseRequestDetailsList, this.start)
            result.put(PURCHASE_REQUEST_DETAILS_LIST_WRAP, purchaseRequestDetailsListWrap)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Get purchase request object
     * @param purchaseRequestDetails - purchase request details id
     * @return - purchase request object
     */
    private Map buildPurchaseRequestMap(ProcPurchaseRequest purchaseRequest) {
        Project project = (Project) projectCacheUtility.read(purchaseRequest.projectId)

        Map purchaseRequestDetailsMap = [
                projectId: project.id,
                projectName: project.name,
                purchaseRequestId: purchaseRequest.id
        ]
        return purchaseRequestDetailsMap
    }
    /**
     * Get wrapped purchase request details list
     * 1. Wrap grid object
     * 2. Show success message
     * @param purchaseRequestDetailsList -map from execute method
     * @param start starting index for grid
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    private List wrapPurchaseRequestListInGridEntityList(List<ProcPurchaseRequestDetails> purchaseRequestDetailsList, int start) {
        List purchaseRequestDetailList = [] as List
        int counter = start + 1
        Item item
        ItemType itemType
        AppUser systemUser
        for (int i = 0; i < purchaseRequestDetailsList.size(); i++) {
            ProcPurchaseRequestDetails prDetails = purchaseRequestDetailsList[i]
            GridEntity obj = new GridEntity()
            obj.id = prDetails.id
            item = (Item) itemCacheUtility.read(prDetails.itemId)
            systemUser = (AppUser) appUserCacheUtility.read(prDetails.createdBy)
            itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            obj.cell = [
                    counter,
                    prDetails.id,
                    itemType.name,
                    item.name,
                    Tools.formatAmountWithoutCurrency(prDetails.quantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.makeAmountWithThousandSeparator(prDetails.rate),
                    Tools.makeAmountWithThousandSeparator((prDetails.quantity * prDetails.rate)),
                    DateUtility.getLongDateForUI(prDetails.createdOn),
                    systemUser.username

            ]
            purchaseRequestDetailList << obj
            counter++
        }

        return purchaseRequestDetailList
    }
    /**
     * do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap purchase request details  for grid show
     * @param obj
     * @return - wrapped purchase request details list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List purchaseRequestList = (List) receiveResult.get(PURCHASE_REQUEST_DETAILS_LIST_WRAP)
            Map purchaseRequestDetailsMap = (Map) receiveResult.get(PURCHASE_REQUEST_MAP)

            result = [page: pageNumber, total: count, rows: purchaseRequestList]
            result.put(PURCHASE_REQUEST_MAP, purchaseRequestDetailsMap)

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
}