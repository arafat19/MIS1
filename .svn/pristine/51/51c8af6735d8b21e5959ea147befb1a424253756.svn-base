package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get Purchase Request list
 * For details go through Use-Case doc named 'ListPurchaseRequestActionService'
 */
class ListPurchaseRequestActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    BudgetPluginConnector budgetImplService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility

    private static final String PURCHASE_REQUEST_LIST_WRAP = "purchaseRequestListWrap"
    private static final String FAILURE_MSG = "Fail to get purchase request list"
    private static final String COUNT = "count"
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get purchased request
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - purchased request list
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)
            List<ProcPurchaseRequest> purchaseRequestList = []
            int count = 0
            List<Long> projectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
            if (projectIds.size() > 0) {
                    purchaseRequestList = ProcPurchaseRequest.findAllByProjectIdInList(projectIds, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                    count = ProcPurchaseRequest.countByProjectIdInList(projectIds)
            }

            List purchaseRequestListWrap = (List) wrapPurchaseRequestListInGrid(purchaseRequestList, start)
            result.put(PURCHASE_REQUEST_LIST_WRAP, purchaseRequestListWrap)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap for grid
     * @param obj - object from execute method
     * @return - wrapped purchase request object
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List purchaseRequestList = (List) receiveResult.get(PURCHASE_REQUEST_LIST_WRAP)
            result = [page: pageNumber, total: count, rows: purchaseRequestList]
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
    public Object buildFailureResultForUI(Object obj) {
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
    /**
     * Wrap pr for grid
     * @param obj - object from execute method
     * @return - wrapped purchase request object
     */
    private List wrapPurchaseRequestListInGrid(List<ProcPurchaseRequest> purchaseRequestList, int start) {
        List purchaseRequests = [] as List
        int counter = start + 1
        String approvedByDirector
        String approvedByProjectDirector
        String isSentForApproval
        Project project
        AppUser systemUser
        for (int i = 0; i < purchaseRequestList.size(); i++) {
            ProcPurchaseRequest purchaseRequestInstance = purchaseRequestList[i]
            GridEntity obj = new GridEntity()
            obj.id = purchaseRequestInstance.id
            project = (Project) projectCacheUtility.read(purchaseRequestInstance.projectId)
            systemUser = (AppUser) appUserCacheUtility.read(purchaseRequestInstance.createdBy)
            approvedByDirector = purchaseRequestInstance.approvedByDirectorId ? Tools.YES : Tools.NO
            approvedByProjectDirector = purchaseRequestInstance.approvedByProjectDirectorId ? Tools.YES : Tools.NO
            isSentForApproval = purchaseRequestInstance.sentForApproval ? Tools.YES : Tools.NO

            obj.cell = [
                    counter,
                    purchaseRequestInstance.id,
                    project.name,
                    purchaseRequestInstance.itemCount,
                    approvedByDirector,
                    approvedByProjectDirector,
                    systemUser.username,
                    isSentForApproval
            ]
            purchaseRequests << obj
            counter++
        }
        return purchaseRequests
    }
}

