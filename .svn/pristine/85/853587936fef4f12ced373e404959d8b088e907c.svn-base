package com.athena.mis.procurement.actions.purchaserequestdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get Purchase Request Details list
 * For details go through Use-Case doc named 'ListPurchaseRequestDetailsActionService'
 */
class ListPurchaseRequestDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String FAILURE_MSG = "Fail to get purchase request list"
    private static final String PURCHASE_REQUEST_DETAILS_LIST_WRAP = "purchaseRequestListWrap"
    private static final String COUNT = "count"

    /**
     * @return -a map containing isError(True/False) & hasAccess True
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Get purchased request details
     * 1. get purchase request details id
     * 2. wrap pr details list for grid entity
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - purchased request details list
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            List<ProcPurchaseRequestDetails> purchaseRequestDetailsList = []
            int count = 0
            long purchaseRequestId = Long.parseLong(parameterMap.purchaseRequestId.toString())
            if (purchaseRequestId > 0) {
                purchaseRequestDetailsList = ProcPurchaseRequestDetails.findAllByPurchaseRequestId(purchaseRequestId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                count = ProcPurchaseRequestDetails.countByPurchaseRequestId(purchaseRequestId)
            }

            List purchaseRequestListWrap = (List) wrapPurchaseRequestListInGridEntityList(purchaseRequestDetailsList, start)
            result.put(PURCHASE_REQUEST_DETAILS_LIST_WRAP, purchaseRequestListWrap)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Wrap pr for grid
     * @param obj - object from execute method
     * @return - wrapped purchase request details object
     */
    private List wrapPurchaseRequestListInGridEntityList(List<ProcPurchaseRequestDetails> purchaseRequestDetailsList, int start) {
        List purchaseRequestDetailList = [] as List
        int counter = start + 1
        Item item
        ItemType itemType
        String updatedDate
        AppUser systemUser
        AppUser updatedBy
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


    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }
    /**
     * Wrap for grid
     * @param obj - object from execute method
     * @return - wrapped purchase request details object
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List purchaseRequestDetailsList = (List) receiveResult.get(PURCHASE_REQUEST_DETAILS_LIST_WRAP)
            result = [page: pageNumber, total: count, rows: purchaseRequestDetailsList]
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
