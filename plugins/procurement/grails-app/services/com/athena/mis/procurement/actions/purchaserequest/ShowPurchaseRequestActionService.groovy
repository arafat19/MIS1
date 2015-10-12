package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.*
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for purchase request CRUD and list of purchase request for grid
 *  For details go through Use-Case doc named 'ShowPurchaseRequestActionService'
 */
class ShowPurchaseRequestActionService extends BaseService implements ActionIntf {

    BudgetPluginConnector budgetImplService
    PurchaseRequestService purchaseRequestService
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
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MSG = "Failed to load purchase request"
    private static final String PURCHASE_REQUEST_LIST_WRAP = "purchaseRequestListWrap"
    private static final String COUNT = "count"
    private static final String IS_USER_DIRECTOR = "isUserDirector"
    private static final String IS_USER_PROJECT_DIRECTOR = "isUserProjectDirector"
    /**
     *
     * @param parameters -N/A
     * @param obj -N/A
     * @return - a map containing has access True message
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }
    /**
     * Get purchase request list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            List<Long> projectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
            List<ProcPurchaseRequest> purchaseRequestList = []
            int count = 0

            if (projectIds.size() > 0) {
                initPager(parameterMap)
                resultPerPage = DEFAULT_RESULT_PER_PAGE
                purchaseRequestList = ProcPurchaseRequest.findAllByProjectIdInList(projectIds, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                count = ProcPurchaseRequest.countByProjectIdInList(projectIds)
            }
            List purchaseRequestListWrap = (List) wrapPurchaseRequestListInGrid(purchaseRequestList, start)
            result.put(PURCHASE_REQUEST_LIST_WRAP, purchaseRequestListWrap)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap purchase request list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List purchaseRequestList = (List) receiveResult.get(PURCHASE_REQUEST_LIST_WRAP)
            boolean isUserDirector = procSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)
            boolean isUserProjectDirector = procSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)

            result = [page: pageNumber, total: count, rows: purchaseRequestList]
            result.put(IS_USER_DIRECTOR, isUserDirector)
            result.put(IS_USER_PROJECT_DIRECTOR, isUserProjectDirector)
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
     * Wrap list of purchase request in grid entity
     * @param purchaseRequestList -list of purchase request object(s)
     * @param start -starting index of the page
     * @return -list of wrapped purchase request
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

